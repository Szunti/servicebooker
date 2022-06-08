package hu.progmasters.servicebooker.repository;

import hu.progmasters.servicebooker.domain.Boose;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class BooseRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Boose save(Boose toSave) {
        entityManager.persist(toSave);
        return toSave;
    }

    public Optional<Boose> findById(int id) {
        return Optional.ofNullable(entityManager.find(Boose.class, id));
    }

    public List<Boose> findAll() {
        TypedQuery<Boose> query = entityManager.createQuery(
                "SELECT b FROM Boose b",
                Boose.class);
        return query.getResultList();
    }
}
