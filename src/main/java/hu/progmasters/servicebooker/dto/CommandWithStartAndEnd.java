package hu.progmasters.servicebooker.dto;

import java.time.LocalDateTime;

public interface CommandWithStartAndEnd {
    LocalDateTime getStart();
    LocalDateTime getEnd();
}
