package hu.progmasters.servicebooker.service;

import hu.progmasters.servicebooker.domain.entity.Customer;
import hu.progmasters.servicebooker.dto.customer.CustomerCreateCommand;
import hu.progmasters.servicebooker.dto.customer.CustomerInfo;
import hu.progmasters.servicebooker.exceptionhandling.customer.NoSuchCustomerException;
import hu.progmasters.servicebooker.repository.CustomerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository repository;

    private final ModelMapper modelMapper;

    public CustomerService(CustomerRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    public CustomerInfo save(CustomerCreateCommand command) {
        Customer toSave = modelMapper.map(command, Customer.class);
        Customer saved = repository.save(toSave);
        return modelMapper.map(saved, CustomerInfo.class);
    }

    public CustomerInfo findById(int id) {
        Customer customer = getFromIdOrThrow(id);
        return modelMapper.map(customer, CustomerInfo.class);
    }

    public List<CustomerInfo> findAll() {
        return repository.findAll().stream()
                .map(customer -> modelMapper.map(customer, CustomerInfo.class))
                .collect(Collectors.toList());
    }

    public Customer getFromIdOrThrow(int id) {
        return repository.findById(id).orElseThrow(
                () -> new NoSuchCustomerException(id)
        );
    }
}
