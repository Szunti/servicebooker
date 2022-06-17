package hu.progmasters.servicebooker.domain;

import java.time.LocalDateTime;

public interface Period {
    LocalDateTime getStart();

    LocalDateTime getEnd();

    String getComment();
}
