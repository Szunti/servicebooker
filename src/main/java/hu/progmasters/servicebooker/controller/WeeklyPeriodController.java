package hu.progmasters.servicebooker.controller;

import hu.progmasters.servicebooker.dto.weeklyperiod.WeeklyPeriodCreateCommand;
import hu.progmasters.servicebooker.dto.weeklyperiod.WeeklyPeriodInfo;
import hu.progmasters.servicebooker.exceptionhandling.weeklyperiod.NoSuchWeeklyPeriodException;
import hu.progmasters.servicebooker.exceptionhandling.controller.WeeklyPeriodNotFoundException;
import hu.progmasters.servicebooker.exceptionhandling.weeklyperiod.WeeklyPeriodNotInBooseException;
import hu.progmasters.servicebooker.service.WeeklyPeriodService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/services/{booseId}/weekly-periods")
public class WeeklyPeriodController {

    private final WeeklyPeriodService weeklyPeriodService;

    public WeeklyPeriodController(WeeklyPeriodService weeklyPeriodService) {
        this.weeklyPeriodService = weeklyPeriodService;
    }

    @PostMapping
    public WeeklyPeriodInfo save(@PathVariable("booseId") int booseId,
                                 @Valid @RequestBody WeeklyPeriodCreateCommand command) {
        return weeklyPeriodService.addWeeklyPeriodForBoose(booseId, command);
    }

    @GetMapping
    public List<WeeklyPeriodInfo> findAll(@PathVariable("booseId") int booseId) {
        return weeklyPeriodService.findAllWeeklyPeriodsForBoose(booseId);
    }

    @GetMapping("/{id}")
    public WeeklyPeriodInfo findById(@PathVariable("booseId") int booseId,
                                     @PathVariable("id") int id) {
        try {
            return weeklyPeriodService.findWeeklyPeriodForBooseById(booseId, id);
        } catch (NoSuchWeeklyPeriodException | WeeklyPeriodNotInBooseException e) {
            throw new WeeklyPeriodNotFoundException(e);
        }
    }
}
