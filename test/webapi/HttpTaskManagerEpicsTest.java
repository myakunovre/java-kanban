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

class HttpTaskManagerEpicsTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = taskServer.getGson();

    HttpTaskManagerEpicsTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.removeTasks();
        manager.removeEpics();
        manager.removeEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing Epic 1", NEW);

        String taskJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = manager.getEpics();
        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Epic 1", epicsFromManager.get(0).getName(), "Некорректное имя эпика");
    }

    @Test
    public void testGetEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic 1", "Testing Epic 1", NEW);
        Epic epic2 = new Epic("Epic 2", "Testing Epic 2", NEW);

        manager.createEpic(epic1);
        manager.createEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testGetEpicByIdSuccess() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing Epic 1", NEW);
        manager.createEpic(epic);

        int id = manager.getEpics().get(0).getId();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testGetEpicByIdFailure() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing Epic 1", NEW);
        manager.createEpic(epic);

        int id = manager.getEpics().get(0).getId();

        List<Epic> epicsFromManager = manager.getEpics();
        assertNotNull(epicsFromManager);
        assertEquals(1, manager.getEpics().size());

        int testId = 2;
        assertNotEquals(testId, id);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + testId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testGetEpicSubtasksByIdSuccess() throws IOException, InterruptedException {
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
        URI url = URI.create("http://localhost:8080/epics/" + epicId + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testGetEpicSubtasksByIdFailure() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Testing Epic", NEW);
        manager.createEpic(epic);

        int epicId = manager.getEpics().get(0).getId();

        Subtask subtask1 = new Subtask("Subtask 1", "Testing Subtask 1", NEW, epicId,
                LocalDateTime.now(), Duration.ofMinutes(5));
        Subtask subtask2 = new Subtask("Subtask 2", "Testing Subtask 2", NEW, epicId,
                LocalDateTime.now().plusMinutes(10), Duration.ofMinutes(5));

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        int failId = 2;
        assertNotEquals(failId, epicId);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + failId + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Testing Epic", NEW);
        manager.createEpic(epic);

        int id = manager.getEpics().get(0).getId();

        List<Epic> epicsFromManager = manager.getEpics();
        assertNotNull(epicsFromManager);
        assertEquals(1, epicsFromManager.size());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        epicsFromManager = manager.getEpics();
        assertEquals(200, response.statusCode());
        assertEquals(0, epicsFromManager.size(), "Эпики не удаляются");
    }
}