package hu.progmasters.servicebooker.dto.booking;

import hu.progmasters.servicebooker.validation.StartBeforeEnd;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@StartBeforeEnd
public class BookingCreateCommand {
    @Schema(type = "string", example = "2022-06-22T08:00")
    @NotNull
    private LocalDateTime start;

    @Schema(type = "string", example = "2022-06-22T10:00")
    @NotNull
    private LocalDateTime end;

    @Schema(example = "Might be a minute late.")
    private String comment;

    @Schema(example = "1")
    @NotNull
    private int booseId;

    @Schema(example = "2")
    @NotNull
    private int customerId;
}
