# testTaskForAP
Тестовое задание для стажеров аналитиков-технологов

## Задание 1
1. Опишите модель данных (в любом удобном для вас представлении) для обслуживания библиотеки. Это может быть описание таблиц с типами данных, диаграмма – что угодно.
![image](https://user-images.githubusercontent.com/81982349/133514221-f2628a34-f4ef-467f-9963-630a6fd26322.png)

2. Напишите SQL-запрос, который бы возвращал самого популярного автора за год. Запрос должен основываться на модели данных, которую вы описали в задании 1.

Select date_part('year', tb.dateTake), auth.nameAuthor, count(tb.idBook) from TakeBook as tb join Book as b on tb.idBook = b.idBook join BookWriters as bw 
on bw.idBook = b.idBook join Author as auth on auth.idAuthor = bw.idAuthor group by date_part('year', tb.dateTake), auth.idAuthor order by count(tb.idBook) desc limit 1

3. Определите понятие «злостный читатель».  Предложите алгоритм для поиска самого злостного читателя библиотеки. На любом языке программирования опишите алгоритм поиска такого читателя. Алгоритм должен основываться на модели данных, которую вы описали в задании 1.
```java
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
   ```
