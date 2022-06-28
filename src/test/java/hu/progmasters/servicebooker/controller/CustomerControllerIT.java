package hu.progmasters.servicebooker.controller;

import hu.progmasters.servicebooker.controller.helper.CustomerHelper;
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
class CustomerControllerIT {

    @Autowired
    CustomerHelper customer;

    @Test
    void save() throws Exception {
        customer.save("John", "john@gmail.com")
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.name", is("John")),
                        jsonPath("$.email", is("john@gmail.com")),
                        jsonPath("$.id", greaterThan(0))
                );
    }

    @Test
    void save_invalidName() throws Exception {
        customer.save("  ", "blank@gmail.com")
                .andExpectAll(
                        status().isBadRequest(),
                        singleValidationError("name", containsString("blank"))
                );
    }

    @Test
    void save_invalidEmail() throws Exception {
        customer.save("john", "johngmail.com")
                .andExpectAll(
                        status().isBadRequest(),
                        singleValidationError("email", containsString("email"))
                );
    }

    @Test
    void findAll_empty() throws Exception {
        customer.findAll()
                .andExpectAll(
                        status().isOk(),
                        content().json("[]")
                );
    }

    @Test
    void findAll() throws Exception {
        customer.save("John", "john@gmail.com");
        customer.save("Teresa", "teresa@gmail.com");
        customer.findAll()
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[*].name", containsInAnyOrder("John", "Teresa"))
                );
    }

    @Test
    void findById() throws Exception {
        int id = customer.saveAndGetId("John", "john@gmail.com");

        customer.findById(id)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(id)),
                        jsonPath("$.name", is("John")),
                        jsonPath("$.email", is("john@gmail.com"))
                );
    }

    @Test
    void findById_notFound() throws Exception {
        int id = 1000;

        customer.findById(id)
                .andExpectAll(
                        status().isNotFound(),
                        singleError(containsString("not found"))
                );
    }

    @Test
    void update() throws Exception {
        int id = customer.saveAndGetId("John", "john@gmail.com");

        customer.update(id, "Jack", "jack@gmail.com")
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(id)),
                        jsonPath("$.name", is("Jack")),
                        jsonPath("$.email", is("jack@gmail.com"))
                );

        customer.findById(id)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.name", is("Jack"))
                );
    }

    @Test
    void testDelete() throws Exception {
        int id = customer.saveAndGetId("John", "john@gmail.com");

        customer.deleteById(id)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(id)),
                        jsonPath("$.name", is("John")),
                        jsonPath("$.email", is("john@gmail.com"))
                );

        customer.findById(id)
                .andExpect(
                        status().isNotFound()
                );
    }
}

