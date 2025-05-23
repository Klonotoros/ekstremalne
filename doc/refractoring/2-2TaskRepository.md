# Task Repository

# Co było czytelne
- Logika repozytorium jest stosunkowo prosta i intuicyjna.
- Zastosowanie Optional w metodzie findById zwiększa bezpieczeństwo odczytu.
- Testy jednostkowe są kompletne – obejmują podstawowe operacje CRUD.

# Co budziło wątpliwości
- Duplikacja inicjalizacji zmiennej tasks w metodach loadTasks oraz w konstruktorze, mapa tasks jest inicjalizowana jako nowa HashMap.
- Brak informacji o błędzie w metodzie loadTasks.

# Jaki widzisz potencjał do refaktoryzacji?
- Ekstrakcja inicjalizacji tasks: Można wyodrębnić inicjalizację tasks = new HashMap<>(); do jednego miejsca.
- Lepsza obsługa błędów

# Task Repository Test
# Co było czytelne
- Jasne nazwy testów
- Użycie @BeforeEach: Konfiguracja przed każdym testem jest prawidłowa i zapewnia izolację testów.

# Co budziło wątpliwości
- Czytelność tworzenia obiektów Task: Tworzenie obiektów Task jest dość obszerne ze względu na wiele pól. Można by rozważyć stworzenie pomocniczej metody do tworzenia instancji Task.
- Testowanie błędów zapisu/odczytu.

# Jaki widzisz potencjał do refaktoryzacji?
- Dodanie prostej metody tworzącej Task
- Dodanie testów testujących Logi błedów.
