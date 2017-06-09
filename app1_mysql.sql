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
  idMeteo integer NOT NULL auto_increment,
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
  CONSTRAINT meteo_FK1 FOREIGN KEY (id) REFERENCES uredaji (id),
    PRIMARY KEY (idMeteo)
);

INSERT INTO uredaji (id,naziv,latitude,longitude) VALUES
 (51,'Stud. dom - ulazna vrata',46.309099,16.348095),
 (52,'Stud. dom - dvorišna vrata',46.309099,16.348095),
 (53,'Stud. dom - dvorana 1',46.309099,16.348095),
 (112,'Gradska dvorana - velika',46.317221,16.360760),
 (113,'Gradska dvorana - mala',46.317221,16.360760),
 (121,'Motiènjak/Aquacity - jezero', 46.307885,16.390317);CREATE TABLE korisnici (
  id integer not null auto_increment,
  korisnicko_ime VARCHAR(255),
  lozinka VARCHAR(255) not null,
  primary key (id)
);

CREATE TABLE dnevnik (
  id integer NOT NULL auto_increment,
  korisnik varchar(25) NOT NULL DEFAULT '',
  url varchar(255) NOT NULL DEFAULT '',
  ipadresa varchar(25) NOT NULL DEFAULT '',
  vrijeme timestamp,
  trajanje int NOT NULL DEFAULT 0,
  status int NOT NULL DEFAULT 0,
  primary key (id)
);


CREATE TABLE `korisnici` (
  `id` int(11) NOT NULL auto_increment,
  `korisnicko_ime` varchar(255) DEFAULT NULL,
  `lozinka` varchar(255) NOT NULL,
  `prezime` varchar(255) DEFAULT "",
  `email` varchar(255) DEFAULT "",
  primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `zahtjevi` (
  `id` int(11) NOT NULL,
  `korisnik` varchar(255) NOT NULL,
  `naredba` varchar(255) NOT NULL,
  `odgovor` VARCHAR(255) NOT NULL,
  `vrijeme` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO `uredaji` (`id`, `naziv`, `latitude`, `longitude`, `status`) VALUES

( 1,'FOI 1 - dvorana  1',46.307719,16.338094,0), 
 ( 2,'FOI 1 - dvorana  2',46.307719,16.338094,0), 
 ( 3,'FOI 1 - dvorana  3',46.307719,16.338094,0), 
 ( 4,'FOI 1 - dvorana  4',46.307719,16.338094,0), 
 (5,'FOI 1 - dvorana  5',46.307719,16.338094,0), 
 (6,'FOI 1 - dvorana  6',46.307719,16.338094,0), 
 (7,'FOI 1 - dvorana  7',46.307719,16.338094,0), 
 (8,'FOI 1 - dvorana  8',46.307719,16.338094,0), 
(9, 'Stud. dom - dvorana 1', 46.3091, 16.3481,0),
(10, 'Stud. dom - dvorana 2', 46.3091, 16.3481,0),
(11, 'Stud. dom - dvorana 3', 46.3091, 16.3481,0),
(12, 'Stud. dom - dvorana 4', 46.3091, 16.3481,0),
(13, 'Stud. dom - dvorana 5', 46.3091, 16.3481,0),
(14, 'Stud. dom - dvorana 6', 46.3091, 16.3481,0),
(15, 'Stud. dom - dvorana 7', 46.3091, 16.3481,0),
 (16,'Stud. restoran - ulazna vrata',46.308388,16.347655,0),
 (17,'Bus kolodvor - ulazna vrata',46.304423,16.334351,0),
 (18,'Želj. kolodvor - ulazna vrata',46.305446,116.346335,0),
(19, 'Gradska dvorana - velika', 46.3172, 16.3608,0),
 (20,'Stari grad - muzej',46.310049,16.334130,0),
(21, 'Motiènjak/Aquacity - jezero', 46.3079, 16.3903,0);