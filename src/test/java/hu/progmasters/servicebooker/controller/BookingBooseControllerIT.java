package hu.progmasters.servicebooker.controller;

import hu.progmasters.servicebooker.controller.helper.BookingBooseHelper;
import hu.progmasters.servicebooker.controller.helper.BooseHelper;
import hu.progmasters.servicebooker.controller.helper.CustomerHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static hu.progmasters.servicebooker.controller.matcher.ErrorResultMatchers.singleError;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class BookingBooseControllerIT {

    @Autowired
    BookingBooseHelper booking;

    @Autowired
    BooseHelper boose;

    @Autowired
    CustomerHelper customer;

    int booseId;
    int customerId;

    @BeforeEach
    void createBoose() throws Exception {
        booseId = boose.saveAndGetId("Hairdresser Lisa", "Cuts hair.");
    }

    @BeforeEach
    void createCustomer() throws Exception {
        customerId = customer.saveAndGetId("John", "john@gmail.com");
    }

    @Test
    void findAll_empty() throws Exception {
        booking.findAll(booseId, "2022-06-20T00:00", "2022-06-28T00:00")
                .andExpectAll(
                        status().isOk(),
                        content().json("[]")
                );
    }

    @Test
    void findAll() throws Exception {
        int johnId = customerId;
        int jackId = customer.saveAndGetId("Jack", "jack@gmail.com");

        booking.saveWithPeriod(booseId, johnId, "2022-06-20T10:00", "2022-06-20T12:00",
                "second in time");
        booking.saveWithPeriod(booseId, jackId, "2022-06-20T08:00", "2022-06-20T10:00",
                "first in time");

        booking.findAll(booseId, "2022-06-20T00:00", "2022-06-28T00:00")
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].start", is("2022-06-20T08:00:00")),
                        jsonPath("$[0].end", is("2022-06-20T10:00:00")),
                        jsonPath("$[0].comment", is("first in time")),
                        jsonPath("$[0].boose.name", is("Hairdresser Lisa")),
                        jsonPath("$[0].customer.name", is("Jack")),
                        jsonPath("$[1].start", is("2022-06-20T10:00:00")),
                        jsonPath("$[1].end", is("2022-06-20T12:00:00")),
                        jsonPath("$[1].comment", is("second in time")),
                        jsonPath("$[1].boose.name", is("Hairdresser Lisa")),
                        jsonPath("$[1].customer.name", is("John"))
                );
    }

    @Test
    void findAll_outsideGlobalBounds() throws Exception {
        booking.findAll(booseId, "3022-06-20T00:00", "3022-06-28T00:00")
                .andExpectAll(
                        status().isBadRequest(),
                        singleError(containsString("does not have any points inside"))
                );
    }

    @Test
    void findById() throws Exception {
        int id = booking.saveWithPeriodAndGetId(booseId, customerId, "2022-06-28T10:00", "2022-06-28T12:00",
                "saved");

        booking.findById(booseId, id)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(id)),
                        jsonPath("$.start", is("2022-06-28T10:00:00")),
                        jsonPath("$.end", is("2022-06-28T12:00:00")),
                        jsonPath("$.comment", is("saved")),
                        jsonPath("$.boose.name", is("Hairdresser Lisa")),
                        jsonPath("$.customer.name", is("John"))
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
    void findById_notForBoose() throws Exception {
        int id = booking.saveWithPeriodAndGetId(booseId, customerId, "2022-06-28T10:00", "2022-06-28T12:00",
                "saved");
        int anotherBooseId = boose.saveAndGetId("Another boose", "The booking is not for this.");

        booking.findById(anotherBooseId, id)
                .andExpectAll(
                        status().isNotFound(),
                        singleError(containsString("not for"))
                );
    }

    @Test
    void update() throws Exception {
        int id = booking.saveWithPeriodAndGetId(booseId, customerId, "2022-06-28T10:00", "2022-06-28T12:00",
                "saved");

        booking.update(booseId, id, "Updated comment")
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(id)),
                        jsonPath("$.comment", is("Updated comment"))
                );

        booking.findById(booseId, id)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.comment", is("Updated comment"))
                );
    }

    @Test
    void delete() throws Exception {
        int id = booking.saveWithPeriodAndGetId(booseId, customerId, "2022-06-28T10:00", "2022-06-28T12:00",
                "saved");

        booking.deleteById(booseId, id);

        booking.findById(booseId, id)
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    void deletedWithCustomer() throws Exception {
        int id = booking.saveWithPeriodAndGetId(booseId, customerId, "2022-06-28T10:00", "2022-06-28T12:00",
                "saved");

        customer.deleteById(customerId);

        booking.findById(booseId, id)
                .andExpect(
                        status().isNotFound()
                );

        booking.findAll(booseId, "2022-06-28T08:00", "2022-06-28T18:00")
                .andExpectAll(
                        status().isOk(),
                        content().json("[]")
                );
    }
}

