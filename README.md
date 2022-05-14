# Vizsgaremek

## Leírás

Backend egy olyan alkalmazáshoz, amivel különböző szolgáltatásokra lehet időpontot foglalni.

### Funkciók szolgáltatóknak 
A szolgáltatók meghirdetik a szolgáltatásaikat és beállítják mely időpontokban érhetőek el.
Az időpontokat kétféleképpen lehet megadni:
1. Hetente ismétlődő periódusok
2. Egyszeri alkalmak, ezek felülírják a heti menetrendet 

Pl, ha egy fodrászat hétfőnként 8-16-ig fogad vendégeket, de egyik nap délelőtt a fodrásznak más elfoglaltsága van,
akkor a délelőtti órák egy egszeri pár órás szünettel felülírhatóak. Ha ezt a fodrász később pótolná akkor hasonlóan
egy egyszeri új periódussal meg lehet jelölni új időpontokat amikben vendégek fogadhatóak.

### Funkciók ügyfeleknek
Az ügyfelek lefoglalhatnak időpontokat.

### API végpontok
- **/api/servicebooker**
  - **/services**
  - **/services/{id}**
  - **/services/{id}/free-periods?start=\<datetime\>&end=\<datetime\>**
  - **/services/{id}/weekly-periods**
  - **/services/{id}/weekly-periods/{id}**
  - **/services/{id}/additional-periods?start=\<datetime\>&end=\<datetime\>**
  - **/services/{id}/additional-periods/{id}**
  - **/services/{id}/excluded-periods?start=\<datetime\>&end=\<datetime\>**
  - **/services/{id}/excluded-periods/{id}**
  - **/customers**
  - **/customers/{id}**
  - **/bookings?service=\<service-id\>**
  - **/bookings?customer=\<customer-id\>**
  - **/bookings/{id}**

## Szerző
- Vandrus Zoltán

## Linkek:
- Diagramok: [diagrams.net](https://app.diagrams.net) webalkalmazással megnyitható [google drive link](https://drive.google.com/file/d/12AK1elUCa2w8mthzNqpbRXYZbONwvwBY/view?usp=sharing)
- Github: [privát PROGMASTERS-es repó](https://github.com/PM-VallalatiBackend-SV2/vizsgaremek-Szunti)