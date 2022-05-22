package hu.progmasters.servicebooker.repository;

import hu.progmasters.servicebooker.domain.SpecificPeriod;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class SpecificPeriodRepository {
    private Map<Integer, SpecificPeriod> periods = new HashMap<>();
    private int nextId = 1;

    public SpecificPeriod save(SpecificPeriod toSave) {
        Objects.requireNonNull(toSave);
        toSave.setId(nextId);
        toSave.setActive(true);
        periods.put(nextId, toSave);
        nextId++;
        return toSave;
    }

    public Optional<SpecificPeriod> findById(Integer id) {
        return Optional.ofNullable(periods.get(id))
                .filter(SpecificPeriod::isActive);
    }

    public List<SpecificPeriod> findAll() {
        return periods.values().stream()
                .filter(SpecificPeriod::isActive)
                .collect(Collectors.toList());
    }

    public Optional<SpecificPeriod> deleteById(Integer id) {
        Optional<SpecificPeriod> optPeriod = findById(id);
        optPeriod.ifPresent(period -> period.setActive(false));
        return optPeriod;
    }
}
