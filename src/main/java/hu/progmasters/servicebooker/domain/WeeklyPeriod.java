package hu.progmasters.servicebooker.domain;

import hu.progmasters.servicebooker.util.DayOfWeekTime;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
public class WeeklyPeriod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int start;
    private int end;

    private String comment;

    @ManyToOne(optional = false)
    private Boose boose;

    public DayOfWeekTime getStart() {
        return DayOfWeekTime.of(start);
    }

    public void setStart(DayOfWeekTime start) {
        this.start = start.toSecondsFromWeekStart();
    }

    public DayOfWeekTime getEnd() {
        return DayOfWeekTime.of(end);
    }

    public void setEnd(DayOfWeekTime end) {
        this.end = end.toSecondsFromWeekStart();
    }

    public boolean crossesWeekBoundary() {
        return end <= start;
    }

    public boolean contains(DayOfWeekTime dayOfWeekTime) {
        int secondsFromWeekStart = dayOfWeekTime.toSecondsFromWeekStart();
        if (crossesWeekBoundary()) {
            return secondsFromWeekStart >= start || secondsFromWeekStart < end;
        } else {
            return secondsFromWeekStart >= start && secondsFromWeekStart < end;
        }
    }
}
