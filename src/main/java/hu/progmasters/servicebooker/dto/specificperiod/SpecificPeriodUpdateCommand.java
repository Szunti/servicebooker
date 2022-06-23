package hu.progmasters.servicebooker.dto.specificperiod;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SpecificPeriodUpdateCommand {
    @Schema(example = "Can work this day, but only this time.")
    private String comment;
}
