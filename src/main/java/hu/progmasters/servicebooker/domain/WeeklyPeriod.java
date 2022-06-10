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

    @AttributeOverrides({
            @AttributeOverride(name = "dayOfWeek", column = @Column(name = "startDayOfWeek")),
            @AttributeOverride(name = "time", column = @Column(name = "startTime"))
    })
    private EmbeddableDayOfWeekTime start;

    @AttributeOverrides({
            @AttributeOverride(name = "dayOfWeek", column = @Column(name = "endDayOfWeek")),
            @AttributeOverride(name = "time", column = @Column(name = "endTime"))
    })
    private EmbeddableDayOfWeekTime end;
    private String comment;

    @ManyToOne(optional = false)
    private Boose boose;

    public DayOfWeekTime getStart() {
        return start.getAsDayOfWeekTime();
    }

    public void setStart(DayOfWeekTime start) {
        EmbeddableDayOfWeekTime embeddedStart = new EmbeddableDayOfWeekTime();
        embeddedStart.setFromDayOfWeekTime(start);
        this.start = embeddedStart;
    }

    public DayOfWeekTime getEnd() {
        return end.getAsDayOfWeekTime();
    }

    public void setEnd(DayOfWeekTime end) {
        EmbeddableDayOfWeekTime embeddedEnd = new EmbeddableDayOfWeekTime();
        embeddedEnd.setFromDayOfWeekTime(end);
        this.end = embeddedEnd;
    }
}
