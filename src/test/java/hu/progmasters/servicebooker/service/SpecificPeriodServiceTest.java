package hu.progmasters.servicebooker.service;

import hu.progmasters.servicebooker.ServicebookerApplication;
import hu.progmasters.servicebooker.domain.entity.Boose;
import hu.progmasters.servicebooker.domain.entity.SpecificPeriod;
import hu.progmasters.servicebooker.dto.specificperiod.SpecificPeriodCreateCommand;
import hu.progmasters.servicebooker.dto.specificperiod.SpecificPeriodInfo;
import hu.progmasters.servicebooker.dto.specificperiod.SpecificPeriodUpdateCommand;
import hu.progmasters.servicebooker.exceptionhandling.specificperiod.OverlappingSpecificPeriodException;
import hu.progmasters.servicebooker.exceptionhandling.specificperiod.SpecificPeriodNotForBooseException;
import hu.progmasters.servicebooker.exceptionhandling.specificperiod.SpecificPeriodNotFoundException;
import hu.progmasters.servicebooker.repository.SpecificPeriodRepository;
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
class SpecificPeriodServiceTest {

    @Mock
    SpecificPeriodRepository specificPeriodRepository;

    @Mock
    BooseService booseService;

    @Mock
    DateTimeBoundChecker dateTimeBoundChecker;

    ModelMapper modelMapper = new ServicebookerApplication().modelMapper();

    SpecificPeriodService specificPeriodService;

    @BeforeEach
    void constructService() {
        specificPeriodService = new SpecificPeriodService(specificPeriodRepository, booseService,
                modelMapper, dateTimeBoundChecker);
    }

    @Test
    void addForBoose() {
        SpecificPeriodCreateCommand command = exampleSpecificPeriodCreateCommand();
        Boose boose = exampleBoose();
        SpecificPeriod exampleNewSpecificPeriod = exampleNewSpecificPeriod(boose);
        SpecificPeriod exampleSavedSpecificPeriod = exampleSavedSpecificPeriod(boose);
        when(booseService.getFromIdOrThrow(1)).thenReturn(boose);
        when(specificPeriodRepository.save(exampleNewSpecificPeriod)).thenReturn(exampleSavedSpecificPeriod);

        SpecificPeriodInfo specificPeriodInfo = specificPeriodService.addForBoose(1, command);

        verify(specificPeriodRepository).save(exampleNewSpecificPeriod);
        SpecificPeriodInfo exampleSpecificPeriodInfo = exampleSpecificPeriodInfo();
        assertThat(specificPeriodInfo).isEqualTo(exampleSpecificPeriodInfo);
    }

    @Test
    void addForBoose_overlapping() {
        SpecificPeriodCreateCommand command = exampleSpecificPeriodCreateCommand();
        Boose boose = exampleBoose();
        SpecificPeriod exampleNewSpecificPeriod = exampleNewSpecificPeriod(boose);
        SpecificPeriod exampleSavedSpecificPeriod = exampleSavedSpecificPeriod(boose);
        when(booseService.getFromIdOrThrow(1)).thenReturn(boose);
        when(specificPeriodRepository.findOverlappingPeriods(boose, exampleNewSpecificPeriod))
                .thenReturn(List.of(exampleSavedSpecificPeriod));

        assertThatExceptionOfType(OverlappingSpecificPeriodException.class).isThrownBy(() -> {
            specificPeriodService.addForBoose(1, command);
        });
    }

    @Test
    void findAllForBoose() {
        Boose boose = exampleBoose();
        SpecificPeriod first = anotherSavedSpecificPeriod(boose);
        SpecificPeriod second = exampleSavedSpecificPeriod(boose);
        Interval<LocalDateTime> interval = interval(
                LocalDateTime.parse("2022-06-21T08:03"),
                LocalDateTime.parse("2022-06-29T11:22")
        );
        when(dateTimeBoundChecker.constrain(interval)).thenReturn(interval);
        when(booseService.getFromIdOrThrow(1)).thenReturn(boose);
        when(specificPeriodRepository.findAllOrderedFor(boose, interval,null, false))
                .thenReturn(List.of(first, second));

        List<SpecificPeriodInfo> specificPeriodInfos =
                specificPeriodService.findAllForBoose(1, interval, null);

        SpecificPeriodInfo exampleSpecificPeriodInfo = exampleSpecificPeriodInfo();
        assertThat(specificPeriodInfos).hasSize(2)
                .element(1)
                .isEqualTo(exampleSpecificPeriodInfo);
    }

    @Test
    void findForBooseById() {
        Boose boose = exampleBoose();
        SpecificPeriod specificPeriod = exampleSavedSpecificPeriod(boose);
        when(booseService.getFromIdOrThrow(1)).thenReturn(boose);
        when(specificPeriodRepository.findById(3)).thenReturn(Optional.of(specificPeriod));

        SpecificPeriodInfo specificPeriodInfo = specificPeriodService.findForBooseById(1, 3);

        SpecificPeriodInfo exampleSpecificPeriodInfo = exampleSpecificPeriodInfo();
        assertThat(specificPeriodInfo).isEqualTo(exampleSpecificPeriodInfo);
    }

    @Test
    void findForBooseById_notFound() {
        Boose boose = exampleBoose();
        when(booseService.getFromIdOrThrow(1)).thenReturn(boose);
        when(specificPeriodRepository.findById(3)).thenReturn(Optional.empty());

        assertThatExceptionOfType(SpecificPeriodNotFoundException.class).isThrownBy(() -> {
            specificPeriodService.findForBooseById(1, 3);
        });
    }

