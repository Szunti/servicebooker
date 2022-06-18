package hu.progmasters.servicebooker.repository;

import hu.progmasters.servicebooker.domain.entity.Boose;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
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
        return entityManager.createQuery("SELECT b FROM Boose b", Boose.class)
            .getResultList();
    }

    public void lockForUpdate(Boose boose) {
        // optimistic lock doesn't work, because inserting rows referencing the boose
        // puts a read lock on the boose row in MySQL and the version update can deadlock
        entityManager.lock(boose, LockModeType.PESSIMISTIC_WRITE);
    }
}
