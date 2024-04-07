create table if not exists article(
    id serial primary key,
    title text,
    status varchar(50)
);

--- article needs replica identity full otherwise delete or update events will not contain the fields of the before state
alter table article replica identity full;

create table if not exists article_status_history(
    id serial primary key,
    article_id int not null,
    from_status varchar(50) not null,
    to_status varchar(50) not null,
    timestamp timestamp not null
);


