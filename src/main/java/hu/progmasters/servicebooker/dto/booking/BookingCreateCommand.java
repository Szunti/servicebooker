package hu.progmasters.servicebooker.dto.booking;

import hu.progmasters.servicebooker.validation.StartBeforeEnd;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@StartBeforeEnd
public class BookingCreateCommand {
    @NotNull
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;

    private String comment;

    @NotNull
    private int booseId;
    @NotNull
    private int customerId;
}
