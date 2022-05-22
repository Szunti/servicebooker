package hu.progmasters.servicebooker.domain;

import lombok.Data;

/**
 * Bookable Service, or boose in short
 *
 */

@Data
public class Boose {
    private Integer id;
    private boolean active;

    private String name;
    private String description;
}
