package hu.progmasters.servicebooker.controller;

import hu.progmasters.servicebooker.dto.booking.BookingCreateCommand;
import hu.progmasters.servicebooker.dto.booking.BookingInfo;
import hu.progmasters.servicebooker.dto.booking.BookingUpdateCommand;
import hu.progmasters.servicebooker.service.BookerService;
import hu.progmasters.servicebooker.service.BookingService;
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
import java.time.LocalDateTime;
import java.util.List;

import static hu.progmasters.servicebooker.controller.LogMessages.*;
import static hu.progmasters.servicebooker.util.interval.Interval.interval;

@Tag(name = "Bookings")
@Slf4j
@RestController
@RequestMapping(BookingCustomerController.BASE_URL + "/{customerId}" + BookingCustomerController.SUB_URL)
public class BookingCustomerController {

    public static final String BASE_URL = "/api/customers";
    public static final String SUB_URL = "/bookings";

    private final BookerService bookerService;
    private final BookingService bookingService;

    public BookingCustomerController(BookerService bookerService, BookingService bookingService) {
        this.bookerService = bookerService;
        this.bookingService = bookingService;
    }

    @Operation(summary = "Save booking by customer")
    @Parameter(name = "customerId", example = "2")
    @ApiResponse(responseCode = "201", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = BookingInfo.class)))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingInfo save(@PathVariable("customerId") int customerId,
                            @Valid @RequestBody BookingCreateCommand command) {
        log.info(LOG_SAVE_SUB, BASE_URL, customerId, SUB_URL, command);
        BookingInfo response = bookerService.save(customerId, command);
        log.info(LOG_RESPONSE, HttpStatus.CREATED, response);
        return response;
    }


    @Operation(summary = "List bookings by customer")
    @Parameter(name = "customerId", example = "2")
    @Parameter(name = "start", example = "2022-06-20T08:00")
    @Parameter(name = "end", example = "2022-06-24T20:00")
    @ApiResponse(responseCode = "200", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            array = @ArraySchema(schema = @Schema(implementation = BookingInfo.class))))
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingInfo> findAll(@PathVariable("customerId") int customerId,
                                     @RequestParam("start") LocalDateTime start,
                                     @RequestParam("end") LocalDateTime end) {
        log.info(LOG_GET_SUB + "?start={}&end={}", BASE_URL, customerId, SUB_URL, start, end);
        List<BookingInfo> response = bookingService.findAllForBooseOrCustomer(null, customerId, interval(start, end));
        log.info(LOG_RESPONSE, HttpStatus.OK, response);
        return response;
    }

    @Operation(summary = "Find booking by id")
    @Parameter(name = "customerId", example = "2")
    @Parameter(name = "id", example = "3")
    @ApiResponse(responseCode = "200", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = BookingInfo.class)))
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingInfo findById(@PathVariable("customerId") int customerId, @PathVariable("id") int id) {
        log.info(LOG_FINDBYID_SUB, BASE_URL, customerId, SUB_URL, id);
        BookingInfo response = bookingService.findForBooseOrCustomerById(id, null, customerId);
        log.info(LOG_RESPONSE, HttpStatus.OK, response);
        return response;
    }

    @Operation(summary = "Modify comment for booking")
    @Parameter(name = "customerId", example = "2")
    @Parameter(name = "id", example = "3")
    @ApiResponse(responseCode = "200", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = BookingInfo.class)))
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingInfo update(@PathVariable("customerId") int customerId,
                              @PathVariable("id") int id,
                              @Valid @RequestBody BookingUpdateCommand command) {
        log.info(LOG_UPDATE_SUB, BASE_URL, customerId, SUB_URL, id, command);
        BookingInfo response = bookingService.updateForBooseOrCustomer(id, null, customerId, command);
        log.info(LOG_RESPONSE, HttpStatus.OK, response);
        return response;
    }

    @Operation(summary = "Delete booking")
    @Parameter(name = "customerId", example = "2")
    @Parameter(name = "id", example = "3")
    @ApiResponse(responseCode = "200", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = BookingInfo.class)))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingInfo delete(@PathVariable("customerId") int customerId, @PathVariable("id") int id) {
        log.info(LOG_DELETE_SUB, BASE_URL, customerId, SUB_URL, id);
        BookingInfo response = bookingService.deleteForBooseOrCustomer(id, null, customerId);
        log.info(LOG_RESPONSE, HttpStatus.OK, response);
        return response;
    }
}
