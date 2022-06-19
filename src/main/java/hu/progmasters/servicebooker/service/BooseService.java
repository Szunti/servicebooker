package hu.progmasters.servicebooker.service;

import hu.progmasters.servicebooker.domain.entity.Boose;
import hu.progmasters.servicebooker.dto.boose.BooseCreateCommand;
import hu.progmasters.servicebooker.dto.boose.BooseInfo;
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
        return modelMapper.map(saved, BooseInfo.class);
    }

    @Transactional
    public BooseInfo findById(int id) {
        Boose boose = getFromIdOrThrow(id);
        return modelMapper.map(boose, BooseInfo.class);
    }

    @Transactional
    public List<BooseInfo> findAll() {
        return repository.findAll().stream()
                .map(boose -> modelMapper.map(boose, BooseInfo.class))
                .collect(Collectors.toList());
    }

    public void lockForUpdate(Boose boose) {
        repository.lockForUpdate(boose);
    }

    public Boose getFromIdOrThrow(int id) {
        return repository.findById(id).orElseThrow(
                () -> new NoSuchBooseException(id)
        );
    }
}
