package hu.progmasters.servicebooker.repository;

import hu.progmasters.servicebooker.domain.entity.Booking;
import hu.progmasters.servicebooker.domain.entity.Boose;
import hu.progmasters.servicebooker.domain.entity.Customer;
import hu.progmasters.servicebooker.util.interval.Interval;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class BookingRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Booking save(Booking toSave) {
        entityManager.persist(toSave);
        return toSave;
    }

    public Optional<Booking> findById(int id) {
        return Optional.ofNullable(entityManager.find(Booking.class, id));
    }

    public Optional<Booking> findByBooseAndDate(Boose boose, Interval<LocalDateTime> interval) {
        List<Booking> bookings = entityManager.createQuery("SELECT b FROM Booking b " +
                "WHERE b.boose = :boose " +
                "  AND b.start = :intervalStart " +
                "  AND b.end = :intervalEnd", Booking.class)
                .getResultList();
        // if there is more than one result, the database is corrupt
        assert bookings.size() <= 1;
        return bookings.stream().findAny();
    }

    public List<Booking> findAllOrderedFor(Boose boose, Customer customer, Interval<LocalDateTime> interval) {
        return entityManager.createQuery("SELECT b FROM Booking b " +
                        "WHERE (b.boose IS NULL OR b.boose = :boose) " +
                        "  AND (b.customer IS NULL OR b.customer = :customer) " +
                        "  AND (b.start < :intervalEnd) AND (b.end > :intervalStart) " +
                        "ORDER BY b.start", Booking.class)
                .setParameter("boose", boose)
                .setParameter("customer", customer)
                .setParameter("intervalStart", interval.getStart())
                .setParameter("intervalEnd", interval.getEnd())
                .getResultList();
    }
}
