package hu.progmasters.servicebooker.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class SpecificPeriod {
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
