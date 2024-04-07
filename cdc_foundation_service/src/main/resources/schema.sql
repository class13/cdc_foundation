create table if not exists advert(
    id serial primary key,
    title text,
    status varchar(50)
);

alter table advert replica identity full;
