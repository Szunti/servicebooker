package hu.progmasters.servicebooker.dto.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class CustomerUpdateCommand {
    @Schema(example = "Joseph Smith")
    @NotBlank
    private String name;

    @Schema(example = "joseph.smith@gmail.com")
    @NotNull
    @Email
    private String email;
}
