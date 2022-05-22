package hu.progmasters.servicebooker.repository;

import hu.progmasters.servicebooker.domain.Boose;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class BooseRepository {
    private Map<Integer, Boose> booses = new HashMap<>();
    private int nextId = 1;

    public Boose save(Boose toSave) {
        Objects.requireNonNull(toSave);
        toSave.setId(nextId);
        toSave.setActive(true);
        booses.put(nextId, toSave);
        nextId++;
        return toSave;
    }

    public Optional<Boose> findById(Integer id) {
        return Optional.ofNullable(booses.get(id))
                .filter(Boose::isActive);
    }

    public List<Boose> findAll() {
        return booses.values().stream()
                .filter(Boose::isActive)
                .collect(Collectors.toList());
    }

    public Optional<Boose> deleteById(Integer id) {
        Optional<Boose> optBoose = findById(id);
        optBoose.ifPresent(boose -> boose.setActive(false));
        return optBoose;
    }
}
