package hu.progmasters.servicebooker.controller;

import hu.progmasters.servicebooker.dto.specificperiod.SpecificPeriodInfo;
import hu.progmasters.servicebooker.dto.weeklyperiod.WeeklyPeriodCreateCommand;
import hu.progmasters.servicebooker.dto.weeklyperiod.WeeklyPeriodInfo;
import hu.progmasters.servicebooker.dto.weeklyperiod.WeeklyPeriodUpdateCommand;
import hu.progmasters.servicebooker.service.WeeklyPeriodService;
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

@Tag(name = "Weekly Periods")
@Slf4j
@RestController
@RequestMapping(WeeklyPeriodController.BASE_URL + "/{booseId}" + WeeklyPeriodController.SUB_URL)
public class WeeklyPeriodController {

    public static final String BASE_URL = "/api/services";
    public static final String SUB_URL = "/weekly-periods";

    private final WeeklyPeriodService weeklyPeriodService;

    public WeeklyPeriodController(WeeklyPeriodService weeklyPeriodService) {
        this.weeklyPeriodService = weeklyPeriodService;
    }

    @Operation(summary = "Save a new weekly period")
    @Parameter(name = "booseId", example = "1")
    @ApiResponse(responseCode = "201", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = SpecificPeriodInfo.class)))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WeeklyPeriodInfo save(@PathVariable("booseId") int booseId,
                                 @Valid @RequestBody WeeklyPeriodCreateCommand command) {
        log.info(LOG_SAVE_SUB, BASE_URL, booseId, SUB_URL, command);
        WeeklyPeriodInfo response = weeklyPeriodService.addForBoose(booseId, command);
        log.info(LOG_RESPONSE, HttpStatus.CREATED, response);
        return response;
    }

    @Operation(summary = "List weekly periods")
    @Parameter(name = "booseId", example = "1")
    @ApiResponse(responseCode = "200", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            array = @ArraySchema(schema = @Schema(implementation = SpecificPeriodInfo.class))))
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<WeeklyPeriodInfo> findAll(@PathVariable("booseId") int booseId) {
        log.info(LOG_GET_SUB, BASE_URL, booseId, SUB_URL);
        List<WeeklyPeriodInfo> response = weeklyPeriodService.findAllForBoose(booseId);
        log.info(LOG_RESPONSE, HttpStatus.OK, response);
        return response;
    }

    @Operation(summary = "Find weekly period by id")
    @Parameter(name = "booseId", example = "1")
    @Parameter(name = "id", example = "4")
    @ApiResponse(responseCode = "200", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = SpecificPeriodInfo.class)))
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public WeeklyPeriodInfo findById(@PathVariable("booseId") int booseId,
                                     @PathVariable("id") int id) {
        log.info(LOG_FINDBYID_SUB, BASE_URL, booseId, SUB_URL, id);
        WeeklyPeriodInfo response = weeklyPeriodService.findForBooseById(booseId, id);
        log.info(LOG_RESPONSE, HttpStatus.OK, response);
        return response;
    }

    @Operation(summary = "Modify comment for weekly period ")
    @Parameter(name = "booseId", example = "1")
    @Parameter(name = "id", example = "4")
    @ApiResponse(responseCode = "200", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = SpecificPeriodInfo.class)))
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public WeeklyPeriodInfo update(@PathVariable("booseId") int booseId,
                                   @PathVariable("id") int id,
                                   @Valid @RequestBody WeeklyPeriodUpdateCommand command) {
        log.info(LOG_UPDATE_SUB, BASE_URL, booseId, SUB_URL, id, command);
        WeeklyPeriodInfo response = weeklyPeriodService.update(booseId, id, command);
        log.info(LOG_RESPONSE, HttpStatus.OK, response);
        return response;
    }
}
