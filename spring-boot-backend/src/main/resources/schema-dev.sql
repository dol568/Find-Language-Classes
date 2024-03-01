DROP TABLE if EXISTS comment;
DROP TABLE if EXISTS `role`;
DROP TABLE if EXISTS training_class;
DROP TABLE if EXISTS `user`;
DROP TABLE if EXISTS user_following;
DROP TABLE if EXISTS users_roles;
DROP TABLE if EXISTS user_training_class;

DROP TABLE if EXISTS chat_room_user;

CREATE TABLE chat_room_user
(
    date_joined    datetime(6),
    username       varchar(255),
    full_name      varchar(255),
    training_class bigint,
    primary key (username)
);

CREATE TABLE comment
(
    author_user    varchar(255),
    created_at     datetime(6),
    id             bigint not null auto_increment,
    training_class bigint,
    body           varchar(255),
    primary key (id)
);

CREATE TABLE `role`
(
    id   bigint      not null auto_increment,
    name varchar(40) not null,
    primary key (id)
);

CREATE TABLE training_class
(
    day_of_week integer not null,
    total_spots integer not null,
    id          bigint  not null auto_increment,
    address     varchar(255),
    category    varchar(255),
    city        varchar(255),
    country     varchar(255),
    description varchar(255),
    postal_code varchar(255),
    province    varchar(255),
    time        varchar(255),
    title       varchar(255),
    primary key (id)
);

CREATE TABLE `user`
(
    is_enabled    bit          not null,
    is_non_locked bit          not null,
    id            varchar(255) not null primary key,
    user_name     varchar(45)  not null,
    password      varchar(64)  not null,
    email         varchar(125) not null,
    bio           varchar(1000),
    full_name     varchar(255),
    photo_url     varchar(255)
);

CREATE TABLE user_following
(
    from_user varchar(255),
    id        bigint not null auto_increment,
    to_user   varchar(255),
    primary key (id)
);

CREATE TABLE users_roles
(
    role_id bigint       not null,
    user_id varchar(255) not null,
    primary key (role_id, user_id)
);

CREATE TABLE `user_training_class`
(
    is_host        bit    not null,
    date_joined    datetime(6),
    id             bigint not null primary key auto_increment,
    training_class bigint,
    `user`         varchar(255)
);