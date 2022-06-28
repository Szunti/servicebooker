package hu.progmasters.servicebooker.controller;

import hu.progmasters.servicebooker.controller.helper.*;
import hu.progmasters.servicebooker.domain.entity.SpecificPeriodType;
import hu.progmasters.servicebooker.service.TimeTableFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static hu.progmasters.servicebooker.controller.matcher.ErrorResultMatchers.singleError;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class TimeTableIT {

    @Autowired
    BooseHelper boose;

    @Autowired
    CustomerHelper customer;

    @Autowired
    BookingBooseHelper booking;

    @Autowired
    WeeklyPeriodHelper weeklyPeriod;

    @Autowired
    SpecificPeriodHelper specificPeriod;

    int booseId;

    @BeforeEach
    void createBoose() throws Exception {
        booseId = boose.saveAndGetId("Doctor Bob", "Your doctor.");
    }

    @Test
    void getTimeTable_empty() throws Exception {
        boose.getTimeTable(booseId, "2022-06-20T08:00", "2022-06-28T18:00", null)
                .andExpectAll(
                        status().isOk(),
                        content().json("[]")
                );
    }

    @Test
    void getTimeTable_outsideGlobalBounds() throws Exception {
        boose.getTimeTable(booseId, "3022-06-20T00:00", "3022-06-28T00:00", null)
                .andExpectAll(
                        status().isBadRequest(),
                        singleError(containsString("does not have any points inside"))
                );
    }

    @Nested
    class TimeTableFilled {

        int johnId;
        int johnBookingId;
        int jackId;
        int jackBookingId;

        @BeforeEach
        void createPeriodsAndBookings() throws Exception {
            weeklyPeriod.save(booseId, "Mon 08:00", "Mon 10:00", "weekly");
            specificPeriod.save(booseId, "2022-06-21T12:00", "2022-06-21T14:00",
                    "specific", SpecificPeriodType.ADD_OR_REPLACE);
            johnId = customer.saveAndGetId("John", "john@gmail.com");
            johnBookingId = booking.saveAndGetId(booseId, johnId,
                    "2022-06-27T08:00", "2022-06-27T10:00", "booking by John");
            jackId = customer.saveAndGetId("Jack", "jack@gmail.com");
            jackBookingId = booking.saveWithPeriodAndGetId(booseId, jackId,
                    "2022-06-21T08:00", "2022-06-21T10:00", "booking by Jack");
        }

        @Test
        void getTimeTable() throws Exception {
            boose.getTimeTable(booseId, "2022-06-20T00:00", "2022-07-05T00:00", null)
                    .andExpectAll(
                            status().isOk(),

                            jsonPath("$[0].start", is("2022-06-20T08:00:00")),
                            jsonPath("$[0].end", is("2022-06-20T10:00:00")),
                            jsonPath("$[0].comment", is("weekly")),
                            jsonPath("$[0].booking", is(nullValue())),

                            jsonPath("$[1].start", is("2022-06-21T08:00:00")),
                            jsonPath("$[1].end", is("2022-06-21T10:00:00")),
                            jsonPath("$[1].booking.id", is(jackBookingId)),
                            jsonPath("$[1].booking.comment", is("booking by Jack")),
                            jsonPath("$[1].booking.customer.id", is(jackId)),
                            jsonPath("$[1].booking.customer.name", is("Jack")),
                            jsonPath("$[1].booking.customer.email", is("jack@gmail.com")),

                            jsonPath("$[2].start", is("2022-06-21T12:00:00")),
                            jsonPath("$[2].end", is("2022-06-21T14:00:00")),
                            jsonPath("$[2].comment", is("specific")),
                            jsonPath("$[2].booking", is(nullValue())),

                            jsonPath("$[3].start", is("2022-06-27T08:00:00")),
                            jsonPath("$[3].end", is("2022-06-27T10:00:00")),
                            jsonPath("$[3].comment", is("weekly")),
                            jsonPath("$[3].booking.id", is(johnBookingId)),
                            jsonPath("$[3].booking.comment", is("booking by John")),
                            jsonPath("$[3].booking.customer.id", is(johnId)),
                            jsonPath("$[3].booking.customer.name", is("John")),
                            jsonPath("$[3].booking.customer.email", is("john@gmail.com")),

                            jsonPath("$[4].start", is("2022-07-04T08:00:00")),
                            jsonPath("$[4].end", is("2022-07-04T10:00:00")),
                            jsonPath("$[4].comment", is("weekly")),
                            jsonPath("$[4].booking", is(nullValue()))
                    );
        }

        @Test
        void getTimeTable_free() throws Exception {
            boose.getTimeTable(booseId, "2022-06-20T00:00", "2022-07-05T00:00", TimeTableFilter.FREE)
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$[0].start", is("2022-06-20T08:00:00")),
                            jsonPath("$[1].start", is("2022-06-21T12:00:00")),
                            jsonPath("$[2].start", is("2022-07-04T08:00:00"))
                    );
        }

        @Test
        void getTimeTable_booked() throws Exception {
            boose.getTimeTable(booseId, "2022-06-20T00:00", "2022-07-05T00:00", TimeTableFilter.BOOKED)
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$[0].start", is("2022-06-21T08:00:00")),
                            jsonPath("$[1].start", is("2022-06-27T08:00:00"))
                    );
        }
    }
}
