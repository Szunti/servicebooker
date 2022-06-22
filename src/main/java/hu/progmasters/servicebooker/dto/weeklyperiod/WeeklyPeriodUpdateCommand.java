package hu.progmasters.servicebooker.dto.weeklyperiod;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WeeklyPeriodUpdateCommand {
    private String comment;
}
