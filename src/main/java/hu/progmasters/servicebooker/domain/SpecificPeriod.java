package hu.progmasters.servicebooker.domain;

import hu.progmasters.servicebooker.util.period.Period;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
public class SpecificPeriod implements Period {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime start;
    private LocalDateTime end;

    private String comment;

    private boolean bookable;

    @ManyToOne(optional = false)
    private Boose boose;

}
