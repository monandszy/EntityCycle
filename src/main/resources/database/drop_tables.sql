DELETE FROM  entity_cycle.food WHERE 1=1;
DELETE FROM  entity_cycle.injury WHERE 1=1;
DELETE FROM  entity_cycle.creature WHERE 1=1;
DELETE FROM  entity_cycle.debuff WHERE 1=1;
DELETE FROM  entity_cycle.dead_creature WHERE 1=1;
DELETE FROM  entity_cycle.address WHERE 1=1;

DROP TABLE IF EXISTS entity_cycle.food CASCADE;
DROP TABLE IF EXISTS entity_cycle.injury CASCADE;
DROP TABLE IF EXISTS entity_cycle.creature CASCADE;
DROP TABLE IF EXISTS entity_cycle.debuff CASCADE;
DROP TABLE IF EXISTS entity_cycle.dead_creature CASCADE;
DROP TABLE IF EXISTS entity_cycle.address CASCADE;

DROP SCHEMA entity_cycle;