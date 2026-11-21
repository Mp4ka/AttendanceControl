# Архитектура приложения и базы данных

**Проект:** «Система контроля посещаемости студентов»
**Версия:** 1.0.0
**Автор:** Lead Developer — Кургачёв Егор
**Дата:** 21.11.2026

## 1. Общая схема

```
Activity (XML-разметка)
      │  вызов suspend-функций
      ▼
Repository (StudentRepository, AttendanceRepository)
      │  DAO-методы
      ▼
DAO (StudentDao, AttendanceDao)
      │  SQL-запросы
      ▼
RoomDatabase (AppDatabase)  ──►  SQLite «attendance.db»
```

Слои разделены по пакетам:

| Слой | Пакет | Ответственность |
|---|---|---|
| Интерфейс | `ui` | Экраны, адаптеры, выбор фильтров, сообщения |
| Модели и репозитории | `data` | `Student`, `Attendance`, `HistoryRecord`, операции с данными |
| База | `data.database` | `Entity`, `Dao`, `AppDatabase`, провайдер базы |

## 2. Ключевые классы

| Класс | Назначение |
|---|---|
| `AttendanceApplication` | создаёт базу и передаёт DAO в репозитории при старте |
| `MainActivity` | главное меню и переходы в разделы |
| `StudentsActivity`, `AddStudentActivity` | список студентов и форма добавления |
| `LessonActivity` | выбор группы, даты и дисциплины |
| `AttendanceActivity` | отметка статусов для каждого студента |
| `StatisticsActivity` | показатели, история и фильтры |
| `StudentAdapter`, `AttendanceAdapter`, `HistoryAdapter` | адаптеры списков |
| `StudentRepository`, `AttendanceRepository` | `suspend`-операции поверх DAO |
| `StudentEntity`, `AttendanceEntity` | таблицы Room |
| `StudentDao`, `AttendanceDao` | запросы и вставки |
| `AppDatabase`, `DatabaseProvider` | база и её единственный экземпляр |

## 3. Схема базы данных

Версия схемы — **1**, файл `attendance.db` во внутреннем каталоге приложения.

### Таблица `students`

| Поле | Тип | Ограничения |
|---|---|---|
| `id` | INTEGER | первичный ключ, автоинкремент |
| `firstName` | TEXT | имя |
| `lastName` | TEXT | фамилия |
| `groupName` | TEXT | учебная группа |

### Таблица `attendance`

| Поле | Тип | Ограничения |
|---|---|---|
| `id` | INTEGER | первичный ключ, автоинкремент |
| `studentId` | INTEGER | внешний ключ → `students.id`, каскадное удаление |
| `lessonDate` | TEXT | дата в формате `yyyy-MM-dd` |
| `subject` | TEXT | дисциплина или тема |
| `status` | TEXT | «Присутствовал», «Отсутствовал», «Опоздал» |

Индексы:

- `index_attendance_studentId` — ускоряет выборку по студенту и нужен внешнему ключу;
- `index_attendance_studentId_lessonDate_subject` — **уникальный**, гарантирует одну отметку
  на студента и занятие; повторная вставка заменяет запись (`OnConflictStrategy.REPLACE`).

Занятие отдельной таблицей не выделено: его определяют группа студента, `lessonDate` и `subject`.

## 4. Инициализация и работа с потоками

1. При старте `AttendanceApplication.onCreate()` создаёт `AppDatabase` через `DatabaseProvider`.
2. DAO передаются в `StudentRepository` и `AttendanceRepository` (метод `init`).
3. Экраны вызывают `suspend`-функции репозиториев внутри `lifecycleScope.launch`;
   Room сам выполняет запросы вне главного потока.
4. Результат приходит в главный поток, где обновляются адаптеры и тексты.

## 5. Принятые решения и ограничения

| Решение | Причина |
|---|---|
| Один модуль `app`, без ViewModel и DI | небольшой учебный проект, экраны простые |
| Фильтры статистики считаются в памяти | данных мало, запросы к Room остаются простыми |
| Уникальный индекс вместо ручной проверки | база сама поддерживает правило «одна отметка» |
| Каскадное удаление на уровне БД | не остаётся «висящих» отметок |
| `exportSchema = false` | схема версии 1, миграции пока не нужны |

## 6. Куда развивать дальше

- отдельная сущность `Lesson` и экран списка занятий;
- миграции Room при изменении схемы;
- ViewModel для сохранения состояния экранов при повороте;
- экспорт отчётов и графики посещаемости.
