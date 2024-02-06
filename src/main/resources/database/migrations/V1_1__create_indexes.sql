CREATE INDEX idx_creature_age ON entity_cycle.creature(age, saturation);
CREATE INDEX idx_dead_creature_cycles_lived
    ON entity_cycle.dead_creature(cycles_lived);