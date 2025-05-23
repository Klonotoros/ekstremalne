# Zapisywanie konfiguracji do pliku JSON

## Opis historyjki

Jako użytkownik/administrator programu, chcę mieć możliwość zapisania pełnej konfiguracji programu (wszystkie parametry - takie jak np. parametry połączenia z bazą danych, z zewnętrznymi serwerami, dane dostępowe dla administratora, ścieżki do plików, itp.) w pliku w formacie JSON.

## Podział na taski

### Task 8.1: Implementacja zapisywania konfiguracji z użyciem Jackson (D. Kmak)

**Estymata:** 1.5 SP (3h)
**Opis:** Dodanie biblioteki Jackson do projektu, implementacja klasy `AppConfig` przechowującej parametry konfiguracyjne oraz metod zapisywania konfiguracji do pliku JSON.

## Karty CRC

### Klasa: KonfiguracjaAplikacji (AppConfig)

- **Odpowiedzialności:** Przechowywanie parametrów konfiguracyjnych, dostarczanie wartości domyślnych, zapisywanie do pliku JSON
- **Współpracownicy:** BiblotekaJackson, KomendyCLI
