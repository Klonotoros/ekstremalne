# Wczytywanie konfiguracji z pliku JSON

## Opis historyjki

Jako użytkownik/administrator programu, chcę, żeby program przy starcie wczytywał konfiguracje z pliku JSON, a jeśli nie znajdzie pliku JSON w bieżącym katalogu, żeby przyjmował domyślne wartości parametrów konfiguracyjnych.

## Podział na taski

### Task 9.1: Implementacja wczytywania konfiguracji przy starcie (D. Król)

**Estymata:** 1 SP (2h)
**Opis:** Rozszerzenie klasy `AppConfig` o metody wczytywania konfiguracji z pliku JSON, obsługę braku pliku konfiguracyjnego oraz inicjalizację parametrów przy starcie aplikacji.

## Karty CRC

### Klasa: KonfiguracjaAplikacji (AppConfig)

- **Odpowiedzialności:** Przechowywanie parametrów konfiguracyjnych, wczytywanie konfiguracji z pliku JSON, dostarczanie wartości domyślnych przy braku pliku
- **Współpracownicy:** BiblotekaJackson, KomendyCLI
