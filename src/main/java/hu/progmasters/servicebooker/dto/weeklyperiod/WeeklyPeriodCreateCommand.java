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

    //TODO null as empty string on view
    private String comment;
}
