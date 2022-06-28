package hu.progmasters.servicebooker.controller;

import hu.progmasters.servicebooker.controller.helper.BooseHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static hu.progmasters.servicebooker.controller.matcher.ErrorResultMatchers.singleError;
import static hu.progmasters.servicebooker.controller.matcher.ErrorResultMatchers.singleValidationError;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class BooseControllerIT {

    @Autowired
    BooseHelper boose;

    @Test
    void save() throws Exception {
        boose.save("Doctor Bob", "Makes you healthy.")
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.name", is("Doctor Bob")),
                        jsonPath("$.description", is("Makes you healthy.")),
                        jsonPath("$.id", greaterThan(0))
                );
    }

    @Test
    void save_invalidName() throws Exception {
        boose.save("  ", "Makes you healthy.")
                .andExpectAll(
                        status().isBadRequest(),
                        singleValidationError("name", containsString("blank"))
                );
    }

    @Test
    void findAll_empty() throws Exception {
        boose.findAll()
                .andExpectAll(
                        status().isOk(),
                        content().json("[]")
                );
    }

    @Test
    void findAll() throws Exception {
        boose.save("Doctor Bob", "Your doctor.");
        boose.save("Electrician Emma", "Working with wires.");
        boose.findAll()
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[*].name", containsInAnyOrder("Doctor Bob", "Electrician Emma"))
                );
    }

    @Test
    void findById() throws Exception {
        int id = boose.saveAndGetId("Doctor Bob", "Your doctor.");

        boose.findById(id)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(id)),
                        jsonPath("$.name", is("Doctor Bob")),
                        jsonPath("$.description", is("Your doctor."))
                );
    }

    @Test
    void findById_notFound() throws Exception {
        int id = 1000;

        boose.findById(id)
                .andExpectAll(
                        status().isNotFound(),
                        singleError(containsString("not found"))
                );
    }

    @Test
    void update() throws Exception {
        int id = boose.saveAndGetId("Doctor Bob", "Your doctor.");

        boose.update(id, "Bob", "Not a doctor anymore.")
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(id)),
                        jsonPath("$.name", is("Bob")),
                        jsonPath("$.description", is("Not a doctor anymore."))
                );

        boose.findById(id)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.name", is("Bob"))
                );
    }

    @Test
    void testDelete() throws Exception {
        int id = boose.saveAndGetId("Doctor Bob", "Your doctor.");

        boose.deleteById(id)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(id)),
                        jsonPath("$.name", is("Doctor Bob")),
                        jsonPath("$.description", is("Your doctor."))
                );

        boose.findById(id)
                .andExpect(
                        status().isNotFound()
                );
    }
}

