package hu.progmasters.servicebooker.service;

import java.time.LocalDateTime;

public interface Period {
    LocalDateTime getStart();
    LocalDateTime getEnd();
    String getComment();
}
