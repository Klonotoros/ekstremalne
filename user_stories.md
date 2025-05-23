# Historyjki użytkownika  

1 SP = 2h

Skala ryzyka:
0-1-2
Completeness (0 - kompletna, 1 - niekompletna, 2 - nieznana)
Volatility (zmienność) (0 - niska, 1 średnia, 2 - wysoka)
Complexity  (0 prosta, 1 srednia, 2 zlozona)
Finalne ryzyki: 0-1, 2-4, 5-6 (niskie-średnie-wysokie)
## 1. Panel zarządzania członkami rodziny (1 release)

Jako domownik, chcę móc dodawać i edytować członków rodziny w aplikacji, aby wszyscy byli widoczni na liście i można było im przydzielać zadania.

Estymata: 3h, 2 SP

Priorytet: Significant

Ryzyko:
0. 1, 1 = 2 średnie


## 2. Dodawanie nowych zadań (1 release)

Jako domownik, chcę móc tworzyć nowe zadania z opisem, terminem wykonania oraz opcjonalnymi szczegółami, aby inni wiedzieli, co i kiedy trzeba zrobić.

Estymata: 2h, 1 SP

Priorytet: Critical

Ryzyko:
0, 1, 1 = 2 średnie

## 3. Przydzielanie zadań (1 release)

Jako domownik, chcę móc przypisać zadanie konkretnej osobie lub do siebie, aby było jasne, kto za co odpowiada.

Estymata: 2h, 1 SP

Priorytet: Significant

Ryzyko:
0, 0, 0 = 0 niskie

## 4. Przeglądanie zadań (1 release)

Jako domownik, chcę mieć dostęp do czytelnej listy wszystkich zadań z możliwością sortowania i filtrowania według terminów, osób lub priorytetów, aby łatwo znaleźć potrzebne informacje.

Estymata: 6h, 3 SP

Priorytet: Critical

Ryzyko: 
1, 2, 2 = 5 wysokie
## 5. Oznaczanie wykonanych zadań (1 release)

Jako domownik, chcę móc oznaczyć zadanie jako ukończone oraz dodać opcjonalny komentarz, aby inni wiedzieli, że zostało wykonane.

Estymata: 3h, 1 SP

Priorytet: Significant

Ryzyko:
0, 0, 0 = 0 niskie

## 6. Co mam zrobić dzisiaj?

Jako domownik, chcę mieć dostęp do listy moich dzisiejszych zadań na dedykowanym ekranie, aby szybko zobaczyć, co muszę zrobić.

Estymata: 6h, 3 SP

Priorytet: Nice to have

Ryzyko:
0, 0, 1 = 1 niskie
## 7. Zadania cykliczne (1 release)

Jako domownik, chcę móc ustawić zadanie jako powtarzające się w określonych odstępach czasu (codziennie, co tydzień, co miesiąc), aby nie musieć za każdym razem tworzyć tych samych zadań od nowa.

Estymata: 5h, 2 SP

Priorytet: Nice to have

Ryzyko:
1, 1, 2 = 4 średnie
## 9. Tablica wiadomości

Jako domownik, chcę móc publikować wiadomości dla rodziny widoczne na głównym ekranie aplikacji, aby przekazywać ważne informacje.

Estymata: 2h, 1 SP

Priorytet: Nice to have

Ryzyko:
1, 1, 1 = 3 średnie
## 10. Priorytety zadań (1 release)

Jako domownik, chcę móc oznaczać zadania różnymi priorytetami (np. wysoki, średni, niski), aby wszyscy wiedzieli, które obowiązki wymagają natychmiastowej uwagi, a które mogą poczekać.

Estymata: 2h, 1 SP

Priorytet: Significant

Ryzyko:
0, 1, 0 = 1 niskie
## 11. Kalendarz rodzinny

Jako domownik, chcę widzieć wszystkie zadania rodziny na przejrzystym kalendarzu z podziałem na dni, tygodnie i miesiące, aby łatwiej planować obowiązki.

Estymata: 7h, 4 SP

Priorytet: Nice to have

Ryzyko: 
1, 2, 2 = 5 wysokie

## 12. System oceny wykonanych zadań

Jako domownik, chcę móc oceniać jakość wykonanych zadań przez innych członków rodziny oraz otrzymywać oceny za swoje prace, aby promować dokładność i sumienność w wykonywaniu obowiązków domowych.

Estymata: 2h, 1 SP

Priorytet: Nice to have

Ryzyko:
2, 2. 1 = 5 wysokie

## 13. Koło fortuny obowiązków

Jako domownik, chcę móc losowo przydzielić zadania za pomocą mechanizmu "koła fortuny", gdy nikt nie chce ich wziąć na siebie, aby wprowadzić element zabawy i sprawiedliwości.

Estymata: 4h, 2 SP

Priorytet: Nice to have

Ryzyko:
0, 0, 1  = 1 niskie

Hierarchia:
[numer zadania, ryzyko, story pointy]
Critical: [2, 2, 1], [4, 5, 3],
Significant: [1, 2, 2], [3, 0, 1], [5,0 1], [10, 1, 1]
Nice to have: [7, 4, 2]

Stack technologiczny:
Java 17 - najnowsza LTS wersja oferująca wydajność i nowoczesne funkcje
Gradle - system budowania wskazany w wymaganiach
Jackson - popularna biblioteka do obsługi JSON w Javie
Picocli - lekka biblioteka do tworzenia interfejsów CLI
JUnit 5 - framework do testów jednostkowych
slf4j - do logowania

Tablica:

STORY | TO DO | IN PROGRESS | TO VERIFY | DONE
