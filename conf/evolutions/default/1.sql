# --- First DB Schema

# --- !Ups

CREATE TABLE simulation_task (
	id				SERIAL PRIMARY KEY,
	name			VARCHAR(255) NOT NULL,
	description		VARCHAR(255),
	status			VARCHAR(10)
);

# --- !Downs

DROP TABLE IF EXISTS simulation_task