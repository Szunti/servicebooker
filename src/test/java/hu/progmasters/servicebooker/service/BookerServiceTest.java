package hu.progmasters.servicebooker.service;

import hu.progmasters.servicebooker.ServicebookerApplication;
import hu.progmasters.servicebooker.domain.TablePeriod;
import hu.progmasters.servicebooker.domain.entity.Booking;
import hu.progmasters.servicebooker.domain.entity.Boose;
import hu.progmasters.servicebooker.domain.entity.Customer;
import hu.progmasters.servicebooker.dto.booking.BookingCreateCommand;
import hu.progmasters.servicebooker.dto.booking.BookingInfo;
import hu.progmasters.servicebooker.exceptionhandling.booking.AlreadyBookedException;
import hu.progmasters.servicebooker.exceptionhandling.booking.BookingNotAvailablePeriodException;
import hu.progmasters.servicebooker.exceptionhandling.booking.NoSuchBooseException;
import hu.progmasters.servicebooker.exceptionhandling.boose.BooseNotFoundException;
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
import java.util.stream.Stream;

import static hu.progmasters.servicebooker.util.interval.Interval.interval;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookerServiceTest {

    @Mock
    BookingRepository bookingRepository;

    @Mock
    BooseService booseService;

    @Mock
    CustomerService customerService;

    @Mock
    TimeTableService timeTableService;

    ModelMapper modelMapper = new ServicebookerApplication().modelMapper();

    @Mock
    DateTimeBoundChecker dateTimeBoundChecker;

    BookerService bookerService;

    Boose boose = BooseExamples.hairdresser();
    Customer customer = CustomerExamples.john();

    @BeforeEach
    void constructService() {
        bookerService = new BookerService(
                bookingRepository, booseService, customerService, timeTableService, modelMapper, dateTimeBoundChecker);
    }

    @BeforeEach
    void setUpStubbing() {
        when(booseService.getFromIdOrThrow(boose.getId())).thenReturn(boose);
        when(customerService.getFromIdOrThrow(customer.getId())).thenReturn(customer);
    }

    @Test
    void save() {
        BookingCreateCommand command = BookingExamples.jun22CreateCommand(boose.getId());
        Booking newBooking = BookingExamples.jun22New(boose, customer);
        Booking savedBooking = BookingExamples.jun22(boose, customer);
        when(bookingRepository.save(newBooking)).thenReturn(savedBooking);
        addFreePeriod(command.getStart(), command.getEnd(), "bookable");

        BookingInfo bookingInfo = bookerService.save(customer.getId(), command);

        verify(bookingRepository).save(newBooking);
        assertThat(bookingInfo)
                .isEqualTo(BookingExamples.jun22Info(BooseExamples.hairdresserInfo(), CustomerExamples.johnInfo()));
    }

    @Test
    void save_booseNotFound() {
        BookingCreateCommand command = BookingExamples.jun22CreateCommand(boose.getId());
        when(booseService.getFromIdOrThrow(boose.getId())).thenThrow(new BooseNotFoundException(57));

        assertThatExceptionOfType(NoSuchBooseException.class).isThrownBy(() -> {
            bookerService.save(customer.getId(), command);
        });
    }

    @Test
    void save_alreadyBooked() {
        BookingCreateCommand command = BookingExamples.jun22CreateCommand(boose.getId());
        Booking existingBooking = BookingExamples.jun22(boose, customer);
        addBookedPeriod("existing", existingBooking);

        assertThatExceptionOfType(AlreadyBookedException.class).isThrownBy(() -> {
            bookerService.save(customer.getId(), command);
        });
    }

    @Test
    void save_noBookablePeriod() {
        BookingCreateCommand command = BookingExamples.jun22CreateCommand(boose.getId());

        assertThatExceptionOfType(BookingNotAvailablePeriodException.class).isThrownBy(() -> {
            bookerService.save(customer.getId(), command);
        });
    }

    TablePeriod addFreePeriod(LocalDateTime start, LocalDateTime end, String comment) {
        TablePeriod period = new TablePeriod(start, end, comment);
        when(timeTableService.getTimeTableStreamForBoose(boose, interval(start, end), true))
                .thenReturn(Stream.of(period));
        return period;
    }

    TablePeriod addBookedPeriod(String comment, Booking booking) {
        TablePeriod period = new TablePeriod(booking.getStart(), booking.getEnd(), comment, booking);
        Interval<LocalDateTime> bookingInterval = interval(booking.getStart(), booking.getEnd());
        when(timeTableService.getTimeTableStreamForBoose(boose, bookingInterval, true))
                .thenReturn(Stream.of(period));
        return period;
    }
}