-- create table scores
CREATE TABLE scores (
	time TEXT NOT NULL,
	score INTEGER NOT NULL
);

-- select top five scores
SELECT score from scores ORDER BY score DESC LIMIT 5;

-- delete line with smallest scores
DELETE FROM scores WHERE score = (SELECT MIN(score) FROM scores);

-- delete table scores
DROP TABLE scores;

-- create table games
CREATE TABLE games (
	time TEXT NOT NULL,
    ip TEXT NOT NULL,
	data INTEGER NOT NULL
);

-- delete table games
DROP TABLE games;
