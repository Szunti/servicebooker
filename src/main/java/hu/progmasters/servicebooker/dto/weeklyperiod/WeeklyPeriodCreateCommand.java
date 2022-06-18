package hu.progmasters.servicebooker.dto.weeklyperiod;

import hu.progmasters.servicebooker.util.DayOfWeekTime;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class WeeklyPeriodCreateCommand {
    @NotNull
    private DayOfWeekTime start;

    @NotNull
    private DayOfWeekTime end;

    @NotNull
    private String comment;
}
