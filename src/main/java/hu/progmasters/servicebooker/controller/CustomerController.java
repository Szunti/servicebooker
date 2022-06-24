package hu.progmasters.servicebooker.controller;

import hu.progmasters.servicebooker.dto.customer.CustomerCreateCommand;
import hu.progmasters.servicebooker.dto.customer.CustomerInfo;
import hu.progmasters.servicebooker.dto.customer.CustomerUpdateCommand;
import hu.progmasters.servicebooker.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static hu.progmasters.servicebooker.controller.LogMessages.*;

@Tag(name = "Customers")
@Slf4j
@RestController
@RequestMapping(CustomerController.BASE_URL)
public class CustomerController {

    public static final String BASE_URL = "/api/customers";
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Operation(summary = "Save a new customer")
    @ApiResponse(responseCode = "201", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = CustomerInfo.class)))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerInfo save(@Valid @RequestBody CustomerCreateCommand command) {
        log.info(LOG_SAVE, BASE_URL, command);
        CustomerInfo response = customerService.save(command);
        log.info(LOG_RESPONSE, HttpStatus.CREATED, response);
        return response;
    }

    @Operation(summary = "List customers")
    @ApiResponse(responseCode = "200", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            array = @ArraySchema(schema = @Schema(implementation = CustomerInfo.class)))) @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CustomerInfo> findAll() {
        log.info(LOG_FINDALL, BASE_URL);
        List<CustomerInfo> response = customerService.findAll();
        log.info(LOG_RESPONSE, HttpStatus.OK, response);
        return response;
    }

    @Operation(summary = "Find customer by id")
    @Parameter(name = "id", example = "2")
    @ApiResponse(responseCode = "200", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = CustomerInfo.class))) @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerInfo findById(@PathVariable("id") int id) {
        log.info(LOG_FINDBYID, BASE_URL, id);
        CustomerInfo response = customerService.findById(id);
        log.info(LOG_RESPONSE, HttpStatus.OK, response);
        return response;
    }

    @Operation(summary = "Modify customer name and email")
    @Parameter(name = "id", example = "2")
    @ApiResponse(responseCode = "200", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = CustomerInfo.class)))
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerInfo update(@PathVariable("id") int id, @Valid @RequestBody CustomerUpdateCommand command) {
        log.info(LOG_UPDATE, BASE_URL, id, command);
        CustomerInfo response = customerService.update(id, command);
        log.info(LOG_RESPONSE, HttpStatus.OK, response);
        return response;
    }

    @Operation(summary = "Delete customer")
    @Parameter(name = "id", example = "2")
    @ApiResponse(responseCode = "200", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = CustomerInfo.class)))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerInfo delete(@PathVariable("id") int id) {
        log.info(LOG_DELETE, BASE_URL, id);
        CustomerInfo response = customerService.delete(id);
        log.info(LOG_RESPONSE, HttpStatus.OK, response);
        return response;
    }
}
