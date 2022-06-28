ALTER TABLE specific_period
    ADD type VARCHAR(255) NOT NULL DEFAULT '' AFTER bookable;

UPDATE specific_period
SET type = CASE bookable
               WHEN TRUE THEN 'ADD_OR_REPLACE'
               WHEN FALSE THEN 'REMOVE'
    END;

ALTER TABLE specific_period
    DROP bookable;