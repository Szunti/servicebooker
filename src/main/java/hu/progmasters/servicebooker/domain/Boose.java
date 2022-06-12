package hu.progmasters.servicebooker.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Bookable Service, or boose in short
 *
 */

@Data
@NoArgsConstructor
@Entity
public class Boose {
    // TODO soft delete

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Version
    private Integer version;

    private String name;

    private String description;
}
