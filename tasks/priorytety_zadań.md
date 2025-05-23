# Priorytety zadań (DAWID KRÓL I DAWID KMAK)

## Opis historyjki

Jako domownik, chcę móc oznaczać zadania różnymi priorytetami (np. wysoki, średni, niski), aby wszyscy wiedzieli, które obowiązki wymagają natychmiastowej uwagi, a które mogą poczekać.

## Podział na taski

### Task 7.1: Rozszerzenie modelu zadania o priorytet

**Estymata:** 0.25 SP (0.5h)
**Opis:** Implementacja enuma `TaskPriority` i dodanie go do klasy `Task`. Zauważ, że część tej pracy została już uwzględniona w poprzednich taskach, gdzie priorytet był założony jako część modelu zadania.

### Task 7.2: Rozszerzenie serwisu zadań o metody obsługi priorytetów

**Estymata:** 0.25 SP (0.5h)
**Opis:** Rozszerzenie `TaskService` o metody do ustawiania i zmiany priorytetów zadań.

### Task 7.3: Rozszerzenie komend CLI o zarządzanie priorytetami

**Estymata:** 0.5 SP (1h)
**Opis:** Rozszerzenie komend CLI o możliwość zmiany priorytetów zadań i filtrowania po priorytetach.

### Task 7.4: Wizualne wyróżnienie priorytetów w interfejsie CLI

**Estymata:** 0.25 SP (0.5h)
**Opis:** Rozszerzenie formatowania zadań w CLI, aby wizualnie wyróżniać priorytety (np. kolory, znaki specjalne).

## Karty CRC

### Klasa: PriorytetZadania (TaskPriority)

- **Odpowiedzialności:** Definiowanie poziomów priorytetów (wysoki, średni, niski)
- **Współpracownicy:** Zadanie, FormatowaniePriorytetów

### Klasa: Zadanie (Task)

- **Odpowiedzialności:** Przechowywanie i zarządzanie priorytetem zadania
- **Współpracownicy:** SerwisZadań, FormatowaniePriorytetów

### Klasa: SerwisZadań (TaskService)

- **Odpowiedzialności:** Logika zmiany priorytetów, walidacja zmian
- **Współpracownicy:** Zadanie, KomendyPriorytetów

### Klasa: FormatowaniePriorytetów (PriorityFormatter)

- **Odpowiedzialności:** Wizualne wyróżnianie priorytetów w CLI (kolory, znaki)
- **Współpracownicy:** Zadanie, KomendyPriorytetów

### Klasa: KomendaPriorytetów (PriorityCommand)

- **Odpowiedzialności:** Obsługa zmian priorytetów i filtrowania w CLI
- **Współpracownicy:** SerwisZadań, FormatowaniePriorytetów
