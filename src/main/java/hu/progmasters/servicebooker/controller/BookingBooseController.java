package hu.progmasters.servicebooker.controller;

import hu.progmasters.servicebooker.dto.booking.BookingInfo;
import hu.progmasters.servicebooker.dto.booking.BookingUpdateCommand;
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
@RequestMapping(BookingBooseController.BASE_URL + "/{booseId}/bookings" + BookingBooseController.SUB_URL)
public class BookingBooseController {

    public static final String BASE_URL = "/api/services";
    public static final String SUB_URL = "/bookings";

    private final BookingService bookingService;

    public BookingBooseController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Operation(summary = "List bookings for service")
    @Parameter(name = "booseId", example = "1")
    @Parameter(name = "start", example = "2022-06-20T08:00")
    @Parameter(name = "end", example = "2022-06-24T20:00")
    @ApiResponse(responseCode = "200", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            array = @ArraySchema(schema = @Schema(implementation = BookingInfo.class))))
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingInfo> findAll(@PathVariable("booseId") int booseId,
                                     @RequestParam("start") LocalDateTime start,
                                     @RequestParam("end") LocalDateTime end) {
        log.info(LOG_GET_SUB + "?start={}&end={}", BASE_URL, booseId, SUB_URL, start, end);
        List<BookingInfo> response = bookingService.findAllForBooseOrCustomer(booseId, null, interval(start, end));
        log.info(LOG_RESPONSE, HttpStatus.OK, response);
        return response;
    }

    @Operation(summary = "Find booking by id")
    @Parameter(name = "booseId", example = "1")
    @Parameter(name = "id", example = "3")
    @ApiResponse(responseCode = "200", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = BookingInfo.class)))
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingInfo findById(@PathVariable("booseId") int booseId, @PathVariable("id") int id) {
        log.info(LOG_FINDBYID_SUB, BASE_URL, booseId, SUB_URL, id);
        BookingInfo response = bookingService.findForBooseOrCustomerById(id, booseId, null);
        log.info(LOG_RESPONSE, HttpStatus.OK, response);
        return response;
    }

    @Operation(summary = "Modify comment for booking")
    @Parameter(name = "booseId", example = "1")
    @Parameter(name = "id", example = "3")
    @ApiResponse(responseCode = "200", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = BookingInfo.class)))
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingInfo update(@PathVariable("booseId") int booseId,
                              @PathVariable("id") int id,
                              @Valid @RequestBody BookingUpdateCommand command) {
        log.info(LOG_UPDATE_SUB, BASE_URL, booseId, SUB_URL, id, command);
        BookingInfo response = bookingService.updateForBooseOrCustomer(id, booseId, null, command);
        log.info(LOG_RESPONSE, HttpStatus.OK, response);
        return response;
    }

    @Operation(summary = "Delete booking")
    @Parameter(name = "booseId", example = "1")
    @Parameter(name = "id", example = "3")
    @ApiResponse(responseCode = "200", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = BookingInfo.class)))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingInfo delete(@PathVariable("booseId") int booseId, @PathVariable("id") int id) {
        log.info(LOG_DELETE_SUB, BASE_URL, booseId, SUB_URL, id);
        BookingInfo response = bookingService.deleteForBooseOrCustomer(id, booseId, null);
        log.info(LOG_RESPONSE, HttpStatus.OK, response);
        return response;
    }
}
