package hu.progmasters.servicebooker.domain;

import hu.progmasters.servicebooker.util.DayOfWeekTime;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class WeeklyPeriod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private EmbeddableDayOfWeekTime start;
    private EmbeddableDayOfWeekTime end;
    private String comment;

    @ManyToOne(optional = false)
    private Boose boose;

    public DayOfWeekTime getStart() {
        return start.getAsDayOfWeekTime();
    }

    public void setStart(DayOfWeekTime start) {
        this.start.setFromDayOfWeekTime(start);
    }

    public DayOfWeekTime getEnd() {
        return end.getAsDayOfWeekTime();
    }

    public void setEnd(DayOfWeekTime end) {
        this.end.setFromDayOfWeekTime(end);
    }
}
