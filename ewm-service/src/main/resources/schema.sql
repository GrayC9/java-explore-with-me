  CREATE TABLE IF NOT EXISTS users (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  email VARCHAR(50),
  name VARCHAR(100),
  CONSTRAINT uq_user_email UNIQUE (email));

  CREATE TABLE IF NOT EXISTS categories (
  id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name VARCHAR(50));

  CREATE TABLE IF NOT EXISTS events (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  annotation VARCHAR(500),
  category_id INTEGER,
  confirmed_requests INTEGER,
  event_date TIMESTAMP,
  initiator_id BIGINT,
  is_paid BOOLEAN,
  title VARCHAR(100),
  views BIGINT,
  CONSTRAINT fk_events_to_categories FOREIGN KEY(category_id) REFERENCES categories(id),
  CONSTRAINT fk_events_to_users FOREIGN KEY(initiator_id) REFERENCES users(id));

  CREATE TABLE IF NOT EXISTS requests (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  created_at TIMESTAMP,
  event_id BIGINT,
  requester_id BIGINT,
  status VARCHAR(15),
  CONSTRAINT fk_requests_to_events FOREIGN KEY(event_id) REFERENCES events(id),
  CONSTRAINT fk_requests_to_users FOREIGN KEY(requester_id) REFERENCES users(id));

  CREATE TABLE IF NOT EXISTS compilations (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  is_pinned BOOLEAN,
  title VARCHAR(50),
  views BIGINT);

  CREATE TABLE IF NOT EXISTS compilation_events (
  compilation_id BIGINT,
  event_id BIGINT,
  CONSTRAINT compilation_events_pk PRIMARY KEY (compilation_id, event_id),
  CONSTRAINT fk_comp_events_to_comps FOREIGN KEY(compilation_id) REFERENCES compilations(id),
  CONSTRAINT fk_comp_events_to_events FOREIGN KEY(event_id) REFERENCES events(id));

