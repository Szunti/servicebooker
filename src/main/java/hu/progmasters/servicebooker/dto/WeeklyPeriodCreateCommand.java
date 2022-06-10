package hu.progmasters.servicebooker.dto;

import hu.progmasters.servicebooker.util.DayOfWeekTime;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class WeeklyPeriodCreateCommand {
    @NotNull
    private DayOfWeekTime start;

    @NotNull
    private DayOfWeekTime end;

    @NotNull
    private String comment;

    @Positive
    private int booseId;
}
