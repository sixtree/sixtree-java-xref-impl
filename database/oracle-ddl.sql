CREATE USER xref IDENTIFIED BY xref 

CREATE TABLE entitytype (
  id NUMBER(10) NOT NULL,
  entitytype VARCHAR2(45) NOT NULL,
  tenant VARCHAR2(45) NOT NULL,
  PRIMARY KEY (id)
 )
;

-- Generate ID using sequence and trigger
CREATE SEQUENCE entitytype_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER entitytype_seq_tr
 BEFORE INSERT ON entitytype FOR EACH ROW
 WHEN (NEW.id IS NULL)
BEGIN
 SELECT entitytype_seq.NEXTVAL INTO :NEW.id FROM DUAL;
END;
/

CREATE INDEX ENTITYTYPE ON entitytype (entitytype ASC);
CREATE INDEX TENANT ON entitytype (tenant ASC, entitytype ASC);


CREATE TABLE relation (
  id NUMBER(10) NOT NULL,
  commonid VARCHAR2(45) NOT NULL,
  entitytype_id NUMBER(10) NOT NULL,
  PRIMARY KEY (id)
 ,
  CONSTRAINT fk_relation_entitytype1
    FOREIGN KEY (entitytype_id)
    REFERENCES entitytype (id)
   )
;

-- Generate ID using sequence and trigger
CREATE SEQUENCE relation_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER relation_seq_tr
 BEFORE INSERT ON relation FOR EACH ROW
 WHEN (NEW.id IS NULL)
BEGIN
 SELECT relation_seq.NEXTVAL INTO :NEW.id FROM DUAL;
END;
/

CREATE INDEX COMMONID ON relation (commonid ASC);
CREATE INDEX fk_relation_entitytype1_idx ON relation (entitytype_id ASC);

CREATE TABLE reference (
  id NUMBER(10) NOT NULL,
  relation_id NUMBER(10) NOT NULL,
  endpoint VARCHAR2(45) NOT NULL,
  endpointid VARCHAR2(45) NOT NULL,
  PRIMARY KEY (id)
 ,
  CONSTRAINT fk_reference_relation
    FOREIGN KEY (relation_id)
    REFERENCES relation (id)
   )
 ;

-- Generate ID using sequence and trigger
CREATE SEQUENCE reference_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER reference_seq_tr
 BEFORE INSERT ON reference FOR EACH ROW
 WHEN (NEW.id IS NULL)
BEGIN
 SELECT reference_seq.NEXTVAL INTO :NEW.id FROM DUAL;
END;
/

CREATE INDEX fk_reference_relation_idx ON reference (relation_id ASC);
CREATE INDEX ENDPOINT ON reference (endpoint ASC, endpointid ASC);

