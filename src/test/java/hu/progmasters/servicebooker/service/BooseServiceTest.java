package hu.progmasters.servicebooker.service;

import hu.progmasters.servicebooker.ServicebookerApplication;
import hu.progmasters.servicebooker.domain.entity.Boose;
import hu.progmasters.servicebooker.dto.boose.BooseCreateCommand;
import hu.progmasters.servicebooker.dto.boose.BooseInfo;
import hu.progmasters.servicebooker.dto.boose.BooseUpdateCommand;
import hu.progmasters.servicebooker.exceptionhandling.boose.BooseNotFoundException;
import hu.progmasters.servicebooker.repository.BooseRepository;
import hu.progmasters.servicebooker.service.examples.BooseExamples;
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
        BooseCreateCommand command = BooseExamples.hairdresserCreateCommand();
        Boose newBoose = BooseExamples.hairdresserNew();
        Boose savedBoose = BooseExamples.hairdresser();
        when(booseRepository.save(newBoose)).thenReturn(savedBoose);

        BooseInfo booseInfo = booseService.save(command);

        verify(booseRepository).save(newBoose);
        BooseInfo expectedBoseInfo = BooseExamples.hairdresserInfo();
        assertThat(booseInfo).isEqualTo(expectedBoseInfo);
    }

    @Test
    void findById() {
        Boose boose = BooseExamples.hairdresser();
        when(booseRepository.findById(BooseExamples.HAIRDRESSER_ID)).thenReturn(Optional.of(boose));

        BooseInfo booseInfo = booseService.findById(BooseExamples.HAIRDRESSER_ID);

        BooseInfo expectedBooseInfo = BooseExamples.hairdresserInfo();
        assertThat(booseInfo).isEqualTo(expectedBooseInfo);
    }

    @Test
    void findById_notFound() {
        when(booseRepository.findById(11)).thenReturn(Optional.empty());

        assertThatExceptionOfType(BooseNotFoundException.class).isThrownBy(() -> {
            booseService.findById(11);
        });
    }

    @Test
    void findAll() {
        Boose firstBoose = BooseExamples.cleaner();
        Boose secondBoose = BooseExamples.hairdresser();
        when(booseRepository.findAll()).thenReturn(List.of(firstBoose, secondBoose));

        List<BooseInfo> booseInfos = booseService.findAll();

        BooseInfo secondBooseInfo = BooseExamples.hairdresserInfo();
        assertThat(booseInfos).hasSize(2)
                .element(1)
                .isEqualTo(secondBooseInfo);
    }

    @Test
    void update() {
        BooseUpdateCommand command = BooseExamples.hairdresserUpdateCommand();
        Boose boose = BooseExamples.hairdresser();
        when(booseRepository.findById(1)).thenReturn(Optional.of(boose));

        BooseInfo booseInfo = booseService.update(BooseExamples.HAIRDRESSER_ID, command);

        assertThat(boose.getName()).isEqualTo(BooseExamples.HAIRDRESSER_UPDATED_NAME);
        assertThat(boose.getDescription()).isEqualTo(BooseExamples.HAIRDRESSER_UPDATED_DESC);
        BooseInfo updatedBooseInfo = BooseExamples.hairdresserUpdatedInfo();
        assertThat(booseInfo).isEqualTo(updatedBooseInfo);
    }

    @Test
    void update_notFound() {
        BooseUpdateCommand command = BooseExamples.hairdresserUpdateCommand();
        when(booseRepository.findById(11)).thenReturn(Optional.empty());

        assertThatExceptionOfType(BooseNotFoundException.class).isThrownBy(() -> {
            booseService.update(11, command);
        });
    }

    @Test
    void delete() {
        Boose boose = BooseExamples.hairdresser();
        when(booseRepository.findById(BooseExamples.HAIRDRESSER_ID)).thenReturn(Optional.of(boose));

        BooseInfo booseInfo = booseService.delete(BooseExamples.HAIRDRESSER_ID);

        assertThat(boose.isDeleted()).isTrue();
        BooseInfo deletedBooseInfo = BooseExamples.hairdresserInfo();
        assertThat(booseInfo).isEqualTo(deletedBooseInfo);
    }

    @Test
    void delete_notFound() {
        when(booseRepository.findById(11)).thenReturn(Optional.empty());

        assertThatExceptionOfType(BooseNotFoundException.class).isThrownBy(() -> {
            booseService.delete(11);
        });
    }
}