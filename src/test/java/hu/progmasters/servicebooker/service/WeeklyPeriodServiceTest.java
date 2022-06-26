package hu.progmasters.servicebooker.service;

import hu.progmasters.servicebooker.ServicebookerApplication;
import hu.progmasters.servicebooker.domain.entity.Boose;
import hu.progmasters.servicebooker.domain.entity.WeeklyPeriod;
import hu.progmasters.servicebooker.dto.weeklyperiod.WeeklyPeriodCreateCommand;
import hu.progmasters.servicebooker.dto.weeklyperiod.WeeklyPeriodInfo;
import hu.progmasters.servicebooker.dto.weeklyperiod.WeeklyPeriodUpdateCommand;
import hu.progmasters.servicebooker.exceptionhandling.weeklyperiod.OverlappingWeeklyPeriodException;
import hu.progmasters.servicebooker.exceptionhandling.weeklyperiod.WeeklyPeriodNotForBooseException;
import hu.progmasters.servicebooker.exceptionhandling.weeklyperiod.WeeklyPeriodNotFoundException;
import hu.progmasters.servicebooker.repository.WeeklyPeriodRepository;
import hu.progmasters.servicebooker.service.examples.BooseExamples;
import hu.progmasters.servicebooker.service.examples.WeeklyPeriodExamples;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeeklyPeriodServiceTest {

    @Mock
    WeeklyPeriodRepository weeklyPeriodRepository;

    @Mock
    BooseService booseService;

    ModelMapper modelMapper = new ServicebookerApplication().modelMapper();

    WeeklyPeriodService weeklyPeriodService;

    @BeforeEach
    void constructService() {
        weeklyPeriodService = new WeeklyPeriodService(weeklyPeriodRepository, booseService, modelMapper);
    }

    @Test
    void addForBoose() {
        WeeklyPeriodCreateCommand command = WeeklyPeriodExamples.mondayCreateCommand();
        Boose boose = BooseExamples.hairdresser();
        WeeklyPeriod newWeeklyPeriod = WeeklyPeriodExamples.mondayNew(boose);
        WeeklyPeriod savedWeeklyPeriod = WeeklyPeriodExamples.monday(boose);
        when(booseService.getFromIdOrThrow(BooseExamples.HAIRDRESSER_ID)).thenReturn(boose);
        when(weeklyPeriodRepository.save(newWeeklyPeriod)).thenReturn(savedWeeklyPeriod);

        WeeklyPeriodInfo weeklyPeriodInfo = weeklyPeriodService.addForBoose(BooseExamples.HAIRDRESSER_ID, command);

        verify(weeklyPeriodRepository).save(newWeeklyPeriod);
        WeeklyPeriodInfo expectedWeeklyPeriodInfo = WeeklyPeriodExamples.mondayInfo();
        assertThat(weeklyPeriodInfo).isEqualTo(expectedWeeklyPeriodInfo);
    }

    @Test
    void addForBoose_overlapping() {
        WeeklyPeriodCreateCommand command = WeeklyPeriodExamples.mondayCreateCommand();
        Boose boose = BooseExamples.hairdresser();
        WeeklyPeriod newWeeklyPeriod = WeeklyPeriodExamples.mondayNew(boose);
        WeeklyPeriod savedWeeklyPeriod = WeeklyPeriodExamples.monday(boose);
        when(booseService.getFromIdOrThrow(BooseExamples.HAIRDRESSER_ID)).thenReturn(boose);
        when(weeklyPeriodRepository.findOverlappingPeriods(boose, newWeeklyPeriod))
                .thenReturn(List.of(savedWeeklyPeriod));

        assertThatExceptionOfType(OverlappingWeeklyPeriodException.class).isThrownBy(() -> {
            weeklyPeriodService.addForBoose(BooseExamples.HAIRDRESSER_ID, command);
        });
    }

    @Test
    void findAllForBoose() {
        Boose boose = BooseExamples.hairdresser();
        WeeklyPeriod first = WeeklyPeriodExamples.monday(boose);
        WeeklyPeriod second = WeeklyPeriodExamples.wednesday(boose);
        when(booseService.getFromIdOrThrow(BooseExamples.HAIRDRESSER_ID)).thenReturn(boose);
        when(weeklyPeriodRepository.findAllOrderedFor(boose, false)).thenReturn(List.of(first, second));

        List<WeeklyPeriodInfo> weeklyPeriodInfos = weeklyPeriodService.findAllForBoose(BooseExamples.HAIRDRESSER_ID);

        WeeklyPeriodInfo firstWeeklyPeriodInfo = WeeklyPeriodExamples.mondayInfo();
        assertThat(weeklyPeriodInfos).hasSize(2)
                .first()
                .isEqualTo(firstWeeklyPeriodInfo);
    }

    @Test
    void findForBooseById() {
        Boose boose = BooseExamples.hairdresser();
        WeeklyPeriod weeklyPeriod = WeeklyPeriodExamples.monday(boose);
        when(booseService.getFromIdOrThrow(BooseExamples.HAIRDRESSER_ID)).thenReturn(boose);
        when(weeklyPeriodRepository.findById(WeeklyPeriodExamples.MONDAY_ID)).thenReturn(Optional.of(weeklyPeriod));

        WeeklyPeriodInfo weeklyPeriodInfo =
                weeklyPeriodService.findForBooseById(BooseExamples.HAIRDRESSER_ID, WeeklyPeriodExamples.MONDAY_ID);

        WeeklyPeriodInfo expectedWeeklyPeriodInfo = WeeklyPeriodExamples.mondayInfo();
        assertThat(weeklyPeriodInfo).isEqualTo(expectedWeeklyPeriodInfo);
    }

    @Test
    void findForBooseById_notFound() {
        Boose boose = BooseExamples.hairdresser();
        when(booseService.getFromIdOrThrow(BooseExamples.HAIRDRESSER_ID)).thenReturn(boose);
        when(weeklyPeriodRepository.findById(32)).thenReturn(Optional.empty());

        assertThatExceptionOfType(WeeklyPeriodNotFoundException.class).isThrownBy(() -> {
            weeklyPeriodService.findForBooseById(BooseExamples.HAIRDRESSER_ID, 32);
        });
    }

    @Test
    void findForBooseById_notForBoose() {
        Boose booseOfPeriod = BooseExamples.hairdresser();
        Boose anotherBoose = BooseExamples.cleaner();
        WeeklyPeriod weeklyPeriod = WeeklyPeriodExamples.monday(booseOfPeriod);
        when(booseService.getFromIdOrThrow(BooseExamples.CLEANER_ID)).thenReturn(anotherBoose);
        when(weeklyPeriodRepository.findById(WeeklyPeriodExamples.MONDAY_ID)).thenReturn(Optional.of(weeklyPeriod));

        assertThatExceptionOfType(WeeklyPeriodNotForBooseException.class).isThrownBy(() -> {
            weeklyPeriodService.findForBooseById(BooseExamples.CLEANER_ID, WeeklyPeriodExamples.MONDAY_ID);
        });
    }

    @Test
    void update() {
        WeeklyPeriodUpdateCommand command = WeeklyPeriodExamples.mondayUpdateCommand();
        Boose boose = BooseExamples.hairdresser();
        WeeklyPeriod weeklyPeriod = WeeklyPeriodExamples.monday(boose);
        when(booseService.getFromIdOrThrow(BooseExamples.HAIRDRESSER_ID)).thenReturn(boose);
        when(weeklyPeriodRepository.findById(WeeklyPeriodExamples.MONDAY_ID)).thenReturn(Optional.of(weeklyPeriod));

        WeeklyPeriodInfo weeklyPeriodInfo =
                weeklyPeriodService.update(BooseExamples.HAIRDRESSER_ID, WeeklyPeriodExamples.MONDAY_ID, command);

        assertThat(weeklyPeriod.getComment()).isEqualTo(WeeklyPeriodExamples.MONDAY_UPDATED_COMMENT);
        WeeklyPeriodInfo updatedWeeklyPeriodInfo = WeeklyPeriodExamples.mondayUpdatedInfo();
        assertThat(weeklyPeriodInfo).isEqualTo(updatedWeeklyPeriodInfo);
    }

    @Test
    void update_notFound() {
        WeeklyPeriodUpdateCommand command = WeeklyPeriodExamples.mondayUpdateCommand();
        Boose boose = BooseExamples.hairdresser();
        when(booseService.getFromIdOrThrow(BooseExamples.HAIRDRESSER_ID)).thenReturn(boose);
        when(weeklyPeriodRepository.findById(35)).thenReturn(Optional.empty());

        assertThatExceptionOfType(WeeklyPeriodNotFoundException.class).isThrownBy(() -> {
            weeklyPeriodService.update(BooseExamples.HAIRDRESSER_ID, 35, command);
        });
    }

    @Test
    void update_notForBoose() {
        WeeklyPeriodUpdateCommand command = WeeklyPeriodExamples.mondayUpdateCommand();
        Boose booseOfPeriod = BooseExamples.hairdresser();
        Boose anotherBoose = BooseExamples.cleaner();
        WeeklyPeriod weeklyPeriod = WeeklyPeriodExamples.monday(booseOfPeriod);
        when(booseService.getFromIdOrThrow(BooseExamples.CLEANER_ID)).thenReturn(anotherBoose);
        when(weeklyPeriodRepository.findById(WeeklyPeriodExamples.MONDAY_ID)).thenReturn(Optional.of(weeklyPeriod));

        assertThatExceptionOfType(WeeklyPeriodNotForBooseException.class).isThrownBy(() -> {
            weeklyPeriodService.update(BooseExamples.CLEANER_ID, WeeklyPeriodExamples.MONDAY_ID, command);
        });
    }
}