package hu.progmasters.servicebooker.repository;

import hu.progmasters.servicebooker.domain.Boose;
import hu.progmasters.servicebooker.domain.WeeklyPeriod;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class WeeklyPeriodRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public WeeklyPeriod save(WeeklyPeriod weeklyPeriod) {
        entityManager.persist(weeklyPeriod);
        return weeklyPeriod;
    }

    public Optional<WeeklyPeriod> findById(int id) {
        return Optional.ofNullable(entityManager.find(WeeklyPeriod.class, id));
    }

    public List<WeeklyPeriod> findAllFor(Boose boose) {
        TypedQuery<WeeklyPeriod> query = entityManager.createQuery(
                        "SELECT wp FROM WeeklyPeriod wp WHERE wp.boose = :boose",
                        WeeklyPeriod.class)
                .setParameter("boose", boose);
        return query.getResultList();
    }
}
