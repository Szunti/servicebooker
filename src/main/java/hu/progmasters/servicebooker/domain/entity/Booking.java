package hu.progmasters.servicebooker.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime start;
    private LocalDateTime end;

    private String comment;

    @ManyToOne(optional = false)
    private Boose boose;

    @ManyToOne(optional = false)
    private Customer customer;
}
