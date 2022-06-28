package hu.progmasters.servicebooker.controller;

import hu.progmasters.servicebooker.controller.helper.BooseHelper;
import hu.progmasters.servicebooker.controller.helper.SpecificPeriodHelper;
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
class SpecificPeriodControllerIT {

    @Autowired
    SpecificPeriodHelper specificPeriod;

    @Autowired
    BooseHelper boose;

    int booseId;

    @BeforeEach
    void createBoose() throws Exception {
        booseId = boose.saveAndGetId("Hairdresser Lisa", "Cuts hair.");
    }

    @Test
    void save() throws Exception {
        specificPeriod.save(booseId, "2022-06-20T08:00", "2022-06-20T10:00:00",
                        "a morning", ADD_OR_REPLACE)
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.start", is("2022-06-20T08:00:00")),
                        jsonPath("$.end", is("2022-06-20T10:00:00")),
                        jsonPath("$.comment", is("a morning")),
                        jsonPath("$.type", is(ADD_OR_REPLACE.name())),
                        jsonPath("$.id", greaterThan(0))
                );
    }

    @Test
    void save_allNulls() throws Exception {
        specificPeriod.save(booseId, "{}")
                .andExpectAll(
                        status().isBadRequest(),
                        validationErrors(
                                validationError("start", containsString("null")),
                                validationError("end", containsString("null")),
                                validationError("type", containsString("null")))
                );
    }

    @Test
    void save_startAfterEnd() throws Exception {
        specificPeriod.save(booseId, "2022-06-20T20:00", "2022-06-20T10:00",
                        "a morning", ADD_OR_REPLACE)
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
        specificPeriod.save(booseId, "2022-06-20T10:00", "2022-06-20T10:00",
                        "a morning", ADD_OR_REPLACE)
                .andExpectAll(
                        status().isBadRequest(),
                        validationErrors(
                                validationError("start", containsString("before")),
                                validationError("end", containsString("before"))
                        )
                );
    }

    @Test
    void save_overlapping() throws Exception {
        specificPeriod.save(booseId, "2022-06-20T09:00", "2022-06-20T11:00",
                "a morning", REMOVE);
        specificPeriod.save(booseId, "2022-06-20T08:00", "2022-06-20T10:00",
                        "overlap", ADD_OR_REPLACE)
                .andExpectAll(
                        status().isBadRequest(),
                        singleError(containsString("overlap"))
                );
    }

    @Test
    void save_outsideGlobalBounds() throws Exception {
        specificPeriod.save(booseId, "1922-06-20T09:00", "3022-06-20T11:00",
                        "a morning", ADD_OR_REPLACE)
                .andExpectAll(
                        status().isBadRequest(),
                        validationErrors(
                                validationError("start", containsString("outside")),
                                validationError("end", containsString("outside")))
                );
    }

    @Test
    void findAll_empty() throws Exception {
        specificPeriod.findAll(booseId, "2022-06-20T00:00", "2022-06-28T00:00",
                        ADD_OR_REPLACE)
                .andExpectAll(
                        status().isOk(),
                        content().json("[]")
                );
    }

    @Test
    void findAll() throws Exception {
        specificPeriod.save(booseId, "2022-06-20T10:00", "2022-06-20T12:00",
                "remove", REMOVE);
        specificPeriod.save(booseId, "2022-06-20T08:00", "2022-06-20T10:00",
                "add_or_replace", ADD_OR_REPLACE);

        specificPeriod.findAll(booseId, "2022-06-20T00:00", "2022-06-28T00:00", null)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].start", is("2022-06-20T08:00:00")),
                        jsonPath("$[0].end", is("2022-06-20T10:00:00")),
                        jsonPath("$[0].comment", is("add_or_replace")),
                        jsonPath("$[0].type", is(ADD_OR_REPLACE.name())),
                        jsonPath("$[1].start", is("2022-06-20T10:00:00")),
                        jsonPath("$[1].end", is("2022-06-20T12:00:00")),
                        jsonPath("$[1].comment", is("remove")),
                        jsonPath("$[1].type", is(REMOVE.name()))
                );
    }

    @Test
    void findAll_removeType() throws Exception {
        specificPeriod.save(booseId, "2022-06-20T10:00", "2022-06-20T12:00",
                "remove", REMOVE);
        specificPeriod.save(booseId, "2022-06-20T08:00", "2022-06-20T10:00",
                "add_or_replace", ADD_OR_REPLACE);

        specificPeriod.findAll(booseId, "2022-06-20T00:00", "2022-06-28T00:00", REMOVE)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].start", is("2022-06-20T10:00:00")),
                        jsonPath("$[0].end", is("2022-06-20T12:00:00")),
                        jsonPath("$[0].comment", is("remove")),
                        jsonPath("$[0].type", is(REMOVE.name())),
                        jsonPath("$[1]").doesNotExist()
                );
    }

    @Test
    void findAll_addOrReplaceType() throws Exception {
        specificPeriod.save(booseId, "2022-06-20T10:00", "2022-06-20T12:00",
                "remove", REMOVE);
        specificPeriod.save(booseId, "2022-06-20T08:00", "2022-06-20T10:00",
                "add_or_replace", ADD_OR_REPLACE);

        specificPeriod.findAll(booseId, "2022-06-20T00:00", "2022-06-28T00:00", ADD_OR_REPLACE)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].start", is("2022-06-20T08:00:00")),
                        jsonPath("$[0].end", is("2022-06-20T10:00:00")),
                        jsonPath("$[0].comment", is("add_or_replace")),
                        jsonPath("$[0].type", is(ADD_OR_REPLACE.name())),
                        jsonPath("$[1]").doesNotExist()
                );
    }

    @Test
    void findAll_outsideGlobalBounds() throws Exception {
        specificPeriod.save(booseId, "2022-06-20T10:00", "2022-06-20T12:00",
                "remove", REMOVE);
        specificPeriod.save(booseId, "2022-06-20T08:00", "2022-06-20T10:00",
                "add_or_replace", ADD_OR_REPLACE);

        specificPeriod.findAll(booseId, "3022-06-20T00:00", "3022-06-28T00:00", ADD_OR_REPLACE)
                .andExpectAll(
                        status().isBadRequest(),
                        singleError(containsString("does not have any points inside"))
                );
    }


    @Test
    void findById() throws Exception {
        int id = specificPeriod.saveAndGetId(booseId, "2022-06-28T10:00", "2022-06-28T12:00",
                "saved", ADD_OR_REPLACE);

        specificPeriod.findById(booseId, id)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(id)),
                        jsonPath("$.start", is("2022-06-28T10:00:00")),
                        jsonPath("$.end", is("2022-06-28T12:00:00")),
                        jsonPath("$.type", is(ADD_OR_REPLACE.name())),
                        jsonPath("$.comment", is("saved"))
                );
    }

    @Test
    void findById_notFound() throws Exception {
        int id = 1000;

        specificPeriod.findById(booseId, id)
                .andExpectAll(
                        status().isNotFound(),
                        singleError(containsString("not found"))
                );
    }

    @Test
    void findById_notForBoose() throws Exception {
        int id = specificPeriod.saveAndGetId(booseId, "2022-06-28T10:00", "2022-06-28T12:00",
                "saved", ADD_OR_REPLACE);
        int anotherBooseId = boose.saveAndGetId("Another boose", "The specific period is not for this.");

        specificPeriod.findById(anotherBooseId, id)
                .andExpectAll(
                        status().isNotFound(),
                        singleError(containsString("not for"))
                );
    }

    @Test
    void update() throws Exception {
        int id = specificPeriod.saveAndGetId(booseId, "2022-06-28T10:00", "2022-06-28T12:00",
                "saved", ADD_OR_REPLACE);

        specificPeriod.update(booseId, id, "Updated comment")
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(id)),
                        jsonPath("$.comment", is("Updated comment"))
                );

        specificPeriod.findById(booseId, id)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.comment", is("Updated comment"))
                );
    }
}

