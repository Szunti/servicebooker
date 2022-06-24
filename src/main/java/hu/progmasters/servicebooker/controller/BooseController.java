package hu.progmasters.servicebooker.controller;

import hu.progmasters.servicebooker.dto.boose.BooseCreateCommand;
import hu.progmasters.servicebooker.dto.boose.BooseInfo;
import hu.progmasters.servicebooker.dto.boose.BooseUpdateCommand;
import hu.progmasters.servicebooker.dto.boose.TablePeriodInfo;
import hu.progmasters.servicebooker.service.BooseService;
import hu.progmasters.servicebooker.service.TimeTableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
import java.util.Map;

import static hu.progmasters.servicebooker.controller.LogMessages.*;
import static hu.progmasters.servicebooker.util.interval.Interval.interval;

@Tag(name = "Bookable Services")
@Slf4j
@RestController
@RequestMapping(BooseController.BASE_URL)
public class BooseController {

    public static final String BASE_URL = "/api/services";

    private final BooseService booseService;
    private final TimeTableService timeTableService;

    public BooseController(BooseService booseService, TimeTableService timeTableService) {
        this.booseService = booseService;
        this.timeTableService = timeTableService;
    }

    @Operation(summary = "Save a new service")
    @ApiResponse(responseCode = "201", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = BooseInfo.class)))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BooseInfo save(@Valid @RequestBody BooseCreateCommand command) {
        log.info(LOG_SAVE, BASE_URL, command);
        BooseInfo response = booseService.save(command);
        log.info(LOG_RESPONSE, HttpStatus.CREATED, response);
        return response;
    }

    @Operation(summary = "List services")
    @ApiResponse(responseCode = "200", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            array = @ArraySchema(schema = @Schema(implementation = BooseInfo.class))))
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BooseInfo> findAll() {
        log.info(LOG_FINDALL, BASE_URL);
        List<BooseInfo> response = booseService.findAll();
        log.info(LOG_RESPONSE, HttpStatus.OK, response);
        return response;
    }

    @Operation(summary = "Find service by id")
    @Parameter(name = "id", example = "1")
    @ApiResponse(responseCode = "200", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = BooseInfo.class)))
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BooseInfo findById(@PathVariable("id") int id) {
        log.info(LOG_FINDBYID, BASE_URL, id);
        BooseInfo response = booseService.findById(id);
        log.info(LOG_RESPONSE, HttpStatus.OK, response);
        return response;
    }

    @Operation(summary = "Modify service name and description")
    @Parameter(name = "id", example = "1")
    @ApiResponse(responseCode = "200", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = BooseInfo.class)
    ))
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BooseInfo update(@PathVariable("id") int id, @Valid @RequestBody BooseUpdateCommand command) {
        log.info(LOG_UPDATE, BASE_URL, id, command);
        BooseInfo response = booseService.update(id, command);
        log.info(LOG_RESPONSE, HttpStatus.OK, response);
        return response;
    }

    @Operation(summary = "Delete service")
    @Parameter(name = "id", example = "1")
    @ApiResponse(responseCode = "200", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = BooseInfo.class)
    ))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BooseInfo delete(@PathVariable("id") int id) {
        log.info(LOG_DELETE, BASE_URL, id);
        BooseInfo response = booseService.delete(id);
        log.info(LOG_RESPONSE, HttpStatus.OK, response);
        return response;
    }

    @Operation(summary = "Obtain the current timetable")
    @Parameter(name = "id", example = "1")
    @Parameter(name = "start", example = "2022-06-20T08:00")
    @Parameter(name = "end", example = "2022-06-24T20:00")
    @Parameter(name = "free", in = ParameterIn.QUERY, allowEmptyValue = true)
    @Parameter(name = "switches", hidden = true)
    @ApiResponse(responseCode = "200", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            array = @ArraySchema(schema = @Schema(implementation = TablePeriodInfo.class))
    ))
    @GetMapping("/{id}/timetable")
    @ResponseStatus(HttpStatus.OK)
    public List<TablePeriodInfo> getTimeTable(@PathVariable("id") int id,
                                              @RequestParam("start") LocalDateTime start,
                                              @RequestParam("end") LocalDateTime end,
                                              @RequestParam Map<String, String> switches) {
        boolean free = switches.get("free") != null;
        log.info(LOG_GET + "/{}/timetable?start={}&end={}{}", BASE_URL, id, start, end, free ? "&free" : "");
        List<TablePeriodInfo> response = timeTableService.assembleTimeTableForBoose(id, interval(start, end), free);
        log.info(LOG_RESPONSE, HttpStatus.OK, response);
        return response;
    }
}
