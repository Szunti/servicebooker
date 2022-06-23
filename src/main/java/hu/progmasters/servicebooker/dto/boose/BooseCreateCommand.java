package hu.progmasters.servicebooker.dto.boose;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class BooseCreateCommand {
    @Schema(description = "Technically does not need to be unique, but should be.",
            example = "Hairdresser Lisa")
    @NotBlank
    private String name;

    @Schema(example = "I have a small shop on the Pearl street.")
    private String description;
}
