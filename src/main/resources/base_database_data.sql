-- Assumes you already have a PostgreSQL database set up,
-- and have set the user 'duckmanager' set up with privileges.
-- With a superuser account:
--     create user duckmanager;
--     alter user duckmanager with password 'I would never store a real password in git!';
--     grant all on database ducks to duckmanager;
--
-- On a production system, a separate account would set up the database tables and
-- the 'duckmanager' would only have select/insert/update/delete privileges, not data definition
-- language rights.

create sequence pond_id_sequence;
create table pond(
    id integer primary key default nextval('pond_id_sequence'),
    pond_name varchar(200) not null,
    pond_location text);
alter table pond add constraint pond_pond_name_uq
    unique (pond_name);
create sequence duck_id_sequence;
create table duck(
    id integer primary key default nextval('duck_id_sequence'),
    duck_name varchar(200) not null,
    tagged timestamptz not null);
alter table duck add constraint duck_duck_name_uq
    unique (duck_name);
create sequence duck_travel_id_sequence;
create table duck_travel(
    id integer primary key default nextval('duck_travel_id_sequence'),
    pond_id integer not null,
    duck_id integer not null,
    arrival timestamptz not null,
    departure timestamptz);
alter table duck_travel add constraint duck_travel_pond_fk
    foreign key (pond_id) references pond(id) on delete cascade;
alter table duck_travel add constraint duck_travel_duck_fk
    foreign key (duck_id) references duck(id) on delete cascade;
alter table duck_travel add constraint duck_travel_duck_pond_arrival_uq
    unique (pond_id, duck_id, arrival);

create view duck_travel_view as
    select d.duck_name, p.pond_name, dt.duck_id, dt.pond_id, dt.arrival, dt.departure
    from duck d, pond p, duck_travel dt
    where
        dt.duck_id = d.id and
        dt.pond_id = p.id
    order by d.duck_name, dt.arrival, dt.departure;

commit; -- you have autocommit off by default, right?

-- Add some test data
insert into pond (id, pond_name, pond_location)
    values
    (nextval('pond_id_sequence'), 'Duckville Lake', 'Duckville, Pennsylvania'),
    (nextval('pond_id_sequence'), 'Duckponds''R''Us', 'Duckerton, South Dakota'),
    (nextval('pond_id_sequence'), 'Janet''s Duck Paradise', 'Acmeville, Missouri');

insert into duck (id, duck_name, tagged)
    values
    (nextval('duck_id_sequence'), 'Quacky', date'2021-05-14'),
    (nextval('duck_id_sequence'), 'Morgansera', date'2021-08-01'),
    (nextval('duck_id_sequence'), 'Swifttailia', date'2022-02-03');

insert into duck_travel (id, pond_id, duck_id, arrival, departure)
    values
    (nextval('duck_travel_id_sequence'), (select min(id) from pond), (select min(id) from duck), date'2021-05-14', date'2021-06-12'),
    (nextval('duck_travel_id_sequence'), (select min(id) + 1 from pond), (select min(id) + 1 from duck), date'2021-08-01', null),
    (nextval('duck_travel_id_sequence'), (select min(id) + 2 from pond), (select min(id) + 2 from duck), date'2022-02-03', date'2022-02-07');
commit;


