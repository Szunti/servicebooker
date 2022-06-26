package hu.progmasters.servicebooker.service;

import hu.progmasters.servicebooker.ServicebookerApplication;
import hu.progmasters.servicebooker.domain.entity.Booking;
import hu.progmasters.servicebooker.domain.entity.Boose;
import hu.progmasters.servicebooker.domain.entity.Customer;
import hu.progmasters.servicebooker.dto.booking.BookingInfo;
import hu.progmasters.servicebooker.dto.booking.BookingUpdateCommand;
import hu.progmasters.servicebooker.exceptionhandling.booking.BookingNotByCustomerException;
import hu.progmasters.servicebooker.exceptionhandling.booking.BookingNotForBooseException;
import hu.progmasters.servicebooker.exceptionhandling.booking.BookingNotFoundException;
import hu.progmasters.servicebooker.repository.BookingRepository;
import hu.progmasters.servicebooker.service.examples.BookingExamples;
import hu.progmasters.servicebooker.service.examples.BooseExamples;
import hu.progmasters.servicebooker.service.examples.CustomerExamples;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        Boose boose = BooseExamples.hairdresser();
        Customer customer = CustomerExamples.john();
        Booking firstBooking = BookingExamples.jun22(boose, customer);
        Booking secondBooking = BookingExamples.jun22Later(boose, customer);
        Interval<LocalDateTime> interval = interval(
                LocalDateTime.parse("2022-06-21T08:03"),
                LocalDateTime.parse("2022-06-29T11:22")
        );
        when(dateTimeBoundChecker.constrain(interval)).thenReturn(interval);
        when(booseService.getFromIdOrThrow(BooseExamples.HAIRDRESSER_ID)).thenReturn(boose);
        when(bookingRepository.findAllOrderedFor(boose, null, interval, false))
                .thenReturn(List.of(firstBooking, secondBooking));

        List<BookingInfo> bookingInfos =
                bookingService.findAllForBooseOrCustomer(BooseExamples.HAIRDRESSER_ID, null, interval);

        BookingInfo expectedBookingInfo =
                BookingExamples.jun22Info(BooseExamples.hairdresserInfo(), CustomerExamples.johnInfo());
        assertThat(bookingInfos).hasSize(2)
                .first()
                .isEqualTo(expectedBookingInfo);
    }

    @Test
    void findForBooseOrCustomerById_customer() {
        Boose boose = BooseExamples.hairdresser();
        Customer customer = CustomerExamples.john();
        Booking booking = BookingExamples.jun22(boose, customer);
        when(customerService.getFromIdOrThrow(CustomerExamples.JOHN_ID)).thenReturn(customer);
        when(bookingRepository.findById(BookingExamples.JUN22_ID)).thenReturn(Optional.of(booking));

        BookingInfo bookingInfo = bookingService.findForBooseOrCustomerById(
                BookingExamples.JUN22_ID, null, CustomerExamples.JOHN_ID);

        BookingInfo expectedBookingInfo =
                BookingExamples.jun22Info(BooseExamples.hairdresserInfo(), CustomerExamples.johnInfo());
        assertThat(bookingInfo).isEqualTo(expectedBookingInfo);
    }

    @Test
    void findForBooseOrCustomerById_customer_notFound() {
        when(bookingRepository.findById(58)).thenReturn(Optional.empty());

        assertThatExceptionOfType(BookingNotFoundException.class).isThrownBy(() -> {
            bookingService.findForBooseOrCustomerById(58, null, 2);
        });
    }

    @Test
    void findForBooseOrCustomerById_notByCustomer() {
        Boose boose = BooseExamples.hairdresser();
        Customer customerOfBooking = CustomerExamples.john();
        Customer anotherCustomer = CustomerExamples.alice();
        Booking booking = BookingExamples.jun22(boose, customerOfBooking);
        when(customerService.getFromIdOrThrow(CustomerExamples.ALICE_ID)).thenReturn(anotherCustomer);
        when(bookingRepository.findById(BookingExamples.JUN22_ID)).thenReturn(Optional.of(booking));

        assertThatExceptionOfType(BookingNotByCustomerException.class).isThrownBy(() -> {
            BookingInfo bookingInfo = bookingService.findForBooseOrCustomerById(
                    BookingExamples.JUN22_ID, null, CustomerExamples.ALICE_ID);
        });
    }

    @Test
    void updateForBooseOrCustomer_boose() {
        BookingUpdateCommand command = BookingExamples.jun22UpdateCommand();
        Boose boose = BooseExamples.hairdresser();
        Customer customer = CustomerExamples.john();
        Booking booking = BookingExamples.jun22(boose, customer);
        when(booseService.getFromIdOrThrow(BooseExamples.HAIRDRESSER_ID)).thenReturn(boose);
        when(bookingRepository.findById(BookingExamples.JUN22_ID)).thenReturn(Optional.of(booking));

        BookingInfo bookingInfo = bookingService.updateForBooseOrCustomer(
                BookingExamples.JUN22_ID, BooseExamples.HAIRDRESSER_ID, null, command);

        assertThat(booking.getComment()).isEqualTo(BookingExamples.JUN22_UPDATED_COMMENT);
        BookingInfo updatedBookingInfo =
                BookingExamples.jun22UpdatedInfo(BooseExamples.hairdresserInfo(), CustomerExamples.johnInfo());
        assertThat(bookingInfo).isEqualTo(updatedBookingInfo);
    }

    @Test
    void updateForBooseOrCustomer_notForBoose() {
        BookingUpdateCommand command = BookingExamples.jun22UpdateCommand();
        Boose booseOfBooking = BooseExamples.hairdresser();
        Boose anotherBoose = BooseExamples.cleaner();
        Customer customer = CustomerExamples.john();
        Booking booking = BookingExamples.jun22(booseOfBooking, customer);
        when(booseService.getFromIdOrThrow(BooseExamples.CLEANER_ID)).thenReturn(anotherBoose);
        when(bookingRepository.findById(BookingExamples.JUN22_ID)).thenReturn(Optional.of(booking));

        assertThatExceptionOfType(BookingNotForBooseException.class).isThrownBy(() -> {
            bookingService.updateForBooseOrCustomer(
                    BookingExamples.JUN22_ID, BooseExamples.CLEANER_ID, null, command);
        });
    }

    @Test
    void deleteForBooseOrCustomer_customer() {
        Boose boose = BooseExamples.hairdresser();
        Customer customer = CustomerExamples.john();
        Booking booking = BookingExamples.jun22(boose, customer);
        when(customerService.getFromIdOrThrow(CustomerExamples.JOHN_ID)).thenReturn(customer);
        when(bookingRepository.findById(BookingExamples.JUN22_ID)).thenReturn(Optional.of(booking));

        BookingInfo bookingInfo = bookingService.deleteForBooseOrCustomer(
                BookingExamples.JUN22_ID, null, CustomerExamples.JOHN_ID);

        verify(bookingRepository).delete(booking);
        BookingInfo deletedBookingInfo =
                BookingExamples.jun22Info(BooseExamples.hairdresserInfo(), CustomerExamples.johnInfo());
        assertThat(bookingInfo).isEqualTo(deletedBookingInfo);
    }
}