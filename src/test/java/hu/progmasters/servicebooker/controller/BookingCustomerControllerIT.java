package hu.progmasters.servicebooker.controller;

import hu.progmasters.servicebooker.controller.helper.BookingCustomerHelper;
import hu.progmasters.servicebooker.controller.helper.BooseHelper;
import hu.progmasters.servicebooker.controller.helper.CustomerHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static hu.progmasters.servicebooker.controller.matcher.ErrorResultMatchers.singleError;
import static hu.progmasters.servicebooker.controller.matcher.ErrorResultMatchers.validationErrors;
import static hu.progmasters.servicebooker.controller.matcher.ValidationErrorMatcher.validationError;
import static hu.progmasters.servicebooker.domain.entity.SpecificPeriodType.ADD_OR_REPLACE;
import static hu.progmasters.servicebooker.domain.entity.SpecificPeriodType.REMOVE;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class BookingCustomerControllerIT {

    @Autowired
    BookingCustomerHelper booking;

    @Autowired
    BooseHelper boose;

    @Autowired
    CustomerHelper customer;

    int customerId;
    int booseId;

    @BeforeEach
    void createCustomer() throws Exception {
        customerId = customer.saveAndGetId("John", "john@gmail.com");
    }

    @BeforeEach
    void createBoose() throws Exception {
        booseId = boose.saveAndGetId("Hairdresser Lisa", "Cuts hair.");
    }

    @Test
    void save() throws Exception {
        booking.saveWithPeriod(customerId, booseId, "2022-06-20T08:00", "2022-06-20T10:00:00",
                        "a booking")
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.start", is("2022-06-20T08:00:00")),
                        jsonPath("$.end", is("2022-06-20T10:00:00")),
                        jsonPath("$.comment", is("a booking")),
                        jsonPath("$.customer.name", is("John")),
                        jsonPath("$.boose.name", is("Hairdresser Lisa")),
                        jsonPath("$.id", greaterThan(0))
                );
    }

    @Test
    void save_allNulls() throws Exception {
        booking.save(customerId, "{}")
                .andExpectAll(
                        status().isBadRequest(),
                        validationErrors(
                                validationError("start", containsString("null")),
                                validationError("end", containsString("null")),
                                validationError("booseId", containsString("null")))
                );
    }

    @Test
    void save_startAfterEnd() throws Exception {
        booking.save(customerId, booseId, "2022-06-20T20:00", "2022-06-20T10:00",
                        "a booking")
                .andExpectAll(
                        status().isBadRequest(),
                        validationErrors(
                                validationError("start", containsString("before")),
                                validationError("end", containsString("before"))
                        )
                );
    }

    @Test
    void save_startAndEndSame() throws Exception {
        booking.save(customerId, booseId, "2022-06-20T10:00", "2022-06-20T10:00",
                        "a booking")
                .andExpectAll(
                        status().isBadRequest(),
                        validationErrors(
                                validationError("start", containsString("before")),
                                validationError("end", containsString("before"))
                        )
                );
    }

    @Test
    void save_noPeriod() throws Exception {
        booking.save(customerId, booseId, "2022-06-20T08:00", "2022-06-20T10:00",
                "a booking")
                .andExpectAll(
                        status().isBadRequest(),
                        singleError(containsString("no bookable period"))
                );
    }

    @Test
    void save_noSuchBoose() throws Exception {
        booking.saveWithPeriod(customerId, 1000, "2022-06-20T08:00", "2022-06-20T10:00",
                "a booking for non-existent boose")
                .andExpectAll(
                        status().isBadRequest(),
                        singleError(containsString("not found"))
                );
    }

    @Test
    void save_alreadyBooked() throws Exception {
        int johnId = customerId;
        int jackId = customer.saveAndGetId("Jack", "jack@gmail.com");
        booking.saveWithPeriod(jackId, booseId, "2022-06-20T08:00", "2022-06-20T10:00",
                "a booking");
        booking.saveWithPeriod(johnId, booseId, "2022-06-20T08:00", "2022-06-20T10:00",
                        "attempted booking")
                .andExpectAll(
                        status().isBadRequest(),
                        singleError(containsString("already booked"))
                );
    }

    @Test
    void save_outsideGlobalBounds() throws Exception {
        booking.save(customerId, booseId, "1922-06-20T09:00", "3022-06-20T11:00",
                        "a booking")
                .andExpectAll(
                        status().isBadRequest(),
                        validationErrors(
                                validationError("start", containsString("outside")),
                                validationError("end", containsString("outside")))
                );
    }

    @Test
    void findAll_empty() throws Exception {
        booking.findAll(customerId, "2022-06-20T00:00", "2022-06-28T00:00")
                .andExpectAll(
                        status().isOk(),
                        content().json("[]")
                );
    }

    @Test
    void findAll() throws Exception {
        int hairdresserId = booseId;
        int doctorId = boose.saveAndGetId("Doctor Bob", "Your doctor.");

        booking.saveWithPeriod(customerId, hairdresserId, "2022-06-20T10:00", "2022-06-20T12:00",
                "second in time");
        booking.saveWithPeriod(customerId, doctorId, "2022-06-20T08:00", "2022-06-20T10:00",
                "first in time");

        booking.findAll(customerId, "2022-06-20T00:00", "2022-06-28T00:00")
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].start", is("2022-06-20T08:00:00")),
                        jsonPath("$[0].end", is("2022-06-20T10:00:00")),
                        jsonPath("$[0].comment", is("first in time")),
                        jsonPath("$[0].customer.name", is("John")),
                        jsonPath("$[0].boose.name", is("Doctor Bob")),
                        jsonPath("$[1].start", is("2022-06-20T10:00:00")),
                        jsonPath("$[1].end", is("2022-06-20T12:00:00")),
                        jsonPath("$[1].comment", is("second in time")),
                        jsonPath("$[1].customer.name", is("John")),
                        jsonPath("$[1].boose.name", is("Hairdresser Lisa"))
                );
    }

    @Test
    void findAll_outsideGlobalBounds() throws Exception {
        booking.findAll(customerId, "3022-06-20T00:00", "3022-06-28T00:00")
                .andExpectAll(
                        status().isBadRequest(),
                        singleError(containsString("does not have any points inside"))
                );
    }

    @Test
    void findById() throws Exception {
        int id = booking.saveWithPeriodAndGetId(customerId, booseId, "2022-06-28T10:00", "2022-06-28T12:00",
                "saved");

        booking.findById(customerId, id)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(id)),
                        jsonPath("$.start", is("2022-06-28T10:00:00")),
                        jsonPath("$.end", is("2022-06-28T12:00:00")),
                        jsonPath("$.comment", is("saved")),
                        jsonPath("$.customer.name", is("John")),
                        jsonPath("$.boose.name", is("Hairdresser Lisa"))
                );
    }

    @Test
    void findById_notFound() throws Exception {
        int id = 1000;

        booking.findById(booseId, id)
                .andExpectAll(
                        status().isNotFound(),
                        singleError(containsString("not found"))
                );
    }

    @Test
    void findById_notByCustomer() throws Exception {
        int id = booking.saveWithPeriodAndGetId(customerId, booseId, "2022-06-28T10:00", "2022-06-28T12:00",
                "saved");
        int anotherCustomerId = customer.saveAndGetId("Another customer", "another@gmail.com");

        booking.findById(anotherCustomerId, id)
                .andExpectAll(
                        status().isNotFound(),
                        singleError(containsString("not by"))
                );
    }

    @Test
    void update() throws Exception {
        int id = booking.saveWithPeriodAndGetId(customerId, booseId, "2022-06-28T10:00", "2022-06-28T12:00",
                "saved");

        booking.update(customerId, id, "Updated comment")
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(id)),
                        jsonPath("$.comment", is("Updated comment"))
                );

        booking.findById(customerId, id)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.comment", is("Updated comment"))
                );
    }

    @Test
    void delete() throws Exception {
        int id = booking.saveWithPeriodAndGetId(customerId, booseId,"2022-06-28T10:00", "2022-06-28T12:00",
                "saved");

        booking.deleteById(customerId, id);

        booking.findById(customerId, id)
                .andExpect(
                        status().isNotFound()
                );
    }
}

