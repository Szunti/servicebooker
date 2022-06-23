package hu.progmasters.servicebooker.dto.boose;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BooseInfo {
    @Schema(example = "1")
    private Integer id;

    @Schema(example = "Hairdresser Lisa")
    private String name;

    @Schema(example = "I have a small shop on the Pearl street.")
    private String description;
}
