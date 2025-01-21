import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
public class Main {
    public static void main(String[] args) throws IOException {
        DatabaseManager.initializeDatabase();
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        
        server.createContext("/", new StaticFileHandler());
        server.createContext("/api/students", new StudentHandler());
        
        server.setExecutor(null);
        server.start();
        System.out.println("Server started at http://localhost:8000");
    }
}

class StaticFileHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = 
            "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <title>Student Management System</title>\n" +
            "    <style>\n" +
            "        body { font-family: Arial; margin: 20px; }\n" +
            "        .form-group { margin: 10px 0; }\n" +
            "        table { border-collapse: collapse; width: 100%; }\n" +
            "        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n" +
            "        button { margin: 5px; padding: 5px 10px; }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <h1>Student Management System</h1>\n" +
            "    \n" +
            "    <div class=\"form-group\">\n" +
            "        <h2>Add/Update Student</h2>\n" +
            "        <input type=\"hidden\" id=\"studentId\">\n" +
            "        <input type=\"text\" id=\"name\" placeholder=\"Name\" required>\n" +
            "        <input type=\"number\" id=\"age\" placeholder=\"Age (18-70)\" required>\n" +
            "        <input type=\"number\" id=\"marks\" placeholder=\"Marks (0-100)\" required>\n" +
            "        <button onclick=\"saveStudent()\">Save</button>\n" +
            "        <button onclick=\"clearForm()\">Clear</button>\n" +
            "    </div>\n" +
            "\n" +
            "    <h2>Students List</h2>\n" +
            "    <table id=\"studentTable\">\n" +
            "        <thead>\n" +
            "            <tr>\n" +
            "                <th>ID</th>\n" +
            "                <th>Name</th>\n" +
            "                <th>Age</th>\n" +
            "                <th>Marks</th>\n" +
            "                <th>Actions</th>\n" +
            "            </tr>\n" +
            "        </thead>\n" +
            "        <tbody></tbody>\n" +
            "    </table>\n" +
            "\n" +
            "    <script>\n" +
            "        function loadStudents() {\n" +
            "            fetch('/api/students')\n" +
            "                .then(response => response.json())\n" +
            "                .then(students => {\n" +
            "                    const tbody = document.querySelector('#studentTable tbody');\n" +
            "                    tbody.innerHTML = '';\n" +
            "                    students.forEach(student => {\n" +
            "                        tbody.innerHTML += `\n" +
            "                            <tr>\n" +
            "                                <td>${student.id}</td>\n" +
            "                                <td>${student.name}</td>\n" +
            "                                <td>${student.age}</td>\n" +
            "                                <td>${student.marks}</td>\n" +
            "                                <td>\n" +
            "                                    <button onclick=\"editStudent(${student.id}, '${student.name}', ${student.age}, ${student.marks})\">Edit</button>\n" +
            "                                    <button onclick=\"deleteStudent(${student.id})\">Delete</button>\n" +
            "                                </td>\n" +
            "                            </tr>\n" +
            "                        `;\n" +
            "                    });\n" +
            "                });\n" +
            "        }\n" +
            "\n" +
            "        function saveStudent() {\n" +
            "            const id = document.getElementById('studentId').value;\n" +
            "            const name = document.getElementById('name').value;\n" +
            "            const age = document.getElementById('age').value;\n" +
            "            const marks = document.getElementById('marks').value;\n" +
            "\n" +
            "            const method = id ? 'PUT' : 'POST';\n" +
            "            const url = id ? `/api/students?id=${id}` : '/api/students';\n" +
            "\n" +
            "            fetch(url, {\n" +
            "                method: method,\n" +
            "                headers: {'Content-Type': 'application/x-www-form-urlencoded'},\n" +
            "                body: `name=${name}&age=${age}&marks=${marks}`\n" +
            "            })\n" +
            "            .then(response => {\n" +
            "                if(response.ok) {\n" +
            "                    clearForm();\n" +
            "                    loadStudents();\n" +
            "                } else {\n" +
            "                    alert('Invalid data! Age should be 18-70 and marks 0-100');\n" +
            "                }\n" +
            "            });\n" +
            "        }\n" +
            "\n" +
            "        function editStudent(id, name, age, marks) {\n" +
            "            document.getElementById('studentId').value = id;\n" +
            "            document.getElementById('name').value = name;\n" +
            "            document.getElementById('age').value = age;\n" +
            "            document.getElementById('marks').value = marks;\n" +
            "        }\n" +
            "\n" +
            "        function deleteStudent(id) {\n" +
            "            if(confirm('Are you sure you want to delete this student?')) {\n" +
            "                fetch(`/api/students?id=${id}`, { method: 'DELETE' })\n" +
            "                    .then(() => loadStudents());\n" +
            "            }\n" +
            "        }\n" +
            "\n" +
            "        function clearForm() {\n" +
            "            document.getElementById('studentId').value = '';\n" +
            "            document.getElementById('name').value = '';\n" +
            "            document.getElementById('age').value = '';\n" +
            "            document.getElementById('marks').value = '';\n" +
            "        }\n" +
            "\n" +
            "        loadStudents();\n" +
            "    </script>\n" +
            "</body>\n" +
            "</html>";
        
        exchange.getResponseHeaders().set("Content-Type", "text/html");
        exchange.sendResponseHeaders(200, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}

class StudentHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "";
        int statusCode = 200;
        
        switch (exchange.getRequestMethod()) {
            case "GET":
                List<Student> students = DatabaseManager.getAllStudents();
                response = convertToJson(students);
                break;
                
            case "POST":
                String requestBody = new String(exchange.getRequestBody().readAllBytes());
                String[] params = requestBody.split("&");
                String name = "";
                int age = 0;
                double marks = 0;
                
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    switch (keyValue[0]) {
                        case "name": name = keyValue[1]; break;
                        case "age": age = Integer.parseInt(keyValue[1]); break;
                        case "marks": marks = Double.parseDouble(keyValue[1]); break;
                    }
                }
                
                boolean success = DatabaseManager.addStudent(name, age, marks);
                statusCode = success ? 200 : 400;
                break;
                
            case "PUT":
                String query = exchange.getRequestURI().getQuery();
                int id = Integer.parseInt(query.split("=")[1]);
                requestBody = new String(exchange.getRequestBody().readAllBytes());
                params = requestBody.split("&");
                name = "";
                age = 0;
                marks = 0;
                
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    switch (keyValue[0]) {
                        case "name": name = keyValue[1]; break;
                        case "age": age = Integer.parseInt(keyValue[1]); break;
                        case "marks": marks = Double.parseDouble(keyValue[1]); break;
                    }
                }
                
                success = DatabaseManager.updateStudent(id, name, age, marks);
                statusCode = success ? 200 : 400;
                break;
                
            case "DELETE":
                query = exchange.getRequestURI().getQuery();
                id = Integer.parseInt(query.split("=")[1]);
                DatabaseManager.deleteStudent(id);
                break;
        }
        
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
    
    private String convertToJson(List<Student> students) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            json.append(String.format(
                "{\"id\":%d,\"name\":\"%s\",\"age\":%d,\"marks\":%.2f}",
                s.getId(), s.getName(), s.getAge(), s.getMarks()
            ));
            if (i < students.size() - 1) json.append(",");
        }
        json.append("]");
        return json.toString();
    }
}

