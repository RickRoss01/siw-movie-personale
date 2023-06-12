SET NAMES 'utf8';
SET CHARACTER SET utf8;
insert into image (id, image_path) values(nextval('hibernate_sequence'), 'images/HP1.png');
insert into image (id, image_path) values(nextval('hibernate_sequence'), 'images/fullmetaljacket.jpg');
insert into image (id, image_path) values(nextval('hibernate_sequence'), 'images/Non_e_un_paese_per_vecchi.jpg');
insert into image (id, image_path) values(nextval('hibernate_sequence'), 'images/The_founder.jpg');
insert into image (id, image_path) values(nextval('hibernate_sequence'), 'images/planet_of_the_apes.jpg');
insert into image (id, image_path) values(nextval('hibernate_sequence'), 'images/614uLDzaUvL._SY445_.jpg');
insert into image (id, image_path) values(nextval('hibernate_sequence'), 'images/yesterday film poster movie universal pictures-2.jpg');
insert into image (id, image_path) values(nextval('hibernate_sequence'), 'images/Tim_Burton_by_Gage_Skidmore.jpg');
insert into image (id, image_path) values(nextval('hibernate_sequence'), 'images/440px-Himesh_Patel_2019_(cropped).jpg');
insert into image (id, image_path) values(nextval('hibernate_sequence'), 'images/440px-Danny_Boyle_2019_(cropped).jpg');
insert into image (id, image_path) values(nextval('hibernate_sequence'), 'images/440px-Tim_Roth_by_Gage_Skidmore_2.jpg');
insert into image (id, image_path) values(nextval('hibernate_sequence'), 'images/Michael_Keaton_by_Gage_Skidmore.jpg');
insert into image (id, image_path) values(nextval('hibernate_sequence'), 'images/Kubrick_on_the_set_of_Barry_Lyndon_(1975_publicity_photo)_crop.jpg');
insert into image (id, image_path) values(nextval('hibernate_sequence'), 'images/RLeeErmeyCrop.jpeg');
insert into image (id, image_path) values(nextval('hibernate_sequence'), 'images/Claudio_Santamaria.jpg');
insert into image (id, image_path) values(nextval('hibernate_sequence'), 'images/Gabriele_Mainetti,_September_2017.jpg');

insert into image (id, image_path) values(nextval('hibernate_sequence'), 'images/full_metal_jacket.png');
insert into image (id, image_path) values(nextval('hibernate_sequence'), 'images/full_metal_jacket_2.png');


insert into movie (id, title, primary_image_id, year,rating) values(nextval('hibernate_sequence'), 'Full metal jacket', 2, 1987,0);
update image set movie_id = 19 where id=17
update image set movie_id = 19 where id=18
insert into movie (id, title, primary_image_id, year,rating) values(nextval('hibernate_sequence'), 'Non e'' un paese per vecchi', 3,2007,0);
insert into movie (id, title, primary_image_id, year,rating) values(nextval('hibernate_sequence'), 'The founder', 4,2016,0);
insert into movie (id, title, primary_image_id, year,rating) values(nextval('hibernate_sequence'), 'Harry Potter e la pietra filosofale', 1,2001,0);
insert into movie (id, title, primary_image_id, year,rating) values(nextval('hibernate_sequence'), 'Il pianeta delle scimmie', 5,2001,0);
insert into movie (id, title, primary_image_id, year,rating) values(nextval('hibernate_sequence'), 'Lo chiamavano Jeeg Robot', 6,2015,0);
insert into movie (id, title, primary_image_id, year,rating) values(nextval('hibernate_sequence'), 'Yesterday', 7,2019,0);

