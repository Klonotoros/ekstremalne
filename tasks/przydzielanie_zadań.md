# Przydzielanie zadań (KRZYSZTOF CZECHOWICZ I JAKUB BŁAŻOWSKI)

## Opis historyjki

Jako domownik, chcę móc przypisać zadanie konkretnej osobie lub do siebie, aby było jasne, kto za co odpowiada.

## Podział na taski

### Task 3.1: Rozszerzenie serwisu zadań o funkcję przydzielania

**Estymata:** 0.5 SP (1h)
**Opis:** Rozszerzenie `TaskService` o metody umożliwiające przypisywanie zadań do użytkowników oraz walidację czy użytkownik o danym ID istnieje.

### Task 3.2: Implementacja CLI dla przydzielania zadań

**Estymata:** 0.5 SP (1h)
**Opis:** Rozszerzenie komend CLI o możliwość przydzielania zadań.

## Karty CRC

### Klasa: Zadanie (Task)

- **Odpowiedzialności:** Przechowywanie informacji o przydzielonym członku rodziny
- **Współpracownicy:** SerwisZadań, RepozytoriumZadań

### Klasa: CzłonekRodziny (FamilyMember)

- **Odpowiedzialności:** Identyfikacja członka rodziny, walidacja istnienia
- **Współpracownicy:** SerwisZadań, SerwisCzłonkówRodziny

### Klasa: SerwisZadań (TaskService)

- **Odpowiedzialności:** Logika przydzielania zadań, walidacja przydziału
- **Współpracownicy:** Zadanie, CzłonekRodziny, KomendyCLI

### Klasa: KomendaPrzydzielania (AssignCommand)

- **Odpowiedzialności:** Obsługa przydzielania zadań przez CLI
- **Współpracownicy:** SerwisZadań, Zadanie, CzłonekRodziny

### Klasa: WalidatorPrzydziału (AssignmentValidator)

- **Odpowiedzialności:** Sprawdzanie poprawności przydziału zadań
- **Współpracownicy:** SerwisZadań, SerwisCzłonkówRodziny
