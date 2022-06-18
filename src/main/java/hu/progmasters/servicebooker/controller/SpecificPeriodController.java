package hu.progmasters.servicebooker.controller;

import hu.progmasters.servicebooker.dto.specificperiod.SpecificPeriodCreateCommand;
import hu.progmasters.servicebooker.dto.specificperiod.SpecificPeriodInfo;
import hu.progmasters.servicebooker.exceptionhandling.specificperiod.NoSuchSpecificPeriodException;
import hu.progmasters.servicebooker.exceptionhandling.controller.SpecificPeriodNotFoundException;
import hu.progmasters.servicebooker.exceptionhandling.specificperiod.SpecificPeriodNotInBooseException;
import hu.progmasters.servicebooker.service.SpecificPeriodService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static hu.progmasters.servicebooker.util.interval.Interval.interval;


@RestController
@RequestMapping("/api/services/{booseId}/specific-periods")
public class SpecificPeriodController {

    private final SpecificPeriodService specificPeriodService;

    public SpecificPeriodController(SpecificPeriodService specificPeriodService) {
        this.specificPeriodService = specificPeriodService;
    }

    @PostMapping
    public SpecificPeriodInfo save(@PathVariable("booseId") int booseId,
                                   @Valid @RequestBody SpecificPeriodCreateCommand command) {
        return specificPeriodService.addSpecificPeriodForBoose(booseId, command);
    }

    @GetMapping
    public List<SpecificPeriodInfo> findAll(@PathVariable("booseId") int booseId,
                                            @RequestParam(value = "start") LocalDateTime start,
                                            @RequestParam(value = "end") LocalDateTime end,
                                            @RequestParam(value = "bookable", required = false) Boolean bookable) {
        return specificPeriodService.findAllSpecificPeriodsForBoose(booseId, interval(start, end), bookable);
    }

    @GetMapping("/{id}")
    public SpecificPeriodInfo findById(@PathVariable("booseId") int booseId,
                                       @PathVariable("id") int id) {
        try {
            return specificPeriodService.findSpecificPeriodForBooseById(booseId, id);
        } catch (NoSuchSpecificPeriodException | SpecificPeriodNotInBooseException e) {
            throw new SpecificPeriodNotFoundException(e);
        }
    }
}
