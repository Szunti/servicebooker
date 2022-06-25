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
import hu.progmasters.servicebooker.util.DayOfWeekTime;
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
        WeeklyPeriodCreateCommand command = exampleWeeklyPeriodCreateCommand();
        Boose boose = exampleBoose();
        WeeklyPeriod exampleNewWeeklyPeriod = exampleNewWeeklyPeriod(boose);
        WeeklyPeriod exampleSavedWeeklyPeriod = exampleSavedWeeklyPeriod(boose);
        when(booseService.getFromIdOrThrow(1)).thenReturn(boose);
        when(weeklyPeriodRepository.save(exampleNewWeeklyPeriod)).thenReturn(exampleSavedWeeklyPeriod);

        WeeklyPeriodInfo weeklyPeriodInfo = weeklyPeriodService.addForBoose(1, command);

        verify(weeklyPeriodRepository).save(exampleNewWeeklyPeriod);
        WeeklyPeriodInfo exampleWeeklyPeriodInfo = exampleWeeklyPeriodInfo();
        assertThat(weeklyPeriodInfo).isEqualTo(exampleWeeklyPeriodInfo);
    }

    @Test
    void addForBoose_overlapping() {
        WeeklyPeriodCreateCommand command = exampleWeeklyPeriodCreateCommand();
        Boose boose = exampleBoose();
        WeeklyPeriod exampleNewWeeklyPeriod = exampleNewWeeklyPeriod(boose);
        WeeklyPeriod exampleSavedWeeklyPeriod = exampleSavedWeeklyPeriod(boose);
        when(booseService.getFromIdOrThrow(1)).thenReturn(boose);
        when(weeklyPeriodRepository.findOverlappingPeriods(boose, exampleNewWeeklyPeriod))
                .thenReturn(List.of(exampleSavedWeeklyPeriod));

        assertThatExceptionOfType(OverlappingWeeklyPeriodException.class).isThrownBy(() -> {
            weeklyPeriodService.addForBoose(1, command);
        });
    }

    @Test
    void findAllForBoose() {
        Boose boose = exampleBoose();
        WeeklyPeriod first = exampleSavedWeeklyPeriod(boose);
        WeeklyPeriod second = anotherSavedWeeklyPeriod(boose);
        when(booseService.getFromIdOrThrow(1)).thenReturn(boose);
        when(weeklyPeriodRepository.findAllOrderedFor(boose, false)).thenReturn(List.of(first, second));

        List<WeeklyPeriodInfo> weeklyPeriodInfos = weeklyPeriodService.findAllForBoose(1);

        WeeklyPeriodInfo exampleWeeklyPeriodInfo = exampleWeeklyPeriodInfo();
        assertThat(weeklyPeriodInfos).hasSize(2)
                .first()
                .isEqualTo(exampleWeeklyPeriodInfo);
    }

    @Test
    void findForBooseById() {
        Boose boose = exampleBoose();
        WeeklyPeriod weeklyPeriod = exampleSavedWeeklyPeriod(boose);
        when(booseService.getFromIdOrThrow(1)).thenReturn(boose);
        when(weeklyPeriodRepository.findById(3)).thenReturn(Optional.of(weeklyPeriod));

        WeeklyPeriodInfo weeklyPeriodInfo = weeklyPeriodService.findForBooseById(1, 3);

        WeeklyPeriodInfo exampleWeeklyPeriodInfo = exampleWeeklyPeriodInfo();
        assertThat(weeklyPeriodInfo).isEqualTo(exampleWeeklyPeriodInfo);
    }

    @Test
    void findForBooseById_notFound() {
        Boose boose = exampleBoose();
        when(booseService.getFromIdOrThrow(1)).thenReturn(boose);
        when(weeklyPeriodRepository.findById(3)).thenReturn(Optional.empty());

        assertThatExceptionOfType(WeeklyPeriodNotFoundException.class).isThrownBy(() -> {
            weeklyPeriodService.findForBooseById(1, 3);
        });
    }

    @Test
    void findForBooseById_notForBoose() {
        Boose firstBoose = exampleBoose();
        Boose secondBoose = anotherBoose();
        WeeklyPeriod weeklyPeriod = exampleSavedWeeklyPeriod(firstBoose);
        when(booseService.getFromIdOrThrow(2)).thenReturn(secondBoose);
        when(weeklyPeriodRepository.findById(3)).thenReturn(Optional.of(weeklyPeriod));

        assertThatExceptionOfType(WeeklyPeriodNotForBooseException.class).isThrownBy(() -> {
            weeklyPeriodService.findForBooseById(2, 3);
        });
    }

    @Test
    void update() {
        WeeklyPeriodUpdateCommand command = exampleWeeklyPeriodUpdateCommand();
        Boose boose = exampleBoose();
        WeeklyPeriod weeklyPeriod = exampleSavedWeeklyPeriod(boose);
        when(booseService.getFromIdOrThrow(1)).thenReturn(boose);
        when(weeklyPeriodRepository.findById(3)).thenReturn(Optional.of(weeklyPeriod));

        WeeklyPeriodInfo weeklyPeriodInfo = weeklyPeriodService.update(1, 3, command);

        assertThat(weeklyPeriod.getComment()).isEqualTo("Actually, Tuesday is the worst.");
        WeeklyPeriodInfo updatedWeeklyPeriodInfo = updatedWeeklyPeriodInfo();
        assertThat(weeklyPeriodInfo).isEqualTo(updatedWeeklyPeriodInfo);
    }

    @Test
    void update_notFound() {
        WeeklyPeriodUpdateCommand command = exampleWeeklyPeriodUpdateCommand();
        Boose boose = exampleBoose();
        when(booseService.getFromIdOrThrow(1)).thenReturn(boose);
        when(weeklyPeriodRepository.findById(3)).thenReturn(Optional.empty());

        assertThatExceptionOfType(WeeklyPeriodNotFoundException.class).isThrownBy(() -> {
            weeklyPeriodService.update(1, 3, command);
        });
    }

    @Test
    void update_notForBoose() {
        WeeklyPeriodUpdateCommand command = exampleWeeklyPeriodUpdateCommand();
        Boose firstBoose = exampleBoose();
        Boose secondBoose = anotherBoose();
        WeeklyPeriod weeklyPeriod = exampleSavedWeeklyPeriod(firstBoose);
        when(booseService.getFromIdOrThrow(2)).thenReturn(secondBoose);
        when(weeklyPeriodRepository.findById(3)).thenReturn(Optional.of(weeklyPeriod));

        assertThatExceptionOfType(WeeklyPeriodNotForBooseException.class).isThrownBy(() -> {
            weeklyPeriodService.update(2, 3, command);
        });
    }

    WeeklyPeriodCreateCommand exampleWeeklyPeriodCreateCommand() {
        WeeklyPeriodCreateCommand command = new WeeklyPeriodCreateCommand();
        command.setStart(DayOfWeekTime.parse("Mon 08:00"));
        command.setEnd(DayOfWeekTime.parse("Mon 12:00"));
        command.setComment("Worst part of the week.");
        return command;
    }

    WeeklyPeriod exampleNewWeeklyPeriod(Boose boose) {
        WeeklyPeriod weeklyPeriod = new WeeklyPeriod();
        weeklyPeriod.setId(null);
        weeklyPeriod.setStart(DayOfWeekTime.parse("Mon 08:00"));
        weeklyPeriod.setEnd(DayOfWeekTime.parse("Mon 12:00"));
        weeklyPeriod.setComment("Worst part of the week.");
        weeklyPeriod.setBoose(boose);
        return weeklyPeriod;
    }

    WeeklyPeriod exampleSavedWeeklyPeriod(Boose boose) {
        WeeklyPeriod weeklyPeriod = exampleNewWeeklyPeriod(boose);
        weeklyPeriod.setId(3);
        return weeklyPeriod;
    }

    WeeklyPeriodInfo exampleWeeklyPeriodInfo() {
        WeeklyPeriodInfo info = new WeeklyPeriodInfo();
        info.setId(3);
        info.setStart(DayOfWeekTime.parse("Mon 08:00"));
        info.setEnd(DayOfWeekTime.parse("Mon 12:00"));
        info.setComment("Worst part of the week.");
        return info;
    }

    WeeklyPeriod anotherSavedWeeklyPeriod(Boose boose) {
        WeeklyPeriod weeklyPeriod = new WeeklyPeriod();
        weeklyPeriod.setId(4);
        weeklyPeriod.setStart(DayOfWeekTime.parse("Tue 18:17:52"));
        weeklyPeriod.setEnd(DayOfWeekTime.parse("Wed 20:04:19"));
        weeklyPeriod.setComment("A long period.");
        weeklyPeriod.setBoose(boose);
        return weeklyPeriod;
    }

    WeeklyPeriodUpdateCommand exampleWeeklyPeriodUpdateCommand() {
        WeeklyPeriodUpdateCommand command = new WeeklyPeriodUpdateCommand();
        command.setComment("Actually, Tuesday is the worst.");
        return command;
    }

    WeeklyPeriodInfo updatedWeeklyPeriodInfo() {
        WeeklyPeriodInfo info = exampleWeeklyPeriodInfo();
        info.setComment("Actually, Tuesday is the worst.");
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