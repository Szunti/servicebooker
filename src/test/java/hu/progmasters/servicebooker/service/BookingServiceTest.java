package hu.progmasters.servicebooker.service;

import hu.progmasters.servicebooker.ServicebookerApplication;
import hu.progmasters.servicebooker.domain.entity.Booking;
import hu.progmasters.servicebooker.domain.entity.Boose;
import hu.progmasters.servicebooker.domain.entity.Customer;
import hu.progmasters.servicebooker.dto.booking.BookingInfo;
import hu.progmasters.servicebooker.dto.booking.BookingUpdateCommand;
import hu.progmasters.servicebooker.dto.boose.BooseInfo;
import hu.progmasters.servicebooker.dto.customer.CustomerInfo;
import hu.progmasters.servicebooker.exceptionhandling.booking.BookingNotByCustomerException;
import hu.progmasters.servicebooker.exceptionhandling.booking.BookingNotForBooseException;
import hu.progmasters.servicebooker.exceptionhandling.booking.BookingNotFoundException;
import hu.progmasters.servicebooker.repository.BookingRepository;
import hu.progmasters.servicebooker.util.interval.Interval;
import hu.progmasters.servicebooker.validation.DateTimeBoundChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static hu.progmasters.servicebooker.util.interval.Interval.interval;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    BookingRepository bookingRepository;

    @Mock
    BooseService booseService;

    @Mock
    CustomerService customerService;

    @Mock
    DateTimeBoundChecker dateTimeBoundChecker;

    ModelMapper modelMapper = new ServicebookerApplication().modelMapper();

    BookingService bookingService;

    @BeforeEach
    void constructService() {
        bookingService = new BookingService(bookingRepository, booseService, customerService,
                modelMapper, dateTimeBoundChecker);
    }

    @Test
    void findAllForBooseOrCustomer_boose() {
        Boose boose = exampleBoose();
        Customer customer = exampleCustomer();
        Booking firstBooking = exampleBooking(boose, customer);
        Booking secondBooking = anotherBooking(boose, customer);
        Interval<LocalDateTime> interval = interval(
                LocalDateTime.parse("2022-06-21T08:03"),
                LocalDateTime.parse("2022-06-29T11:22")
        );
        when(dateTimeBoundChecker.constrain(interval)).thenReturn(interval);
        when(booseService.getFromIdOrThrow(1)).thenReturn(boose);
        when(bookingRepository.findAllOrderedFor(boose, null, interval, false))
                .thenReturn(List.of(firstBooking, secondBooking));

        List<BookingInfo> bookingInfos = bookingService.findAllForBooseOrCustomer(1, null, interval);

        BookingInfo exampleBookingInfo = exampleBookingInfo();
        assertThat(bookingInfos).hasSize(2)
                .first()
                .isEqualTo(exampleBookingInfo);
    }

    @Test
    void findForBooseOrCustomerById_customer() {
        Boose boose = exampleBoose();
        Customer customer = exampleCustomer();
        Booking booking = exampleBooking(boose, customer);
        when(customerService.getFromIdOrThrow(2)).thenReturn(customer);
        when(bookingRepository.findById(5)).thenReturn(Optional.of(booking));

        BookingInfo bookingInfo = bookingService.findForBooseOrCustomerById(5, null, 2);

        BookingInfo exampleBookingInfo = exampleBookingInfo();
        assertThat(bookingInfo).isEqualTo(exampleBookingInfo);
    }

    @Test
    void findForBooseOrCustomerById_customer_notFound() {
        when(bookingRepository.findById(5)).thenReturn(Optional.empty());

        assertThatExceptionOfType(BookingNotFoundException.class).isThrownBy(() -> {
            bookingService.findForBooseOrCustomerById(5, null, 2);
        });
    }

    @Test
    void findForBooseOrCustomerById_notByCustomer() {
        Boose boose = exampleBoose();
        Customer customerOfBooking = exampleCustomer();
        Customer anotherCustomer = anotherCustomer();
        Booking booking = exampleBooking(boose, customerOfBooking);
        when(customerService.getFromIdOrThrow(3)).thenReturn(anotherCustomer);
        when(bookingRepository.findById(5)).thenReturn(Optional.of(booking));

        assertThatExceptionOfType(BookingNotByCustomerException.class).isThrownBy(() -> {
            BookingInfo bookingInfo = bookingService.findForBooseOrCustomerById(5, null, 3);
        });
    }

    @Test
    void updateForBooseOrCustomer_boose() {
        BookingUpdateCommand command = exampleBookingUpdateCommand();
        Boose boose = exampleBoose();
        Customer customer = exampleCustomer();
        Booking booking = exampleBooking(boose, customer);
        when(booseService.getFromIdOrThrow(1)).thenReturn(boose);
        when(bookingRepository.findById(5)).thenReturn(Optional.of(booking));

        BookingInfo bookingInfo = bookingService.updateForBooseOrCustomer(5, 1, null, command);

        assertThat(booking.getComment()).isEqualTo("I was wrong, I can definitely arrive on time.");
        BookingInfo updatedBookingInfo = updatedBookingInfo();
        assertThat(bookingInfo).isEqualTo(updatedBookingInfo);
    }

    @Test
    void updateForBooseOrCustomer_notForBoose() {
        BookingUpdateCommand command = exampleBookingUpdateCommand();
        Boose booseOfBooking = exampleBoose();
        Boose anotherBoose = anotherBoose();
        Customer customer = exampleCustomer();
        Booking booking = exampleBooking(booseOfBooking, customer);
        when(booseService.getFromIdOrThrow(2)).thenReturn(anotherBoose);
        when(bookingRepository.findById(5)).thenReturn(Optional.of(booking));

        assertThatExceptionOfType(BookingNotForBooseException.class).isThrownBy(() -> {
            bookingService.updateForBooseOrCustomer(5, 2, null, command);
        });
    }

    @Test
    void deleteForBooseOrCustomer_customer() {
        Boose boose = exampleBoose();
        Customer customer = exampleCustomer();
        Booking booking = exampleBooking(boose, customer);
        when(customerService.getFromIdOrThrow(2)).thenReturn(customer);
        when(bookingRepository.findById(5)).thenReturn(Optional.of(booking));

        BookingInfo bookingInfo = bookingService.deleteForBooseOrCustomer(5, null, 2);

        verify(bookingRepository).delete(booking);
        BookingInfo exampleBookingInfo = exampleBookingInfo();
        assertThat(bookingInfo).isEqualTo(exampleBookingInfo);
    }

    Boose exampleBoose() {
        Boose boose = new Boose();
        boose.setId(1);
        boose.setName("Hairdresser Lisa");
        boose.setDescription("I have a small shop on the Pearl Street.");
        boose.setDeleted(false);
        return boose;
    }

    BooseInfo exampleBooseInfo() {
        BooseInfo info = new BooseInfo();
        info.setId(1);
        info.setName("Hairdresser Lisa");
        info.setDescription("I have a small shop on the Pearl Street.");
        return info;
    }

    Boose anotherBoose() {
        Boose boose = new Boose();
        boose.setId(2);
        boose.setName("Cleaner Jack");
        boose.setDescription("Dust is my enemy.");
        boose.setDeleted(false);
        return boose;
    }

    Customer exampleCustomer() {
        Customer customer = new Customer();
        customer.setId(2);
        customer.setName("John Smith");
        customer.setEmail("john.smith@gmail.com");
        customer.setDeleted(false);
        return customer;
    }

    CustomerInfo exampleCustomerInfo() {
        CustomerInfo info = new CustomerInfo();
        info.setId(2);
        info.setName("John Smith");
        info.setEmail("john.smith@gmail.com");
        return info;
    }

    Customer anotherCustomer() {
        Customer customer = new Customer();
        customer.setId(3);
        customer.setName("Alice Tailor");
        customer.setEmail("alice.tailor@gmail.com");
        customer.setDeleted(false);
        return customer;
    }


    Booking exampleBooking(Boose boose, Customer customer) {
        Booking booking = new Booking();
        booking.setId(5);
        booking.setStart(LocalDateTime.parse("2022-06-22T10:00"));
        booking.setEnd(LocalDateTime.parse("2022-06-22T12:00"));
        booking.setComment("Might be a minute late.");
        booking.setBoose(boose);
        booking.setCustomer(customer);
        return booking;
    }

    BookingInfo exampleBookingInfo() {
        BookingInfo info = new BookingInfo();
        info.setId(5);
        info.setStart(LocalDateTime.parse("2022-06-22T10:00"));
        info.setEnd(LocalDateTime.parse("2022-06-22T12:00"));
        info.setComment("Might be a minute late.");
        info.setBoose(exampleBooseInfo());
        info.setCustomer(exampleCustomerInfo());
        return info;
    }

    Booking anotherBooking(Boose boose, Customer customer) {
        Booking booking = new Booking();
        booking.setId(6);
        booking.setStart(LocalDateTime.parse("2022-06-22T12:00"));
        booking.setEnd(LocalDateTime.parse("2022-06-22T14:00"));
        booking.setComment("This will be my first time. My friends recommended you.");
        booking.setBoose(boose);
        booking.setCustomer(customer);
        return booking;
    }

    BookingUpdateCommand exampleBookingUpdateCommand() {
        BookingUpdateCommand command = new BookingUpdateCommand();
        command.setComment("I was wrong, I can definitely arrive on time.");
        return command;
    }

    BookingInfo updatedBookingInfo() {
        BookingInfo info = exampleBookingInfo();
        info.setComment("I was wrong, I can definitely arrive on time.");
        return info;
    }
}