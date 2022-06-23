package hu.progmasters.servicebooker.dto.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CustomerInfo {
    @Schema(example = "2")
    private Integer id;

    @Schema(example = "John Smith")
    private String name;

    @Schema(example = "john.smith@gmail.com")
    private String email;
}
