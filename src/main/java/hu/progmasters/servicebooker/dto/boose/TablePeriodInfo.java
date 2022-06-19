package hu.progmasters.servicebooker.dto.boose;

import hu.progmasters.servicebooker.dto.booking.BookingInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TablePeriodInfo {
    private LocalDateTime start;
    private LocalDateTime end;
    private String comment;
    private BookingInfo booking;
}
