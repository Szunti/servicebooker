package hu.progmasters.servicebooker.dto.specificperiod;

import hu.progmasters.servicebooker.validation.StartBeforeEnd;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@StartBeforeEnd
public class SpecificPeriodCreateCommand {
    // TODO use interval instead of start and end

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;

    @NotNull
    private String comment;

    @NotNull
    private Boolean bookable;
}
