# Oznaczanie wykonanych zadań (KRZYSZTOF CZECHOWICZ I JAKUB BŁAŻOWSKI)

## Opis historyjki

Jako domownik, chcę móc oznaczyć zadanie jako ukończone oraz dodać opcjonalny komentarz, aby inni wiedzieli, że zostało wykonane.

## Podział na taski

### Task 5.1: Rozszerzenie modelu zadania o is done

**Estymata:** 0.25 SP (0.5h)
**Opis:** Rozszerzenie klasy modelu `Task` o pole przechowujące komentarze dotyczące wykonania zadania.

### Task 5.3: Implementacja komend CLI do oznaczania zadań jako wykonane

**Estymata:** 0.5 SP (1h)
**Opis:** Rozszerzenie komend CLI o możliwość oznaczania zadań jako wykonane i przywracania ich do stanu oczekującego.

## Karty CRC

### Klasa: Zadanie (Task)

- **Odpowiedzialności:** Przechowywanie statusu wykonania, komentarzy, metadanych
- **Współpracownicy:** SerwisZadań, RepozytoriumZadań, KomendyCLI

### Klasa: SerwisZadań (TaskService)

- **Odpowiedzialności:** Logika biznesowa wykonania, walidacja, zarządzanie stanem
- **Współpracownicy:** Zadanie, RepozytoriumZadań, KomendyCLI

### Klasa: RepozytoriumZadań (TaskRepository)

- **Odpowiedzialności:** Persystencja statusu i komentarzy, serializacja
- **Współpracownicy:** Zadanie, SerwisZadań

### Klasa: KomendaOznaczaniaZadania (CompleteTaskCommand)

- **Odpowiedzialności:** Obsługa wejścia użytkownika, wyświetlanie potwierdzeń
- **Współpracownicy:** SerwisZadań, Zadanie

### Klasa: KomentarzWykonania (TaskCompletionComment)

- **Odpowiedzialności:** Przechowywanie treści i czasu komentarza, walidacja
- **Współpracownicy:** Zadanie, SerwisZadań
