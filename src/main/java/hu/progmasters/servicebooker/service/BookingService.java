package hu.progmasters.servicebooker.service;

import hu.progmasters.servicebooker.domain.entity.Booking;
import hu.progmasters.servicebooker.domain.entity.Boose;
import hu.progmasters.servicebooker.domain.entity.Customer;
import hu.progmasters.servicebooker.dto.booking.BookingInfo;
import hu.progmasters.servicebooker.dto.booking.BookingUpdateCommand;
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
    public BookingInfo findForBooseOrCustomerById(int id, Integer booseId, Integer customerId) {
        Booking booking = getByIdOrThrow(id);
        if (booseId != null) {
            checkBoose(booking, booseId);
        }
        if (customerId != null) {
            checkCustomer(booking, customerId);
        }
        return toDto(booking);
    }

    private Booking getByIdOrThrow(int id) {
        return repository.findById(id).orElseThrow(
                () -> new NoSuchBookingException(id)
        );
    }

    @Transactional
    public List<BookingInfo> findAllForBooseOrCustomer(Integer booseId, Integer customerId,
                                                       Interval<LocalDateTime> interval) {
        Interval<LocalDateTime> constrainedInterval = dateTimeBoundChecker.constrain(interval);
        Boose boose = booseId != null ? booseService.getFromIdOrThrow(booseId) : null;
        Customer customer = customerId != null ? customerService.getFromIdOrThrow(customerId) : null;
        return repository.findAllOrderedFor(boose, customer, constrainedInterval, false).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<Booking> getAllForBoose(Boose boose, Interval<LocalDateTime> interval, boolean lock) {
        return repository.findAllOrderedFor(boose, null, interval, lock);
    }

    @Transactional
    public BookingInfo updateForBooseOrCustomer(int id, Integer booseId, Integer customerId, BookingUpdateCommand command) {
        Booking booking = getByIdOrThrow(id);
        if (booseId != null) {
            checkBoose(booking, booseId);
        }
        if (customerId != null) {
            checkCustomer(booking, customerId);
        }
        modelMapper.map(command, booking);
        return toDto(booking);
    }

    @Transactional
    public BookingInfo deleteForBooseOrCustomer(int id, Integer booseId, Integer customerId) {
        Booking booking = getByIdOrThrow(id);
        if (booseId != null) {
            checkBoose(booking, booseId);
        }
        if (customerId != null) {
            checkCustomer(booking, customerId);
        }
        repository.delete(booking);
        return toDto(booking);
    }

    private void checkBoose(Booking booking, int booseId) {
        Boose boose = booseService.getFromIdOrThrow(booseId);
        if (booking.getBoose() != boose) {
            throw new BookingNotForBooseException(booking.getId(), booseId);
        }
    }

    private void checkCustomer(Booking booking, int customerId) {
        Customer customer = customerService.getFromIdOrThrow(customerId);
        if (booking.getCustomer() != customer) {
            throw new BookingNotByCustomerException(booking.getId(), customerId);
        }
    }

    private BookingInfo toDto(Booking booking) {
        return modelMapper.map(booking, BookingInfo.class);
    }
}
