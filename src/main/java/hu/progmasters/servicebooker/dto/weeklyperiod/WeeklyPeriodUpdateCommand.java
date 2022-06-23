package hu.progmasters.servicebooker.dto.weeklyperiod;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WeeklyPeriodUpdateCommand {
    @Schema(example = "Actually, Tuesday is the worst.")
    private String comment;
}
