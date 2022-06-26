package hu.progmasters.servicebooker.service.examples;

import hu.progmasters.servicebooker.domain.entity.Booking;
import hu.progmasters.servicebooker.domain.entity.Boose;
import hu.progmasters.servicebooker.domain.entity.Customer;
import hu.progmasters.servicebooker.dto.booking.BookingInfo;
import hu.progmasters.servicebooker.dto.booking.BookingUpdateCommand;
import hu.progmasters.servicebooker.dto.boose.BooseInfo;
import hu.progmasters.servicebooker.dto.customer.CustomerInfo;

import java.time.LocalDateTime;

public class BookingExamples {

    public static final int JUN22_ID = 5;
    public static final LocalDateTime JUN22_START = LocalDateTime.parse("2022-06-22T10:00");
    public static final LocalDateTime JUN22_END = LocalDateTime.parse("2022-06-22T12:00");
    public static final String JUN22_COMMENT = "Might be a minute late.";

    public static final int JUN22LATER_ID = 6;
    public static final LocalDateTime JUN22LATER_START = LocalDateTime.parse("2022-06-22T12:00");
    public static final LocalDateTime JUN22LATER_END = LocalDateTime.parse("2022-06-22T14:00");
    public static final String JUN22LATER_COMMENT = "This will be my first time. My friends recommended you.";
    public static final String JUN22_UPDATED_COMMENT = "I was wrong, I can definitely arrive on time.";

    public static Booking jun22(Boose boose, Customer customer) {
        Booking booking = new Booking();
        booking.setId(JUN22_ID);
        booking.setStart(JUN22_START);
        booking.setEnd(JUN22_END);
        booking.setComment(JUN22_COMMENT);
        booking.setBoose(boose);
        booking.setCustomer(customer);
        return booking;
    }

    public static BookingInfo jun22Info(BooseInfo booseInfo, CustomerInfo customerInfo) {
        BookingInfo info = new BookingInfo();
        info.setId(JUN22_ID);
        info.setStart(JUN22_START);
        info.setEnd(JUN22_END);
        info.setComment(JUN22_COMMENT);
        info.setBoose(booseInfo);
        info.setCustomer(customerInfo);
        return info;
    }

    public static Booking jun22Later(Boose boose, Customer customer) {
        Booking booking = new Booking();
        booking.setId(JUN22LATER_ID);
        booking.setStart(JUN22LATER_START);
        booking.setEnd(JUN22LATER_END);
        booking.setComment(JUN22LATER_COMMENT);
        booking.setBoose(boose);
        booking.setCustomer(customer);
        return booking;
    }

    public static BookingUpdateCommand jun22UpdateCommand() {
        BookingUpdateCommand command = new BookingUpdateCommand();
        command.setComment(JUN22_UPDATED_COMMENT);
        return command;
    }

    public static BookingInfo jun22UpdatedInfo(BooseInfo booseInfo, CustomerInfo customerInfo) {
        BookingInfo info = jun22Info(booseInfo, customerInfo);
        info.setComment(JUN22_UPDATED_COMMENT);
        return info;
    }
}

