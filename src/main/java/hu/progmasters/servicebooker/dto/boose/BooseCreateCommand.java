package hu.progmasters.servicebooker.dto.boose;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class BooseCreateCommand {
    @NotBlank
    private String name;

    @NotNull
    private String description;
}
