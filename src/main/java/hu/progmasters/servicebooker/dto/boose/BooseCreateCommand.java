package hu.progmasters.servicebooker.dto.boose;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class BooseCreateCommand {
    @NotBlank
    private String name;

    @NotNull
    private String description;
}
