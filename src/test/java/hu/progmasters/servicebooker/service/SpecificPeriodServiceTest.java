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
import hu.progmasters.servicebooker.service.examples.BooseExamples;
import hu.progmasters.servicebooker.service.examples.SpecificPeriodExamples;
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
        SpecificPeriodCreateCommand command = SpecificPeriodExamples.jun22CreateCommand();
        Boose boose = BooseExamples.hairdresser();
        SpecificPeriod newSpecificPeriod = SpecificPeriodExamples.jun22New(boose);
        SpecificPeriod savedSpecificPeriod = SpecificPeriodExamples.jun22(boose);
        when(booseService.getFromIdOrThrow(BooseExamples.HAIRDRESSER_ID)).thenReturn(boose);
        when(specificPeriodRepository.save(newSpecificPeriod)).thenReturn(savedSpecificPeriod);

        SpecificPeriodInfo specificPeriodInfo =
                specificPeriodService.addForBoose(BooseExamples.HAIRDRESSER_ID, command);

        verify(specificPeriodRepository).save(newSpecificPeriod);
        SpecificPeriodInfo expectedSpecificPeriodInfo = SpecificPeriodExamples.jun22Info();
        assertThat(specificPeriodInfo).isEqualTo(expectedSpecificPeriodInfo);
    }

    @Test
    void addForBoose_overlapping() {
        SpecificPeriodCreateCommand command = SpecificPeriodExamples.jun22CreateCommand();
        Boose boose = BooseExamples.hairdresser();
        SpecificPeriod newSpecificPeriod = SpecificPeriodExamples.jun22New(boose);
        SpecificPeriod savedSpecificPeriod = SpecificPeriodExamples.jun22(boose);
        when(booseService.getFromIdOrThrow(BooseExamples.HAIRDRESSER_ID)).thenReturn(boose);
        when(specificPeriodRepository.findOverlappingPeriods(boose, newSpecificPeriod))
                .thenReturn(List.of(savedSpecificPeriod));

        assertThatExceptionOfType(OverlappingSpecificPeriodException.class).isThrownBy(() -> {
            specificPeriodService.addForBoose(BooseExamples.HAIRDRESSER_ID, command);
        });
    }

    @Test
    void findAllForBoose() {
        Boose boose = BooseExamples.hairdresser();
        SpecificPeriod first = SpecificPeriodExamples.jun21(boose);
        SpecificPeriod second = SpecificPeriodExamples.jun22(boose);
        Interval<LocalDateTime> interval = interval(
                LocalDateTime.parse("2022-06-20T08:03"),
                LocalDateTime.parse("2022-06-29T11:22")
        );
        when(dateTimeBoundChecker.constrain(interval)).thenReturn(interval);
        when(booseService.getFromIdOrThrow(BooseExamples.HAIRDRESSER_ID)).thenReturn(boose);
        when(specificPeriodRepository.findAllOrderedFor(boose, interval,null, false))
                .thenReturn(List.of(first, second));

        List<SpecificPeriodInfo> specificPeriodInfos =
                specificPeriodService.findAllForBoose(BooseExamples.HAIRDRESSER_ID, interval, null);

        SpecificPeriodInfo expectedSpecificPeriodInfo = SpecificPeriodExamples.jun22Info();
        assertThat(specificPeriodInfos).hasSize(2)
                .element(1)
                .isEqualTo(expectedSpecificPeriodInfo);
    }

    @Test
    void findForBooseById() {
        Boose boose = BooseExamples.hairdresser();
        SpecificPeriod specificPeriod = SpecificPeriodExamples.jun22(boose);
        when(booseService.getFromIdOrThrow(BooseExamples.HAIRDRESSER_ID)).thenReturn(boose);
        when(specificPeriodRepository.findById(SpecificPeriodExamples.JUN22_ID))
                .thenReturn(Optional.of(specificPeriod));

        SpecificPeriodInfo specificPeriodInfo =
                specificPeriodService.findForBooseById(BooseExamples.HAIRDRESSER_ID, SpecificPeriodExamples.JUN22_ID);

        SpecificPeriodInfo expectedSpecificPeriodInfo = SpecificPeriodExamples.jun22Info();
        assertThat(specificPeriodInfo).isEqualTo(expectedSpecificPeriodInfo);
    }

    @Test
    void findForBooseById_notFound() {
        Boose boose = BooseExamples.hairdresser();
        when(booseService.getFromIdOrThrow(BooseExamples.HAIRDRESSER_ID)).thenReturn(boose);
        when(specificPeriodRepository.findById(42)).thenReturn(Optional.empty());

        assertThatExceptionOfType(SpecificPeriodNotFoundException.class).isThrownBy(() -> {
            specificPeriodService.findForBooseById(BooseExamples.HAIRDRESSER_ID, 42);
        });
    }

    @Test
    void findForBooseById_notForBoose() {
        Boose booseOfPeriod = BooseExamples.hairdresser();
        Boose anotherBoose = BooseExamples.cleaner();
        SpecificPeriod specificPeriod = SpecificPeriodExamples.jun22(booseOfPeriod);
        when(booseService.getFromIdOrThrow(BooseExamples.CLEANER_ID)).thenReturn(anotherBoose);
        when(specificPeriodRepository.findById(SpecificPeriodExamples.JUN22_ID))
                .thenReturn(Optional.of(specificPeriod));

        assertThatExceptionOfType(SpecificPeriodNotForBooseException.class).isThrownBy(() -> {
            specificPeriodService.findForBooseById(BooseExamples.CLEANER_ID, SpecificPeriodExamples.JUN22_ID);
        });
    }

    @Test
    void update() {
        SpecificPeriodUpdateCommand command = SpecificPeriodExamples.jun22UpdateCommand();
        Boose boose = BooseExamples.hairdresser();
        SpecificPeriod specificPeriod = SpecificPeriodExamples.jun22(boose);
        when(booseService.getFromIdOrThrow(BooseExamples.HAIRDRESSER_ID)).thenReturn(boose);
        when(specificPeriodRepository.findById(SpecificPeriodExamples.JUN22_ID))
                .thenReturn(Optional.of(specificPeriod));

        SpecificPeriodInfo specificPeriodInfo =
                specificPeriodService.update(BooseExamples.HAIRDRESSER_ID, SpecificPeriodExamples.JUN22_ID, command);

        assertThat(specificPeriod.getComment()).isEqualTo(SpecificPeriodExamples.JUN22_UPDATED_COMMENT);
        SpecificPeriodInfo updatedSpecificPeriodInfo = SpecificPeriodExamples.jun22UpdatedInfo();
        assertThat(specificPeriodInfo).isEqualTo(updatedSpecificPeriodInfo);
    }

    @Test
    void update_notFound() {
        SpecificPeriodUpdateCommand command = SpecificPeriodExamples.jun22UpdateCommand();
        Boose boose = BooseExamples.hairdresser();
        when(booseService.getFromIdOrThrow(BooseExamples.HAIRDRESSER_ID)).thenReturn(boose);
        when(specificPeriodRepository.findById(42)).thenReturn(Optional.empty());

        assertThatExceptionOfType(SpecificPeriodNotFoundException.class).isThrownBy(() -> {
            specificPeriodService.update(BooseExamples.HAIRDRESSER_ID, 42, command);
        });
    }

    @Test
    void update_notForBoose() {
        SpecificPeriodUpdateCommand command = SpecificPeriodExamples.jun22UpdateCommand();
        Boose booseOfPeriod = BooseExamples.hairdresser();
        Boose anotherBoose = BooseExamples.cleaner();
        SpecificPeriod specificPeriod = SpecificPeriodExamples.jun22(booseOfPeriod);
        when(booseService.getFromIdOrThrow(BooseExamples.CLEANER_ID)).thenReturn(anotherBoose);
        when(specificPeriodRepository.findById(SpecificPeriodExamples.JUN22_ID))
                .thenReturn(Optional.of(specificPeriod));

        assertThatExceptionOfType(SpecificPeriodNotForBooseException.class).isThrownBy(() -> {
            specificPeriodService.update(BooseExamples.CLEANER_ID, SpecificPeriodExamples.JUN22_ID, command);
        });
    }
}