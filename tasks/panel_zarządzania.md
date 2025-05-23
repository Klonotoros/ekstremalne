# Zarządzanie członkami rodziny (KRZYSZTOF CZECHOWICZ JAKUB BŁAŻOWSKI, DAWID KMAK)

## Opis historyjki

Jako domownik, chcę móc dodawać i edytować członków rodziny w aplikacji, aby wszyscy byli widoczni na liście i można było im przydzielać zadania.

## Podział na taski

### Task 1.1: Utworzenie modelu danych dla członka rodziny

**Estymata:** 0.5 SP (1h)
**Opis:** Implementacja klasy modelu `FamilyMember` z odpowiednimi polami.

### Task 1.2: Implementacja repozytorium dla członków rodziny

**Estymata:** 0.5 SP (1h)
**Opis:** Utworzenie interfejsu i implementacji repozytorium do zarządzania danymi członków rodziny w pliku JSON.

### Task 1.3: Implementacja serwisu biznesowego dla członków rodziny

**Estymata:** 0.5 SP (1h)
**Opis:** Utworzenie serwisu obsługującego logikę biznesową dotyczącą członków rodziny.

### Task 1.4: Implementacja komend CLI dla zarządzania członkami rodziny

**Estymata:** 0.5 SP (1h)
**Opis:** Utworzenie klas komend CLI z wykorzystaniem Picocli do obsługi operacji na członkach rodziny.

## Karty CRC

### Klasa: CzłonekRodziny (FamilyMember)

- **Odpowiedzialności:** Przechowywanie danych osobowych, identyfikatora, statusu
- **Współpracownicy:** SerwisCzłonkówRodziny, RepozytoriumCzłonkówRodziny, KomendyCLI

### Klasa: SerwisCzłonkówRodziny (FamilyMemberService)

- **Odpowiedzialności:** Logika biznesowa zarządzania członkami, walidacja danych
- **Współpracownicy:** CzłonekRodziny, RepozytoriumCzłonkówRodziny, KomendyCLI

### Klasa: RepozytoriumCzłonkówRodziny (FamilyMemberRepository)

- **Odpowiedzialności:** Persystencja danych członków, serializacja JSON
- **Współpracownicy:** CzłonekRodziny, SerwisCzłonkówRodziny

### Klasa: KomendaZarządzaniaCzłonkami (FamilyMemberCommand)

- **Odpowiedzialności:** Obsługa wejścia użytkownika, wyświetlanie listy członków
- **Współpracownicy:** SerwisCzłonkówRodziny, CzłonekRodziny

### Klasa: DaneOsobowe (PersonalData)

- **Odpowiedzialności:** Przechowywanie i walidacja danych osobowych członka
- **Współpracownicy:** CzłonekRodziny, SerwisCzłonkówRodziny
