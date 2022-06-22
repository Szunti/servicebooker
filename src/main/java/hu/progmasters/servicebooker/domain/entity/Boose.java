package hu.progmasters.servicebooker.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Bookable Service, or boose in short
 */

@Data
@NoArgsConstructor
@Entity
public class Boose {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String description;

    private boolean deleted;
}
