package hu.progmasters.servicebooker.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FreePeriodInfo {
    private LocalDateTime start;
    private LocalDateTime end;
    private String comment;
}
