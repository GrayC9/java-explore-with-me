INSERT INTO users(email, name)
       VALUES ('tulip@gmail.com', 'tulip'),
       ('lilyA@gmail.com', 'lily'),
       ('lavender@gmail.com', 'lavender');


  INSERT INTO public.categories(name) VALUES ('прогулки'),
                                             ('поездки');

  INSERT INTO public.events(annotation, category_id, created_on, description, event_date, initiator_id, is_paid,
  title, loc_lat, loc_lon, participant_limit, request_moderation, published_on, state, confirmed_requests)
  VALUES ('Конная прогулка', 1, '2023-07-19 09:43:05.879', 'Прогулка по полю верхом', '2023-07-19 14:43:08.000',
  1, true, 'Прогулка', 60.03129959106445, -112.30819702148438, 10, false, '2023-07-19 09:43:08.829', 'PUBLISHED', 5);