package hu.progmasters.servicebooker.repository;

import hu.progmasters.servicebooker.domain.entity.Boose;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class BooseRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Boose save(Boose toSave) {
        entityManager.persist(toSave);
        return toSave;
    }

    public Optional<Boose> findById(int id) {
        TypedQuery<Boose> query = entityManager.createQuery("SELECT b FROM Boose b " +
                        "WHERE b.id = :id AND b.deleted = FALSE", Boose.class)
                .setParameter("id", id);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException exception) {
            return Optional.empty();
        }
    }

    public List<Boose> findAll() {
        return entityManager.createQuery("SELECT b FROM Boose b WHERE b.deleted = FALSE", Boose.class)
                .getResultList();
    }

    public void lockForUpdate(Boose boose) {
        // optimistic lock doesn't work, because inserting rows referencing the boose
        // puts a read lock on the boose row in MySQL and the version update can deadlock
        entityManager.lock(boose, LockModeType.PESSIMISTIC_WRITE);
    }
}
