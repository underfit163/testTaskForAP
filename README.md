# testTaskForAP
Тестовое задание для стажеров аналитиков-технологов

## Задание 1
1. Опишите модель данных (в любом удобном для вас представлении) для обслуживания библиотеки. Это может быть описание таблиц с типами данных, диаграмма – что угодно.
![image](https://user-images.githubusercontent.com/81982349/133514221-f2628a34-f4ef-467f-9963-630a6fd26322.png)

```sql
CREATE SEQUENCE id_for_publisher AS int;
CREATE SEQUENCE id_for_book AS int;
CREATE SEQUENCE id_for_author AS int;
CREATE SEQUENCE id_for_bookWriters AS int;
CREATE SEQUENCE id_for_student AS int;
CREATE SEQUENCE id_for_takeBook AS int;

CREATE TABLE Publisher
(idPublisher int NOT NULL default nextval('id_for_publisher'),
 namePublisher varchar(50) NOT NULL,
 CONSTRAINT idPublisher_pk PRIMARY KEY(idPublisher)
 );

CREATE TABLE Book
(idBook int NOT NULL default nextval('id_for_book'),
 nameBook varchar(50) NOT NULL,
 yearPublication date,
 idPublisher int NOT NULL,
 CONSTRAINT idBook_pk PRIMARY KEY(idBook),
 CONSTRAINT idPublisher_fk FOREIGN KEY (idPublisher) REFERENCES Publisher(idPublisher)
 );

CREATE TABLE Author
(idAuthor int NOT NULL default nextval('id_for_author'),
 nameAuthor varchar(50) NOT NULL,
 CONSTRAINT idAuthor_pk PRIMARY KEY(idAuthor)
 );

CREATE TABLE BookWriters
(idBookWriters int NOT NULL default nextval('id_for_bookWriters'),
 idBook int NOT NULL,
 idAuthor int,
 CONSTRAINT idBookWriters_pk PRIMARY KEY(idBookWriters),
 CONSTRAINT idBook_fk FOREIGN KEY (idBook) REFERENCES Book(idBook),
 CONSTRAINT idAuthor_fk FOREIGN KEY (idAuthor) REFERENCES Author(idAuthor)
 );

CREATE TABLE Student
(idStudent int NOT NULL default nextval('id_for_student'),
 nameStudent varchar(50) NOT NULL,
 CONSTRAINT idStudent_pk PRIMARY KEY(idStudent)
 );

CREATE TABLE TakeBook
(idTakeBook int NOT NULL default nextval('id_for_takeBook'),
 idStudent int NOT NULL,
 idBook int NOT NULL unique,
 dateTake date NOT NULL,
 dateMust date CHECK (dateMust >= dateTake),
 dateReturn date CHECK (dateReturn >= dateTake),
 CONSTRAINT idTakeBook_pk PRIMARY KEY(idTakeBook),
 CONSTRAINT idBook_fk FOREIGN KEY (idBook) REFERENCES Book(idBook),
 CONSTRAINT idStudent_fk FOREIGN KEY (idStudent) REFERENCES Student(idStudent)
 );

INSERT INTO Publisher VALUES (default,'Liters');
INSERT INTO Publisher VALUES (default,'Education');
INSERT INTO Publisher VALUES (default,'AСT');

INSERT INTO Book VALUES (default, 'Snake', '2015-04-05', 1);
INSERT INTO Book VALUES (default, 'Frills', '2013-07-04', 1);
INSERT INTO Book VALUES (default, 'TRIPS', '2013-05-06', 2);
INSERT INTO Book VALUES (default, 'Closer', '2017-01-08', 2);
INSERT INTO Book VALUES (default, 'Desire', '2013-02-09', 3);
INSERT INTO Book VALUES (default, 'Roman', '2020-03-05', 3);
INSERT INTO Book VALUES (default, 'Poem', '2014-03-05', 3);

INSERT INTO Author VALUES (default, 'Andrey');
INSERT INTO Author VALUES (default, 'Denis');
INSERT INTO Author VALUES (default, 'Alex');
INSERT INTO Author VALUES (default, 'Artur');
INSERT INTO Author VALUES (default, 'Misha');


INSERT INTO Student VALUES (default, 'Ivan');
INSERT INTO Student VALUES (default, 'Lera');
INSERT INTO Student VALUES (default, 'Anna');

INSERT INTO TakeBook VALUES (default, 1, 1, '2021-04-05', '2021-05-01', '2021-04-16');
INSERT INTO TakeBook VALUES (default, 1, 2, '2021-07-04', '2021-08-04', '2021-08-01');
INSERT INTO TakeBook VALUES (default, 2, 3, '2021-05-06', '2021-06-06', '2021-07-06');
INSERT INTO TakeBook VALUES (default, 2, 4, '2021-01-08', '2021-06-08', '2021-09-16');
INSERT INTO TakeBook VALUES (default, 3, 5, '2021-02-09', '2021-04-09', '2021-09-09');
INSERT INTO TakeBook VALUES (default, 3, 6, '2021-03-05', '2021-05-05', '2021-04-05');
INSERT INTO TakeBook VALUES (default, 3, 7, '2021-07-05', '2021-10-05', '2021-09-05');

INSERT INTO BookWriters VALUES (default, 1, 1);
INSERT INTO BookWriters VALUES (default, 1, 2);
INSERT INTO BookWriters VALUES (default, 2, 3);
INSERT INTO BookWriters VALUES (default, 2, 4);
INSERT INTO BookWriters VALUES (default, 3, 1);
INSERT INTO BookWriters VALUES (default, 4, 5);
INSERT INTO BookWriters VALUES (default, 5, 2);
INSERT INTO BookWriters VALUES (default, 6, 3);
INSERT INTO BookWriters VALUES (default, 7, 2);
```

## Задание 2
2. Напишите SQL-запрос, который бы возвращал самого популярного автора за год. Запрос должен основываться на модели данных, которую вы описали в задании 1.
```sql
Select auth.nameAuthor from TakeBook as tb join Book as b on tb.idBook = b.idBook join BookWriters as bw 
on bw.idBook = b.idBook join Author as auth on auth.idAuthor = bw.idAuthor group by date_part('year', tb.dateTake), auth.idAuthor order by count(tb.idBook) desc limit 1
```

## Задание 3
3. Определите понятие «злостный читатель».  Предложите алгоритм для поиска самого злостного читателя библиотеки. На любом языке программирования опишите алгоритм поиска такого читателя. Алгоритм должен основываться на модели данных, которую вы описали в задании 1.
```java
import java.io.IOException;
import java.sql.*;
import java.time.temporal.ChronoUnit;

public class Main {

    public static void main(String[] args) {
        ConnectBD conn = new ConnectBD();
        try (Connection c = conn.getConnection()) {
            //3 Задание
            /*
            Самый злостный читатель, это тот, который просрочил больше всего дней в сумме для всех имеющихся у него книг
             */
            Statement stat = c.createStatement();
            ResultSet rs = stat.executeQuery("Select s.nameStudent, b.nameBook, " +
                    "tb.dateMust, tb.dateReturn " +
                    "From Student as s join TakeBook as tb on s.idStudent=tb.idStudent join Book as b " +
                    "on b.idBook = tb.idBook");
            String nameStudent = "";
            String nameEvilStudent = "";
            long countDate = 0;
            long countMaxDate = 0;
            while (rs.next()) {
                if (rs.getDate(3).toLocalDate().isBefore(rs.getDate(4).toLocalDate())) {
                    System.out.println("Возврат книги " + rs.getString(2) + " просрочен!" +
                            " Студентом " + rs.getString(1));
                    if (nameStudent.equals(rs.getString(1))) {
                        countDate += ChronoUnit.DAYS.between(rs.getDate(3).toLocalDate(), rs.getDate(4).toLocalDate());
                    } else {
                        countDate = ChronoUnit.DAYS.between(rs.getDate(3).toLocalDate(), rs.getDate(4).toLocalDate());
                    }
                    System.out.println(countDate + " дней просрочено");
                    nameStudent = rs.getString(1);
                    if (countMaxDate < countDate) {
                        countMaxDate = countDate;
                        nameEvilStudent = nameStudent;
                        countDate = 0;
                    }
                }
            }
            System.out.println("Имя самого злостного читателя: " + nameEvilStudent +
                    " | Просрочено дней по всем взятым книгам: " + countMaxDate);
        } catch (SQLException ex) {
            System.out.println("Ошибка соединения!");
        } catch (IOException ex) {
            System.out.println("Ошибка чтения!");
        }
    }
}
   ```
