CREATE TABLE entity_cycle.address
(
    address_id   SERIAL      NOT NULL,
    city         TEXT        NOT NULL,
    time_created timestamptz NOT NULL,
    postal_code  TEXT        NOT NULL,
    street       TEXT        NOT NULL,
    PRIMARY KEY (address_id)
);
CREATE TABLE entity_cycle.creature
(
    creature_id SERIAL NOT NULL,
    age         INT    NOT NULL,
    name        TEXT   NOT NULL,
    saturation  INT    NOT NULL,
    birth_cycle INT    NOT NULL,
    address_id  INT    NOT NULL,
    PRIMARY KEY (creature_id),
    CONSTRAINT creature_address FOREIGN KEY (address_id)
        REFERENCES entity_cycle.address (address_id)
);

CREATE TABLE entity_cycle.dead_creature
(
    dead_creature_id SERIAL NOT NULL,
    cycles_lived     INT    NOT NULL,
    name             TEXT   NOT NULL,
    birth_cycle      INT    NOT NULL,
    cause_of_death   TEXT   NOT NULL,
    PRIMARY KEY (dead_creature_id)
);

CREATE TABLE entity_cycle.debuff
(
    debuff_id        SERIAL NOT NULL,
    saturation_drain INT    NOT NULL,
    description      TEXT   NOT NULL,
    value            TEXT   NOT NULL,
    CONSTRAINT value_in CHECK (
        value IN
        (
         'poisoning',
         'starvation',
         'fracture'
            )),
    UNIQUE (value),
    PRIMARY KEY (debuff_id)
);

CREATE TABLE entity_cycle.injury
(
    injury_id   SERIAL NOT NULL,
    debuff_id   INT    NOT NULL,
    creature_id INT    NOT NULL,
    PRIMARY KEY (injury_id),
    CONSTRAINT fk_injury_debuff FOREIGN KEY (debuff_id)
        REFERENCES entity_cycle.debuff (debuff_id),
    CONSTRAINT fk_injury_creature FOREIGN KEY (creature_id)
        REFERENCES entity_cycle.creature (creature_id)
);

CREATE TABLE entity_cycle.food
(
    food_id           SERIAL NOT NULL,
    nutritional_value INT    NOT NULL,
    description       TEXT   NOT NULL,
    creature_id       INT    NOT NULL,
    PRIMARY KEY (food_id),
    CONSTRAINT fk_food_creature FOREIGN KEY (creature_id)
        REFERENCES entity_cycle.creature (creature_id)
);