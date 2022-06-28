package hu.progmasters.servicebooker.dto.boose;

import hu.progmasters.servicebooker.dto.customer.CustomerInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TablePeriodBookingInfo {
    @Schema(example = "3")
    private Integer id;

    @Schema(example = "Might be a minute late.")
    private String comment;

    private CustomerInfo customer;
}
