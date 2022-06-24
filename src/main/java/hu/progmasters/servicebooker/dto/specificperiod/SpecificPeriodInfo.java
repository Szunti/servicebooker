package hu.progmasters.servicebooker.dto.specificperiod;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SpecificPeriodInfo {
    @Schema(example = "3")
    private Integer id;

    @Schema(type = "string", example = "2022-06-22T08:00")
    private LocalDateTime start;

    @Schema(type = "string", example = "2022-06-22T10:00")
    private LocalDateTime end;

    @Schema(example = "Can work this day.")
    private String comment;

    @Schema(example = "true")
    private boolean bookable;
}
