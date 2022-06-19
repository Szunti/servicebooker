package hu.progmasters.servicebooker.controller;

import hu.progmasters.servicebooker.dto.boose.BooseCreateCommand;
import hu.progmasters.servicebooker.dto.boose.BooseInfo;
import hu.progmasters.servicebooker.dto.boose.TablePeriodInfo;
import hu.progmasters.servicebooker.exceptionhandling.boose.NoSuchBooseException;
import hu.progmasters.servicebooker.exceptionhandling.controller.BooseNotFoundException;
import hu.progmasters.servicebooker.service.BooseService;
import hu.progmasters.servicebooker.service.TimeTableService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static hu.progmasters.servicebooker.util.interval.Interval.interval;

@RestController
@RequestMapping("/api/services")
public class BooseController {
    // TODO logging

    private final BooseService booseService;
    private final TimeTableService timeTableService;

    public BooseController(BooseService booseService, TimeTableService timeTableService) {
        this.booseService = booseService;
        this.timeTableService = timeTableService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BooseInfo save(@Valid @RequestBody BooseCreateCommand command) {
        return booseService.save(command);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BooseInfo> findAll() {
        return booseService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BooseInfo findById(@PathVariable("id") int id) {
        try {
            return booseService.findById(id);
        } catch (NoSuchBooseException exception) {
            throw new BooseNotFoundException(exception);
        }
    }

    @GetMapping("/{id}/timetable")
    @ResponseStatus(HttpStatus.OK)
    public List<TablePeriodInfo> getTimeTable(@PathVariable("id") int id,
                                              @RequestParam("start") LocalDateTime start,
                                              @RequestParam("end") LocalDateTime end) {
        return timeTableService.collectTimeTableForBoose(id, interval(start, end));
    }
}
