package hu.progmasters.servicebooker.service;

import hu.progmasters.servicebooker.domain.Boose;
import hu.progmasters.servicebooker.domain.SpecificPeriod;
import hu.progmasters.servicebooker.domain.WeeklyPeriod;
import hu.progmasters.servicebooker.dto.*;
import hu.progmasters.servicebooker.exceptionhandling.NoSuchBooseException;
import hu.progmasters.servicebooker.exceptionhandling.OverlappingSpecificPeriodException;
import hu.progmasters.servicebooker.exceptionhandling.OverlappingWeeklyPeriodException;
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

import static hu.progmasters.servicebooker.service.PeriodInterval.periodInterval;
import static hu.progmasters.servicebooker.util.interval.SimpleInterval.interval;

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

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public WeeklyPeriodInfo addWeeklyPeriodForBoose(WeeklyPeriodCreateCommand command) {
        Boose boose = getFromIdOrThrow(command.getBooseId());
        WeeklyPeriod toSave = modelMapper.map(command, WeeklyPeriod.class);
        toSave.setBoose(boose);

        booseRepository.lockForUpdate(boose);
        // this check needs to read already commited, that is why the isolation level is set
        if (!weeklyPeriodRepository.findOverlappingPeriods(boose, toSave).isEmpty()) {
            throw new OverlappingWeeklyPeriodException();
        }
        WeeklyPeriod saved = weeklyPeriodRepository.save(toSave);
        return modelMapper.map(saved, WeeklyPeriodInfo.class);
    }

    public List<WeeklyPeriodInfo> findAllWeeklyPeriodsForBoose(int booseId) {
        Boose boose = getFromIdOrThrow(booseId);
        return weeklyPeriodRepository.findAllOrderedFor(boose).stream()
                .map(weeklyPeriod -> modelMapper.map(weeklyPeriod, WeeklyPeriodInfo.class))
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public SpecificPeriodInfo addSpecificPeriodForBoose(SpecificPeriodCreateCommand command) {
        Boose boose = getFromIdOrThrow(command.getBooseId());
        SpecificPeriod toSave = modelMapper.map(command, SpecificPeriod.class);
        toSave.setBoose(boose);

        booseRepository.lockForUpdate(boose);
        // this check needs to read already commited thats why the isolation level is set
        if (!specificPeriodRepository.findOverlappingPeriods(boose, toSave).isEmpty()) {
            throw new OverlappingSpecificPeriodException();
        }
        Interval<LocalDateTime> periodInterval = interval(command.getStart(), command.getEnd());
        IntervalSet<LocalDateTime> expandedWeeklyPeriods = expandWeeklyPeriods(boose, periodInterval);
        // TODO return if weekly is replaced, partially covered or neither
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


    public List<FreePeriodInfo> getFreePeriodsForBoose(int booseId, Interval<LocalDateTime> interval) {
        Boose boose = getFromIdOrThrow(booseId);
        IntervalSet<LocalDateTime> weeklyPeriods = expandWeeklyPeriods(boose, interval);

        List<SpecificPeriod> specificPeriodList =
                specificPeriodRepository.findAllOrderedFor(boose, interval, null);

        IntervalSet<LocalDateTime> specificPeriodsToRemove = new IntervalSet<>();
        IntervalSet<LocalDateTime> specificPeriodsToAdd = new IntervalSet<>();

        for (SpecificPeriod specificPeriod : specificPeriodList) {
            PeriodInterval periodInterval = periodInterval(specificPeriod);
            if (specificPeriod.isBookable()) {
                specificPeriodsToAdd.addAssumingNoOverlap(periodInterval);
            }
            specificPeriodsToRemove.addAssumingNoOverlap(periodInterval);
        }

        weeklyPeriods.subtract(specificPeriodsToRemove);
        weeklyPeriods.addAssumingNoOverlap(specificPeriodsToAdd);

        return weeklyPeriods.stream()
                .map(periodInterval ->
                        modelMapper.map(((PeriodInterval)periodInterval).getPeriod(), FreePeriodInfo.class))
                .collect(Collectors.toList());
    }

    private IntervalSet<LocalDateTime> expandWeeklyPeriods(Boose boose, Interval<LocalDateTime> queriedInterval) {
        IntervalSet<LocalDateTime> result = new IntervalSet<>();

        List<WeeklyPeriod> weeklyPeriods = weeklyPeriodRepository.findAllOrderedFor(boose);

        if (weeklyPeriods.isEmpty()) {
            return result;
        }

        LocalDateTime currentDateTime = queriedInterval.getStart();
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
            if (start.isBefore(queriedInterval.getEnd())) {
                break;
            }
            result.addAssumingNoOverlap(periodInterval(SimplePeriod.of(start, end, toExpand.getComment())));
            currentDateTime = end;
            if (!expansionIterator.hasNext()) {
                expansionIterator = weeklyPeriods.listIterator();
            }
        }
        return result;
    }

    Boose getFromIdOrThrow(int id) {
        return booseRepository.findById(id).orElseThrow(
                () -> new NoSuchBooseException(id)
        );
    }
}
