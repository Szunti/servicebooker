package hu.progmasters.servicebooker.service;

import hu.progmasters.servicebooker.ServicebookerApplication;
import hu.progmasters.servicebooker.domain.entity.Customer;
import hu.progmasters.servicebooker.dto.customer.CustomerCreateCommand;
import hu.progmasters.servicebooker.dto.customer.CustomerInfo;
import hu.progmasters.servicebooker.dto.customer.CustomerUpdateCommand;
import hu.progmasters.servicebooker.exceptionhandling.customer.CustomerNotFoundException;
import hu.progmasters.servicebooker.repository.CustomerRepository;
import hu.progmasters.servicebooker.service.examples.CustomerExamples;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        CustomerCreateCommand command = CustomerExamples.johnCreateCommand();
        Customer newCustomer = CustomerExamples.johnNew();
        Customer savedCustomer = CustomerExamples.john();
        when(customerRepository.save(newCustomer)).thenReturn(savedCustomer);

        CustomerInfo customerInfo = customerService.save(command);

        verify(customerRepository).save(newCustomer);
        CustomerInfo expectedCustomerInfo = CustomerExamples.johnInfo();
        assertThat(customerInfo).isEqualTo(expectedCustomerInfo);
    }

    @Test
    void findAll() {
        Customer firstCustomer = CustomerExamples.john();
        Customer secondCustomer = CustomerExamples.alice();
        when(customerRepository.findAll()).thenReturn(List.of(firstCustomer, secondCustomer));

        List<CustomerInfo> customerInfos = customerService.findAll();

        CustomerInfo firstCustomerInfo = CustomerExamples.johnInfo();
        assertThat(customerInfos).hasSize(2)
                .first()
                .isEqualTo(firstCustomerInfo);
    }

    @Test
    void findById() {
        Customer customer = CustomerExamples.john();
        when(customerRepository.findById(CustomerExamples.JOHN_ID)).thenReturn(Optional.of(customer));

        CustomerInfo customerInfo = customerService.findById(CustomerExamples.JOHN_ID);

        CustomerInfo expectedCustomerInfo = CustomerExamples.johnInfo();
        assertThat(customerInfo).isEqualTo(expectedCustomerInfo);
    }

    @Test
    void findById_notFound() {
        when(customerRepository.findById(21)).thenReturn(Optional.empty());

        assertThatExceptionOfType(CustomerNotFoundException.class).isThrownBy(() -> {
            customerService.findById(21);
        });
    }

    @Test
    void update() {
        CustomerUpdateCommand command = CustomerExamples.johnUpdateCommand();
        Customer customer = CustomerExamples.john();
        when(customerRepository.findById(CustomerExamples.JOHN_ID)).thenReturn(Optional.of(customer));

        CustomerInfo customerInfo = customerService.update(CustomerExamples.JOHN_ID, command);

        assertThat(customer.getName()).isEqualTo(CustomerExamples.JOHN_UPDATED_NAME);
        assertThat(customer.getEmail()).isEqualTo(CustomerExamples.JOHN_UPDATED_EMAIL);
        CustomerInfo updatedCustomerInfo = CustomerExamples.johnUpdatedInfo();
        assertThat(customerInfo).isEqualTo(updatedCustomerInfo);
    }


    @Test
    void update_notFound() {
        CustomerUpdateCommand command = CustomerExamples.johnUpdateCommand();
        when(customerRepository.findById(21)).thenReturn(Optional.empty());

        assertThatExceptionOfType(CustomerNotFoundException.class).isThrownBy(() -> {
            customerService.update(21, command);
        });
    }

    @Test
    void delete() {
        Customer customer = CustomerExamples.john();
        when(customerRepository.findById(CustomerExamples.JOHN_ID)).thenReturn(Optional.of(customer));

        CustomerInfo customerInfo = customerService.delete(CustomerExamples.JOHN_ID);

        assertThat(customer.isDeleted()).isTrue();
        CustomerInfo deletedCustomerInfo = CustomerExamples.johnInfo();
        assertThat(customerInfo).isEqualTo(deletedCustomerInfo);
    }

    @Test
    void delete_notFound() {
        when(customerRepository.findById(21)).thenReturn(Optional.empty());

        assertThatExceptionOfType(CustomerNotFoundException.class).isThrownBy(() -> {
            customerService.delete(21);
        });
    }
}