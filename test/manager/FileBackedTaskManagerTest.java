package manager;

import exceptions.ManagerSaveException;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<TaskManager> {

    @Test
    void shouldSaveEmptyFileWhenHaveNotTasks() throws IOException {
        File file = File.createTempFile("prefix", "suffix");
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
        fileBackedTaskManager.save();
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            while (fileReader.ready()) {
                String string = fileReader.readLine();
                assertNull(string, "Файл не пустой");
            }
        }
    }

    @Test
    void shouldNotHaveTasksWhenLoadFromEmptyFile() throws IOException {
        File file = File.createTempFile("prefix", "suffix");
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
        assertTrue(fileBackedTaskManager.tasks.isEmpty(), "Мапа с тасками не пустая");
        assertTrue(fileBackedTaskManager.epics.isEmpty(), "Мапа с эпиками не пустая");
        assertTrue(fileBackedTaskManager.subtasks.isEmpty(), "Мапа с сабтасками не пустая");
    }

    @Test
    void SaveToFileAndLoadFromFile() throws IOException {
        File file = File.createTempFile("prefix", "suffix");
        FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(file);

        Task task1 = new Task("Task1", "description Task1", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        fileBackedTaskManager1.createTask(task1);
        Task task2 = new Task("Task1", "description Task2", Status.NEW, LocalDateTime.now().plusMinutes(5), Duration.ofMinutes(5));
        fileBackedTaskManager1.createTask(task2);
        Task task3 = new Task("Task3", "description Task3", Status.NEW, LocalDateTime.now().plusMinutes(10), Duration.ofMinutes(5));
        fileBackedTaskManager1.createTask(task3);

        Epic epic = new Epic("Epic", "description Epic", Status.NEW);
        fileBackedTaskManager1.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask1", "description Subtask1", Status.NEW, epic.getId(), LocalDateTime.now().plusMinutes(15), Duration.ofMinutes(5));
        fileBackedTaskManager1.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask2", "description Subtask2", Status.NEW, epic.getId(), LocalDateTime.now().plusMinutes(20), Duration.ofMinutes(5));
        fileBackedTaskManager1.createSubtask(subtask2);

        FileBackedTaskManager fileBackedTaskManager2 = FileBackedTaskManager.loadFromFile(file);

        assertEquals(fileBackedTaskManager1.tasks, fileBackedTaskManager2.tasks, "Таски различаются");
        assertEquals(fileBackedTaskManager1.epics, fileBackedTaskManager2.epics, "Эпики различаются");
        assertEquals(fileBackedTaskManager1.subtasks, fileBackedTaskManager2.subtasks, "Сабтаски различаются");
    }

    @Test
    public void testException() {
        assertThrows(ManagerSaveException.class, () -> {
            File testFile = Files.createTempFile("test", "test").toFile();
            testFile.setReadOnly();
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(testFile.toPath());

            Task task1 = new Task("Task1", "description Task1", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
            fileBackedTaskManager.createTask(task1);
        }, "Запись в файл, доступный только для чтения, должна приводить к исключению");
    }
}