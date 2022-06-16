package hu.progmasters.servicebooker.dto;

import hu.progmasters.servicebooker.util.DayOfWeekTime;
import lombok.Data;

@Data
public class WeeklyPeriodInfo {
    private Integer id;
    private DayOfWeekTime start;
    private DayOfWeekTime end;
    private String comment;
}
