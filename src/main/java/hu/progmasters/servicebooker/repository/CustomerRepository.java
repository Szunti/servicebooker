package hu.progmasters.servicebooker.repository;

import hu.progmasters.servicebooker.domain.entity.Customer;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
        return Optional.ofNullable(entityManager.find(Customer.class, id));
    }

    public List<Customer> findAll() {
        return entityManager.createQuery("SELECT c FROM Customer c", Customer.class)
                .getResultList();
    }
}
