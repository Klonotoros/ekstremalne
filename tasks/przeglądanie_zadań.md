# Przeglądanie zadań (FILIP I KACPER)

## Opis historyjki

Jako domownik, chcę mieć dostęp do czytelnej listy wszystkich zadań z możliwością sortowania i filtrowania według terminów, osób lub priorytetów, aby łatwo znaleźć potrzebne informacje.

## Podział na taski

### Task 4.1: Implementacja filtów i sortowania w serwisie zadań

**Estymata:** 1 SP (2h)
**Opis:** Rozszerzenie `TaskService` o metody do filtrowania i sortowania zadań.

### Task 4.2: Implementacja formatowania wyników dla CLI

**Estymata:** 1 SP (2h)
**Opis:** Utworzenie klas formatujących wyniki zadań dla lepszej czytelności w CLI.

## Karty CRC

### Klasa: Zadanie (Task)

- **Odpowiedzialności:** Przechowywanie danych do filtrowania i sortowania
- **Współpracownicy:** SerwisZadań, FormatowanieZadań

### Klasa: SerwisZadań (TaskService)

- **Odpowiedzialności:** Implementacja logiki filtrowania i sortowania
- **Współpracownicy:** Zadanie, KryteriaFiltrowania

### Klasa: KryteriaFiltrowania (FilterCriteria)

- **Odpowiedzialności:** Definiowanie warunków filtrowania i sortowania
- **Współpracownicy:** SerwisZadań, FormatowanieZadań

### Klasa: FormatowanieZadań (TaskFormatter)

- **Odpowiedzialności:** Przygotowanie czytelnego wyświetlania zadań w CLI
- **Współpracownicy:** Zadanie, KomendyCLI

### Klasa: KomendaPrzeglądania (ViewCommand)

- **Odpowiedzialności:** Obsługa parametrów filtrowania i sortowania w CLI
- **Współpracownicy:** SerwisZadań, FormatowanieZadań
