package hu.progmasters.servicebooker.service;

import hu.progmasters.servicebooker.domain.entity.Boose;
import hu.progmasters.servicebooker.domain.entity.WeeklyPeriod;
import hu.progmasters.servicebooker.dto.weeklyperiod.WeeklyPeriodCreateCommand;
import hu.progmasters.servicebooker.dto.weeklyperiod.WeeklyPeriodInfo;
import hu.progmasters.servicebooker.dto.weeklyperiod.WeeklyPeriodUpdateCommand;
import hu.progmasters.servicebooker.exceptionhandling.weeklyperiod.OverlappingWeeklyPeriodException;
import hu.progmasters.servicebooker.exceptionhandling.weeklyperiod.WeeklyPeriodNotForBooseException;
import hu.progmasters.servicebooker.exceptionhandling.weeklyperiod.WeeklyPeriodNotFoundException;
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
    public WeeklyPeriodInfo addForBoose(int booseId, WeeklyPeriodCreateCommand command) {
        Boose boose = booseService.getFromIdOrThrow(booseId);
        WeeklyPeriod toSave = modelMapper.map(command, WeeklyPeriod.class);
        toSave.setBoose(boose);

        booseService.lockForUpdate(boose);
        if (!repository.findOverlappingPeriods(boose, toSave).isEmpty()) {
            throw new OverlappingWeeklyPeriodException();
        }
        WeeklyPeriod saved = repository.save(toSave);
        return toDto(saved);
    }

    @Transactional
    public List<WeeklyPeriodInfo> findAllForBoose(int booseId) {
        Boose boose = booseService.getFromIdOrThrow(booseId);
        return getAllForBoose(boose, false).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<WeeklyPeriod> getAllForBoose(Boose boose, boolean lock) {
        return repository.findAllOrderedFor(boose, lock);
    }

    @Transactional
    public WeeklyPeriodInfo findForBooseById(int booseId, int id) {
        WeeklyPeriod weeklyPeriod = getForBooseByIdOrThrow(booseId, id);
        return toDto(weeklyPeriod);
    }

    private WeeklyPeriod getForBooseByIdOrThrow(int booseId, int id) {
        Boose boose = booseService.getFromIdOrThrow(booseId);
        WeeklyPeriod weeklyPeriod = repository.findById(id).orElseThrow(
                () -> new WeeklyPeriodNotFoundException(id)
        );
        if (weeklyPeriod.getBoose() != boose) {
            throw new WeeklyPeriodNotForBooseException(id, booseId);
        }
        return weeklyPeriod;
    }

    @Transactional
    public WeeklyPeriodInfo update(int booseId, int id, WeeklyPeriodUpdateCommand command) {
        WeeklyPeriod weeklyPeriod = getForBooseByIdOrThrow(booseId, id);
        modelMapper.map(command, weeklyPeriod);
        return toDto(weeklyPeriod);
    }

    private WeeklyPeriodInfo toDto(WeeklyPeriod weeklyPeriod) {
        return modelMapper.map(weeklyPeriod, WeeklyPeriodInfo.class);
    }
}
