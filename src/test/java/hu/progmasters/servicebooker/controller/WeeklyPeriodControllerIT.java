package hu.progmasters.servicebooker.controller;

import hu.progmasters.servicebooker.controller.helper.BooseHelper;
import hu.progmasters.servicebooker.controller.helper.WeeklyPeriodHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static hu.progmasters.servicebooker.controller.matcher.ErrorResultMatchers.singleError;
import static hu.progmasters.servicebooker.controller.matcher.ErrorResultMatchers.validationErrors;
import static hu.progmasters.servicebooker.controller.matcher.ValidationErrorMatcher.validationError;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class WeeklyPeriodControllerIT {

    @Autowired
    WeeklyPeriodHelper weeklyPeriod;

    @Autowired
    BooseHelper boose;

    int booseId;

    @BeforeEach
    void createBoose() throws Exception {
        booseId = boose.saveAndGetId("Hairdresser Lisa", "Cuts hair.");
    }

    @Test
    void save() throws Exception {
        weeklyPeriod.save(booseId, "Mon 08:00", "Mon 10:00:00", "Monday morning")
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.start", is("Mon 08:00:00")),
                        jsonPath("$.end", is("Mon 10:00:00")),
                        jsonPath("$.comment", is("Monday morning")),
                        jsonPath("$.id", greaterThan(0))
                );
    }

    @Test
    void save_allNulls() throws Exception {
        weeklyPeriod.save(booseId, "{}")
                .andExpectAll(
                        status().isBadRequest(),
                        validationErrors(
                                validationError("start", containsString("null")),
                                validationError("end", containsString("null")))
                );
    }

    @Test
    void save_overlapping() throws Exception {
        weeklyPeriod.save(booseId, "Mon 09:00", "Mon 11:00:00", "Monday morning");
        weeklyPeriod.save(booseId, "Mon 08:00", "Mon 10:00:00", "overlap")
                .andExpectAll(
                        status().isBadRequest(),
                        singleError(containsString("overlap"))
                );
    }

    @Test
    void findAll_empty() throws Exception {
        weeklyPeriod.findAll(booseId)
                .andExpectAll(
                        status().isOk(),
                        content().json("[]")
                );
    }

    @Test
    void findAll() throws Exception {
        weeklyPeriod.save(booseId, "Sun 18:01", "Mon 10:10", "Sunday to Monday");
        weeklyPeriod.save(booseId, "Wed 10:23:56", "Fri 06:12", "Wednesday to Friday");
        weeklyPeriod.findAll(booseId)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].start", is("Wed 10:23:56")),
                        jsonPath("$[0].end", is("Fri 06:12:00")),
                        jsonPath("$[0].comment", is("Wednesday to Friday")),
                        jsonPath("$[1].start", is("Sun 18:01:00")),
                        jsonPath("$[1].end", is("Mon 10:10:00")),
                        jsonPath("$[1].comment", is("Sunday to Monday"))
                );
    }

    @Test
    void findById() throws Exception {
        int id = weeklyPeriod.saveAndGetId(booseId, "Thu 08:00", "Fri 10:00", "Thursday to Friday");

        weeklyPeriod.findById(booseId, id)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(id)),
                        jsonPath("$.start", is("Thu 08:00:00")),
                        jsonPath("$.end", is("Fri 10:00:00")),
                        jsonPath("$.comment", is("Thursday to Friday"))
                );
    }

    @Test
    void findById_notFound() throws Exception {
        int id = 1000;

        weeklyPeriod.findById(booseId, id)
                .andExpectAll(
                        status().isNotFound(),
                        singleError(containsString("not found"))
                );
    }

    @Test
    void findById_notForBoose() throws Exception {
        int id = weeklyPeriod.saveAndGetId(booseId, "Thu 08:00", "Fri 10:00", "Thursday to Friday");
        int anotherBooseId = boose.saveAndGetId("Another boose", "The weekly period is not for this.");

        weeklyPeriod.findById(anotherBooseId, id)
                .andExpectAll(
                        status().isNotFound(),
                        singleError(containsString("not for"))
                );
    }

    @Test
    void update() throws Exception {
        int id = weeklyPeriod.saveAndGetId(booseId, "Thu 08:00", "Fri 10:00", "Thursday to Friday");

        weeklyPeriod.update(booseId, id, "Updated comment")
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(id)),
                        jsonPath("$.comment", is("Updated comment"))
                );

        weeklyPeriod.findById(booseId, id)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.comment", is("Updated comment"))
                );
    }
}

