package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.Status.NEW;

class InMemoryHistoryManagerTest {
    TaskManager taskManager = Managers.getDefault();
    HistoryManager historyManager = taskManager.getHistoryManager();

    @AfterEach
    void removeAllTasks() {
        taskManager.removeTasks();
    }

    @Test
    void addInEmptyHistory() {
        assertTrue(historyManager.getHistory().isEmpty());

        historyManager.add(new Task("task1", "Test task1", NEW, LocalDateTime.now(), Duration.ofMinutes(5)));
        assertEquals(1, historyManager.getHistory().size(), "При добавлении задачи в пустую историю в истории должна быть одна задача");
    }

    @Test
    void addDoubleTasks() {
        Task task1 = new Task("task1", "Test task1", NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.createTask(task1);

        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.add(task1);

        assertEquals(1, historyManager.getHistory().size(), "Одна и та же задача не должна дублироваться в истории");
    }

    @Test
    void removeFirst() {
        Task task1 = new Task("task1", "Test task1", NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        taskManager.createTask(task1);
        Task task2 = new Task("task2", "Test task2", NEW, LocalDateTime.now().plusMinutes(15), Duration.ofMinutes(10));
        taskManager.createTask(task2);
        Task task3 = new Task("task3", "Test task3", NEW, LocalDateTime.now().plusMinutes(30), Duration.ofMinutes(10));
        taskManager.createTask(task3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        assertEquals(3, historyManager.getHistory().size(), "В истории должно быть 3 просмотра");

        historyManager.remove(task1.getId());

        assertEquals(2, historyManager.getHistory().size(), "В истории должно остаться 2 просмотра, а у вас: " + historyManager.getHistory().size());
        assertNotEquals(historyManager.getHistory().getFirst(), task1, "Удаление первого просмотра работает не корректно");
        assertEquals(historyManager.getHistory().getFirst(), task2, "Удаление первого просмотра работает не корректно");
        assertEquals(historyManager.getHistory().getLast(), task3, "Удаление первого просмотра работает не корректно");
    }

    @Test
    void removeLast() {
        Task task1 = new Task("task1", "Test task1", NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        taskManager.createTask(task1);
        Task task2 = new Task("task2", "Test task2", NEW, LocalDateTime.now().plusMinutes(15), Duration.ofMinutes(10));
        taskManager.createTask(task2);
        Task task3 = new Task("task3", "Test task3", NEW, LocalDateTime.now().plusMinutes(30), Duration.ofMinutes(10));
        taskManager.createTask(task3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        assertEquals(3, historyManager.getHistory().size(), "В истории должно быть 3 просмотра");

        historyManager.remove(task3.getId());

        assertEquals(2, historyManager.getHistory().size(), "В истории должно остаться 2 просмотра, а у вас: " + historyManager.getHistory().size());
        assertNotEquals(historyManager.getHistory().getLast(), task3, "Удаление последнего просмотра работает не корректно");
        assertEquals(historyManager.getHistory().getFirst(), task1, "Удаление последнего просмотра работает не корректно");
        assertEquals(historyManager.getHistory().getLast(), task2, "Удаление последнего просмотра работает не корректно");
    }

    @Test
    void removeMiddle() {
        Task task1 = new Task("task1", "Test task1", NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        taskManager.createTask(task1);
        Task task2 = new Task("task2", "Test task2", NEW, LocalDateTime.now().plusMinutes(15), Duration.ofMinutes(10));
        taskManager.createTask(task2);
        Task task3 = new Task("task3", "Test task3", NEW, LocalDateTime.now().plusMinutes(30), Duration.ofMinutes(10));
        taskManager.createTask(task3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        assertEquals(3, historyManager.getHistory().size(), "В истории должно быть 3 просмотра");

        historyManager.remove(task2.getId());

        assertEquals(2, historyManager.getHistory().size(), "В истории должно остаться 2 просмотра, а у вас: " + historyManager.getHistory().size());
        assertNotEquals(historyManager.getHistory().get(1), task2, "Удаление среднего просмотра работает не корректно");
        assertEquals(historyManager.getHistory().getFirst(), task1, "Удаление среднего просмотра работает не корректно");
        assertEquals(historyManager.getHistory().getLast(), task3, "Удаление среднего просмотра работает не корректно");
    }
}