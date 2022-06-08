package hu.progmasters.servicebooker.repository;

import hu.progmasters.servicebooker.domain.Boose;
import hu.progmasters.servicebooker.domain.SpecificPeriod;
import hu.progmasters.servicebooker.util.interval.Interval;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
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
        entityManager.persist(toSave);
        return toSave;
    }

    public Optional<SpecificPeriod> findById(int id) {
        return Optional.ofNullable(entityManager.find(SpecificPeriod.class, id));
    }

    public List<SpecificPeriod> findAllFor(Boose boose, Interval<LocalDateTime> interval, Boolean bookable) {
        TypedQuery<SpecificPeriod> query = entityManager.createQuery(
                        "SELECT sp FROM SpecificPeriod sp WHERE sp.boose = :boose " +
                                "AND sp.start < :intervalEnd AND sp.end > :intervalStart " +
                                "AND (:bookable IS NULL OR sp.bookable = :bookable)",
                        SpecificPeriod.class)
                .setParameter("boose", boose)
                .setParameter("intervalStart", interval.getStart())
                .setParameter("intervalEnd", interval.getEnd())
                .setParameter("bookable", bookable);
        return query.getResultList();
    }
}
