package hu.progmasters.servicebooker.controller;

import hu.progmasters.servicebooker.dto.boose.BooseCreateCommand;
import hu.progmasters.servicebooker.dto.boose.BooseInfo;
import hu.progmasters.servicebooker.dto.boose.TablePeriodInfo;
import hu.progmasters.servicebooker.exceptionhandling.boose.NoSuchBooseException;
import hu.progmasters.servicebooker.exceptionhandling.controller.BooseNotFoundException;
import hu.progmasters.servicebooker.service.BooseService;
import hu.progmasters.servicebooker.service.TimeTableService;
import io.swagger.v3.oas.annotations.Operation;
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

import static hu.progmasters.servicebooker.util.interval.Interval.interval;

@Tag(name = "Services")
@RestController
@Slf4j
@RequestMapping("/api/services")
public class BooseController {
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
        log.info("POST request on /api/services/ with request body: {}", command);
        BooseInfo response = booseService.save(command);
        log.info("HTTP status CREATED, response: {}", response);
        return response;
    }

    @Operation(summary = "List services")
    @ApiResponse(responseCode = "200", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            array = @ArraySchema(schema = @Schema(implementation = BooseInfo.class))))
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BooseInfo> findAll() {
        log.info("GET request on /api/services/");
        List<BooseInfo> response = booseService.findAll();
        log.info("HTTP status OK, response: {}", response);
        return response;
    }

    @Operation(summary = "Get service by id")
    @ApiResponse(responseCode = "200", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = BooseInfo.class)))
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BooseInfo findById(@PathVariable("id") int id) {
        log.info("GET request on /api/services/{}", id);
        BooseInfo response;
        try {
            response = booseService.findById(id);
        } catch (NoSuchBooseException exception) {
            throw new BooseNotFoundException(exception);
        }
        log.info("HTTP status OK, response: {}", response);
        return response;
    }

    @GetMapping("/{id}/timetable")
    @ResponseStatus(HttpStatus.OK)
    public List<TablePeriodInfo> getTimeTable(@PathVariable("id") int id,
                                              @RequestParam("start") LocalDateTime start,
                                              @RequestParam("end") LocalDateTime end,
                                              @RequestParam Map<String, String> switches) {
        boolean free = switches.get("free") != null;
        log.info("GET request on /api/services/{}/timetable?start={}&end={}{}", id, start, end, free ? "&free" : "");
        List<TablePeriodInfo> response = timeTableService.assembleTimeTableForBoose(id, interval(start, end), free);
        log.info("HTTP status OK, response: {}", response);
        return response;
    }
}
