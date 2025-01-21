import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/student_db";
    private static final String USER = "root";
    private static final String PASSWORD = "password";

    public static void initializeDatabase() {
        try {
            Connection conn = DriverManager.getConnection(URL.replace("/student_db", ""), USER, PASSWORD);
            Statement stmt = conn.createStatement();
            
            // Create database if not exists
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS student_db");
            stmt.executeUpdate("USE student_db");
            
            // Create table if not exists
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS students (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "name VARCHAR(100) NOT NULL," +
                "age INT NOT NULL," +
                "marks DOUBLE NOT NULL)"
            );
            
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean addStudent(String name, int age, double marks) {
        if (!validateData(age, marks)) return false;
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO students (name, age, marks) VALUES (?, ?, ?)"
            );
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setDouble(3, marks);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM students ORDER BY name");
            
            while (rs.next()) {
                students.add(new Student(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("age"),
                    rs.getDouble("marks")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public static boolean updateStudent(int id, String name, int age, double marks) {
        if (!validateData(age, marks)) return false;
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement pstmt = conn.prepareStatement(
                "UPDATE students SET name=?, age=?, marks=? WHERE id=?"
            );
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setDouble(3, marks);
            pstmt.setInt(4, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteStudent(int id) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM students WHERE id=?");
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean validateData(int age, double marks) {
        return age >= 18 && age <= 70 && marks >= 0 && marks <= 100;
    }
}

