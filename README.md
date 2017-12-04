# PadovaAstronomyTour

## Il progetto

App realizzata durante il mese di Novembre per concorrere alla challenge <b>"Space App Contest"</b> indetta da <a href="http://www.unismart.it">Unismart</a> e <a href="http://www.pd-promex.it/it">Padova Promex</a> in occasione della Padova Space Week (4&#8211;7 Dicembre 2017)

L'itinerario di base è stato creato, assieme ai testi di descrizione, da una sintesi della guida “Seconda stella a destra” di Bas Bleu Illustration. La guida è reperibile online <a href="http://www.basbleuillustration.com">www.basbleuillustration.com</a> o nelle librerie della città di Padova.

## Setup

Per compilare correttamente l'applicazione dovete dotarvi di una Google API KEY valida ed inserirla nel file _values/google_maps_api.xml_ al posto della scritta _GOOGLE_MAP_API_

## Come funziona

### Esplora Padova divertendoti.

Ogni luogo da visitare può essere associato ad un evento.
Ci sono tre tipi di eventi che vengono lanciati, ma solo uno per ciascun luogo: Extra, Domanda oppure Raggiungi il punto nascosto.

Per sbloccare l'evento devi prima raggiungere fisicamente il luogo indicato o almeno recarti nelle vicinanze.
- **Extra:** puoi sbloccare ulteriori informazioni che verranno visualizzate in aggiunta alla descrizione già in tuo possesso.
- **Domanda:** ti viene posta una domanda con quattro risposte possibili. Soltanto una è quella corretta. Sarai in grado di individuarla?
- **Raggiungi il luogo nascosto:** ti viene proposto un indovinello da risolvere per poter trovare un luogo nei dintorni di quello appena raggiunto. Risolvi l’enigma, recati fisicamente dove richiesto e completerai la sfida!

### Stelle
Le stelle indicano il raggiungimento del punto:
- Stella vuota significa che non lo hai raggiunto
- Stella piena a metà significa che hai raggiunto il punto, ma non hai completato l'evento
- Stella piena significa che hai completato l'evento associato al punto


### Hai 4 sezioni differenti

1. La lista di tutti i punti sempre consultabile anche senza connessione a internet.

2. La mappa per individuare i luoghi e poter accedere a Google Maps in modo da riuscire a raggiungerli. Avvicinandoti ai punti previsti,  sulla mappa si evidenzieranno delle icone:
 - icona grigia indica i punti non raggiunti
 - icona verde segnala quelli raggiunti o completati
 - icona gialla nel caso si provenga da uno dei punti dopo aver cliccato l'apposito bottone
 
 Cliccando sopra qualsiasi icona compariranno il nome ed il titolo relativi al luogo corrispondente in una infobox; se la clicchi puoi entrare nella scheda informativa. Se invece clicchi sull'icona di Google Maps che appare nell'angolo in basso a destra dello schermo, potrai aprire l'applicazione Mappe ed ottenere una guida per raggiungere il punto desiderato.

3. La sezione percorsi ti permette di giocare in una sotto-sezione per raggiungere luoghi selezionati per categorie. Nel caso tu non abbia abbastanza tempo per girare tutta Padova, puoi scegliere quale percorso preferisci e affrontare le sfide. Una volta impostato un percorso lo si può anche cambiare, ma solo tornando alla sezione percorsi iniziale saranno nuovamente visibili tutti i luoghi da raggiungere. Maggiori info sono presenti nell'apposita schermata.

4. La sezione Trofei ti mostra i progressi che hai fatto nel gioco. Sblocca le coppe di bronzo e d'argento fino ad arrivare a quella d'oro per ciascun gruppo!


Menu laterale:

In ogni momento puoi fare swipe dal bordo sinistro del dispositivo verso il centro dello stesso per visualizzare il menu principale dell'applicazione, in modo da poter accedere agilmente a qualsiasi sezione tu voglia.


## Contributors

### Il team

Catalin Copil (https://github.com/catacopil)

Stefano Nicolai (https://github.com/stefanonicolai)

### Analisi, progettazione e brainstorming

Catalin Copil, Stefano Nicolai

### Sviluppo

Stefano Nicolai

### Testing

Catalin Copil, Stefano Nicolai

## Testi itinerario di base

L. Benacchio, C. Di Benedetto, V. Cappelli

## Gamification

Stefano Nicolai

## License

Padova Astronomy Tour
Copyright (C) 2017  Stefano Nicolai

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
