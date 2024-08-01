package webapi;

import com.google.gson.Gson;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.Status.NEW;

class HttpTaskManagerSubtasksTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = taskServer.getGson();

    HttpTaskManagerSubtasksTest() throws IOException {
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
    public void testAddSubtaskSuccess() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic", NEW);
        manager.createEpic(epic);

        int epicId = manager.getEpics().get(0).getId();
        Subtask subtask = new Subtask("Test 1", "Testing subtask 1", NEW, epicId, LocalDateTime.now(),
                Duration.ofMinutes(5));

        String taskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getSubtasks();
        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Test 1", subtasksFromManager.get(0).getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void testUpdateSubtaskSuccess() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Testing Epic", NEW);
        manager.createEpic(epic);

        int epicId = manager.getEpics().get(0).getId();
        Subtask subtask1 = new Subtask("Subtask 1", "Testing Subtask 1", NEW, epicId, LocalDateTime.now(),
                Duration.ofMinutes(5));
        String task1Json = gson.toJson(subtask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url1 = URI.create("http://localhost:8080/subtasks");
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(url1)
                .POST(HttpRequest.BodyPublishers.ofString(task1Json))
                .build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());

        List<Subtask> subtasksFromManager = manager.getSubtasks();
        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Subtask 1", subtasksFromManager.get(0).getName(), "Некорректное имя подзадачи");

        Subtask subtask2 = new Subtask("Subtask 1 update", "Testing Subtask 1 update", NEW, epicId,
                LocalDateTime.now().plusMinutes(10), Duration.ofMinutes(5));
        int id = subtasksFromManager.get(0).getId();
        subtask2.setId(id);
        String task2Json = gson.toJson(subtask2);

        URI url2 = URI.create("http://localhost:8080/subtasks/" + id);
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .POST(HttpRequest.BodyPublishers.ofString(task2Json))
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode());

        subtasksFromManager = manager.getSubtasks();
        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач после обновления");
        assertEquals("Subtask 1 update", subtasksFromManager.get(0).getName(), "Некорректное имя обновленной задачи");
    }

    @Test
    public void testAddSubtaskFailure() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Testing Epic", NEW);
        manager.createEpic(epic);

        int epicId = manager.getEpics().get(0).getId();
        Subtask subtask1 = new Subtask("Subtask 1", "Testing Subtask 1", NEW, epicId, LocalDateTime.now(),
                Duration.ofMinutes(5));
        String task1Json = gson.toJson(subtask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(task1Json))
                .build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());

        List<Subtask> subtasksFromManager = manager.getSubtasks();
        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Subtask 1", subtasksFromManager.get(0).getName(), "Некорректное имя подзадачи");

        Subtask subtask2 = new Subtask("Subtask 1 update", "Testing Subtask 1 update", NEW, epicId,
                LocalDateTime.now(), Duration.ofMinutes(5));
        String task2Json = gson.toJson(subtask2);

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(task2Json))
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response2.statusCode());

        subtasksFromManager = manager.getSubtasks();
        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Неудачная попытка добавления не должна " +
                "менять кол-во задач");
        assertEquals("Subtask 1", subtasksFromManager.get(0).getName(), "Неудачная попытка добавления не должна " +
                "менять задачу");
    }

    @Test
    public void testUpdateSubtaskFailure() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Testing Epic", NEW);
        manager.createEpic(epic);

        int epicId = manager.getEpics().get(0).getId();
        Subtask subtask1 = new Subtask("Subtask 1", "Testing Subtask 1", NEW, epicId, LocalDateTime.now(),
                Duration.ofMinutes(5));
        String task1Json = gson.toJson(subtask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url1 = URI.create("http://localhost:8080/subtasks");
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(url1)
                .POST(HttpRequest.BodyPublishers.ofString(task1Json))
                .build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());

        List<Subtask> subtasksFromManager = manager.getSubtasks();
        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Subtask 1", subtasksFromManager.get(0).getName(), "Некорректное имя подзадачи");

        Subtask subtask2 = new Subtask("Subtask 1 update", "Testing Subtask 1 update", NEW, epicId,
                LocalDateTime.now(), Duration.ofMinutes(5));
        int id = subtasksFromManager.get(0).getId();
        subtask2.setId(id);
        String task2Json = gson.toJson(subtask2);

        URI url2 = URI.create("http://localhost:8080/subtasks/" + id);
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .POST(HttpRequest.BodyPublishers.ofString(task2Json))
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response2.statusCode());

        subtasksFromManager = manager.getSubtasks();
        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Неудачная попытка обновления не должна " +
                "менять кол-во задач");
        assertEquals("Subtask 1", subtasksFromManager.get(0).getName(), "Неудачная попытка обновления не должна " +
                "менять задачу");
    }

    @Test
    public void testGetSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Testing Epic", NEW);
        manager.createEpic(epic);

        int epicId = manager.getEpics().get(0).getId();

        Subtask subtask1 = new Subtask("Subtask 1", "Testing Subtask 1", NEW, epicId,
                LocalDateTime.now(), Duration.ofMinutes(5));
        Subtask subtask2 = new Subtask("Subtask 2", "Testing Subtask 2", NEW, epicId,
                LocalDateTime.now().plusMinutes(10), Duration.ofMinutes(5));

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testGetSubtaskByIdSuccess() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Testing Epic", NEW);
        manager.createEpic(epic);

        int epicId = manager.getEpics().get(0).getId();
        Subtask subtask1 = new Subtask("Subtask 1", "Testing Subtask 1", NEW, epicId,
                LocalDateTime.now(), Duration.ofMinutes(5));
        manager.createSubtask(subtask1);

        int id = manager.getSubtasks().get(0).getId();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testGetSubtaskByIdFailure() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Testing Epic", NEW);
        manager.createEpic(epic);

        int epicId = manager.getEpics().get(0).getId();
        Subtask subtask1 = new Subtask("Subtask 1", "Testing Subtask 1", NEW, epicId,
                LocalDateTime.now(), Duration.ofMinutes(5));
        manager.createSubtask(subtask1);

        int id = manager.getSubtasks().get(0).getId();
        List<Subtask> subtasksFromManager = manager.getSubtasks();
        assertNotNull(subtasksFromManager);
        assertEquals(1, manager.getSubtasks().size());

        int testId = 3;
        assertNotEquals(testId, id);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + testId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Testing Epic", NEW);
        manager.createEpic(epic);

        int epicId = manager.getEpics().get(0).getId();
        Subtask subtask = new Subtask("Subtask 1", "Testing Subtask 1", NEW, epicId,
                LocalDateTime.now(), Duration.ofMinutes(5));
        manager.createSubtask(subtask);

        int id = manager.getSubtasks().get(0).getId();
        List<Subtask> subtasksFromManager = manager.getSubtasks();
        assertNotNull(subtasksFromManager);
        assertEquals(1, subtasksFromManager.size());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        subtasksFromManager = manager.getSubtasks();
        assertEquals(200, response.statusCode());
        assertEquals(0, subtasksFromManager.size(), "Задачи не удаляются");
    }
}