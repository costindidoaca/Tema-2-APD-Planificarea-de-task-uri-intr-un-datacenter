# Costin Didoaca #
# Tema 2 APD

MyHost

    Pentru a ordona corect taskurile, folosesc o coada de prioritati care 
imi ordoneaza constant taskurile in functie de prioritatea acestora.In 
metoda Run, imi extrag taskul venit de la dispatcher prin algoritmul 
selectat(din PriorityQueue) si tin timpul curent de start intr-o
variabila.Pentru preemptabilitate, folosesc o metoda care verifica constant
daca exista un task cu prioritate mai mare gata sa preempteze taskul curent.
Retin timpii de oprire a taskului curent in cazul in care acesta este 
preemptibil iar taskul urmator are prioritate mai mare si repun taskul in
coada pentru a continua mai tarziu executia. Deoarece sistemul este intr-o
continua rulare, in timp ce verific daca aplic sau nu logica de 
preemptibilitate, retin timpii ficsi din momentul in care am inceput
verificarea pana cand continui sa execut pentru a evita eventualele erori.
La final, termin executia taskului curent si transmit dispatcherului.
    Continui in metoda run dupa verificare si pun taskul curent din nou
in coada de prioritate pentru a continua executia cu timpul ramas.
    Pentru Algoritmul Least Work Left, am nevoie de metoda getWorkLeft()
din clasa MyHost. Functia o sa mi returneze timpul total ramas compus din
cat timp mai are taskul curent de rulat si de cat timp are nevoie fiecare
task sa ruleze.

MyDispatcher

    In aceasta clasa distribui fiecarui host taskurile dupa algoritmii 
prezentati in enunt. Avand metoda dispatchTask() aleg carui host (indexul
acestuia) atribui taskul curent si il adaug in coada asa cum este 
specificat in enunt.

Feedback:
O tema interesantasi destul de migaloasa, cea mai complicata parte fiind 
gestionarea race conditionurilor in host si verificarea amanuntita a 
conditiilor transmise in enuntul acesteia.Great Work! :)


