package hu.progmasters.servicebooker.service;

import hu.progmasters.servicebooker.domain.entity.Customer;
import hu.progmasters.servicebooker.dto.customer.CustomerCreateCommand;
import hu.progmasters.servicebooker.dto.customer.CustomerInfo;
import hu.progmasters.servicebooker.dto.customer.CustomerUpdateCommand;
import hu.progmasters.servicebooker.exceptionhandling.customer.CustomerNotFoundException;
import hu.progmasters.servicebooker.repository.CustomerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository repository;

    private final ModelMapper modelMapper;

    public CustomerService(CustomerRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public CustomerInfo save(CustomerCreateCommand command) {
        Customer toSave = modelMapper.map(command, Customer.class);
        Customer saved = repository.save(toSave);
        return toDto(saved);
    }

    @Transactional
    public CustomerInfo findById(int id) {
        Customer customer = getFromIdOrThrow(id);
        return toDto(customer);
    }

    public Customer getFromIdOrThrow(int id) {
        return repository.findById(id).orElseThrow(
                () -> new CustomerNotFoundException(id)
        );
    }

    @Transactional
    public List<CustomerInfo> findAll() {
        return repository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CustomerInfo update(int id, CustomerUpdateCommand command) {
        Customer customer = getFromIdOrThrow(id);
        modelMapper.map(command, customer);
        return toDto(customer);
    }

    @Transactional
    public CustomerInfo delete(int id) {
        Customer customer = getFromIdOrThrow(id);
        customer.setDeleted(true);
        return toDto(customer);
    }

    private CustomerInfo toDto(Customer customer) {
        return modelMapper.map(customer, CustomerInfo.class);
    }
}
