package hu.progmasters.servicebooker.dto.weeklyperiod;

import hu.progmasters.servicebooker.util.DayOfWeekTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class WeeklyPeriodCreateCommand {
    @Schema(example = "Mon 08:00")
    @NotNull
    private DayOfWeekTime start;

    @Schema(example = "Mon 12:00")
    @NotNull
    private DayOfWeekTime end;

    @Schema(example = "Worst part of the week.")
    //TODO null as empty string on view
    private String comment;
}
