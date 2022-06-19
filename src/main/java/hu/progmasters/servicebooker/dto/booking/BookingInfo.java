package hu.progmasters.servicebooker.dto.booking;

import hu.progmasters.servicebooker.dto.boose.BooseInfo;
import hu.progmasters.servicebooker.dto.customer.CustomerInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BookingInfo {
    private Integer id;

    private LocalDateTime start;
    private LocalDateTime end;

    private String comment;

    private BooseInfo boose;

    private CustomerInfo customer;
}
