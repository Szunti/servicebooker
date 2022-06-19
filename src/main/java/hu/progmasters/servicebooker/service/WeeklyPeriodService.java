package hu.progmasters.servicebooker.service;

import hu.progmasters.servicebooker.domain.entity.Boose;
import hu.progmasters.servicebooker.domain.entity.WeeklyPeriod;
import hu.progmasters.servicebooker.dto.weeklyperiod.WeeklyPeriodCreateCommand;
import hu.progmasters.servicebooker.dto.weeklyperiod.WeeklyPeriodInfo;
import hu.progmasters.servicebooker.exceptionhandling.weeklyperiod.NoSuchWeeklyPeriodException;
import hu.progmasters.servicebooker.exceptionhandling.weeklyperiod.OverlappingWeeklyPeriodException;
import hu.progmasters.servicebooker.exceptionhandling.weeklyperiod.WeeklyPeriodNotInBooseException;
import hu.progmasters.servicebooker.repository.WeeklyPeriodRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WeeklyPeriodService {

    private final WeeklyPeriodRepository repository;
    private final BooseService booseService;

    private final ModelMapper modelMapper;

    public WeeklyPeriodService(WeeklyPeriodRepository repository,
                               BooseService booseService,
                               ModelMapper modelMapper) {
        this.repository = repository;
        this.booseService = booseService;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public WeeklyPeriodInfo addWeeklyPeriodForBoose(int booseId, WeeklyPeriodCreateCommand command) {
        Boose boose = booseService.getFromIdOrThrow(booseId);
        WeeklyPeriod toSave = modelMapper.map(command, WeeklyPeriod.class);
        toSave.setBoose(boose);

        booseService.lockForUpdate(boose);
        if (!repository.findOverlappingPeriods(boose, toSave).isEmpty()) {
            throw new OverlappingWeeklyPeriodException();
        }
        WeeklyPeriod saved = repository.save(toSave);
        return modelMapper.map(saved, WeeklyPeriodInfo.class);
    }

    @Transactional
    public List<WeeklyPeriodInfo> findAllWeeklyPeriodsForBoose(int booseId) {
        Boose boose = booseService.getFromIdOrThrow(booseId);
        return repository.findAllOrderedFor(boose).stream()
                .map(weeklyPeriod -> modelMapper.map(weeklyPeriod, WeeklyPeriodInfo.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<WeeklyPeriod> getAllWeeklyPeriodsForBoose(Boose boose) {
        return repository.findAllOrderedFor(boose);
    }

    @Transactional
    public WeeklyPeriodInfo findWeeklyPeriodForBooseById(int booseId, int id) {
        WeeklyPeriod weeklyPeriod = getWeeklyPeriodFromBooseAndIdOrThrow(booseId, id);
        return modelMapper.map(weeklyPeriod, WeeklyPeriodInfo.class);
    }

    private WeeklyPeriod getWeeklyPeriodFromBooseAndIdOrThrow(int booseId, int id) {
        // TODO maybe getReference is enough
        Boose boose = booseService.getFromIdOrThrow(booseId);
        WeeklyPeriod weeklyPeriod = repository.findById(id).orElseThrow(
                () -> new NoSuchWeeklyPeriodException(id)
        );
        if (weeklyPeriod.getBoose() != boose) {
            throw new WeeklyPeriodNotInBooseException(id, booseId);
        }
        return weeklyPeriod;
    }
}
