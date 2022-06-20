package hu.progmasters.servicebooker.service;

import hu.progmasters.servicebooker.domain.PeriodInterval;
import hu.progmasters.servicebooker.domain.TablePeriod;
import hu.progmasters.servicebooker.domain.entity.Booking;
import hu.progmasters.servicebooker.domain.entity.Boose;
import hu.progmasters.servicebooker.domain.entity.SpecificPeriod;
import hu.progmasters.servicebooker.domain.entity.WeeklyPeriod;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public List<TablePeriodInfo> assembleTimeTableForBoose(int booseId, Interval<LocalDateTime> interval) {
        Interval<LocalDateTime> constrainedInterval = dateTimeBoundChecker.constrain(interval);
        Boose boose = booseService.getFromIdOrThrow(booseId);
        return getTimeTableStreamForBoose(boose, constrainedInterval, false)
                .map(period -> modelMapper.map(period, TablePeriodInfo.class))
                .collect(Collectors.toList());
    }

    public Stream<TablePeriod> getTimeTableStreamForBoose(Boose boose, Interval<LocalDateTime> interval, boolean lock) {
        // expand weekly periods
        IntervalSet<PeriodInterval, LocalDateTime> timeTable = expandWeeklyPeriods(boose, interval, lock);

        // modify with specific periods
        List<SpecificPeriod> specificPeriodList =
                specificPeriodService.getAllForBoose(boose, interval, null, lock);

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

        // add booking information
        List<Booking> bookingList = bookingService.getAllForBoose(boose, interval, lock);
        for (Booking booking : bookingList) {
            Interval<LocalDateTime> bookingInterval = interval(booking.getStart(), booking.getEnd());
            PeriodInterval intervalFromTimeTable = timeTable.get(bookingInterval);
            assert intervalFromTimeTable != null;
            intervalFromTimeTable.getPeriod().setBooking(booking);
        }

        return timeTable.stream()
                .map(PeriodInterval::getPeriod);
    }

    private IntervalSet<PeriodInterval, LocalDateTime> expandWeeklyPeriods(Boose boose,
                                                                           Interval<LocalDateTime> queriedInterval,
                                                                           boolean lock) {
        IntervalSet<PeriodInterval, LocalDateTime> result = new IntervalSet<>();

        List<WeeklyPeriod> weeklyPeriods = weeklyPeriodService.getAllForBoose(boose, lock);

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
            result.addWithoutChecks(periodInterval(new TablePeriod(start, end, toExpand.getComment())));
            currentDateTime = end;
            if (!expansionIterator.hasNext()) {
                expansionIterator = weeklyPeriods.listIterator();
            }
        }
        return result;
    }
}
