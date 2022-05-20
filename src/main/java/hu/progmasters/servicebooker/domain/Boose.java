package hu.progmasters.servicebooker.domain;

import lombok.Data;

import java.util.List;

/**
 * Bookable Service, or boose in short
 *
 */

@Data
public class Boose {
    private String name;
    private String description;

    private List<WeeklyPeriod> weeklyPeriods;
    private List<SpecificPeriod> specificPeriods;
}
