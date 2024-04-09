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

create table if not exists category(
    id serial primary key,
    name text
);

create table if not exists article_category(
  category_id int not null references category(id),
  article_id int not null references  article(id),
  constraint article_category_pk primary key (category_id, article_id)
)

