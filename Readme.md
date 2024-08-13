# Project Overview

**Written:** 2024.02.05 - 2024.02.08

## Project Summary
This is a practice project aimed at using Hibernate without relying on Spring JPA. The project employs Flyway for managing database migrations in a PostgreSQL environment.

## Key Features
- **Architecture:** The project follows a layered architecture design, incorporating MapStruct mappers and a DAO layer. This approach ensures dependency inversion and isolates JPA repositories.
- **CycleService:** `Creature` entities are initialized. These creatures are assigned food, and those with insufficient saturation die. As creatures age, the probability of obtaining food decreases, while the likelihood of receiving a debuff increases. 