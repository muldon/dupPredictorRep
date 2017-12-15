

--DROP SEQUENCE recallrate_id;
CREATE SEQUENCE recallrate_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;



--DROP SEQUENCE experiment_id;

CREATE SEQUENCE experiment_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;


--DROP TABLE experiment;


CREATE TABLE experiment
(
  id integer NOT NULL,
  tag character varying(50),
  numberoftestedquestions integer,
  date character varying(30),
  ttweight numeric(3,2),
  ccweight numeric(3,2),
  bbweight numeric(3,2),
  btweight numeric(3,2),
  topictopicweight numeric(3,2),
  tbweight numeric(3,2),
  aaweight numeric(3,2),
  bm25k numeric(3,2),
  bm25b numeric(3,2),
  observacao character varying(500),
  app character varying(100),
  base character varying(200),
  maxresultsize integer,
  lote integer,
  estimateWeights boolean,
  duration character varying(50),
  trm character varying(30),
  tagtagweight numeric(3,2),
  CONSTRAINT experiment_pk PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE experiment
  OWNER TO postgres;




--DROP TABLE recallrate;
CREATE TABLE recallrate
(
  id integer NOT NULL,
  origem character varying(50),
  hits50000 integer,
  hits10000 integer,
  hits1000 integer,
  hits100 integer,
  hits50 integer,
  hits20 integer,
  hits10 integer,
  hits5 integer,
  hits1 integer,
  recallrate_50000 numeric(5,2),
  recallrate_10000 numeric(5,2),
  recallrate_1000 numeric(5,2),
  recallrate_100 numeric(5,2),
  recallrate_50 numeric(5,2),
  recallrate_20 numeric(5,2),
  recallrate_10 numeric(5,2),
  recallrate_5 numeric(5,2),
  recallrate_1 numeric(5,2),
  experiment_id integer,
  CONSTRAINT recall_pk PRIMARY KEY (id),
  CONSTRAINT experiment_fk FOREIGN KEY (experiment_id)
      REFERENCES experiment (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE recallrate
  OWNER TO postgres;




-- DROP TABLE topicvector;

CREATE TABLE topicvector
(
  postid integer NOT NULL,
  vectors text,
  CONSTRAINT topic_vector_pk PRIMARY KEY (postid)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE topicvector
  OWNER TO postgres;



ALTER TABLE posts ADD COLUMN tagssyn text;
ALTER TABLE posts ADD COLUMN code text;








