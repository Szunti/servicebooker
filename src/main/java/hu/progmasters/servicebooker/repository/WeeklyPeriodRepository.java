package hu.progmasters.servicebooker.repository;

import hu.progmasters.servicebooker.domain.WeeklyPeriod;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class WeeklyPeriodRepository {
    private Map<Integer, WeeklyPeriod> periods = new HashMap<>();
    private int nextId = 1;

    public WeeklyPeriod save(WeeklyPeriod toSave) {
        Objects.requireNonNull(toSave);
        toSave.setId(nextId);
        toSave.setActive(true);
        periods.put(nextId, toSave);
        nextId++;
        return toSave;
    }

    public Optional<WeeklyPeriod> findById(Integer id) {
        return Optional.ofNullable(periods.get(id))
                .filter(WeeklyPeriod::isActive);
    }

    public List<WeeklyPeriod> findAll() {
        return periods.values().stream()
                .filter(WeeklyPeriod::isActive)
                .collect(Collectors.toList());
    }

    public Optional<WeeklyPeriod> deleteById(Integer id) {
        Optional<WeeklyPeriod> optPeriod = findById(id);
        optPeriod.ifPresent(period -> period.setActive(false));
        return optPeriod;
    }
}
