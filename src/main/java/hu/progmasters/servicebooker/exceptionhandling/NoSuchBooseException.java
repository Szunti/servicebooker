package hu.progmasters.servicebooker.exceptionhandling;

import lombok.Getter;

@Getter
public class NoSuchBooseException extends RuntimeException {
    private final int id;

    public NoSuchBooseException(int id) {
        this.id = id;
    }
}
