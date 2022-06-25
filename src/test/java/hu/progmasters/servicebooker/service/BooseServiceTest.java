package hu.progmasters.servicebooker.service;

import hu.progmasters.servicebooker.ServicebookerApplication;
import hu.progmasters.servicebooker.domain.entity.Boose;
import hu.progmasters.servicebooker.dto.boose.BooseCreateCommand;
import hu.progmasters.servicebooker.dto.boose.BooseInfo;
import hu.progmasters.servicebooker.dto.boose.BooseUpdateCommand;
import hu.progmasters.servicebooker.exceptionhandling.boose.BooseNotFoundException;
import hu.progmasters.servicebooker.repository.BooseRepository;
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
class BooseServiceTest {

    @Mock
    BooseRepository booseRepository;

    ModelMapper modelMapper = new ServicebookerApplication().modelMapper();

    BooseService booseService;

    @BeforeEach
    void constructService() {
        booseService = new BooseService(booseRepository, modelMapper);
    }

    @Test
    void save() {
        BooseCreateCommand command = exampleBooseCreateCommand();
        Boose newBoose = exampleNewBoose();
        Boose savedBoose = exampleSavedBoose();
        when(booseRepository.save(newBoose)).thenReturn(savedBoose);

        BooseInfo booseInfo = booseService.save(command);

        verify(booseRepository).save(newBoose);
        BooseInfo exampleBooseInfo = exampleBooseInfo();
        assertThat(booseInfo).isEqualTo(exampleBooseInfo);
    }

    @Test
    void findById() {
        Boose boose = exampleSavedBoose();
        when(booseRepository.findById(1)).thenReturn(Optional.of(boose));

        BooseInfo booseInfo = booseService.findById(1);

        BooseInfo exampleBooseInfo = exampleBooseInfo();
        assertThat(booseInfo).isEqualTo(exampleBooseInfo);
    }

    @Test
    void findById_notFound() {
        when(booseRepository.findById(1)).thenReturn(Optional.empty());

        assertThatExceptionOfType(BooseNotFoundException.class).isThrownBy(() -> {
            booseService.findById(1);
        });
    }

    @Test
    void findAll() {
        Boose firstBoose = anotherSavedBoose();
        Boose secondBoose = exampleSavedBoose();
        when(booseRepository.findAll()).thenReturn(List.of(firstBoose, secondBoose));

        List<BooseInfo> booseInfos = booseService.findAll();

        BooseInfo exampleBooseInfo = exampleBooseInfo();
        assertThat(booseInfos).hasSize(2)
                .element(1)
                .isEqualTo(exampleBooseInfo);
    }

    @Test
    void update() {
        BooseUpdateCommand command = exampleBooseUpdateCommand();
        Boose boose = exampleSavedBoose();
        when(booseRepository.findById(1)).thenReturn(Optional.of(boose));

        BooseInfo booseInfo = booseService.update(1, command);

        assertThat(boose.getName()).isEqualTo("Doctor Bob");
        BooseInfo updatedBooseInfo = updatedBooseInfo();
        assertThat(booseInfo).isEqualTo(updatedBooseInfo);
    }

    @Test
    void update_notFound() {
        BooseUpdateCommand command = exampleBooseUpdateCommand();
        when(booseRepository.findById(1)).thenReturn(Optional.empty());

        assertThatExceptionOfType(BooseNotFoundException.class).isThrownBy(() -> {
            booseService.update(1, command);
        });
    }

    @Test
    void delete() {
        Boose boose = exampleSavedBoose();
        when(booseRepository.findById(1)).thenReturn(Optional.of(boose));

        BooseInfo booseInfo = booseService.delete(1);

        assertThat(boose.isDeleted()).isTrue();
        BooseInfo exampleBooseInfo = exampleBooseInfo();
        assertThat(booseInfo).isEqualTo(exampleBooseInfo);
    }

    @Test
    void delete_notFound() {
        when(booseRepository.findById(1)).thenReturn(Optional.empty());

        assertThatExceptionOfType(BooseNotFoundException.class).isThrownBy(() -> {
            booseService.delete(1);
        });
    }

    BooseCreateCommand exampleBooseCreateCommand() {
        BooseCreateCommand command = new BooseCreateCommand();
        command.setName("Hairdresser Lisa");
        command.setDescription("I have a small shop on the Pearl Street.");
        return command;
    }

    Boose exampleNewBoose() {
        Boose boose = new Boose();
        boose.setId(null);
        boose.setName("Hairdresser Lisa");
        boose.setDescription("I have a small shop on the Pearl Street.");
        boose.setDeleted(false);
        return boose;
    }

    Boose exampleSavedBoose() {
        Boose boose = exampleNewBoose();
        boose.setId(1);
        return boose;
    }

    BooseInfo exampleBooseInfo() {
        BooseInfo info = new BooseInfo();
        info.setId(1);
        info.setName("Hairdresser Lisa");
        info.setDescription("I have a small shop on the Pearl Street.");
        return info;
    }

    Boose anotherSavedBoose() {
        Boose boose = new Boose();
        boose.setId(2);
        boose.setName("Cleaner Jack");
        boose.setDescription("Dust is my enemy.");
        boose.setDeleted(false);
        return boose;
    }

    BooseUpdateCommand exampleBooseUpdateCommand() {
        BooseUpdateCommand command = new BooseUpdateCommand();
        command.setName("Doctor Bob");
        command.setDescription("I am not a hairdresser.");
        return command;
    }

    BooseInfo updatedBooseInfo() {
        BooseInfo info = exampleBooseInfo();
        info.setName("Doctor Bob");
        info.setDescription("I am not a hairdresser.");
        return info;
    }
}