insert into artist (id, name, surname, image_id, date_of_birth,biography) values(nextval('hibernate_sequence'), 'Tim', 'Burton', 8,'1958-08-25','Tim Burton e nato il 25 agosto 1958. Luogo di nascita: Usa. È conosciuto come produttore e regista. È celebre per aver partecipato a Edward mani di forbice (1990), Frankenweenie (2012) e La sposa cadavere (2005). È stata sposato con Lena Gieseke.');
insert into artist (id, name, surname, image_id, date_of_birth,biography) values(nextval('hibernate_sequence'), 'Himesh', 'Patel', 9,'1990-10-13','Himesh Patel è nato il 13 ottobre 1990. Luogo di nascita: Inghilterra, Regno Unito. È conosciuto come attore e sceneggiatore. È celebre per aver partecipato a Yesterday (2019), Dont Look Up (2021) e Station Eleven (2021).');
insert into artist (id, name, surname, image_id, date_of_birth,biography) values(nextval('hibernate_sequence'), 'Danny', 'Boyle', 10,'1956-10-20','Danny Boyle è nato il 20 ottobre 1956. Luogo di nascita: Inghilterra, Regno Unito. È conosciuto come regista e produttore. È celebre per aver partecipato a 127 ore (2010), 28 giorni dopo (2002) e The Millionaire (2008).');
insert into artist (id, name, surname, image_id, date_of_birth,biography) values(nextval('hibernate_sequence'), 'Tim', 'Roth', 11,'1961-05-14','Tim Roth è nato il 14 maggio 1961. Luogo di nascita: Inghilterra, Regno Unito. È conosciuto come attore e produttore. È celebre per aver partecipato a Rob Roy (1995), The Hateful Eight (2015) e Pulp Fiction (1994). Ha due figli/e.');
insert into artist (id, name, surname, image_id, date_of_birth,biography) values(nextval('hibernate_sequence'), 'Michael', 'Keaton', 12,'1951-09-05','Michael Keaton è nato il 5 settembre 1951. Luogo di nascita: Usa. È conosciuto come attore e produttore. È celebre per aver partecipato a Birdman o (L imprevedibile virtù dell ignoranza) (2014), Il caso Spotlight (2015) e The Founder (2016).');
insert into artist (id, name, surname, image_id, date_of_birth,biography) values(nextval('hibernate_sequence'), 'Stanley', 'Kubrick', 13,'1928-07-26','Stanley Kubrick è nato il 26 luglio 1928. Luogo di nascita: Usa. È conosciuto come regista e sceneggiatore. È celebre per 2001: Odissea nello spazio (1968) e Arancia meccanica (1971).  Morì il 7 marzo 1999. Luogo di morte: Inghilterra, Regno Unito.');
insert into artist (id, name, surname, image_id, date_of_birth,biography) values(nextval('hibernate_sequence'), 'Ronald Lee', 'Ermey', 14,'1944-03-24','R. Lee Ermey è nato il 24 marzo 1944. Luogo di nascita: Usa. È conosciuto come attore e produttore. È celebre per aver partecipato a Full Metal Jacket (1987), Seven (1995) e Non aprite quella porta (2003). Morì il 15 aprile 2018. Luogo di morte: Usa.');	
insert into artist (id, name, surname, image_id, date_of_birth,biography) values(nextval('hibernate_sequence'), 'Claudio', 'Santamaria', 15,'1974-07-22','Claudio Santamaria è nato il 22 luglio 1974. Luogo di nascita: Roma, Italia. È conosciuto come attore e regista. È celebre per aver partecipato a Casino Royale (2006), Romanzo criminale (2005) e Lo chiamavano Jeeg Robot (2015). Ha un/a figlio/a.');
insert into artist (id, name, surname, image_id, date_of_birth,biography) values(nextval('hibernate_sequence'), 'Gabriele', 'Mainetti', 16,'1976-11-07','Gabriele Mainetti è nato il 7 novembre 1976. Luogo di nascita: Roma, Italia. È conosciuto come regista e produttore. È celebre per aver partecipato a Lo chiamavano Jeeg Robot (2015), Freaks Out (2021) e Tiger Boy (2012).');

insert into movie_actors (starred_movies_id,actors_id) values(19,29);