package hu.progmasters.servicebooker.service.examples;

import hu.progmasters.servicebooker.domain.entity.Boose;
import hu.progmasters.servicebooker.domain.entity.WeeklyPeriod;
import hu.progmasters.servicebooker.dto.weeklyperiod.WeeklyPeriodCreateCommand;
import hu.progmasters.servicebooker.dto.weeklyperiod.WeeklyPeriodInfo;
import hu.progmasters.servicebooker.dto.weeklyperiod.WeeklyPeriodUpdateCommand;
import hu.progmasters.servicebooker.util.DayOfWeekTime;

public class WeeklyPeriodExamples {

    public static final int MONDAY_ID = 3;
    public static final DayOfWeekTime MONDAY_START = DayOfWeekTime.parse("Mon 08:00");
    public static final DayOfWeekTime MONDAY_END = DayOfWeekTime.parse("Mon 12:00");
    public static final String MONDAY_COMMENT = "Worst part of the week.";
    public static final String MONDAY_UPDATED_COMMENT = "Actually, Tuesday is the worst.";

    public static final int WEDNESDAY_ID = 4;
    public static final DayOfWeekTime WEDNESDAY_START = DayOfWeekTime.parse("Tue 18:17:52");
    public static final DayOfWeekTime WEDNESDAY_END = DayOfWeekTime.parse("Wed 20:04:19");
    public static final String WEDNESDAY_COMMENT = "A long period.";

    public static WeeklyPeriodCreateCommand mondayCreateCommand() {
        WeeklyPeriodCreateCommand command = new WeeklyPeriodCreateCommand();
        command.setStart(MONDAY_START);
        command.setEnd(MONDAY_END);
        command.setComment(MONDAY_COMMENT);
        return command;
    }

    public static WeeklyPeriod mondayNew(Boose boose) {
        WeeklyPeriod weeklyPeriod = new WeeklyPeriod();
        weeklyPeriod.setId(null);
        weeklyPeriod.setStart(MONDAY_START);
        weeklyPeriod.setEnd(MONDAY_END);
        weeklyPeriod.setComment(MONDAY_COMMENT);
        weeklyPeriod.setBoose(boose);
        return weeklyPeriod;
    }

    public static WeeklyPeriod monday(Boose boose) {
        WeeklyPeriod weeklyPeriod = mondayNew(boose);
        weeklyPeriod.setId(MONDAY_ID);
        return weeklyPeriod;
    }

    public static WeeklyPeriodInfo mondayInfo() {
        WeeklyPeriodInfo info = new WeeklyPeriodInfo();
        info.setId(MONDAY_ID);
        info.setStart(MONDAY_START);
        info.setEnd(MONDAY_END);
        info.setComment(MONDAY_COMMENT);
        return info;
    }

    public static WeeklyPeriod wednesday(Boose boose) {
        WeeklyPeriod weeklyPeriod = new WeeklyPeriod();
        weeklyPeriod.setId(WEDNESDAY_ID);
        weeklyPeriod.setStart(WEDNESDAY_START);
        weeklyPeriod.setEnd(WEDNESDAY_END);
        weeklyPeriod.setComment(WEDNESDAY_COMMENT);
        weeklyPeriod.setBoose(boose);
        return weeklyPeriod;
    }

    public static WeeklyPeriodUpdateCommand mondayUpdateCommand() {
        WeeklyPeriodUpdateCommand command = new WeeklyPeriodUpdateCommand();
        command.setComment(MONDAY_UPDATED_COMMENT);
        return command;
    }

    public static WeeklyPeriodInfo mondayUpdatedInfo() {
        WeeklyPeriodInfo info = mondayInfo();
        info.setComment(MONDAY_UPDATED_COMMENT);
        return info;
    }
}
