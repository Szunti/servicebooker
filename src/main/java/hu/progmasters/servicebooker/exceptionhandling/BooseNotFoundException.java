package hu.progmasters.servicebooker.exceptionhandling;

import lombok.Getter;

@Getter
public class BooseNotFoundException extends RuntimeException {
    private final int id;

    public BooseNotFoundException(int id) {
        super("Boose not found with id: " + id);
        this.id = id;
    }
}
