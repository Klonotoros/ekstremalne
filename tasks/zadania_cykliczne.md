# Zadania cykliczne (FILIP I KACPER)

## Opis historyjki

Jako domownik, chcę móc ustawić zadanie jako powtarzające się w określonych odstępach czasu (codziennie, co tydzień, co miesiąc), aby nie musieć za każdym razem tworzyć tych samych zadań od nowa.

## Podział na taski

### Task 6.1: Rozszerzenie modelu zadania o cykliczność

**Estymata:** 0.5 SP (1h)
**Opis:** Rozszerzenie klasy modelu `Task` o informacje związane z cyklicznością.

### Task 6.2: Implementacja serwisu do zarządzania zadaniami cyklicznymi

**Estymata:** 1 SP (2h)
**Opis:** Implementacja logiki biznesowej do generowania i zarządzania zadaniami cyklicznymi.

### Task 6.3: Integracja zadań cyklicznych z istniejącymi serwisami

**Estymata:** 0.5 SP (1h)
**Opis:** Rozszerzenie `TaskService` o integrację z `RecurringTaskService` oraz modyfikacja metody `completeTask`, aby automatycznie generować nowe zadania cykliczne.

## Karty CRC

### Klasa: Zadanie (Task)

- **Odpowiedzialności:** Przechowywanie informacji o cykliczności zadania
- **Współpracownicy:** SerwisZadań, SerwisZadańCyklicznych

### Klasa: KonfiguracjaCykliczności (RecurrenceConfig)

- **Odpowiedzialności:** Definiowanie interwału powtarzania, daty końcowej
- **Współpracownicy:** Zadanie, SerwisZadańCyklicznych

### Klasa: SerwisZadańCyklicznych (RecurringTaskService)

- **Odpowiedzialności:** Generowanie nowych zadań cyklicznych, zarządzanie cyklami
- **Współpracownicy:** Zadanie, SerwisZadań, KonfiguracjaCykliczności

### Klasa: SerwisZadań (TaskService)

- **Odpowiedzialności:** Integracja z zadaniami cyklicznymi, obsługa ukończenia
- **Współpracownicy:** Zadanie, SerwisZadańCyklicznych

### Klasa: KomendaZadańCyklicznych (RecurringTaskCommand)

- **Odpowiedzialności:** Obsługa tworzenia i zarządzania zadaniami cyklicznymi
- **Współpracownicy:** SerwisZadańCyklicznych, Zadanie
