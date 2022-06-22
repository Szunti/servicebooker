package hu.progmasters.servicebooker.dto.booking;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BookingUpdateCommand {
    private String comment;
}
