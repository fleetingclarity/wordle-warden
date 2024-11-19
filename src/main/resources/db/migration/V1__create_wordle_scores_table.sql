create table wordle_scores (
    id serial primary key,
    user_id varchar(50) not null,
    user_name varchar(100),
    message_id varchar(50) unique not null,
    score integer not null,
    puzzle_number integer not null,
    puzzle_date date not null,
    created_at timestamp default current_timestamp
)
