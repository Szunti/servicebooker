package hu.progmasters.servicebooker.controller;

import hu.progmasters.servicebooker.domain.entity.SpecificPeriodType;
import hu.progmasters.servicebooker.dto.specificperiod.SpecificPeriodCreateCommand;
import hu.progmasters.servicebooker.dto.specificperiod.SpecificPeriodInfo;
import hu.progmasters.servicebooker.dto.specificperiod.SpecificPeriodUpdateCommand;
import hu.progmasters.servicebooker.service.SpecificPeriodService;
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

@Tag(name = "Weekly Periods")
@Slf4j
@RestController
@RequestMapping(SpecificPeriodController.BASE_URL + "/{booseId}" + SpecificPeriodController.SUB_URL)
public class SpecificPeriodController {

    public static final String BASE_URL = "/api/services";
    public static final String SUB_URL = "/specific-periods";

    private final SpecificPeriodService specificPeriodService;

    public SpecificPeriodController(SpecificPeriodService specificPeriodService) {
        this.specificPeriodService = specificPeriodService;
    }

    @Operation(summary = "Save a new specific period")
    @Parameter(name = "booseId", example = "1")
    @ApiResponse(responseCode = "201", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = SpecificPeriodInfo.class)))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SpecificPeriodInfo save(@PathVariable("booseId") int booseId,
                                   @Valid @RequestBody SpecificPeriodCreateCommand command) {
        log.info(LOG_SAVE_SUB, BASE_URL, booseId, SUB_URL, command);
        SpecificPeriodInfo response = specificPeriodService.addForBoose(booseId, command);
        log.info(LOG_RESPONSE, HttpStatus.CREATED, response);
        return response;
    }

    @Operation(summary = "List specific periods")
    @Parameter(name = "booseId", example = "1")
    @Parameter(name = "start", example = "2022-06-20T08:00")
    @Parameter(name = "end", example = "2022-06-24T20:00")
    @Parameter(name = "type", example = "ADD_OR_REPLACE")
    @ApiResponse(responseCode = "200", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            array = @ArraySchema(schema = @Schema(implementation = SpecificPeriodInfo.class))))
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<SpecificPeriodInfo> findAll(@PathVariable("booseId") int booseId,
                                            @RequestParam("start") LocalDateTime start,
                                            @RequestParam("end") LocalDateTime end,
                                            @RequestParam(value = "type", required = false) SpecificPeriodType type) {
        log.info(LOG_GET_SUB + "?start={}&end={}{}", BASE_URL, booseId, SUB_URL,
                start, end, type != null ? "&type=" + type : "");
        List<SpecificPeriodInfo> response = specificPeriodService.findAllForBoose(booseId, interval(start, end), type);
        log.info(LOG_RESPONSE, HttpStatus.OK, response);
        return response;
    }

    @Operation(summary = "Find specific period by id")
    @Parameter(name = "booseId", example = "1")
    @Parameter(name = "id", example = "3")
    @ApiResponse(responseCode = "200", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = SpecificPeriodInfo.class)))
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SpecificPeriodInfo findById(@PathVariable("booseId") int booseId,
                                       @PathVariable("id") int id) {
        log.info(LOG_FINDBYID_SUB, BASE_URL, booseId, SUB_URL, id);
        SpecificPeriodInfo response = specificPeriodService.findForBooseById(booseId, id);
        log.info(LOG_RESPONSE, HttpStatus.OK, response);
        return response;
    }

    @Operation(summary = "Modify comment for specific period ")
    @Parameter(name = "booseId", example = "1")
    @Parameter(name = "id", example = "3")
    @ApiResponse(responseCode = "200", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = SpecificPeriodInfo.class)))
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SpecificPeriodInfo update(@PathVariable("booseId") int booseId,
                                     @PathVariable("id") int id,
                                     @Valid @RequestBody SpecificPeriodUpdateCommand command) {
        log.info(LOG_UPDATE_SUB, BASE_URL, booseId, SUB_URL, id, command);
        SpecificPeriodInfo response = specificPeriodService.update(booseId, id, command);
        log.info(LOG_RESPONSE, HttpStatus.OK, response);
        return response;
    }
}
