package hu.progmasters.servicebooker.repository;

import hu.progmasters.servicebooker.domain.Boose;
import hu.progmasters.servicebooker.domain.WeeklyPeriod;
import hu.progmasters.servicebooker.exceptionhandling.OverlappingWeeklyPeriodException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class WeeklyPeriodRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public WeeklyPeriod save(WeeklyPeriod toSave) {
        Boose boose = toSave.getBoose();
        // Optimistic locking could work here, but MySQL locks rows referenced by foreign keys
        // and that causes a deadlock
        entityManager.lock(boose, LockModeType.PESSIMISTIC_WRITE);
        List<WeeklyPeriod> overlappingPeriods = overlappingPeriods(toSave, boose);
        if (!overlappingPeriods.isEmpty()) {
            throw new OverlappingWeeklyPeriodException();
        }
        entityManager.persist(toSave);
        return toSave;
    }

    private List<WeeklyPeriod> overlappingPeriods(WeeklyPeriod weeklyPeriod, Boose boose) {
        // this one is a bit difficult because periods can cross week boundaries

        // Let the period be [s,e) (closed on s, open on e). When s is SUNDAY 20:00 and e is MONDAY 4:00 this is a
        // perfectly valid period, but we store the seconds since MONDAY 00:00:00 so in this case e will be a lower
        // integer in the database.
        // This is because weekday and time is not enough fields to have an order. But we can define an interval from s
        // to the next occurrence of e.

        // With that interval definition, there is an intersection, when the start point of one period is in the other:
        // s1 is inside [s2, e2) OR s2 is inside [s1, e2)

        // and x inside [s, e) is given by:
        // s <= x < e (if s and e is the same week)
        //  OR
        // e <= s AND (s <= x  OR  x < e)

        TypedQuery<WeeklyPeriod> query = entityManager.createQuery(
                "SELECT wp FROM WeeklyPeriod wp " +
                        "WHERE wp.boose = :boose " +
                        "AND (" +
                        "    (wp.start <= :start AND :start < wp.end) " +
                        "           OR " +
                        "    (wp.end <= wp.start AND (wp.start <= :start OR :start < wp.end))" +
                        "           OR " +
                        "    (:start <= wp.start AND wp.start < :end) " +
                        "           OR " +
                        "    (:end <= :start AND (:start <= wp.start OR wp.start < :end))" +
                        ")", WeeklyPeriod.class)
                .setParameter("boose", boose)
                .setParameter("start", weeklyPeriod.getStart().toSecondsFromWeekStart())
                .setParameter("end", weeklyPeriod.getEnd().toSecondsFromWeekStart());
        return query.getResultList();
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
