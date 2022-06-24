package hu.progmasters.servicebooker.service;

import hu.progmasters.servicebooker.ServicebookerApplication;
import hu.progmasters.servicebooker.domain.entity.Customer;
import hu.progmasters.servicebooker.dto.customer.CustomerCreateCommand;
import hu.progmasters.servicebooker.dto.customer.CustomerInfo;
import hu.progmasters.servicebooker.dto.customer.CustomerUpdateCommand;
import hu.progmasters.servicebooker.exceptionhandling.customer.CustomerNotFoundException;
import hu.progmasters.servicebooker.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    CustomerRepository customerRepository;

    ModelMapper modelMapper = new ServicebookerApplication().modelMapper();

    CustomerService customerService;

    @BeforeEach
    void constructService() {
        customerService = new CustomerService(customerRepository, modelMapper);
    }

    @Test
    void save() {
        CustomerCreateCommand command = exampleCustomerCreateCommand();
        Customer customer = exampleNewCustomer();
        Customer savedCustomer = exampleSavedCustomer();
        when(customerRepository.save(customer)).thenReturn(savedCustomer);

        CustomerInfo customerInfo = customerService.save(command);

        verify(customerRepository).save(customer);
        CustomerInfo exampleCustomerInfo = exampleCustomerInfo();
        assertThat(customerInfo).isEqualTo(exampleCustomerInfo);
    }

    @Test
    void findById() {
        Customer savedCustomer = exampleSavedCustomer();
        when(customerRepository.findById(1)).thenReturn(Optional.of(savedCustomer));

        CustomerInfo customerInfo = customerService.findById(1);

        CustomerInfo exampleCustomerInfo = exampleCustomerInfo();
        assertThat(customerInfo).isEqualTo(exampleCustomerInfo);
    }

    @Test
    void findById_notFound() {
        when(customerRepository.findById(1)).thenReturn(Optional.empty());

        assertThatExceptionOfType(CustomerNotFoundException.class).isThrownBy(() -> {
            customerService.findById(1);
        });
    }

    @Test
    void findAll() {
        Customer firstCustomer = exampleSavedCustomer();
        Customer secondCustomer = anotherSavedCustomer();
        when(customerRepository.findAll()).thenReturn(List.of(firstCustomer, secondCustomer));

        List<CustomerInfo> customerInfos = customerService.findAll();

        CustomerInfo exampleCustomerInfo = exampleCustomerInfo();
        assertThat(customerInfos).hasSize(2)
                .first()
                .isEqualTo(exampleCustomerInfo);
    }

    @Test
    void update() {
        CustomerUpdateCommand command = exampleCustomerUpdateCommand();
        Customer customer = exampleSavedCustomer();
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));

        CustomerInfo customerInfo = customerService.update(1, command);

        CustomerInfo updatedInfo = updatedCustomerInfo();
        assertThat(customerInfo).isEqualTo(updatedInfo);
    }


    @Test
    void update_notFound() {
        CustomerUpdateCommand command = exampleCustomerUpdateCommand();
        when(customerRepository.findById(1)).thenReturn(Optional.empty());

        assertThatExceptionOfType(CustomerNotFoundException.class).isThrownBy(() -> {
            customerService.update(1, command);
        });
    }

    @Test
    void delete() {
        Customer customer = exampleSavedCustomer();
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));

        CustomerInfo customerInfo = customerService.delete(1);

        assertThat(customer.isDeleted()).isTrue();
        CustomerInfo exampleCustomerInfo = exampleCustomerInfo();
        assertThat(customerInfo).isEqualTo(exampleCustomerInfo);
    }

    @Test
    void delete_notFound() {
        when(customerRepository.findById(1)).thenReturn(Optional.empty());

        assertThatExceptionOfType(CustomerNotFoundException.class).isThrownBy(() -> {
            customerService.delete(1);
        });
    }

    CustomerCreateCommand exampleCustomerCreateCommand() {
        CustomerCreateCommand command = new CustomerCreateCommand();
        command.setName("John Smith");
        command.setEmail("john.smith@gmail.com");
        return command;
    }

    CustomerUpdateCommand exampleCustomerUpdateCommand() {
        CustomerUpdateCommand command = new CustomerUpdateCommand();
        command.setName("Joseph Smith");
        command.setEmail("joseph.smith@gmail.com");
        return command;
    }

    Customer exampleNewCustomer() {
        Customer customer = new Customer();
        customer.setId(null);
        customer.setName("John Smith");
        customer.setEmail("john.smith@gmail.com");
        customer.setDeleted(false);
        return customer;
    }

    Customer exampleSavedCustomer() {
        Customer customer = exampleNewCustomer();
        customer.setId(1);
        return customer;
    }

    CustomerInfo exampleCustomerInfo() {
        CustomerInfo info = new CustomerInfo();
        info.setId(1);
        info.setName("John Smith");
        info.setEmail("john.smith@gmail.com");
        return info;
    }

    Customer anotherSavedCustomer() {
        Customer customer = new Customer();
        customer.setId(2);
        customer.setName("Alice Tailor");
        customer.setEmail("alice.tailor@gmail.com");
        customer.setDeleted(false);
        return customer;
    }

    CustomerInfo updatedCustomerInfo() {
        CustomerInfo info = exampleCustomerInfo();
        info.setName("Joseph Smith");
        info.setEmail("joseph.smith@gmail.com");
        return info;
    }


}