package webapi;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.Status.NEW;

class HttpTaskManagerTasksTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = taskServer.getGson();

    public HttpTaskManagerTasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.removeTasks();
        manager.removeSubtasks();
        manager.removeEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTaskSuccess() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1", NEW, LocalDateTime.now(), Duration.ofMinutes(5));

        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTaskSuccess() throws IOException, InterruptedException {
        Task task1 = new Task("Test 2", "Testing task 2", NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        String task1Json = gson.toJson(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url1 = URI.create("http://localhost:8080/tasks");
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(url1)
                .POST(HttpRequest.BodyPublishers.ofString(task1Json))
                .build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());

        List<Task> tasksFromManager = manager.getTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");

        Task task2 = new Task("Test update", "Testing update", NEW, LocalDateTime.now().plusMinutes(10),
                Duration.ofMinutes(5));
        int idTask2 = tasksFromManager.get(0).getId();
        task2.setId(idTask2);
        String task2Json = gson.toJson(task2);

        URI url2 = URI.create("http://localhost:8080/tasks/" + idTask2);
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .POST(HttpRequest.BodyPublishers.ofString(task2Json))
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode());

        tasksFromManager = manager.getTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач после обновления");
        assertEquals("Test update", tasksFromManager.get(0).getName(), "Некорректное имя обновленной задачи");
    }

    @Test
    public void testAddTaskFailure() throws IOException, InterruptedException {
        Task task1 = new Task("Test 3.1", "Testing task 3.1", NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        String task1Json = gson.toJson(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(task1Json))
                .build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());

        List<Task> tasksFromManager = manager.getTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 3.1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");

        Task task2 = new Task("Test 3.2", "Testing task 3.2", NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        String task2Json = gson.toJson(task2);

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(task2Json))
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response2.statusCode());

        tasksFromManager = manager.getTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 3.1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTaskSuccessFailure() throws IOException, InterruptedException {
        Task task1 = new Task("Test 4", "Testing task 4", NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        String task1Json = gson.toJson(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url1 = URI.create("http://localhost:8080/tasks");
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(url1)
                .POST(HttpRequest.BodyPublishers.ofString(task1Json))
                .build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());

        List<Task> tasksFromManager = manager.getTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 4", tasksFromManager.get(0).getName(), "Некорректное имя задачи");

        Task task2 = new Task("Test update", "Testing update", NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        int idTask2 = tasksFromManager.get(0).getId();
        task2.setId(idTask2);
        String task2Json = gson.toJson(task2);

        URI url2 = URI.create("http://localhost:8080/tasks/" + idTask2);
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .POST(HttpRequest.BodyPublishers.ofString(task2Json))
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response2.statusCode());

        tasksFromManager = manager.getTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 4", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        Task taskForJson1 = new Task("Test 5.1", "Testing task 5.1", NEW, LocalDateTime.now(),
                Duration.ofMinutes(5));
        manager.createTask(taskForJson1);

        Task taskForJson2 = new Task("Test 5.2", "Testing task 5.2", NEW, LocalDateTime.now().plusMinutes(10),
                Duration.ofMinutes(5));
        manager.createTask(taskForJson2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonArray(), "Полученная строка не является JSON-массивом");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

        JsonObject jsonObject1 = jsonElement.getAsJsonArray().get(0).getAsJsonObject();
        String name1 = jsonObject1.get("name").getAsString();
        String description1 = jsonObject1.get("description").getAsString();
        int id1 = jsonObject1.get("id").getAsInt();
        String status1 = jsonObject1.get("status").getAsString();
        String startTime1 = jsonObject1.get("startTime").getAsString();
        long duration1 = jsonObject1.get("duration").getAsLong();

        Task taskFromJson1 = new Task(name1, description1, manager.convertStringToStatus(status1),
                LocalDateTime.parse(startTime1, dtf), Duration.ofMinutes(duration1));
        taskFromJson1.setId(id1);

        JsonObject jsonObject2 = jsonElement.getAsJsonArray().get(1).getAsJsonObject();
        String name2 = jsonObject2.get("name").getAsString();
        String description2 = jsonObject2.get("description").getAsString();
        int id2 = jsonObject2.get("id").getAsInt();
        String status2 = jsonObject2.get("status").getAsString();
        String startTime2 = jsonObject2.get("startTime").getAsString();
        long duration2 = jsonObject2.get("duration").getAsLong();

        Task taskFromJson2 = new Task(name2, description2, manager.convertStringToStatus(status2),
                LocalDateTime.parse(startTime2, dtf), Duration.ofMinutes(duration2));
        taskFromJson2.setId(id2);

        assertEquals(taskForJson1, taskFromJson1, "Задача 1, полученная из JSON-объекта, не равна исходной");
        assertEquals(taskForJson2, taskFromJson2, "Задача 2, полученная из JSON-объекта, не равна исходной");
    }

    @Test
    public void testGetTaskByIdSuccess() throws IOException, InterruptedException {
        Task taskSource = new Task("Test 6", "Testing task 6", NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.createTask(taskSource);

        int idForUri = manager.getTasks().get(0).getId();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + idForUri);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Полученная строка не является JSON-объектом");

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        String description = jsonObject.get("description").getAsString();
        int id = jsonObject.get("id").getAsInt();
        String status = jsonObject.get("status").getAsString();
        String startTime = jsonObject.get("startTime").getAsString();
        long duration = jsonObject.get("duration").getAsLong();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
        Task taskResult = new Task(name, description, manager.convertStringToStatus(status),
                LocalDateTime.parse(startTime, dtf), Duration.ofMinutes(duration));
        taskResult.setId(id);

        assertEquals(taskSource, taskResult, "Задача, полученная из JSON-объекта, не равна исходной");
    }

    @Test
    public void testGetTaskByIdFailure() throws IOException, InterruptedException {
        Task task1 = new Task("Test 6", "Testing task 6", NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.createTask(task1);

        int id = manager.getTasks().get(0).getId();
        List<Task> tasksFromManager = manager.getTasks();
        assertNotNull(tasksFromManager);
        assertEquals(1, manager.getTasks().size());

        int testId = 2;
        assertNotEquals(testId, id);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + testId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Test 7", "Testing task 7", NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.createTask(task);

        int id = manager.getTasks().get(0).getId();
        List<Task> tasksFromManager = manager.getTasks();
        assertNotNull(tasksFromManager);
        assertEquals(1, tasksFromManager.size());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        tasksFromManager = manager.getTasks();
        assertEquals(200, response.statusCode());
        assertEquals(0, tasksFromManager.size(), "Задачи не удаляются");
    }
}