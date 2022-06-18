package hu.progmasters.servicebooker.controller;

import hu.progmasters.servicebooker.dto.customer.CustomerCreateCommand;
import hu.progmasters.servicebooker.dto.customer.CustomerInfo;
import hu.progmasters.servicebooker.exceptionhandling.boose.NoSuchBooseException;
import hu.progmasters.servicebooker.exceptionhandling.controller.CustomerNotFoundException;
import hu.progmasters.servicebooker.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerInfo save(@Valid @RequestBody CustomerCreateCommand command) {
        return customerService.save(command);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CustomerInfo> findAll() {
        return customerService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerInfo findById(@PathVariable("id") int id) {
        try {
            return customerService.findById(id);
        } catch (NoSuchBooseException exception) {
            throw new CustomerNotFoundException(exception);
        }
    }
}
