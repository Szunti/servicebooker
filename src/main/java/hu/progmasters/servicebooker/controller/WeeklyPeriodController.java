package hu.progmasters.servicebooker.controller;

import hu.progmasters.servicebooker.domain.WeeklyPeriod;
import hu.progmasters.servicebooker.dto.WeeklyPeriodCreateCommand;
import hu.progmasters.servicebooker.dto.WeeklyPeriodInfo;
import hu.progmasters.servicebooker.exceptionhandling.NoSuchWeeklyPeriodException;
import hu.progmasters.servicebooker.exceptionhandling.WeeklyPeriodNotFoundException;
import hu.progmasters.servicebooker.exceptionhandling.WeeklyPeriodNotInBooseException;
import hu.progmasters.servicebooker.service.BooseService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/services/{booseId}/weekly-periods")
public class WeeklyPeriodController {

    private final BooseService booseService;

    public WeeklyPeriodController(BooseService booseService) {
        this.booseService = booseService;
    }

    @PostMapping
    public WeeklyPeriodInfo save(@PathVariable("booseId") int booseId,
                                 @Valid @RequestBody WeeklyPeriodCreateCommand command) {
        return booseService.addWeeklyPeriodForBoose(booseId, command);
    }

    @GetMapping
    public List<WeeklyPeriodInfo> findAll(@PathVariable("booseId") int booseId) {
        return booseService.findAllWeeklyPeriodsForBoose(booseId);
    }

    @GetMapping("{id}")
    public WeeklyPeriodInfo findById(@PathVariable("booseId") int booseId,
                                     @PathVariable("id") int id) {
        try {
            return booseService.findWeeklyPeriodForBooseById(booseId, id);
        } catch (NoSuchWeeklyPeriodException | WeeklyPeriodNotInBooseException e) {
            throw new WeeklyPeriodNotFoundException(e);
        }
    }
}
