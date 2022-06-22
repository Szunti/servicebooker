package hu.progmasters.servicebooker.dto.boose;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Schema(title = "ServiceUpdateCommand")
@Data
@NoArgsConstructor
public class BooseUpdateCommand {
    @Schema(description = "Technically does not need to be unique, but should be.",
            example = "Doctor Bob")
    @NotBlank
    private String name;

    @Schema(example = "I am not a hairdresser.")
    private String description;
}
