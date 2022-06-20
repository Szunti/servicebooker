CREATE TABLE boose
(
    id          INT NOT NULL AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(5000),

    PRIMARY KEY (id)
);

CREATE TABLE weekly_period
(
    id       INT NOT NULL AUTO_INCREMENT,
    start    INT NOT NULL,
    end      INT NOT NULL,
    boose_id INT NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_weekly_period_boose FOREIGN KEY (boose_id) REFERENCES boose (id)
);

CREATE TABLE specific_period
(
    id       INT         NOT NULL AUTO_INCREMENT,
    start    DATETIME(0) NOT NULL,
    end      DATETIME(0) NOT NULL,
    bookable BOOLEAN     NOT NULL,
    boose_id INT         NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_specific_period_boose FOREIGN KEY (boose_id) REFERENCES boose (id)
);

CREATE TABLE customer
(
    id    INT          NOT NULL AUTO_INCREMENT,
    name  VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,

    PRIMARY KEY (id)
);

CREATE TABLE booking
(
    id          INT         NOT NULL AUTO_INCREMENT,
    start       DATETIME(0) NOT NULL,
    end         DATETIME(0) NOT NULL,
    boose_id    INT         NOT NULL,
    customer_id INT         NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_booking_boose FOREIGN KEY (boose_id) REFERENCES boose (id),
    CONSTRAINT fk_booking_customer FOREIGN KEY (customer_id) REFERENCES customer (id)
);
