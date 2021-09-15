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
