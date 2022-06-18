package hu.progmasters.servicebooker.dto.boose;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class FreePeriodInfo {
    private LocalDateTime start;
    private LocalDateTime end;
    private String comment;
}
