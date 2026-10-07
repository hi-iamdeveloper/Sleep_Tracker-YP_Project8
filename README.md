# Sleep Tracker — анализ лога сна

Java-приложение для анализа лога сессий сна. Читает файл с записями о сне, парсит их и вычисляет набор метрик: количество сессий, длительности, качество, бессонные ночи и хронотип пользователя.

![Java](https://img.shields.io/badge/Java-17+-blue)
![JUnit](https://img.shields.io/badge/JUnit-5-green)
![Checkstyle](https://img.shields.io/badge/Checkstyle-passing-brightgreen)

---

## Возможности

- 📖 **Чтение лога сна** из текстового файла с разделителем `;`.
- 🧩 **Парсинг строк** в объекты `SleepingSession` через `DateTimeFormatter`.
- 🔢 **Подсчёт общего количества сессий** сна.
- ⏱ **Минимальная, максимальная и средняя продолжительность** сна.
- ⚠️ **Количество сессий с плохим качеством** (`BAD`).
- 🌙 **Количество бессонных ночей** — ночей, где окно `00:00–06:00` не пересечено ни одной сессией.
- 🦉 **Определение хронотипа** пользователя: сова, жаворонок или голубь.
- 🧪 **Юнит-тесты** на все аналитические функции.

---

## Структура проекта

| Класс | Назначение |
|---|---|
| `SleepTrackerApp` | Точка входа, аналитические функции, вывод результата |
| `SleepLogProvider` | Читает файл лога, проверяет его наличие и непустоту |
| `SleepingSession` | Одна сессия сна: начало, конец, качество |
| `SleepingQuality` | Enum качества сна: `GOOD`, `NORMAL`, `BAD` |
| `Chronotype` | Enum хронотипа: `OWL`, `LARK`, `PIGEON` |
| `SleepLogIsMissing` | Исключение: файл не найден или не читается |
| `SleepLogIsEmpty` | Исключение: файл пуст |

### Ответственности классов

| Класс | Что делает | Чего **не** делает |
|---|---|---|
| `SleepTrackerApp` | Парсит лог, считает метрики, печатает результат | Не читает файл напрямую |
| `SleepLogProvider` | Читает файл, валидирует его | Не парсит строки |
| `SleepingSession` | Хранит данные одной сессии | Не знает про метрики |
| `SleepingQuality` | Перечисляет возможные качества | — |
| `Chronotype` | Перечисляет хронотипы | Не считает себя |
| `SleepLogIsMissing` | Хранит путь и сообщение об ошибке | — |
| `SleepLogIsEmpty` | Хранит путь и сообщение об ошибке | — |

---

## Формат файла лога

Каждая строка — одна сессия сна:

```
dd.MM.yy HH:mm;dd.MM.yy HH:mm;QUALITY
```

Пример:

```
01.10.25 23:15;02.10.25 07:30;GOOD
02.10.25 23:50;03.10.25 06:40;NORMAL
03.10.25 14:10;03.10.25 15:00;NORMAL
03.10.25 23:40;04.10.25 08:00;BAD
30.10.25 23:50;31.10.25 06:30;GOOD
```

Поля:

- **начало** — дата и время засыпания,
- **конец** — дата и время пробуждения,
- **качество** — `GOOD`, `NORMAL` или `BAD`.

---

## Ключевые решения

### 1. Парсинг через `DateTimeFormatter`

```java
static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
```

Единый форматтер на всё приложение — не создаём `DateTimeFormatter` на каждой строке. `yy` даёт год в диапазоне 2000–2099, что подходит для логов.

### 2. Стримы для аналитики

Все метрики считаются через `Stream API`:

- `mapToLong(SleepTrackerApp::sleepDuration)` — превращает сессию в длительность,
- `min()` / `max()` / `average()` — дают `Optional*`,
- `orElseThrow(...)` — явная обработка пустого списка,
- `count()` — количество подходящих элементов.

Стримы читаются декларативно и убирают ручные циклы.

### 3. Enum `Chronotype` с русским названием

```java
OWL("сова"), LARK("жаворонок"), PIGEON("голубь")
```

В коде используются латинские константы (`Chronotype.OWL`), а в `toString()` возвращается русское имя — удобно и для switch-case, и для вывода.

### 4. Правило «до/после 12» для первой ночи

```java
LocalDate firstNight = firstStart.toLocalTime().isBefore(NOON)
        ? firstStart.toLocalDate().minusDays(1)
        : firstStart.toLocalDate();
```

Если первая сессия началась после полудня, потенциальная ночь — эта же дата. Если до полудня — предыдущая. Это позволяет корректно обрабатывать сессии через полночь.

### 5. Поток ночей без циклов

```java
LongStream.range(0, nights)
        .mapToObj(firstNight::plusDays)
        .filter(night -> sessions.stream().noneMatch(...))
        .count();
```

Бессонные ночи считаются через `LongStream.range` + `noneMatch`. Циклов в коде нет — только стримы, как требует Checkstyle.

### 6. Собственные checked-исключения

```java
throw new SleepLogIsMissing(path, "Файл не найден");
throw new SleepLogIsEmpty(path, "Лог пуст");
```

`SleepLogIsMissing` и `SleepLogIsEmpty` хранят путь к файлу и пробрасываются в `main`, где обрабатываются единообразно: `e.getMessage()` + `e.getPath()`.

### 7. Константы для времени

```java
private static final LocalTime NOON = LocalTime.of(12, 0);
private static final LocalTime NIGHT_START = LocalTime.of(0, 0);
private static final LocalTime NIGHT_END = LocalTime.of(6, 0);
```

Магические числа `12`, `0`, `6` вынесены в именованные константы — легче читать и менять.

---

## Методы `SleepTrackerApp`

| Метод | Что делает |
|---|---|
| `sleepingSessionsParsing(SleepLogProvider)` | Превращает список строк в список `SleepingSession` |
| `lineParse(String)` | Парсит одну строку лога |
| `countSessions(List)` | Считает общее количество сессий |
| `sleepDuration(SleepingSession)` | Длительность одной сессии в минутах |
| `minSleepDuration(List)` | Минимальная длительность |
| `maxSleepDuration(List)` | Максимальная длительность |
| `averageSleepDuration(List)` | Средняя длительность |
| `countBadQualitySessions(List)` | Количество сессий с качеством `BAD` |
| `countSleeplessNights(List)` | Количество бессонных ночей |
| `detectChronotype(List)` | Хронотип пользователя |

### Пример использования

```java
SleepLogProvider provider = new SleepLogProvider("sleep_log.txt");
List<SleepingSession> sessions = SleepTrackerApp.sleepingSessionsParsing(provider);

System.out.println("Всего сессий сна: " + SleepTrackerApp.countSessions(sessions));
System.out.println("Максимальная сессия сна: " + SleepTrackerApp.maxSleepDuration(sessions) + " минут");
System.out.println("Минимальная сессия сна: " + SleepTrackerApp.minSleepDuration(sessions) + " минут");
System.out.println("Средняя продолжительность сна: " + SleepTrackerApp.averageSleepDuration(sessions) + " минут");
System.out.println("Количество ночей с плохим сном: " + SleepTrackerApp.countBadQualitySessions(sessions));
System.out.println("Количество бессонных ночей: " + SleepTrackerApp.countSleeplessNights(sessions));
System.out.println("Вы: " + SleepTrackerApp.detectChronotype(sessions));
```

### Запуск

В IntelliJ IDEA: **Run → Edit Configurations → Program arguments** — укажите путь к файлу лога.

---

## Логика подсчёта бессонных ночей

Ночь `N` — это окно `[N 00:00, N 06:00]`. Сессия пересекает окно, если:

```
session.start < N 06:00   И   session.end > N 00:00
```

Ночь **бессонная**, если ни одна сессия не пересекает её окно.

Диапазон проверяемых ночей:

- первая ночь — по правилу «до/после 12» для первой сессии,
- последняя ночь — дата окончания последней сессии.

Дневные сессии (например, `14:10–15:00`) окно не пересекают и в подсчёте не участвуют.

---

## Логика определения хронотипа

Для каждой **ночной** сессии (той, что пересекает окно `00:00–06:00`) определяется тип:

| Тип | Условие |
|---|---|
| Сова (`OWL`) | засыпание **после 23:00** И пробуждение **после 09:00** |
| Жаворонок (`LARK`) | засыпание **до 22:00** И пробуждение **до 07:00** |
| Голубь (`PIGEON`) | всё остальное |

Затем выбирается тип с **наибольшим** количеством ночей. При равенстве или отсутствии данных — `PIGEON`.

---

## Технологии

- **Java 17+** — стримы, `Optional`, `LocalDateTime`.
- **JUnit 5** — модульные тесты на все аналитические функции.
- **Checkstyle** — единый стиль кода, проверка в CI.
- **Коллекции:**
  - `ArrayList` — хранение сессий после парсинга.
  - `Stream API` — вычисление всех метрик.

---

## Тестирование

Каждая функция покрыта минимум двумя тестами: нормальный случай и граничный. Особое внимание — функции `countSleeplessNights` и `detectChronotype`:

- пустой список,
- одна ночная сессия,
- сессии с пропуском нескольких дней,
- переход через месяц,
- ничья между хронотипами,
- дневные сессии игнорируются.

---

<p align="center">
  <i>Проект выполнен в рамках обучения в <a href="https://practicum.yandex.ru/">Яндекс Практикуме</a></i>
</p>
