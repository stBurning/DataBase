-- phpMyAdmin SQL Dump
-- version 5.0.4
-- https://www.phpmyadmin.net/
--
-- Хост: 127.0.0.1:3306
-- Время создания: Мар 20 2021 г., 16:41
-- Версия сервера: 8.0.19
-- Версия PHP: 7.1.33

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS = @@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION = @@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- База данных: `students`
--

-- --------------------------------------------------------


--
-- Структура таблицы `specializations`
--

CREATE TABLE `specializations`
(
    `id`   varchar(8)                                                   NOT NULL,
    `name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Структура таблицы `academic_plans`
--

CREATE TABLE `academic_plans`
(
    `id`                int        NOT NULL,
    `year`              year       NOT NULL,
    `specialization_id` varchar(8) NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Дамп данных таблицы `academic_plans`
--


-- --------------------------------------------------------

--
-- Структура таблицы `cathedras`
--

CREATE TABLE `cathedras`
(
    `id`   int         NOT NULL,
    `name` varchar(40) NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Структура таблицы `disciplines`
--

CREATE TABLE `disciplines`
(
    `id`          varchar(40) NOT NULL,
    `name`        varchar(80) NOT NULL,
    `cathedra_id` int         NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Структура таблицы `disciplines_plan`
--

CREATE TABLE disciplines_plans
(
    `id`               int         NOT NULL,
    `academic_plan_id` int         NOT NULL,
    `discipline_id`    varchar(40) NOT NULL,
    `semester_number`  int         NOT NULL,
    `hours`            int         NOT NULL,
    `reporting_form`   varchar(10) NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Структура таблицы `groups`
--

CREATE TABLE `groups`
(
    `id`               varchar(6)                                 NOT NULL,
    `academic_plan_id` int                                        NOT NULL,
    `qualification`    enum ('master','bachelor','specialist') NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Дамп данных таблицы `groups`
--


-- --------------------------------------------------------


--
-- Структура таблицы `students`
--

CREATE TABLE `students`
(
    `id`         int                                                            NOT NULL,
    `first_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci   NOT NULL,
    `last_name`  varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci   NOT NULL,
    `mid_name`   varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
    `group_id`   varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci    NOT NULL,
    `gender`     set ('М','Ж') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
    `birthday`   date                                                           NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


--
-- Структура таблицы `performance`
--

CREATE TABLE `performance`
(
    `student_id`          INT NULL,
    `disciplines_plan_id` INT NULL,
    `score`               INT NULL,
    `attempt`             INT NOT NULL DEFAULT '1'
) ENGINE = InnoDB;



-- --------------------------------------------------------


--
-- Дамп данных таблицы `students`
--


--
-- Индексы сохранённых таблиц
--

--
-- Индексы таблицы `academic_plans`
--
ALTER TABLE `academic_plans`
    ADD PRIMARY KEY (`id`),
    ADD KEY `specialization_id` (`specialization_id`);

--
-- Индексы таблицы `cathedras`
--
ALTER TABLE `cathedras`
    ADD PRIMARY KEY (`id`);

--
-- Индексы таблицы `disciplines`
--
ALTER TABLE `disciplines`
    ADD PRIMARY KEY (`id`),
    ADD KEY `cathedra_id` (`cathedra_id`);

--
-- Индексы таблицы `performance`
--
ALTER TABLE `performance`
    ADD PRIMARY KEY (`student_id`, `disciplines_plan_id`),
    ADD KEY (`disciplines_plan_id`),
    ADD KEY (`student_id`);


--
-- Индексы таблицы `disciplines_plan`
--
ALTER TABLE disciplines_plans
    ADD PRIMARY KEY (`id`),
    ADD KEY `academic_plan_id` (`academic_plan_id`),
    ADD KEY `discipline_id` (`discipline_id`);

--
-- Индексы таблицы `groups`
--
ALTER TABLE `groups`
    ADD PRIMARY KEY (`id`),
    ADD KEY `academic_plan_id` (`academic_plan_id`);

--
-- Индексы таблицы `specializations`
--
ALTER TABLE `specializations`
    ADD PRIMARY KEY (`id`);

--
-- Индексы таблицы `students`
--
ALTER TABLE `students`
    ADD PRIMARY KEY (`id`),
    ADD KEY `name` (`last_name`, `first_name`),
    ADD KEY `group_id` (`group_id`);

--
-- AUTO_INCREMENT для сохранённых таблиц
--

--
-- AUTO_INCREMENT для таблицы `academic_plans`
--
ALTER TABLE `academic_plans`
    MODIFY `id` int NOT NULL AUTO_INCREMENT,
    AUTO_INCREMENT = 2;

--
-- AUTO_INCREMENT для таблицы `cathedras`
--
ALTER TABLE `cathedras`
    MODIFY `id` int NOT NULL AUTO_INCREMENT;


--
-- AUTO_INCREMENT для таблицы `disciplines_plan`
--
ALTER TABLE disciplines_plans
    MODIFY `id` int NOT NULL AUTO_INCREMENT;


--
-- AUTO_INCREMENT для таблицы `students`
--
ALTER TABLE `students`
    MODIFY `id` int NOT NULL AUTO_INCREMENT,
    AUTO_INCREMENT = 2;

--
-- Ограничения внешнего ключа сохраненных таблиц
--

--
-- Ограничения внешнего ключа таблицы `academic_plans`
--
ALTER TABLE `academic_plans`
    ADD CONSTRAINT `academic_plans_ibfk_1` FOREIGN KEY (`specialization_id`) REFERENCES `specializations` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Ограничения внешнего ключа таблицы `disciplines`
--
ALTER TABLE `disciplines`
    ADD CONSTRAINT `disciplines_ibfk_1` FOREIGN KEY (`cathedra_id`) REFERENCES `cathedras` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Ограничения внешнего ключа таблицы `disciplines_plan`
--
ALTER TABLE disciplines_plans
    ADD CONSTRAINT `disciplines_plan_ibfk_1` FOREIGN KEY (`academic_plan_id`) REFERENCES `academic_plans` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT `disciplines_plan_ibfk_2` FOREIGN KEY (`discipline_id`) REFERENCES `disciplines` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Ограничения внешнего ключа таблицы `groups`
--
ALTER TABLE `groups`
    ADD CONSTRAINT `groups_ibfk_1` FOREIGN KEY (`academic_plan_id`) REFERENCES `academic_plans` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Ограничения внешнего ключа таблицы `students`
--
ALTER TABLE `students`
    ADD CONSTRAINT `students_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE;
COMMIT;

ALTER TABLE `performance`
    ADD FOREIGN KEY (`disciplines_plan_id`) REFERENCES `disciplines_plans` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE;
/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS = @OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION = @OLD_COLLATION_CONNECTION */;
