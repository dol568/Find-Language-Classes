INSERT INTO `user` (id, bio, email, full_name, password, photo_url, user_name, is_enabled, is_non_locked)
VALUES ('72308add-0813-4e37-9601-0eca8df8ca89', 'thom bio', 'thomyorke@gmail.com', 'Thom Yorke',
        '$2a$10$yyR/BmyGcjJpcHnovCPxSuTXp98XAd1aYheBGbHi9jQvCnfQgprGu', '', 'thomy568',
        TRUE, TRUE);
INSERT INTO `user` (id, bio, email, full_name, password, photo_url, user_name, is_enabled, is_non_locked)
VALUES ('72308add-0813-4e37-9602-0eca8df8ca89', 'joshes bio', 'joshhomme@gmail.com', 'Josh Homme',
        '$2a$10$yyR/BmyGcjJpcHnovCPxSuTXp98XAd1aYheBGbHi9jQvCnfQgprGu', '', 'joshh568',
        TRUE, TRUE);

INSERT INTO `role` (id, name)
VALUES (1, 'USER');

INSERT INTO users_roles (role_id, user_id)
VALUES (1, '72308add-0813-4e37-9601-0eca8df8ca89');
INSERT INTO users_roles (role_id, user_id)
VALUES (1, '72308add-0813-4e37-9602-0eca8df8ca89');

INSERT INTO training_class (day_of_week, time, total_spots, id, address, category, city, country, description,
                            postal_code, province, title)
VALUES (3, '15:30', 15, 1, 'Radiohead St', 'German', 'Warsaw', 'Poland',
        'Maecenas sodales ex id blandit tincidunt. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Curabitur id bibendum risus.',
        '16-400', 'Mazowieckie', 'Beginner german class');
INSERT INTO `training_class` (day_of_week, time, total_spots, id, address, category, city, country, description,
                            postal_code, province, title)
VALUES (3, '12:30', 10, 2, 'Qotsa St', 'French', 'Warsaw', 'Poland',
        'Nullam varius aliquam vulputate. Phasellus vulputate ligula in neque vulputate commodo. Sed vitae lectus ligula. Ut vehicula sem et nibh volutpat posuere. Mauris et sem pulvinar, ornare libero vitae, ultricies arcu.',
        '16-400', 'Mazowieckie', 'Beginner french class');
INSERT INTO `user_training_class` (date_joined, is_host, training_class, `user`)
VALUES (CURRENT_TIMESTAMP, TRUE, 1, '72308add-0813-4e37-9601-0eca8df8ca89');
INSERT INTO `user_training_class` (date_joined, is_host, training_class, `user`)
VALUES (CURRENT_TIMESTAMP, TRUE, 2, '72308add-0813-4e37-9602-0eca8df8ca89');
INSERT INTO `user_training_class` (date_joined, is_host, training_class, `user`)
VALUES (CURRENT_TIMESTAMP, FALSE, 1, '72308add-0813-4e37-9602-0eca8df8ca89');
