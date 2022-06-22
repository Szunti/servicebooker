package hu.progmasters.servicebooker.repository;

import hu.progmasters.servicebooker.domain.entity.Customer;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class CustomerRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Customer save(Customer toSave) {
        entityManager.persist(toSave);
        return toSave;
    }

    public Optional<Customer> findById(int id) {
        TypedQuery<Customer> query = entityManager.createQuery("SELECT c FROM Customer c " +
                "WHERE c.id = :id AND c.deleted = FALSE", Customer.class)
                .setParameter("id", id);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException exception) {
            return Optional.empty();
        }
    }

    public List<Customer> findAll() {
        return entityManager.createQuery("SELECT c FROM Customer c WHERE c.deleted = FALSE", Customer.class)
                .getResultList();
    }
}
