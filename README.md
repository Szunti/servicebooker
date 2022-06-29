# Vizsgaremek

## Leírás

Backend egy olyan alkalmazáshoz, amivel különböző szolgáltatásokra lehet időpontot foglalni.

### Funkciók szolgáltatóknak

A szolgáltatók meghirdetik a szolgáltatásaikat és beállítják, mely időpontokban érhetőek el.
Az időpontokat kétféleképpen lehet megadni:

1. Hetente ismétlődő periódusok (WeeklyPeriod)
2. Egyszeri alkalmak, ezek felülírják a heti menetrendet (SpecificPeriod)

A lenti ábra mutatja, hogy ez hogy működik:
- A heti menetrend van bal oldalon. A WeeklyPeriod csak a hét napjaihoz kötött. 
- Foglalni viszont konkrét dátumokat lehet, ehhez a heti menetrendet le kell képezni ismétlésekkel az idővonalra. Ez a második oszlop. A heti menetrendből származó időpontok narancssárgák.
- A SpecificPeriod már az idővonalat módosítja. Lehet új foglalható időpontokat hozzáadni (zöld), módosítani meglévőket (lila), vagy törölni (szürke).
- Az utolsó oszlop az így létrejött idővonal. Az adatbázisban ez nem szerepel. A TimeTableService feladata, hogy ennek a megfelelő részletét létrehozza a foglalásokhoz.

![Időkezelés](doc/time-management.svg)

### Funkciók ügyfeleknek

Az ügyfelek foglalhatnak időpontokat. Az ábrán jobboldalt létrehozott idővonalról választanak.

### Domain objektumok

#### Boose (Bookable Service röviden)

Tulajdonságok:
- név
- leírás

Például ha van egy fodrászat, akkor minden egyes fodrásznak lehet egy saját Boose-a. Ezekhez tartoznak az időpontok. Egy Boose-ra egy időpontban csak egy ügyfél foglalhat.


#### WeeklyPeriod

Ez már feljebb szerepelt, a heti menetrendben egy időpont. pl Kedd 8:00-tól 8:30-ig. Mindig egy Boose-hoz tartozik.

Tulajdonságok:
- kezdet
- vég
- megjegyzés
- Boose amihez tartozik

Egy ilyen periódus átnyúlhat a következő hétre, például vasárnap-tól hétfőig.

#### SpecificPeriod

Szintén volt feljebb. Ezek már konkrét dátumokhoz kötöttek, pl 2022. június 20. 8 órától 10 óráig. Ez is egy Boose-hoz tartozik

Tulajdonságok:
- kezdet
- vég
- megjegyzés
- típus:
  - ADD_OR_REPLACE, ha felülírja a hetit és foglalható (zöld és lila volt az ábrán)
  - REMOVE, ez is felülírja a hetit, csak nem foglalható
- Boose amihez tartozik

#### Customer

Az ügyfelek foglalják az időpontokat.

Tulajdonságok:
- név
- email

#### Booking

Foglalás egy adott időpontra. Mindig egy Customer foglal egy Boose-ra egy szabad időpontot.

Tulajdonságok:
- kezdet
- vég
- megjegyzés
- Boose, amire történt a foglalás
- Customer, aki foglalt

#### TablePeriod
A fenti ábra jobb oldala még nem tartalmazza a foglalásokat, de ha azt is belevesszük akkor kapunk egy időtáblát. (TimeTableService dolga ezt kiszámolni)

Ennek az elemei a TablePeriod-ok, ezek az adatbázisban nem szerepelnek.

Tulajdonságok:
- kezdet
- vég
- megjegyzés
- Booking, ha van foglalás

### API végpontok

- `/services` [GET, POST]
- `/services/{id}` [GET, PUT, DELETE]
- `/services/{id}/timetable?start=\<datetime\>&end=\<datetime\>[&filter=FREE]` [GET]
- `/services/{id}/weekly-periods` [GET, POST]
- `/services/{id}/weekly-periods/{id}` [GET, PUT]
- `/services/{id}/specific-periods?start=\<datetime\>&end=\<datetime\>[&type=ADD_OR_REPLACE]` [GET, POST]
- `/services/{id}/specific-periods/{id}` [GET, PUT]
- `/services/{id}/bookings?customer=\<customer-id\>&start=\<datetime\>&end=\<datetime\>` [GET]
- `/services/{id}/bookings/{id}` [GET, PUT, DELETE]
- `/customers` [GET, POST]
- `/customers/{id}` [GET, PUT, DELETE]
- `/customer/{id}/bookings` [POST]
- `/customer/{id}/bookings?service=\<service-id\>&start=\<datetime\>&end=\<datetime\>` [GET]
- `/customer/{id}/bookings/{id}` [GET, PUT, DELETE]

### Alkalmazás elindítása
Docker image (servicebooker:latest) készítése:
```shell
$ ./docker-rebuild.sh 
```

Ezután docker compose-zal indítható a mysql szerver és az alkalmazás:
```shell
$ docker compose up -d
```


A szerver a localhost:8080 -on elérhető.

---

Leállítás, a kontérek megtartásával:
```shell
$ docker compose stop
````


---
Újraindítás:
```shell
$ docker compose start
```
---

Konténerek, hálózat és képfájl törlése:
```shell
$ docker compose down
$ docker image remove servicebooker
```

## Követelmények 

A követelményeket a [requirements.md](requirements.md) tartalmazza. Értelmezésem szerint mind telejesítve.

Swagger: http://localhost:8080/swagger-ui/index.html

Teszlefedettség:
- unit tesztek
  - service package `97%`
  - util package `94%`
- integrációs tesztek
  - teljes kód `87%`

## Szerző

- Vandrus Zoltán

## Linkek:

- Diagramok: [diagrams.net](https://app.diagrams.net) webalkalmazással
  megnyitható [google drive link](https://drive.google.com/file/d/12AK1elUCa2w8mthzNqpbRXYZbONwvwBY/view?usp=sharing)
- Github: [privát PROGmasters-es repó](https://github.com/PM-VallalatiBackend-SV2/vizsgaremek-Szunti)