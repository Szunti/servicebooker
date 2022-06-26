package hu.progmasters.servicebooker.service.examples;

import hu.progmasters.servicebooker.domain.entity.Boose;
import hu.progmasters.servicebooker.domain.entity.SpecificPeriod;
import hu.progmasters.servicebooker.dto.specificperiod.SpecificPeriodCreateCommand;
import hu.progmasters.servicebooker.dto.specificperiod.SpecificPeriodInfo;
import hu.progmasters.servicebooker.dto.specificperiod.SpecificPeriodUpdateCommand;

import java.time.LocalDateTime;

public class SpecificPeriodExamples {

    public static final int JUN22_ID = 3;
    public static final LocalDateTime JUN22_START = LocalDateTime.parse("2022-06-22T08:00");
    public static final LocalDateTime JUN22_END = LocalDateTime.parse("2022-06-22T10:00");
    public static final String JUN22_COMMENT = "Can work this day.";
    public static final String JUN22_UPDATED_COMMENT = "Can work this day, but only this time.";

    public static final int JUN21_ID = 4;
    public static final LocalDateTime JUN21_START = LocalDateTime.parse("2022-06-21T18:50:23");
    public static final LocalDateTime JUN21_END = LocalDateTime.parse("2022-06-22T06:02:53");
    public static final String JUN21_COMMENT = "A long period.";

    public static SpecificPeriodCreateCommand jun22CreateCommand() {
        SpecificPeriodCreateCommand command = new SpecificPeriodCreateCommand();
        command.setStart(JUN22_START);
        command.setEnd(JUN22_END);
        command.setComment(JUN22_COMMENT);
        command.setBookable(true);
        return command;
    }

    public static SpecificPeriod jun22New(Boose boose) {
        SpecificPeriod specificPeriod = new SpecificPeriod();
        specificPeriod.setId(null);
        specificPeriod.setStart(JUN22_START);
        specificPeriod.setEnd(JUN22_END);
        specificPeriod.setComment(JUN22_COMMENT);
        specificPeriod.setBookable(true);
        specificPeriod.setBoose(boose);
        return specificPeriod;
    }

    public static SpecificPeriod jun22(Boose boose) {
        SpecificPeriod specificPeriod = jun22New(boose);
        specificPeriod.setId(JUN22_ID);
        return specificPeriod;
    }

    public static SpecificPeriodInfo jun22Info() {
        SpecificPeriodInfo info = new SpecificPeriodInfo();
        info.setId(JUN22_ID);
        info.setStart(JUN22_START);
        info.setEnd(JUN22_END);
        info.setComment(JUN22_COMMENT);
        info.setBookable(true);
        return info;
    }

    public static SpecificPeriod jun21(Boose boose) {
        SpecificPeriod specificPeriod = new SpecificPeriod();
        specificPeriod.setId(JUN21_ID);
        specificPeriod.setStart(JUN21_START);
        specificPeriod.setEnd(JUN21_END);
        specificPeriod.setComment(JUN21_COMMENT);
        specificPeriod.setBookable(false);
        specificPeriod.setBoose(boose);
        return specificPeriod;
    }

    public static SpecificPeriodUpdateCommand jun22UpdateCommand() {
        SpecificPeriodUpdateCommand command = new SpecificPeriodUpdateCommand();
        command.setComment(JUN22_UPDATED_COMMENT);
        return command;
    }

    public static SpecificPeriodInfo jun22UpdatedInfo() {
        SpecificPeriodInfo info = jun22Info();
        info.setComment(JUN22_UPDATED_COMMENT);
        return info;
    }
}
