# Vizsgaremek

## Leírás

Backend egy olyan alkalmazáshoz, amivel különböző szolgáltatásokra lehet időpontot foglalni.

### Funkciók szolgáltatóknak

A szolgáltatók meghirdetik a szolgáltatásaikat és beállítják mely időpontokban érhetőek el.
Az időpontokat kétféleképpen lehet megadni:

1. Hetente ismétlődő periódusok
2. Egyszeri alkalmak, ezek felülírják a heti menetrendet

Pl, ha egy fodrászat hétfőnként 8-16-ig fogad vendégeket, de egyik nap délelőtt a fodrásznak más elfoglaltsága van,
akkor a délelőtti órák egy egyszeri pár órás szünettel felülírhatóak. Ha ezt a fodrász később pótolná akkor hasonlóan
egy egyszeri új periódussal meg lehet jelölni új időpontokat amikben vendégek fogadhatóak.

### Funkciók ügyfeleknek

Az ügyfelek lefoglalhatnak időpontokat.

### API végpontok

- **/services** [GET ✔, POST ✔]
- **/services/{id}** [GET ✔, PUT ✔, DELETE ✔]
- **/services/{id}/timetable?start=\<datetime\>&end=\<datetime\>&free** [GET ✔]
- **/services/{id}/weekly-periods** [GET, POST]
- **/services/{id}/weekly-periods/{id}** [GET, PUT]
- **/services/{id}/specific-periods?start=\<datetime\>&end=\<datetime\>&bookable=true** [GET, POST]
- **/services/{id}/specific-periods/{id}** [GET, PUT]
- **/services/{id}/bookings?customer=\<customer-id\>&start=\<datetime\>&end=\<datetime\>** [GET]
- **/services/{id}/bookings/{id}** [GET, PUT, DELETE]
- **/customers** [GET, POST]
- **/customers/{id}** [GET, PUT, DELETE]
- **/customer/{id}/bookings** [POST]
- **/customer/{id}/bookings?service=\<service-id\>&start=\<datetime\>&end=\<datetime\>** [GET]
- **/customer/{id}/bookings/{id}** [GET, PUT, DELETE]

## Szerző

- Vandrus Zoltán

## Linkek:

- Diagramok: [diagrams.net](https://app.diagrams.net) webalkalmazással
  megnyitható [google drive link](https://drive.google.com/file/d/12AK1elUCa2w8mthzNqpbRXYZbONwvwBY/view?usp=sharing)
- Github: [privát PROGmasters-es repó](https://github.com/PM-VallalatiBackend-SV2/vizsgaremek-Szunti)