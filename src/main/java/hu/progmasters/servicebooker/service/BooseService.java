package hu.progmasters.servicebooker.service;

import hu.progmasters.servicebooker.domain.Boose;
import hu.progmasters.servicebooker.domain.SpecificPeriod;
import hu.progmasters.servicebooker.domain.WeeklyPeriod;
import hu.progmasters.servicebooker.dto.*;
import hu.progmasters.servicebooker.exceptionhandling.BooseNotFoundException;
import hu.progmasters.servicebooker.repository.BooseRepository;
import hu.progmasters.servicebooker.repository.SpecificPeriodRepository;
import hu.progmasters.servicebooker.repository.WeeklyPeriodRepository;
import hu.progmasters.servicebooker.util.DayOfWeekTime;
import hu.progmasters.servicebooker.util.interval.Interval;
import hu.progmasters.servicebooker.util.interval.IntervalSet;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import static hu.progmasters.servicebooker.util.interval.Interval.interval;

@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
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
        return weeklyPeriodRepository.findAllOrderedFor(boose).stream()
                .map(weeklyPeriod -> modelMapper.map(weeklyPeriod, WeeklyPeriodInfo.class))
                .collect(Collectors.toList());
    }

    public SpecificPeriodInfo addSpecificPeriodForBoose(SpecificPeriodCreateCommand command) {
        Boose boose = getFromIdOrThrow(command.getBooseId());
        SpecificPeriod toSave = modelMapper.map(command, SpecificPeriod.class);
        toSave.setBoose(boose);

        Interval<LocalDateTime> periodInterval = interval(command.getStart(), command.getEnd());
        IntervalSet<LocalDateTime> expandedWeeklyPeriods = expandWeeklyPeriods(boose, periodInterval);

        SpecificPeriod saved = specificPeriodRepository.save(toSave);
        return modelMapper.map(saved, SpecificPeriodInfo.class);
    }

    public List<SpecificPeriodInfo> findAllSpecificPeriodsForBoose(int booseId, Interval<LocalDateTime> interval,
                                                                   Boolean bookable) {
        Boose boose = getFromIdOrThrow(booseId);
        return specificPeriodRepository.findAllOrderedFor(boose, interval, bookable).stream()
                .map(specificPeriod -> modelMapper.map(specificPeriod, SpecificPeriodInfo.class))
                .collect(Collectors.toList());
    }

    private IntervalSet<LocalDateTime> expandWeeklyPeriods(Boose boose, Interval<LocalDateTime> periodInterval) {
        IntervalSet<LocalDateTime> result = new IntervalSet<>();

        List<WeeklyPeriod> weeklyPeriods = weeklyPeriodRepository.findAllOrderedFor(boose);

        if (weeklyPeriods.isEmpty()) {
            return result;
        }

        LocalDateTime currentDateTime = periodInterval.getStart();
        DayOfWeekTime currentWeekTime = DayOfWeekTime.from(currentDateTime);

        // set firstToExpand
        WeeklyPeriod lastWeeklyPeriod = weeklyPeriods.get(weeklyPeriods.size() - 1);
        boolean lastPeriodCrossesWeekBoundary = lastWeeklyPeriod.crossesWeekBoundary();

        ListIterator<WeeklyPeriod> firstToExpand = null;
        if (lastPeriodCrossesWeekBoundary && currentWeekTime.sameWeekBefore(lastWeeklyPeriod.getEnd())) {
            firstToExpand = weeklyPeriods.listIterator(weeklyPeriods.size() - 1);
        } else {
            ListIterator<WeeklyPeriod> iterator = weeklyPeriods.listIterator();
            while (iterator.hasNext()) {
                WeeklyPeriod firstToExpandCandidate = iterator.next();
                if (currentWeekTime.sameWeekBefore(firstToExpandCandidate.getEnd())) {
                    iterator.previous();
                    firstToExpand = iterator;
                    break;
                }
            }
            if (firstToExpand == null) {
                if (lastPeriodCrossesWeekBoundary) {
                    iterator.previous();
                    firstToExpand = iterator;
                } else {
                    firstToExpand = weeklyPeriods.listIterator();
                }
            }
        }

        ListIterator<WeeklyPeriod> expansionIterator = firstToExpand;
        while (true) {
            WeeklyPeriod toExpand = firstToExpand.next();
            LocalDateTime start = currentDateTime.with(DayOfWeekTime.nextOrSame(toExpand.getStart()));
            LocalDateTime end = currentDateTime.with(DayOfWeekTime.next(toExpand.getEnd()));
            if (start.isBefore(periodInterval.getEnd())) {
                break;
            }
            result.addAssumingNoOverlap(interval(start, end));
            currentDateTime = end;
            if (!expansionIterator.hasNext()) {
                expansionIterator = weeklyPeriods.listIterator();
            }
        }
        return result;
    }

    Boose getFromIdOrThrow(int id) {
        return booseRepository.findById(id).orElseThrow(
                () -> new BooseNotFoundException(id)
        );
    }
}
