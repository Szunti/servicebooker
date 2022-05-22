package hu.progmasters.servicebooker.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SpecificPeriod {
    private Integer id;
    private boolean active;

    private Boose boose;
    private LocalDateTime start;
    private LocalDateTime end;
    private String comment;
    private Boolean bookable;

}
