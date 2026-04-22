SimonPrototype – Consegna 1 (intermedia)

Questo progetto costituisce la consegna intermedia del corso Programmazione di sistemi embedded (a.a. 2025/26).  
L’app implementa un primo prototipo del gioco “Simon” con due schermate, supporto multilingua e gestione dello stato in portrait/landscape.



Dispositivo utilizzato per lo sviluppo

- Dispositivo fisico: Samsung Galaxy A34 (modello A346B)
- Versione Android: Android 14
- Collegamento tramite USB e modalità sviluppatore attiva.

- Emulatore Android Studio: Pixel 6 – API 34


Funzionalità implementate

Schermata 1
- Griglia 3×2 con i colori: rosso, verde, blu, magenta, giallo, ciano
- Visualizzazione della sequenza premuta (lettere inglesi separate da virgole)
- Pulsante **Cancella** per azzerare la sequenza
- Pulsante **Fine partita** per memorizzare la sequenza (anche vuota) e passare alla schermata 2
- Layout adattivo per portrait e landscape
- Gestione dello stato dell’istanza (la sequenza non si perde ruotando lo schermo)

Schermata 2
- Lista dinamica delle partite concluse (RecyclerView)
- Visualizzazione del numero di pressioni e della sequenza
- Troncamento automatico delle sequenze troppo lunghe
- Ritorno alla schermata 1 tramite tasto “back” di sistema
- Stato della lista preservato durante la rotazione


Supporto multilingua:
L’interfaccia è disponibile in:
- Italiano (`values/strings.xml`)
- Inglese (`values-en/strings.xml`)

Struttura del progetto:
Il repository non contiene file generati da Android Studio (directory `build/` escluse come richiesto).

