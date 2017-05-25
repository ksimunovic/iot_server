CREATE TABLE uredaji (
  id int NOT NULL DEFAULT 1,
  naziv varchar(30) NOT NULL DEFAULT '',
  latitude float(6) NOT NULL DEFAULT 0.0,
  longitude float(6) NOT NULL DEFAULT 0.0,
  status int NOT NULL DEFAULT 0,
  vrijeme_promjene timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,  
  vrijeme_kreiranja timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

CREATE TABLE meteo (
  idMeteo integer NOT NULL 
                PRIMARY KEY GENERATED ALWAYS AS IDENTITY 
                (START WITH 1, INCREMENT BY 1),
  id integer NOT NULL,				
  adresaStanice varchar(255) NOT NULL DEFAULT '',
  latitude float(6) NOT NULL DEFAULT 0.0,
  longitude float(6) NOT NULL DEFAULT 0.0,
  vrijeme varchar(25) NOT NULL DEFAULT '',
  vrijemeOpis varchar(25) NOT NULL DEFAULT '',
  temp float NOT NULL DEFAULT -999,
  tempMin float NOT NULL DEFAULT -999,
  tempMax float NOT NULL DEFAULT -999,
  vlaga float NOT NULL DEFAULT -999,
  tlak float NOT NULL DEFAULT -999,
  vjetar float NOT NULL DEFAULT -999,
  vjetarSmjer float NOT NULL DEFAULT -999,
  preuzeto timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT meteo_FK1 FOREIGN KEY (id) REFERENCES uredaji (id) 
);


INSERT INTO uredaji (id,naziv,latitude,longitude) VALUES 
 (51,'Stud. dom - ulazna vrata',46.309099,16.348095),
 (52,'Stud. dom - dvorišna vrata',46.309099,16.348095),
 (53,'Stud. dom - dvorana 1',46.309099,16.348095),
 (112,'Gradska dvorana - velika',46.317221,16.360760),
 (113,'Gradska dvorana - mala',46.317221,16.360760),
 (121,'Motiènjak/Aquacity - jezero', 46.307885,16.390317);