    @Test
    void findForBooseById_notForBoose() {
        Boose firstBoose = exampleBoose();
        Boose secondBoose = anotherBoose();
        SpecificPeriod specificPeriod = exampleSavedSpecificPeriod(firstBoose);
        when(booseService.getFromIdOrThrow(2)).thenReturn(secondBoose);
        when(specificPeriodRepository.findById(3)).thenReturn(Optional.of(specificPeriod));

        assertThatExceptionOfType(SpecificPeriodNotForBooseException.class).isThrownBy(() -> {
            specificPeriodService.findForBooseById(2, 3);
        });
    }

    @Test
    void update() {
        SpecificPeriodUpdateCommand command = exampleSpecificPeriodUpdateCommand();
        Boose boose = exampleBoose();
        SpecificPeriod specificPeriod = exampleSavedSpecificPeriod(boose);
        when(booseService.getFromIdOrThrow(1)).thenReturn(boose);
        when(specificPeriodRepository.findById(3)).thenReturn(Optional.of(specificPeriod));

        SpecificPeriodInfo specificPeriodInfo = specificPeriodService.update(1, 3, command);

        assertThat(specificPeriod.getComment()).isEqualTo("Can work this day, but only this time.");
        SpecificPeriodInfo updatedSpecificPeriodInfo = updatedSpecificPeriodInfo();
        assertThat(specificPeriodInfo).isEqualTo(updatedSpecificPeriodInfo);
    }

    @Test
    void update_notFound() {
        SpecificPeriodUpdateCommand command = exampleSpecificPeriodUpdateCommand();
        Boose boose = exampleBoose();
        when(booseService.getFromIdOrThrow(1)).thenReturn(boose);
        when(specificPeriodRepository.findById(3)).thenReturn(Optional.empty());

        assertThatExceptionOfType(SpecificPeriodNotFoundException.class).isThrownBy(() -> {
            specificPeriodService.update(1, 3, command);
        });
    }

    @Test
    void update_notForBoose() {
        SpecificPeriodUpdateCommand command = exampleSpecificPeriodUpdateCommand();
        Boose firstBoose = exampleBoose();
        Boose secondBoose = anotherBoose();
        SpecificPeriod specificPeriod = exampleSavedSpecificPeriod(firstBoose);
        when(booseService.getFromIdOrThrow(2)).thenReturn(secondBoose);
        when(specificPeriodRepository.findById(3)).thenReturn(Optional.of(specificPeriod));

        assertThatExceptionOfType(SpecificPeriodNotForBooseException.class).isThrownBy(() -> {
            specificPeriodService.update(2, 3, command);
        });
    }

    SpecificPeriodCreateCommand exampleSpecificPeriodCreateCommand() {
        SpecificPeriodCreateCommand command = new SpecificPeriodCreateCommand();
        command.setStart(LocalDateTime.parse("2022-06-22T08:00"));
        command.setEnd(LocalDateTime.parse("2022-06-22T10:00"));
        command.setComment("Can work this day.");
        command.setBookable(true);
        return command;
    }

    SpecificPeriod exampleNewSpecificPeriod(Boose boose) {
        SpecificPeriod specificPeriod = new SpecificPeriod();
        specificPeriod.setId(null);
        specificPeriod.setStart(LocalDateTime.parse("2022-06-22T08:00"));
        specificPeriod.setEnd(LocalDateTime.parse("2022-06-22T10:00"));
        specificPeriod.setComment("Can work this day.");
        specificPeriod.setBookable(true);
        specificPeriod.setBoose(boose);
        return specificPeriod;
    }

    SpecificPeriod exampleSavedSpecificPeriod(Boose boose) {
        SpecificPeriod specificPeriod = exampleNewSpecificPeriod(boose);
        specificPeriod.setId(3);
        return specificPeriod;
    }

    SpecificPeriodInfo exampleSpecificPeriodInfo() {
        SpecificPeriodInfo info = new SpecificPeriodInfo();
        info.setId(3);
        info.setStart(LocalDateTime.parse("2022-06-22T08:00"));
        info.setEnd(LocalDateTime.parse("2022-06-22T10:00"));
        info.setComment("Can work this day.");
        info.setBookable(true);
        return info;
    }

    SpecificPeriod anotherSavedSpecificPeriod(Boose boose) {
        SpecificPeriod specificPeriod = new SpecificPeriod();
        specificPeriod.setId(4);
        specificPeriod.setStart(LocalDateTime.parse("2022-06-21T18:50:23"));
        specificPeriod.setEnd(LocalDateTime.parse("2022-06-22T06:02:53"));
        specificPeriod.setComment("A long period.");
        specificPeriod.setBookable(false);
        specificPeriod.setBoose(boose);
        return specificPeriod;
    }

    SpecificPeriodUpdateCommand exampleSpecificPeriodUpdateCommand() {
        SpecificPeriodUpdateCommand command = new SpecificPeriodUpdateCommand();
        command.setComment("Can work this day, but only this time.");
        return command;
    }

    SpecificPeriodInfo updatedSpecificPeriodInfo() {
        SpecificPeriodInfo info = exampleSpecificPeriodInfo();
        info.setComment("Can work this day, but only this time.");
        return info;
    }

    Boose exampleBoose() {
        Boose boose = new Boose();
        boose.setId(1);
        boose.setName("Hairdresser Lisa");
        boose.setDescription("I have a small shop on the Pearl Street.");
        boose.setDeleted(false);
        return boose;
    }

    Boose anotherBoose() {
        Boose boose = new Boose();
        boose.setId(2);
        boose.setName("Cleaner Jack");
        boose.setDescription("Dust is my enemy.");
        boose.setDeleted(false);
        return boose;
    }
}