create database nas;

\c nas;

create table file (
    id integer primary key generated always as identity,
    external_uuid uuid unique default gen_random_uuid() not null,
    name text,
    size bigint
);

create table permission (
    user_uuid text not null,
    file_id integer not null,
    roles text[] not null,
    foreign key (file_id) references file(id) on delete cascade
);

-- insert into file(name, size) values ('a1', 1), ('a2', 2), ('a3', 3), ('b1', 1);
-- insert into permission values ('a', 1, '{file:owner, file:viewer}'), ('a', 2, '{file:owner, file:viewer}'), ('a', 3, '{file:viewer}'), ('b', 3, '{file:owner, file:viewer}'), ('b', 4, '{file:owner, file:viewer}');

-- select roles from permission where file_id = 1 and 'file:viewer' = any(roles);

-- select f.external_uuid, f.name, f.size from file f where (f.id = any(select p.file_id from permission p where p.user_uuid = '' and 'file:viewer' = any(p.roles)));
