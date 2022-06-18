package hu.progmasters.servicebooker.dto.specificperiod;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SpecificPeriodInfo {
    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private String comment;
    private boolean bookable;
}
