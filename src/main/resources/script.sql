CREATE TABLE IF NOT EXISTS user_acc
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    first_name VARCHAR(255)                        NOT NULL,
    last_name  VARCHAR(255)                        NOT NULL,
    username   VARCHAR(255) UNIQUE                 NOT NULL,
    password   VARCHAR(255)                        NOT NULL,
    is_active  BOOLEAN                             NOT NULL DEFAULT TRUE,
    CONSTRAINT user_acc_pk PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS trainee
(
    id            BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    date_of_birth DATE,
    address       TEXT,
    user_id       BIGINT UNIQUE                       NOT NULL,
    CONSTRAINT trainee_pk PRIMARY KEY (id),
    CONSTRAINT fk_trainee_user FOREIGN KEY (user_id) REFERENCES user_acc (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS trainer
(
    id               BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    training_type_id BIGINT UNIQUE                       NOT NULL,
    user_id          BIGINT UNIQUE                       NOT NULL,
    CONSTRAINT trainer_pk PRIMARY KEY (id),
    CONSTRAINT fk_trainer_user FOREIGN KEY (user_id) REFERENCES user_acc (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS training_type
(
    id                 BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    training_type_name VARCHAR(255)                        NOT NULL UNIQUE,
    CONSTRAINT training_type_pk PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS training
(
    id                BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    trainee_id        BIGINT                              NOT NULL,
    trainer_id        BIGINT                              NOT NULL,
    training_name     VARCHAR(255)                        NOT NULL,
    training_type_id  BIGINT                              NOT NULL,
    training_date     DATE                                NOT NULL,
    training_duration BIGINT                              NOT NULL CHECK (training_duration > 0),
    CONSTRAINT training_pk PRIMARY KEY (id),

    CONSTRAINT fk_training_trainee FOREIGN KEY (trainee_id) REFERENCES trainee (id) ON DELETE CASCADE,
    CONSTRAINT fk_training_trainer FOREIGN KEY (trainer_id) REFERENCES trainer (id) ON DELETE CASCADE,
    CONSTRAINT fk_training_type FOREIGN KEY (training_type_id) REFERENCES training_type (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS trainee_trainer
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    trainee_id BIGINT                              NOT NULL,
    trainer_id BIGINT                              NOT NULL,

    CONSTRAINT trainee_trainer_pk PRIMARY KEY (id),
    CONSTRAINT fk_map_trainee FOREIGN KEY (trainee_id) REFERENCES trainee (id) ON DELETE CASCADE,
    CONSTRAINT fk_map_trainer FOREIGN KEY (trainer_id) REFERENCES trainer (id) ON DELETE CASCADE
);

INSERT INTO training_type(training_type_name)
VALUES ('Strength training'),
       ('Endurance training'),
       ('Speed training'),
       ('Flexibility training'),
       ('Skill training'),
       ('Cardio training'),
       ('Upper-body training'),
       ('Mental training'),
       ('Legs training'),
       ('Full-body training');

