package hu.progmasters.servicebooker.service;

import hu.progmasters.servicebooker.domain.Boose;
import hu.progmasters.servicebooker.domain.SpecificPeriod;
import hu.progmasters.servicebooker.domain.WeeklyPeriod;
import hu.progmasters.servicebooker.dto.*;
import hu.progmasters.servicebooker.exceptionhandling.BooseNotFoundException;
import hu.progmasters.servicebooker.repository.BooseRepository;
import hu.progmasters.servicebooker.repository.SpecificPeriodRepository;
import hu.progmasters.servicebooker.repository.WeeklyPeriodRepository;
import hu.progmasters.servicebooker.util.interval.Interval;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BooseService {

    private final BooseRepository booseRepository;
    private final WeeklyPeriodRepository weeklyPeriodRepository;
    private final SpecificPeriodRepository specificPeriodRepository;

    private final ModelMapper modelMapper;

    public BooseService(BooseRepository booseRepository,
                        WeeklyPeriodRepository weeklyPeriodRepository,
                        SpecificPeriodRepository specificPeriodRepository, ModelMapper modelMapper) {
        this.booseRepository = booseRepository;
        this.weeklyPeriodRepository = weeklyPeriodRepository;
        this.specificPeriodRepository = specificPeriodRepository;
        this.modelMapper = modelMapper;
    }

    public BooseInfo save(BooseCreateCommand command) {
        Boose toSave = modelMapper.map(command, Boose.class);
        Boose saved = booseRepository.save(toSave);
        return modelMapper.map(saved, BooseInfo.class);
    }

    public BooseInfo findById(int id) {
        Boose boose = getFromIdOrThrow(id);
        return modelMapper.map(boose, BooseInfo.class);
    }

    public List<BooseInfo> findAll() {
        return booseRepository.findAll().stream()
                .map(boose -> modelMapper.map(boose, BooseInfo.class))
                .collect(Collectors.toList());
    }

    public WeeklyPeriodInfo addWeeklyPeriodForBoose(WeeklyPeriodCreateCommand command) {
        Boose boose = getFromIdOrThrow(command.getBooseId());
        WeeklyPeriod toSave = modelMapper.map(command, WeeklyPeriod.class);
        toSave.setBoose(boose);
        WeeklyPeriod saved = weeklyPeriodRepository.save(toSave);
        return modelMapper.map(saved, WeeklyPeriodInfo.class);
    }

    public List<WeeklyPeriodInfo> findAllWeeklyPeriodsForBoose(int booseId) {
        Boose boose = getFromIdOrThrow(booseId);
        return weeklyPeriodRepository.findAllFor(boose).stream()
                .map(weeklyPeriod -> modelMapper.map(weeklyPeriod, WeeklyPeriodInfo.class))
                .collect(Collectors.toList());
    }

    public SpecificPeriodInfo addSpecificPeriodForBoose(SpecificPeriodCreateCommand command) {
        Boose boose = getFromIdOrThrow(command.getBooseId());
        SpecificPeriod toSave = modelMapper.map(command, SpecificPeriod.class);
        toSave.setBoose(boose);
        SpecificPeriod saved = specificPeriodRepository.save(toSave);
        return modelMapper.map(saved, SpecificPeriodInfo.class);
    }

    public List<SpecificPeriodInfo> findAllSpecificPeriodsForBoose(int booseId, Interval<LocalDateTime> interval,
                                                                   Boolean bookable) {
        Boose boose = getFromIdOrThrow(booseId);
        return specificPeriodRepository.findAllFor(boose, interval, bookable).stream()
                .map(specificPeriod -> modelMapper.map(specificPeriod, SpecificPeriodInfo.class))
                .collect(Collectors.toList());
    }

    Boose getFromIdOrThrow(int id) {
        return booseRepository.findById(id).orElseThrow(
                () -> new BooseNotFoundException(id)
        );
    }
}
