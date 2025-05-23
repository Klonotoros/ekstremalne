# Dodawanie nowych zadań (ZROBIONE - JAKUB I FILIP)

## Opis historyjki

Jako domownik, chcę móc tworzyć nowe zadania z opisem, terminem wykonania oraz opcjonalnymi szczegółami, aby inni wiedzieli, co i kiedy trzeba zrobić.

## Podział na taski

### Task 2.1: Utworzenie modelu danych dla zadania

**Estymata:** 0.25 SP (0.5h)
**Opis:** Implementacja klasy modelu `Task` z odpowiednimi polami.

### Task 2.2: Implementacja repozytorium dla zadań

**Estymata:** 0.25 SP (0.5h)
**Opis:** Utworzenie interfejsu i implementacji repozytorium do zarządzania danymi zadań w pliku JSON.

### Task 2.3: Implementacja serwisu biznesowego dla zadań

**Estymata:** 0.25 SP (0.5h)
**Opis:** Utworzenie serwisu obsługującego logikę biznesową dotyczącą zadań.

### Task 2.4: Implementacja komend CLI dla tworzenia zadań

**Estymata:** 0.25 SP (0.5h)
**Opis:** Utworzenie klas komend CLI z wykorzystaniem Picocli do tworzenia nowych zadań.

## Karty CRC

### Klasa: Zadanie (Task)

- **Odpowiedzialności:** Przechowywanie danych zadania (opis, termin, szczegóły)
- **Współpracownicy:** SerwisZadań, RepozytoriumZadań

### Klasa: RepozytoriumZadań (TaskRepository)

- **Odpowiedzialności:** Persystencja danych zadań w JSON, operacje CRUD
- **Współpracownicy:** Zadanie, SerwisZadań

### Klasa: SerwisZadań (TaskService)

- **Odpowiedzialności:** Logika biznesowa tworzenia zadań, walidacja danych
- **Współpracownicy:** Zadanie, RepozytoriumZadań, KomendyCLI

### Klasa: KomendaTworzenia (CreateTaskCommand)

- **Odpowiedzialności:** Obsługa tworzenia zadań przez CLI, walidacja wejścia
- **Współpracownicy:** SerwisZadań, Zadanie

### Klasa: WalidatorZadania (TaskValidator)

- **Odpowiedzialności:** Sprawdzanie poprawności danych zadania
- **Współpracownicy:** Zadanie, SerwisZadań
