package hu.progmasters.servicebooker.exceptionhandling;

import lombok.Getter;

@Getter
public class BooseNotFoundException extends RuntimeException {
    private final int id;

    public BooseNotFoundException(int id) {
        this.id = id;
    }

    public BooseNotFoundException(int id, Throwable cause) {
        super(cause);
        this.id = id;
    }
}
