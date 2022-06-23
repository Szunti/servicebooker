package hu.progmasters.servicebooker.dto.weeklyperiod;

import hu.progmasters.servicebooker.util.DayOfWeekTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WeeklyPeriodInfo {
    @Schema(example = "1")
    private Integer id;

    @Schema(example = "Mon 08:00")
    private DayOfWeekTime start;

    @Schema(example = "Mon 12:00")
    private DayOfWeekTime end;

    @Schema(example = "Can work this day.")
    private String comment;
}
