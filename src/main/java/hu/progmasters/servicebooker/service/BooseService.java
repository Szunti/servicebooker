package hu.progmasters.servicebooker.service;

import hu.progmasters.servicebooker.domain.entity.Boose;
import hu.progmasters.servicebooker.dto.boose.BooseCreateCommand;
import hu.progmasters.servicebooker.dto.boose.BooseInfo;
import hu.progmasters.servicebooker.dto.boose.BooseUpdateCommand;
import hu.progmasters.servicebooker.exceptionhandling.boose.NoSuchBooseException;
import hu.progmasters.servicebooker.repository.BooseRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BooseService {

    private final BooseRepository repository;

    private final ModelMapper modelMapper;

    public BooseService(BooseRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public BooseInfo save(BooseCreateCommand command) {
        Boose toSave = modelMapper.map(command, Boose.class);
        Boose saved = repository.save(toSave);
        return toDto(saved);
    }

    @Transactional
    public BooseInfo findById(int id) {
        Boose boose = getFromIdOrThrow(id);
        return toDto(boose);
    }

    public Boose getFromIdOrThrow(int id) {
        return repository.findById(id).orElseThrow(
                () -> new NoSuchBooseException(id)
        );
    }

    @Transactional
    public List<BooseInfo> findAll() {
        return repository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BooseInfo update(int id, BooseUpdateCommand command) {
        Boose boose = getFromIdOrThrow(id);
        modelMapper.map(command, boose);
        return toDto(boose);
    }

    @Transactional
    public BooseInfo delete(int id) {
        Boose boose = getFromIdOrThrow(id);
        boose.setDeleted(true);
        return toDto(boose);
    }

    public void lockForUpdate(Boose boose) {
        repository.lockForUpdate(boose);
    }

    private BooseInfo toDto(Boose boose) {
        return modelMapper.map(boose, BooseInfo.class);
    }
}
