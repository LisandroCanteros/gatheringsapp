create table event_template (
    id bigserial primary key,
    name varchar(120) not null,
    description varchar(500),
    timezone varchar(64) not null,
    recurrence_type varchar(32) not null,
    recurrence_interval integer,
    weekly_day smallint,
    start_date date not null,
    end_date date,
    start_time time not null,
    active boolean not null default true,
    created_at timestamp not null default now()
);

create table template_sub_activity (
    id bigserial primary key,
    template_id bigint not null references event_template(id) on delete cascade,
    name varchar(120) not null,
    position integer not null
);

create table event_occurrence (
    id bigserial primary key,
    template_id bigint not null references event_template(id) on delete cascade,
    occurrence_date date not null,
    join_code varchar(20) not null unique,
    created_at timestamp not null default now(),
    unique(template_id, occurrence_date)
);

create table occurrence_sub_activity (
    id bigserial primary key,
    occurrence_id bigint not null references event_occurrence(id) on delete cascade,
    template_sub_activity_id bigint,
    name varchar(120) not null,
    position integer not null
);

create table rsvp_vote (
    id bigserial primary key,
    occurrence_sub_activity_id bigint not null references occurrence_sub_activity(id) on delete cascade,
    participant_name varchar(120) not null,
    participant_email varchar(255),
    yes_no boolean not null,
    created_at timestamp not null default now()
);

create index idx_occurrence_date on event_occurrence(occurrence_date);
create index idx_rsvp_sub_activity on rsvp_vote(occurrence_sub_activity_id);
