package hu.progmasters.servicebooker.dto.customer;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class CustomerCreateCommand {
    @NotBlank
    private String name;

    @NotNull
    @Email
    private String email;
}
