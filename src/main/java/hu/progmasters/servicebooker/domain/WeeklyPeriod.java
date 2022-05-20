package hu.progmasters.servicebooker.domain;

import lombok.Data;

@Data
public class WeeklyPeriod {
    private Boose boose;

    private DayOfWeekTime start;
    private DayOfWeekTime end;

    private String comment;
}
