package hu.progmasters.servicebooker.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class SpecificPeriodCreateCommand {
    private LocalDateTime start;

    private LocalDateTime end;

    private String comment;

    @NotNull
    private Boolean bookable;

    @NotNull
    private Integer booseId;
}
