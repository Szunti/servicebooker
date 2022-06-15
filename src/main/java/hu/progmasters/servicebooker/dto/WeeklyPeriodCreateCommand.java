package hu.progmasters.servicebooker.dto;

import hu.progmasters.servicebooker.util.DayOfWeekTime;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class WeeklyPeriodCreateCommand {
    @NotNull
    private DayOfWeekTime start;

    @NotNull
    private DayOfWeekTime end;

    @NotNull
    private String comment;
}
