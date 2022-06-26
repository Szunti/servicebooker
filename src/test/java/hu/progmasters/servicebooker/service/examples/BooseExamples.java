package hu.progmasters.servicebooker.service.examples;

import hu.progmasters.servicebooker.domain.entity.Boose;
import hu.progmasters.servicebooker.dto.boose.BooseCreateCommand;
import hu.progmasters.servicebooker.dto.boose.BooseInfo;
import hu.progmasters.servicebooker.dto.boose.BooseUpdateCommand;

public class BooseExamples {

    public static final int HAIRDRESSER_ID = 1;
    public static final String HAIRDRESSER_NAME = "Hairdresser Lisa";
    public static final String HAIRDRESSER_DESC = "I have a small shop on the Pearl Street.";
    public static final String HAIRDRESSER_UPDATED_NAME = "Hairdresser Alice";
    public static final String HAIRDRESSER_UPDATED_DESC = "Oops, I had a wrong name.";

    public static final int CLEANER_ID = 2;
    public static final String CLEANER_NAME = "Cleaner Jack";
    public static final String CLEANER_DESC = "Dust is my enemy.";

    public static Boose hairdresserNew() {
        Boose boose = new Boose();
        boose.setId(null);
        boose.setName(HAIRDRESSER_NAME);
        boose.setDescription(HAIRDRESSER_DESC);
        boose.setDeleted(false);
        return boose;
    }

    public static Boose hairdresser() {
        Boose boose = hairdresserNew();
        boose.setId(HAIRDRESSER_ID);
        return boose;
    }

    public static BooseInfo hairdresserInfo() {
        BooseInfo info = new BooseInfo();
        info.setId(HAIRDRESSER_ID);
        info.setName(HAIRDRESSER_NAME);
        info.setDescription(HAIRDRESSER_DESC);
        return info;
    }

    public static BooseCreateCommand hairdresserCreateCommand() {
        BooseCreateCommand command = new BooseCreateCommand();
        command.setName(HAIRDRESSER_NAME);
        command.setDescription(HAIRDRESSER_DESC);
        return command;
    }

    public static BooseUpdateCommand hairdresserUpdateCommand() {
        BooseUpdateCommand command = new BooseUpdateCommand();
        command.setName(HAIRDRESSER_UPDATED_NAME);
        command.setDescription(HAIRDRESSER_UPDATED_DESC);
        return command;
    }

    public static BooseInfo hairdresserUpdatedInfo() {
        BooseInfo info = hairdresserInfo();
        info.setName(HAIRDRESSER_UPDATED_NAME);
        info.setDescription(HAIRDRESSER_UPDATED_DESC);
        return info;
    }

    public static Boose cleaner() {
        Boose boose = new Boose();
        boose.setId(CLEANER_ID);
        boose.setName(CLEANER_NAME);
        boose.setDescription(CLEANER_DESC);
        boose.setDeleted(false);
        return boose;
    }
}
