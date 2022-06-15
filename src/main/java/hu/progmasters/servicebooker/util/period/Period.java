package hu.progmasters.servicebooker.util.period;

import java.time.LocalDateTime;

public interface Period {
    LocalDateTime getStart();

    LocalDateTime getEnd();

    String getComment();
}
