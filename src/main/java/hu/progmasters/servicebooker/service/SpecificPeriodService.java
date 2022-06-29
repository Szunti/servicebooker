package hu.progmasters.servicebooker.service;

import hu.progmasters.servicebooker.domain.entity.Boose;
import hu.progmasters.servicebooker.domain.entity.SpecificPeriod;
import hu.progmasters.servicebooker.domain.entity.SpecificPeriodType;
import hu.progmasters.servicebooker.dto.specificperiod.SpecificPeriodCreateCommand;
import hu.progmasters.servicebooker.dto.specificperiod.SpecificPeriodInfo;
import hu.progmasters.servicebooker.dto.specificperiod.SpecificPeriodUpdateCommand;
import hu.progmasters.servicebooker.exceptionhandling.specificperiod.OverlappingSpecificPeriodException;
import hu.progmasters.servicebooker.exceptionhandling.specificperiod.SpecificPeriodNotForBooseException;
import hu.progmasters.servicebooker.exceptionhandling.specificperiod.SpecificPeriodNotFoundException;
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
        SpecificPeriod saved = repository.save(toSave);
        return toDto(saved);
    }

    @Transactional
    public List<SpecificPeriodInfo> findAllForBoose(int booseId, Interval<LocalDateTime> interval,
                                                    SpecificPeriodType type) {
        Interval<LocalDateTime> constrainedInterval = dateTimeBoundChecker.constrain(interval);
        Boose boose = booseService.getFromIdOrThrow(booseId);
        return getAllForBoose(boose, constrainedInterval, type, false).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<SpecificPeriod> getAllForBoose(Boose boose, Interval<LocalDateTime> interval,
                                               SpecificPeriodType type, boolean lock) {
        Interval<LocalDateTime> constrainedInterval = dateTimeBoundChecker.constrain(interval);
        return repository.findAllOrderedFor(boose, constrainedInterval, type, lock);
    }

    @Transactional
    public SpecificPeriodInfo findForBooseById(int booseId, int id) {
        SpecificPeriod specificPeriod = getForBooseByIdOrThrow(booseId, id);
        return toDto(specificPeriod);
    }

    private SpecificPeriod getForBooseByIdOrThrow(int booseId, int id) {
        Boose boose = booseService.getFromIdOrThrow(booseId);
        SpecificPeriod specificPeriod = repository.findById(id).orElseThrow(
                () -> new SpecificPeriodNotFoundException(id)
        );
        if (specificPeriod.getBoose() != boose) {
            throw new SpecificPeriodNotForBooseException(id, booseId);
        }
        return specificPeriod;
    }

    @Transactional
    public SpecificPeriodInfo update(int booseId, int id, SpecificPeriodUpdateCommand command) {
        SpecificPeriod specificPeriod = getForBooseByIdOrThrow(booseId, id);
        modelMapper.map(command, specificPeriod);
        return toDto(specificPeriod);
    }

    private SpecificPeriodInfo toDto(SpecificPeriod specificPeriod) {
        return modelMapper.map(specificPeriod, SpecificPeriodInfo.class);
    }
}
