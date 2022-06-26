package hu.progmasters.servicebooker.service.examples;

import hu.progmasters.servicebooker.domain.entity.Customer;
import hu.progmasters.servicebooker.dto.customer.CustomerCreateCommand;
import hu.progmasters.servicebooker.dto.customer.CustomerInfo;
import hu.progmasters.servicebooker.dto.customer.CustomerUpdateCommand;

public class CustomerExamples {

    public static final int JOHN_ID = 1;
    public static final String JOHN_NAME = "John Smith";
    public static final String JOHN_EMAIL = "john.smith@gmail.com";
    public static final String JOHN_UPDATED_NAME = "Joseph Smith";
    public static final String JOHN_UPDATED_EMAIL = "joseph.smith@gmail.com";

    public static final int ALICE_ID = 2;
    public static final String ALICE_NAME = "Alice Tailor";
    public static final String ALICE_EMAIL = "alice.tailor@gmail.com";

    public static CustomerCreateCommand johnCreateCommand() {
        CustomerCreateCommand command = new CustomerCreateCommand();
        command.setName(JOHN_NAME);
        command.setEmail(JOHN_EMAIL);
        return command;
    }

    public static Customer johnNew() {
        Customer customer = new Customer();
        customer.setId(null);
        customer.setName(JOHN_NAME);
        customer.setEmail(JOHN_EMAIL);
        customer.setDeleted(false);
        return customer;
    }

    public static Customer john() {
        Customer customer = johnNew();
        customer.setId(JOHN_ID);
        return customer;
    }

    public static CustomerInfo johnInfo() {
        CustomerInfo info = new CustomerInfo();
        info.setId(JOHN_ID);
        info.setName(JOHN_NAME);
        info.setEmail(JOHN_EMAIL);
        return info;
    }

    public static Customer alice() {
        Customer customer = new Customer();
        customer.setId(ALICE_ID);
        customer.setName(ALICE_NAME);
        customer.setEmail(ALICE_EMAIL);
        customer.setDeleted(false);
        return customer;
    }

    public static CustomerUpdateCommand johnUpdateCommand() {
        CustomerUpdateCommand command = new CustomerUpdateCommand();
        command.setName(JOHN_UPDATED_NAME);
        command.setEmail(JOHN_UPDATED_EMAIL);
        return command;
    }

    public static CustomerInfo johnUpdatedInfo() {
        CustomerInfo info = johnInfo();
        info.setName(JOHN_UPDATED_NAME);
        info.setEmail(JOHN_UPDATED_EMAIL);
        return info;
    }
}
