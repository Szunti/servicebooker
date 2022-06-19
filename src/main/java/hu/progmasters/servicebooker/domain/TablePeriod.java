package hu.progmasters.servicebooker.domain;

import hu.progmasters.servicebooker.domain.entity.Booking;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TablePeriod implements Period {
    private final LocalDateTime start;
    private final LocalDateTime end;
    private String comment;
    private Booking booking;

    public TablePeriod(LocalDateTime start, LocalDateTime end, String comment, Booking booking) {
        this.start = start;
        this.end = end;
        this.comment = comment;
        this.booking = booking;
    }

    public TablePeriod(LocalDateTime start, LocalDateTime end, String comment) {
        this.start = start;
        this.end = end;
        this.comment = comment;
    }
}
