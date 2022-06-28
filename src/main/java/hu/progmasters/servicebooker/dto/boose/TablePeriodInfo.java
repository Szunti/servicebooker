package hu.progmasters.servicebooker.dto.boose;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TablePeriodInfo {
    @Schema(type = "string", example = "2022-06-22T08:00")
    private LocalDateTime start;

    @Schema(type = "string", example = "2022-06-22T10:00")
    private LocalDateTime end;

    @Schema(example = "Might be a minute late")
    private String comment;

    private TablePeriodBookingInfo booking;
}
