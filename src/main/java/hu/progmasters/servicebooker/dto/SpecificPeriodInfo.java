package hu.progmasters.servicebooker.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SpecificPeriodInfo {
    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private String comment;
    private boolean bookable;
}
