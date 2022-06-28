package hu.progmasters.servicebooker.dto.specificperiod;

import hu.progmasters.servicebooker.domain.entity.SpecificPeriodType;
import hu.progmasters.servicebooker.validation.StartBeforeEnd;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@StartBeforeEnd
public class SpecificPeriodCreateCommand {
    // TODO use interval instead of start and end
    @Schema(type = "string", example = "2022-06-22T08:00")
    @NotNull
    private LocalDateTime start;

    @Schema(type = "string", example = "2022-06-22T10:00")
    @NotNull
    private LocalDateTime end;

    @Schema(example = "Can work this day.")
    private String comment;

    @Schema(example = "ADD_OR_REPLACE")
    @NotNull
    private SpecificPeriodType type;
}
