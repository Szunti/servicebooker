package hu.progmasters.servicebooker.dto.weeklyperiod;

import hu.progmasters.servicebooker.util.DayOfWeekTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WeeklyPeriodInfo {
    private Integer id;
    private DayOfWeekTime start;
    private DayOfWeekTime end;
    private String comment;
}
