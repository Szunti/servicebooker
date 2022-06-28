package hu.progmasters.servicebooker.service;

import hu.progmasters.servicebooker.ServicebookerApplication;
import hu.progmasters.servicebooker.domain.TablePeriod;
import hu.progmasters.servicebooker.domain.entity.*;
import hu.progmasters.servicebooker.dto.boose.TablePeriodBookingInfo;
import hu.progmasters.servicebooker.dto.boose.TablePeriodInfo;
import hu.progmasters.servicebooker.service.examples.BooseExamples;
import hu.progmasters.servicebooker.service.examples.CustomerExamples;
import hu.progmasters.servicebooker.util.DayOfWeekTime;
import hu.progmasters.servicebooker.util.interval.Interval;
import hu.progmasters.servicebooker.validation.DateTimeBoundChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static hu.progmasters.servicebooker.util.interval.Interval.interval;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TimeTableServiceTest {

    @Mock
    BooseService booseService;

    @Mock
    WeeklyPeriodService weeklyPeriodService;

    @Mock
    SpecificPeriodService specificPeriodService;

    @Mock
    BookingService bookingService;

    @Mock
    DateTimeBoundChecker dateTimeBoundChecker;

    ModelMapper modelMapper = new ServicebookerApplication().modelMapper();

    TimeTableService timeTableService;

    Boose boose = BooseExamples.hairdresser();
    Customer customer = CustomerExamples.john();
    List<WeeklyPeriod> weeklyPeriods = new ArrayList<>();
    List<SpecificPeriod> specificPeriods = new ArrayList<>();
    List<Booking> bookings = new ArrayList<>();

    @BeforeEach
    void constructService() {
        timeTableService = new TimeTableService(booseService, weeklyPeriodService, specificPeriodService,
                bookingService, modelMapper, dateTimeBoundChecker);
    }

    @BeforeEach
    void setUpStubbing() {
        when(weeklyPeriodService.getAllForBoose(boose, false)).thenReturn(weeklyPeriods);
        when(specificPeriodService.getAllForBoose(eq(boose), any(), eq(null), eq(false)))
                .thenReturn(specificPeriods);
        when(bookingService.getAllForBoose(eq(boose), any(), eq(false))).thenReturn(bookings);
    }

    @Test
    void getTimeTableStreamForBoose_oneWeekly() {
        addWeeklyPeriod("Mon 08:00", "Mon 12:00", "weekly");
        Interval<LocalDateTime> interval = interval(
                LocalDateTime.parse("2022-06-18T08:00"), // Saturday
                LocalDateTime.parse("2022-06-29T18:00") // Wednesday on second next week
        );

        List<TablePeriod> timeTable = getTimeTable(interval);

        assertThat(timeTable).containsExactly(
                period("2022-06-20T08:00:00", "2022-06-20T12:00:00", "weekly"),
                period("2022-06-27T08:00:00", "2022-06-27T12:00:00", "weekly")
        );
    }

    @Test
    void getTimeTableStreamForBoose_oneWeekly_queryIntervalEndsInsidePeriod() {
        addWeeklyPeriod("Mon 08:00", "Mon 12:00", "weekly");
        Interval<LocalDateTime> interval = interval(
                LocalDateTime.parse("2022-06-20T09:00"), // Monday
                LocalDateTime.parse("2022-06-27T09:00") // Monday on next week
        );

        List<TablePeriod> timeTable = getTimeTable(interval);

        assertThat(timeTable).containsExactly(
                period("2022-06-20T08:00:00", "2022-06-20T12:00:00", "weekly"),
                period("2022-06-27T08:00:00", "2022-06-27T12:00:00", "weekly")
        );
    }

    @Test
    void getTimeTableStreamForBoose_weeklies() {
        addWeeklyPeriod("Thu 08:00", "Thu 12:00", "weekly");
        addWeeklyPeriod("Sat 08:00", "Tue 12:00", "week crossing");
        Interval<LocalDateTime> interval = interval(
                LocalDateTime.parse("2022-06-20T09:00"), // Monday
                LocalDateTime.parse("2022-06-27T09:00") // Monday on next week
        );

        List<TablePeriod> timeTable = getTimeTable(interval);

        assertThat(timeTable).containsExactly(
                period("2022-06-18T08:00:00", "2022-06-21T12:00:00", "week crossing"),
                period("2022-06-23T08:00:00", "2022-06-23T12:00:00", "weekly"),
                period("2022-06-25T08:00:00", "2022-06-28T12:00:00", "week crossing")
        );
    }

    @Test
    void getTimeTableStreamForBoose_specificToAdd() {
        addSpecificPeriod("2022-06-20T08:00:00", "2022-06-20T12:00:00",
                "specific", SpecificPeriodType.ADD_OR_REPLACE);
        Interval<LocalDateTime> interval = interval(
                LocalDateTime.parse("2022-06-20T09:00"), // Monday
                LocalDateTime.parse("2022-06-20T14:00") // same Monday
        );

        List<TablePeriod> timeTable = getTimeTable(interval);

        assertThat(timeTable).hasSize(1)
                .containsExactly(
                        period("2022-06-20T08:00:00", "2022-06-20T12:00:00", "specific"));
    }

    @Test
    void getTimeTableStreamForBoose_weeklyOverriddenBySpecific() {
        addWeeklyPeriod("Mon 08:00", "Mon 12:00", "weekly");
        addSpecificPeriod("2022-06-20T08:00:00", "2022-06-20T12:00:00",
                "specific", SpecificPeriodType.ADD_OR_REPLACE);
        Interval<LocalDateTime> interval = interval(
                LocalDateTime.parse("2022-06-20T09:00"), // Monday
                LocalDateTime.parse("2022-06-28T14:00") // Tuesday on next week
        );

        List<TablePeriod> timeTable = getTimeTable(interval);

        assertThat(timeTable).containsExactly(
                period("2022-06-20T08:00:00", "2022-06-20T12:00:00", "specific"),
                period("2022-06-27T08:00:00", "2022-06-27T12:00:00", "weekly"));
    }

    @Test
    void getTimeTableStreamForBoose_weeklyOverlappingSpecific() {
        addWeeklyPeriod("Mon 08:00", "Mon 12:00", "weekly");
        addSpecificPeriod("2022-06-20T10:00:00", "2022-06-20T13:00:00",
                "specific", SpecificPeriodType.ADD_OR_REPLACE);
        Interval<LocalDateTime> interval = interval(
                LocalDateTime.parse("2022-06-20T09:00"), // Monday
                LocalDateTime.parse("2022-06-28T14:00") // Tuesday on next week
        );

        List<TablePeriod> timeTable = getTimeTable(interval);

        assertThat(timeTable).containsExactly(
                period("2022-06-20T10:00:00", "2022-06-20T13:00:00", "specific"),
                period("2022-06-27T08:00:00", "2022-06-27T12:00:00", "weekly"));
    }

    @Test
    void getTimeTableStreamForBoose_removeSpecific() {
        addWeeklyPeriod("Mon 08:00", "Mon 12:00", "weekly");
        addSpecificPeriod("2022-06-20T10:00:00", "2022-06-20T13:00:00",
                "specific", SpecificPeriodType.REMOVE);
        Interval<LocalDateTime> interval = interval(
                LocalDateTime.parse("2022-06-20T09:00"), // Monday
                LocalDateTime.parse("2022-06-28T14:00") // Tuesday on next week
        );

        List<TablePeriod> timeTable = getTimeTable(interval);

        assertThat(timeTable).containsExactly(
                period("2022-06-27T08:00:00", "2022-06-27T12:00:00", "weekly"));
    }

    @Test
    void getTimeTableStreamForBoose_withBooking() {
        addWeeklyPeriod("Sun 18:00", "Mon 12:00", "weekly");
        addSpecificPeriod("2022-06-20T10:00:00", "2022-06-20T13:00:00",
                "specific", SpecificPeriodType.ADD_OR_REPLACE);
        Booking first = addBooking("2022-06-20T10:00:00", "2022-06-20T13:00:00", "first");
        Interval<LocalDateTime> interval = interval(
                LocalDateTime.parse("2022-06-18T08:00"), // Saturday
                LocalDateTime.parse("2022-06-28T14:00") // Tuesday on second next week
        );

        List<TablePeriod> timeTable = getTimeTable(interval);

        assertThat(timeTable).containsExactly(
                period("2022-06-20T10:00:00", "2022-06-20T13:00:00", "specific", first),
                period("2022-06-26T18:00:00", "2022-06-27T12:00:00", "weekly"));
    }

    @Test
    void assembleTimeTableForBoose() {
        addWeeklyPeriod("Mon 08:00", "Mon 12:00", "weekly");
        addSpecificPeriod("2022-06-20T10:00:00", "2022-06-20T13:00:00",
                "specific", SpecificPeriodType.ADD_OR_REPLACE);
        Booking first = addBooking("2022-06-20T10:00:00", "2022-06-20T13:00:00", "first");
        Interval<LocalDateTime> interval = interval(
                LocalDateTime.parse("2022-06-18T08:00"), // Saturday
                LocalDateTime.parse("2022-06-28T14:00") // Tuesday on second next week
        );
        when(booseService.getFromIdOrThrow(boose.getId())).thenReturn(boose);
        when(dateTimeBoundChecker.constrain(interval)).thenReturn(interval);

        List<TablePeriodInfo> timeTableInfo =
                timeTableService.assembleTimeTableForBoose(boose.getId(), interval, null);

        assertThat(timeTableInfo).containsExactly(
                periodInfo("2022-06-20T10:00", "2022-06-20T13:00", "specific", bookingInfo(first)),
                periodInfo("2022-06-27T08:00", "2022-06-27T12:00", "weekly", null));
    }

    @Test
    void assembleTimeTableForBoose_free() {
        addWeeklyPeriod("Mon 08:00", "Mon 12:00", "weekly");
        addSpecificPeriod("2022-06-20T10:00:00", "2022-06-20T13:00:00",
                "specific", SpecificPeriodType.ADD_OR_REPLACE);
        Booking first = addBooking("2022-06-20T10:00:00", "2022-06-20T13:00:00", "first");
        Interval<LocalDateTime> interval = interval(
                LocalDateTime.parse("2022-06-18T08:00"), // Saturday
                LocalDateTime.parse("2022-06-28T14:00") // Tuesday on second next week
        );
        when(booseService.getFromIdOrThrow(boose.getId())).thenReturn(boose);
        when(dateTimeBoundChecker.constrain(interval)).thenReturn(interval);

        List<TablePeriodInfo> timeTableInfo =
                timeTableService.assembleTimeTableForBoose(boose.getId(), interval, TimeTableFilter.FREE);

        assertThat(timeTableInfo).containsExactly(
                periodInfo("2022-06-27T08:00", "2022-06-27T12:00", "weekly", null));
    }


    List<TablePeriod> getTimeTable(Interval<LocalDateTime> interval) {
        return timeTableService.getTimeTableStreamForBoose(boose, interval, false)
                .collect(Collectors.toList());
    }

    WeeklyPeriod addWeeklyPeriod(String start, String end, String comment) {
        WeeklyPeriod toAdd = new WeeklyPeriod();
        toAdd.setStart(DayOfWeekTime.parse(start));
        toAdd.setEnd(DayOfWeekTime.parse(end));
        toAdd.setComment(comment);
        toAdd.setBoose(boose);
        toAdd.setId(weeklyPeriods.size());
        weeklyPeriods.add(toAdd);
        return toAdd;
    }

    SpecificPeriod addSpecificPeriod(String start, String end, String comment, SpecificPeriodType type) {
        SpecificPeriod toAdd = new SpecificPeriod();
        toAdd.setStart(LocalDateTime.parse(start));
        toAdd.setEnd(LocalDateTime.parse(end));
        toAdd.setComment(comment);
        toAdd.setType(type);
        toAdd.setBoose(boose);
        toAdd.setId(specificPeriods.size());
        specificPeriods.add(toAdd);
        return toAdd;
    }

    Booking addBooking(String start, String end, String comment) {
        Booking toAdd = new Booking();
        toAdd.setStart(LocalDateTime.parse(start));
        toAdd.setEnd(LocalDateTime.parse(end));
        toAdd.setComment(comment);
        toAdd.setBoose(boose);
        toAdd.setCustomer(customer);
        toAdd.setId(bookings.size());
        bookings.add(toAdd);
        return toAdd;
    }

    TablePeriod period(String start, String end, String comment) {
        return period(start, end, comment, null);
    }

    TablePeriod period(String start, String end, String comment, Booking booking) {
        return new TablePeriod(
                LocalDateTime.parse(start),
                LocalDateTime.parse(end),
                comment, booking);
    }

    TablePeriodInfo periodInfo(String start, String end, String comment, TablePeriodBookingInfo bookingInfo) {
        TablePeriodInfo info = new TablePeriodInfo();
        info.setStart(LocalDateTime.parse(start));
        info.setEnd(LocalDateTime.parse(end));
        info.setComment(comment);
        info.setBooking(bookingInfo);
        return info;
    }

    TablePeriodBookingInfo bookingInfo(Booking booking) {
        TablePeriodBookingInfo info = new TablePeriodBookingInfo();
        info.setId(booking.getId());
        info.setComment(booking.getComment());
        info.setCustomer(CustomerExamples.johnInfo());
        return info;
    }
}