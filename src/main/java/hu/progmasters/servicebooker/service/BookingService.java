package hu.progmasters.servicebooker.service;

import hu.progmasters.servicebooker.domain.entity.Booking;
import hu.progmasters.servicebooker.domain.entity.Boose;
import hu.progmasters.servicebooker.domain.entity.Customer;
import hu.progmasters.servicebooker.dto.booking.BookingInfo;
import hu.progmasters.servicebooker.exceptionhandling.booking.BookingNotByCustomerException;
import hu.progmasters.servicebooker.exceptionhandling.booking.BookingNotForBooseException;
import hu.progmasters.servicebooker.exceptionhandling.booking.NoSuchBookingException;
import hu.progmasters.servicebooker.repository.BookingRepository;
import hu.progmasters.servicebooker.util.interval.Interval;
import hu.progmasters.servicebooker.validation.DateTimeBoundChecker;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository repository;
    private final BooseService booseService;
    private final CustomerService customerService;

    private final ModelMapper modelMapper;

    private final DateTimeBoundChecker dateTimeBoundChecker;

    public BookingService(BookingRepository repository,
                          BooseService booseService,
                          CustomerService customerService,
                          ModelMapper modelMapper, DateTimeBoundChecker dateTimeBoundChecker) {
        this.repository = repository;
        this.booseService = booseService;
        this.customerService = customerService;
        this.modelMapper = modelMapper;
        this.dateTimeBoundChecker = dateTimeBoundChecker;
    }

    @Transactional
    public BookingInfo findForBooseById(int booseId, int id) {
        Booking booking = getForBooseByIdOrThrow(booseId, id);
        return modelMapper.map(booking, BookingInfo.class);
    }


    @Transactional
    public BookingInfo findForCustomerById(int customerId, int id) {
        Booking booking = getForCustomerByIdOrThrow(customerId, id);
        return modelMapper.map(booking, BookingInfo.class);
    }

    @Transactional
    public List<BookingInfo> findAllForBoose(Integer booseId, Interval<LocalDateTime> interval) {
        Interval<LocalDateTime> constrainedInterval = dateTimeBoundChecker.constrain(interval);
        Boose boose = booseService.getFromIdOrThrow(booseId);
        return getAllForBoose(boose, constrainedInterval, false).stream()
                .map(booking -> modelMapper.map(booking, BookingInfo.class))
                .collect(Collectors.toList());
    }

    public List<Booking> getAllForBoose(Boose boose, Interval<LocalDateTime> interval, boolean lock) {
        return repository.findAllOrderedFor(boose, null, interval, lock);
    }

    @Transactional
    public List<BookingInfo> findAllForCustomer(Integer customerId, Interval<LocalDateTime> interval) {
        Interval<LocalDateTime> constrainedInterval = dateTimeBoundChecker.constrain(interval);
        Customer customer = customerService.getFromIdOrThrow(customerId);
        return repository.findAllOrderedFor(null, customer, constrainedInterval, false).stream()
                .map(booking -> modelMapper.map(booking, BookingInfo.class))
                .collect(Collectors.toList());
    }

    private Booking getByIdOrThrow(int id) {
        return repository.findById(id).orElseThrow(
                () -> new NoSuchBookingException(id)
        );
    }

    private Booking getForBooseByIdOrThrow(int booseId, int id) {
        Boose boose = booseService.getFromIdOrThrow(booseId);
        Booking booking = getByIdOrThrow(id);
        if (booking.getBoose() != boose) {
            throw new BookingNotForBooseException(id, booseId);
        }
        return booking;
    }

    private Booking getForCustomerByIdOrThrow(int customerId, int id) {
        Customer customer = customerService.getFromIdOrThrow(id);
        Booking booking = getByIdOrThrow(id);
        if (booking.getCustomer() != customer) {
            throw new BookingNotByCustomerException(id, customerId);
        }
        return booking;
    }
}
