package hu.progmasters.servicebooker.dto.booking;

import hu.progmasters.servicebooker.dto.boose.BooseInfo;
import hu.progmasters.servicebooker.dto.customer.CustomerInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BookingInfo {
    @Schema(example = "3")
    private Integer id;

    @Schema(type = "string", example = "2022-06-22T08:00")
    private LocalDateTime start;

    @Schema(type = "string", example = "2022-06-22T10:00")
    private LocalDateTime end;

    @Schema(example = "Might be a minute late.")
    private String comment;

    private BooseInfo boose;

    private CustomerInfo customer;
}
