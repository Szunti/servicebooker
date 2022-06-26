package hu.progmasters.servicebooker.service;

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
import hu.progmasters.servicebooker.util.interval.Interval;
import hu.progmasters.servicebooker.validation.DateTimeBoundChecker;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static hu.progmasters.servicebooker.util.interval.Interval.interval;

@Service
public class BookerService {

    private final BookingRepository repository;
    private final BooseService booseService;
    private final CustomerService customerService;
    private final TimeTableService timeTableService;

    private final ModelMapper modelMapper;

    private final DateTimeBoundChecker dateTimeBoundChecker;

    public BookerService(BookingRepository repository,
                         BooseService booseService,
                         CustomerService customerService,
                         TimeTableService timeTableService,
                         ModelMapper modelMapper,
                         DateTimeBoundChecker dateTimeBoundChecker) {
        this.repository = repository;
        this.booseService = booseService;
        this.customerService = customerService;
        this.timeTableService = timeTableService;
        this.modelMapper = modelMapper;
        this.dateTimeBoundChecker = dateTimeBoundChecker;
    }

    @Transactional
    public BookingInfo save(int customerId, BookingCreateCommand command) {
        Interval<LocalDateTime> interval = interval(command.getStart(), command.getEnd());
        dateTimeBoundChecker.checkInBound(interval);
        Customer customer = customerService.getFromIdOrThrow(customerId);
        Boose boose;
        try {
            boose = booseService.getFromIdOrThrow(command.getBooseId());
        } catch (BooseNotFoundException exception) {
            throw new NoSuchBooseException(exception);
        }
        Booking toSave = modelMapper.map(command, Booking.class);
        toSave.setCustomer(customer);
        toSave.setBoose(boose);

        booseService.lockForUpdate(boose);
        // the part of the timeTable where periods intersect the booking
        List<TablePeriod> timeTable = timeTableService.getTimeTableStreamForBoose(boose, interval, true)
                .collect(Collectors.toList());
        // timeTable should be a singe period with the same start and end as command and not booked already

        TablePeriod period = getPeriodMatchingBooking(toSave, timeTable);

        if (period.getBooking() != null) {
            throw new AlreadyBookedException(period);
        }
        Booking saved = repository.save(toSave);
        return toDto(saved);
    }

    private TablePeriod getPeriodMatchingBooking(Booking booking, List<TablePeriod> timeTable) {
        if (timeTable.size() == 1) {
            TablePeriod period = timeTable.get(0);
            if (period.getStart().equals(booking.getStart()) && period.getEnd().equals(booking.getEnd())) {
                return period;
            }
        }
        throw new BookingNotAvailablePeriodException(booking);
    }

    private BookingInfo toDto(Booking booking) {
        return modelMapper.map(booking, BookingInfo.class);
    }
}
