package hu.progmasters.servicebooker.repository;

import hu.progmasters.servicebooker.domain.Boose;
import hu.progmasters.servicebooker.domain.SpecificPeriod;
import hu.progmasters.servicebooker.exceptionhandling.OverlappingSpecificPeriodException;
import hu.progmasters.servicebooker.util.interval.Interval;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class SpecificPeriodRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public SpecificPeriod save(SpecificPeriod toSave) {
        Boose boose = toSave.getBoose();
        entityManager.lock(boose, LockModeType.PESSIMISTIC_WRITE);
        List<SpecificPeriod> overlappingPeriods = overlappingPeriods(toSave, boose);
        if (!overlappingPeriods.isEmpty()) {
            throw new OverlappingSpecificPeriodException();
        }
        entityManager.persist(toSave);
        return toSave;
    }

    private List<SpecificPeriod> overlappingPeriods(SpecificPeriod specificPeriod, Boose boose) {
        // Let the period be [s,e) (closed on s, open on e).
        // [s1, e1) intersects [s2, e2) when:
        // s1 < e2 AND s2 < e1
        TypedQuery<SpecificPeriod> query = entityManager.createQuery(
                        "SELECT sp FROM SpecificPeriod sp " +
                                "WHERE sp.boose = :boose " +
                                "AND :start < sp.end AND sp.start < :end",
                        SpecificPeriod.class)
                .setParameter("boose", boose)
                .setParameter("start", specificPeriod.getStart())
                .setParameter("end", specificPeriod.getEnd());
        return query.getResultList();
    }

    public Optional<SpecificPeriod> findById(int id) {
        return Optional.ofNullable(entityManager.find(SpecificPeriod.class, id));
    }

    public List<SpecificPeriod> findAllOrderedFor(Boose boose, Interval<LocalDateTime> interval, Boolean bookable) {
        TypedQuery<SpecificPeriod> query = entityManager.createQuery(
                        "SELECT sp FROM SpecificPeriod sp WHERE sp.boose = :boose " +
                                "AND sp.start < :intervalEnd AND sp.end > :intervalStart " +
                                "AND (:bookable IS NULL OR sp.bookable = :bookable) " +
                                "ORDER BY sp.start",
                        SpecificPeriod.class)
                .setParameter("boose", boose)
                .setParameter("intervalStart", interval.getStart())
                .setParameter("intervalEnd", interval.getEnd())
                .setParameter("bookable", bookable);
        return query.getResultList();
    }
}
