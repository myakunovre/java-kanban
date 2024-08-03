package webapi;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tasks.Status.NEW;

public class HttpTaskManagerPrioritizedTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);

    public HttpTaskManagerPrioritizedTest() throws IOException {
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

        Task task1 = new Task("Test 1", "Testing task 1", NEW, LocalDateTime.now(),
                Duration.ofMinutes(5));
        manager.createTask(task1);

        Task task2 = new Task("Test 2", "Testing task 2", NEW, LocalDateTime.now().plusMinutes(10),
                Duration.ofMinutes(5));
        manager.createTask(task2);

        Task task3 = new Task("Test 3", "Testing task 3", NEW, LocalDateTime.now().plusMinutes(20),
                Duration.ofMinutes(5));
        manager.createTask(task3);

        Epic epic = new Epic("Epic", "Testing Epic", NEW);
        manager.createEpic(epic);

        int epicId = manager.getEpics().get(0).getId();

        Subtask subtask1 = new Subtask("Subtask 1", "Testing Subtask 1", NEW, epicId,
                LocalDateTime.now().plusMinutes(30), Duration.ofMinutes(5));
        manager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask 1", "Testing Subtask 1", NEW, epicId,
                LocalDateTime.now().plusMinutes(40), Duration.ofMinutes(5));
        manager.createSubtask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonArray(), "Полученная строка не является JSON-массивом");
    }
}
