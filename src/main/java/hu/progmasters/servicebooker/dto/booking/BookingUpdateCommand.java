package hu.progmasters.servicebooker.dto.booking;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BookingUpdateCommand {
    @Schema(example = "I was wrong, I can definitely arrive on time.")
    private String comment;
}
