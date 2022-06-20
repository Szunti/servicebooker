ALTER TABLE weekly_period
    ADD comment VARCHAR(5000) AFTER end;

ALTER TABLE specific_period
    ADD comment VARCHAR(5000) AFTER end;

ALTER TABLE booking
    ADD comment VARCHAR(5000) after end;