package hu.progmasters.servicebooker.service;

import hu.progmasters.servicebooker.domain.PeriodInterval;
import hu.progmasters.servicebooker.domain.SimplePeriod;
import hu.progmasters.servicebooker.domain.entity.Booking;
import hu.progmasters.servicebooker.domain.entity.Boose;
import hu.progmasters.servicebooker.domain.entity.SpecificPeriod;
import hu.progmasters.servicebooker.domain.entity.WeeklyPeriod;
import hu.progmasters.servicebooker.dto.booking.BookingInfo;
import hu.progmasters.servicebooker.dto.boose.TablePeriodInfo;
import hu.progmasters.servicebooker.util.DayOfWeekTime;
import hu.progmasters.servicebooker.util.interval.Interval;
import hu.progmasters.servicebooker.util.interval.IntervalSet;
import hu.progmasters.servicebooker.validation.DateTimeBoundChecker;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.stream.Collectors;

import static hu.progmasters.servicebooker.domain.PeriodInterval.periodInterval;
import static hu.progmasters.servicebooker.util.interval.Interval.interval;

@Service
public class TimeTableService {

    private final BooseService booseService;
    private final WeeklyPeriodService weeklyPeriodService;
    private final SpecificPeriodService specificPeriodService;
    private final BookingService bookingService;

    private final ModelMapper modelMapper;

    private final DateTimeBoundChecker dateTimeBoundChecker;

    public TimeTableService(BooseService booseService,
                            WeeklyPeriodService weeklyPeriodService,
                            SpecificPeriodService specificPeriodService,
                            BookingService bookingService,
                            ModelMapper modelMapper,
                            DateTimeBoundChecker dateTimeBoundChecker) {
        this.booseService = booseService;
        this.weeklyPeriodService = weeklyPeriodService;
        this.specificPeriodService = specificPeriodService;
        this.bookingService = bookingService;
        this.modelMapper = modelMapper;
        this.dateTimeBoundChecker = dateTimeBoundChecker;
    }

    @Transactional
    public List<TablePeriodInfo> getTimeTableForBoose(int booseId, Interval<LocalDateTime> interval) {
        Interval<LocalDateTime> constrainedInterval = dateTimeBoundChecker.constrain(interval);
        Boose boose = booseService.getFromIdOrThrow(booseId);
        IntervalSet<PeriodInterval, LocalDateTime> timeTable = expandWeeklyPeriods(boose, constrainedInterval);

        List<SpecificPeriod> specificPeriodList =
                specificPeriodService.getAllForBoose(boose, constrainedInterval, null);

        IntervalSet<PeriodInterval, LocalDateTime> specificPeriodsToRemove = new IntervalSet<>();
        IntervalSet<PeriodInterval, LocalDateTime> specificPeriodsToAdd = new IntervalSet<>();

        for (SpecificPeriod specificPeriod : specificPeriodList) {
            PeriodInterval periodInterval = periodInterval(specificPeriod);
            if (specificPeriod.isBookable()) {
                specificPeriodsToAdd.addWithoutChecks(periodInterval);
            }
            specificPeriodsToRemove.addWithoutChecks(periodInterval);
        }

        timeTable.subtract(specificPeriodsToRemove);
        timeTable.addAllWithoutChecks(specificPeriodsToAdd);

        return timeTable.stream()
                .map(periodInterval -> {
                    TablePeriodInfo periodInfo = modelMapper.map(periodInterval.getPeriod(), TablePeriodInfo.class);
                    Interval<LocalDateTime> intervalOfPeriod =
                            interval(periodInterval.getStart(), periodInterval.getEnd());
                    Optional<Booking> optBooking =
                            bookingService.getOptionalByBooseAndDate(boose, intervalOfPeriod);
                    if (optBooking.isPresent()) {
                        periodInfo.setBooking(modelMapper.map(optBooking.get(), BookingInfo.class));
                    }
                    return periodInfo;
                })
                .collect(Collectors.toList());
    }

    private IntervalSet<PeriodInterval, LocalDateTime> expandWeeklyPeriods(Boose boose,
                                                                           Interval<LocalDateTime> queriedInterval) {
        IntervalSet<PeriodInterval, LocalDateTime> result = new IntervalSet<>();

        List<WeeklyPeriod> weeklyPeriods = weeklyPeriodService.getAllForBoose(boose);

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
            WeeklyPeriod toExpand = expansionIterator.next();
            LocalDateTime start = currentDateTime.with(DayOfWeekTime.nextOrSame(toExpand.getStart()));
            LocalDateTime end = currentDateTime.with(DayOfWeekTime.next(toExpand.getEnd()));
            if (!start.isBefore(queriedInterval.getEnd())) {
                break;
            }
            result.addWithoutChecks(periodInterval(SimplePeriod.of(start, end, toExpand.getComment())));
            currentDateTime = end;
            if (!expansionIterator.hasNext()) {
                expansionIterator = weeklyPeriods.listIterator();
            }
        }
        return result;
    }
}
