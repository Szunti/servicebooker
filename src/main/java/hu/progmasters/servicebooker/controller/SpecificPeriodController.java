package hu.progmasters.servicebooker.controller;

import hu.progmasters.servicebooker.dto.SpecificPeriodCreateCommand;
import hu.progmasters.servicebooker.dto.SpecificPeriodInfo;
import hu.progmasters.servicebooker.exceptionhandling.NoSuchSpecificPeriodException;
import hu.progmasters.servicebooker.exceptionhandling.SpecificPeriodNotFoundException;
import hu.progmasters.servicebooker.exceptionhandling.SpecificPeriodNotInBooseException;
import hu.progmasters.servicebooker.service.BooseService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static hu.progmasters.servicebooker.util.interval.SimpleInterval.interval;

@RestController
@RequestMapping("/api/services/{booseId}/specific-periods")
public class SpecificPeriodController {

    private final BooseService booseService;

    public SpecificPeriodController(BooseService booseService) {
        this.booseService = booseService;
    }

    @PostMapping
    public SpecificPeriodInfo save(@PathVariable("booseId") int booseId,
                                   @Valid @RequestBody SpecificPeriodCreateCommand command) {
        return booseService.addSpecificPeriodForBoose(booseId, command);
    }

    @GetMapping
    public List<SpecificPeriodInfo> findAll(@PathVariable("booseId") int booseId,
                                            @RequestParam(value = "start") LocalDateTime start,
                                            @RequestParam(value = "end") LocalDateTime end,
                                            @RequestParam(value = "bookable", required = false) Boolean bookable) {
        return booseService.findAllSpecificPeriodsForBoose(booseId, interval(start, end), bookable);
    }

    @GetMapping("{id}")
    public SpecificPeriodInfo findById(@PathVariable("booseId") int booseId,
                                       @PathVariable("id") int id) {
        try {
            return booseService.findSpecificPeriodForBooseById(booseId, id);
        } catch (NoSuchSpecificPeriodException | SpecificPeriodNotInBooseException e) {
            throw new SpecificPeriodNotFoundException(e);
        }
    }
}