package hu.progmasters.servicebooker.service;

import hu.progmasters.servicebooker.domain.entity.Boose;
import hu.progmasters.servicebooker.domain.entity.SpecificPeriod;
import hu.progmasters.servicebooker.dto.specificperiod.SpecificPeriodCreateCommand;
import hu.progmasters.servicebooker.dto.specificperiod.SpecificPeriodInfo;
import hu.progmasters.servicebooker.exceptionhandling.specificperiod.NoSuchSpecificPeriodException;
import hu.progmasters.servicebooker.exceptionhandling.specificperiod.OverlappingSpecificPeriodException;
import hu.progmasters.servicebooker.exceptionhandling.specificperiod.SpecificPeriodNotForBooseException;
import hu.progmasters.servicebooker.repository.SpecificPeriodRepository;
import hu.progmasters.servicebooker.util.interval.Interval;
import hu.progmasters.servicebooker.validation.DateTimeBoundChecker;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static hu.progmasters.servicebooker.util.interval.Interval.interval;

@Service
public class SpecificPeriodService {

    private final SpecificPeriodRepository repository;
    private final BooseService booseService;

    private final ModelMapper modelMapper;

    private final DateTimeBoundChecker dateTimeBoundChecker;

    public SpecificPeriodService(SpecificPeriodRepository repository,
                                 BooseService booseService,
                                 ModelMapper modelMapper,
                                 DateTimeBoundChecker dateTimeBoundChecker) {
        this.repository = repository;
        this.booseService = booseService;
        this.modelMapper = modelMapper;
        this.dateTimeBoundChecker = dateTimeBoundChecker;
    }

    @Transactional
    public SpecificPeriodInfo addForBoose(int booseId, SpecificPeriodCreateCommand command) {
        dateTimeBoundChecker.checkInBound(interval(command.getStart(), command.getEnd()));
        Boose boose = booseService.getFromIdOrThrow(booseId);
        SpecificPeriod toSave = modelMapper.map(command, SpecificPeriod.class);
        toSave.setBoose(boose);

        booseService.lockForUpdate(boose);
        if (!repository.findOverlappingPeriods(boose, toSave).isEmpty()) {
            throw new OverlappingSpecificPeriodException();
        }
        // TODO return if weekly is replaced, partially covered or neither
        SpecificPeriod saved = repository.save(toSave);
        return modelMapper.map(saved, SpecificPeriodInfo.class);
    }

    @Transactional
    public List<SpecificPeriodInfo> findAllForBoose(int booseId, Interval<LocalDateTime> interval,
                                                    Boolean bookable) {
        Interval<LocalDateTime> constrainedInterval = dateTimeBoundChecker.constrain(interval);
        Boose boose = booseService.getFromIdOrThrow(booseId);
        return repository.findAllOrderedFor(boose, constrainedInterval, bookable).stream()
                .map(specificPeriod -> modelMapper.map(specificPeriod, SpecificPeriodInfo.class))
                .collect(Collectors.toList());
    }

    public List<SpecificPeriod> getAllForBoose(Boose boose, Interval<LocalDateTime> interval,
                                               Boolean bookable) {
        Interval<LocalDateTime> constrainedInterval = dateTimeBoundChecker.constrain(interval);
        return repository.findAllOrderedFor(boose, constrainedInterval, bookable);
    }

    @Transactional
    public SpecificPeriodInfo findForBooseById(int booseId, int id) {
        SpecificPeriod specificPeriod = getForBooseByIdOrThrow(booseId, id);
        return modelMapper.map(specificPeriod, SpecificPeriodInfo.class);
    }


    private SpecificPeriod getForBooseByIdOrThrow(int booseId, int id) {
        // TODO maybe getReference is enough
        Boose boose = booseService.getFromIdOrThrow(booseId);
        SpecificPeriod specificPeriod = repository.findById(id).orElseThrow(
                () -> new NoSuchSpecificPeriodException(id)
        );
        if (specificPeriod.getBoose() != boose) {
            throw new SpecificPeriodNotForBooseException(id, booseId);
        }
        return specificPeriod;
    }

}
