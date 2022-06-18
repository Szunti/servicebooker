package hu.progmasters.servicebooker.dto.customer;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CustomerInfo {
    private Integer id;
    private String name;
    private String email;
}
