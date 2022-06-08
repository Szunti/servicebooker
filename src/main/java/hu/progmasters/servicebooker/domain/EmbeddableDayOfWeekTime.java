package hu.progmasters.servicebooker.domain;

import hu.progmasters.servicebooker.util.DayOfWeekTime;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Embeddable
@NoArgsConstructor
public class EmbeddableDayOfWeekTime {
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    private LocalTime time;

    public DayOfWeekTime getAsDayOfWeekTime() {
        return DayOfWeekTime.of(dayOfWeek, time);
    }

    public void setFromDayOfWeekTime(DayOfWeekTime dayOfWeekTime) {
        dayOfWeek = dayOfWeekTime.getDayOfWeek();
        time = dayOfWeekTime.getTime();
    }
}
