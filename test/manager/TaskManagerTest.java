package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.Status.*;

abstract class TaskManagerTest<T extends TaskManager> {

    TaskManager taskManager = Managers.getDefault();

    @AfterEach
    void removeAllTasks() {
        taskManager.removeTasks();
        taskManager.removeEpics();
    }

    @Test
    void tasksAreEqualsIfTheirIdsAreEqual() {
        Task task1 = new Task("task1", "Test task1", NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.createTask(task1);
        final int task1Id = task1.getId();

        Task task2 = new Task("task2", "Test task2", NEW, LocalDateTime.now().plusMinutes(5), Duration.ofMinutes(5));
        taskManager.createTask(task2);
        task2.setId(task1Id);
        final int task2Id = task2.getId();

        assertEquals(task1Id, task2Id, "ID сравниваемых задач не равны");
        assertEquals(task1, task2, "Задачи не совпадают");
    }

    @Test
    void epicsAreEqualsIfTheirIdsAreEqual() {
        Epic epic1 = new Epic("epic1", "Test epic1", NEW);
        taskManager.createEpic(epic1);
        final int epic1Id = epic1.getId();

        Epic epic2 = new Epic("epic2", "Test epic2", NEW);
        taskManager.createEpic(epic2);
        epic2.setId(epic1Id);
        final int epic2Id = epic2.getId();

        assertEquals(epic1Id, epic2Id, "ID сравниваемых эпиков не равны");
        assertEquals(epic1Id, epic2Id, "Эпики не совпадают");
    }

    @Test
    void subtasksAreEqualsIfTheirIdsAreEqual() {
        Epic epic1 = new Epic("epic1", "Test epic1", NEW);
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("subtask1", "Test subtask1", NEW, epic1.getId(), LocalDateTime.now(),
                Duration.ofMinutes(5));
        taskManager.createSubtask(subtask1);
        final int subtask1Id = subtask1.getId();

        Subtask subtask2 = new Subtask("subtask2", "Test subtask2", NEW, epic1.getId(), LocalDateTime.now(),
                Duration.ofMinutes(5));
        taskManager.createSubtask(subtask2);
        subtask2.setId(subtask1Id);
        final int subtask2Id = subtask2.getId();

        assertEquals(subtask1Id, subtask2Id, "ID сравниваемых подзадач не равны");
        assertEquals(subtask1, subtask2, "Подзадачи не совпадают");
    }

    @Test
    void ShouldAddingTaskAndFindById() {
        int tasksSizeBeforeAdding = taskManager.getTasks().size();
        Task task1 = new Task("Task", "Test task", NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.createTask(task1);
        int tasksSizeAfterAdding = taskManager.getTasks().size();

        assertEquals(0, tasksSizeBeforeAdding);
        assertEquals(1, tasksSizeAfterAdding, "Менеджер задач не добавил задачу");

        Task task2 = taskManager.getTaskById(task1.getId());

        assertNotNull(task2, "Менеджер не нашел задачу по ID");
        assertEquals(task1, task2, "Менеджер нашел не ту задачу");
    }

    @Test
    void ShouldAddingEpicAndFindById() {
        int epicsSizeBeforeAdding = taskManager.getEpics().size();
        Epic epic1 = new Epic("Epic", "Test epic", NEW);
        taskManager.createEpic(epic1);
        int epicsSizeAfterAdding = taskManager.getEpics().size();

        assertEquals(0, epicsSizeBeforeAdding);
        assertEquals(1, epicsSizeAfterAdding, "Менеджер задач не добавил задачу");

        Task epic2 = taskManager.getEpicById(epic1.getId());

        assertNotNull(epic2, "Менеджер не нашел эпик по ID");
        assertEquals(epic1, epic2, "Менеджер нашел не тот эпик");
    }

    @Test
    void ShouldAddingSubtaskAndFindById() {
        int subtasksSizeBeforeAdding = taskManager.getSubtasks().size();
        Epic epic = new Epic("Epic", "Test epic", NEW);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask", "Test subtask", NEW, epic.getId(), LocalDateTime.now(),
                Duration.ofMinutes(5));
        taskManager.createSubtask(subtask1);
        int subtasksSizeAfterAdding = taskManager.getSubtasks().size();

        assertEquals(0, subtasksSizeBeforeAdding);
        assertEquals(1, subtasksSizeAfterAdding, "Менеджер задач не добавил подзадачу");

        Subtask subtask2 = taskManager.getSubtaskById(subtask1.getId());

        assertNotNull(subtask2, "Менеджер не нашел задачу по ID");
        assertEquals(subtask1, subtask2, "Менеджер нашел не ту задачу");
    }

    @Test
    void ShouldSetGeneratedIdInTask() {
        Task task = new Task("Task", "Test task", NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        int firstId = task.getId();
        taskManager.createTask(task);
        int generatedId = task.getId();

        assertEquals(0, firstId);
        assertNotEquals(firstId, generatedId, "ID не сгенерировался или не присвоился");
    }

    @Test
    void ShouldSetGeneratedIdInEpic() {
        Epic epic = new Epic("Epic", "Test epic", NEW);
        int firstId = epic.getId();
        taskManager.createEpic(epic);
        int generatedId = epic.getId();

        assertEquals(0, firstId);
        assertNotEquals(firstId, generatedId, "ID не сгенерировался или не присвоился");
    }

    @Test
    void ShouldSetGeneratedIdInSubtask() {
        Epic epic = new Epic("Epic", "Test epic", NEW);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Test subtask", NEW, epic.getId(), LocalDateTime.now(),
                Duration.ofMinutes(5));
        int firstId = subtask.getId();
        taskManager.createTask(subtask);
        int generatedId = subtask.getId();

        assertEquals(0, firstId);
        assertNotEquals(firstId, generatedId, "ID не сгенерировался или не присвоился");
    }

    @Test
    void ShouldNotChangeTaskFields() {
        Task taskBeforeAdding = new Task("Task", "Test task", NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.createTask(taskBeforeAdding);

        Task taskAfterAdding = taskManager.getTaskById(taskBeforeAdding.getId());

        String nameBeforeAdding = taskBeforeAdding.getName();
        String nameAfterAdding = taskAfterAdding.getName();
        assertEquals(nameBeforeAdding, nameAfterAdding, "При добавлении задачи изменилось поле name");

        String descriptionBeforeAdding = taskBeforeAdding.getDescription();
        String descriptionAfterAdding = taskAfterAdding.getDescription();
        assertEquals(descriptionBeforeAdding, descriptionAfterAdding, "При добавлении задачи изменилось поле description");

        Status statusBeforeAdding = taskBeforeAdding.getStatus();
        Status statusAfterAdding = taskAfterAdding.getStatus();
        assertEquals(statusBeforeAdding, statusAfterAdding, "При добавлении задачи изменилось поле status");
    }

    @Test
    void ShouldNotChangeEpicFields() {
        Epic epicBeforeAdding = new Epic("epic", "Test epic", NEW);
        taskManager.createEpic(epicBeforeAdding);

        Epic epicAfterAdding = taskManager.getEpicById(epicBeforeAdding.getId());

        String nameBeforeAdding = epicBeforeAdding.getName();
        String nameAfterAdding = epicAfterAdding.getName();
        assertEquals(nameBeforeAdding, nameAfterAdding, "Эпик не добавлен или добавлен не корректно");

        String descriptionBeforeAdding = epicBeforeAdding.getDescription();
        String descriptionAfterAdding = epicAfterAdding.getDescription();
        assertEquals(descriptionBeforeAdding, descriptionAfterAdding, "При добавлении эпика изменилось поле description");

        Status statusBeforeAdding = epicBeforeAdding.getStatus();
        Status statusAfterAdding = epicAfterAdding.getStatus();
        assertEquals(statusBeforeAdding, statusAfterAdding, "При добавлении эпика изменилось поле status");
    }

    @Test
    void ShouldNotChangeSubtaskFields() {
        Epic epic = new Epic("epic", "Test epic", NEW);
        taskManager.createEpic(epic);
        Subtask subtaskBeforeAdding = new Subtask("subtask", "Test subtask", NEW, epic.getId(),
                LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.createSubtask(subtaskBeforeAdding);

        Subtask subtaskAfterAdding = taskManager.getSubtaskById(subtaskBeforeAdding.getId());

        String nameBeforeAdding = subtaskBeforeAdding.getName();
        String nameAfterAdding = subtaskAfterAdding.getName();
        assertEquals(nameBeforeAdding, nameAfterAdding, "Подзадачи не добавлена или добавлена не корректно");

        String descriptionBeforeAdding = subtaskBeforeAdding.getDescription();
        String descriptionAfterAdding = subtaskAfterAdding.getDescription();
        assertEquals(descriptionBeforeAdding, descriptionAfterAdding, "При добавлении подзадачи изменилось поле description");

        Status statusBeforeAdding = subtaskBeforeAdding.getStatus();
        Status statusAfterAdding = subtaskAfterAdding.getStatus();
        assertEquals(statusBeforeAdding, statusAfterAdding, "При добавлении подзадачи изменилось поле status");

        int epicIdBeforeAdding = subtaskBeforeAdding.getEpicId();
        int epicIdAfterAdding = subtaskAfterAdding.getEpicId();
        assertEquals(epicIdBeforeAdding, epicIdAfterAdding, "При добавлении подзадачи изменилось поле epicId");
    }

    @Test
    void shouldNotChangeTaskInHistory() {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Task1", "Test task1", NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.createTask(task1);

        taskManager.getTaskById(task1.getId());

        Status statusBeforeUpdate = taskManager.getHistoryManager().getHistory().get(0).getStatus();

        Task task2 = new Task("Task2", "Test task2", IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(5));
        task2.setId(task1.getId());
        taskManager.updateTask(task2);

        Status statusAfterUpdate = taskManager.getHistoryManager().getHistory().get(0).getStatus();

        assertEquals(statusBeforeUpdate, statusAfterUpdate, "historyManager не сохраняет предыдущую историю задачи");
    }

    @Test
    void shouldAddDifferentTasksInHistoryAndNotDouble() {
        Task task1 = new Task("Task1", "Test task1", NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.createTask(task1);

        Task task2 = new Task("Task2", "Test task2", NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.createTask(task2);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task1.getId());

        int historySize = taskManager.getHistoryManager().getHistory().size();
        assertEquals(2, historySize, "Менеджер истории работает не корректно");

        String firstTaskNameInHistory = taskManager.getHistoryManager().getHistory().get(0).getName();
        String secondTaskNameInHistory = taskManager.getHistoryManager().getHistory().get(1).getName();

        assertEquals("Task2", firstTaskNameInHistory, "Порядок хранения не правильный");
        assertEquals("Task1", secondTaskNameInHistory, "Порядок хранения не правильный");
    }

    @Test
    void shouldRemoveTasksInHistoryWhenRemoveTasks() {
        Task task1 = new Task("Task1", "Test task1", NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.createTask(task1);

        Task task2 = new Task("Task2", "Test task2", NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.createTask(task2);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());

        taskManager.removeTaskById(task1.getId());

        int historySize = taskManager.getHistoryManager().getHistory().size();
        assertEquals(1, historySize, "При удалении задачи история хранения работает не корректно");

        String taskNameInHistory = taskManager.getHistoryManager().getHistory().get(0).getName();
        assertEquals("Task2", taskNameInHistory, "Удаленная задача не удалилась из истории");
    }

    @Test
    void ShouldRemoveSubtaskIdInEpicWhenRemoveSubtask() {
        Epic epic = new Epic("Epic", "Test epic", NEW);
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Test subtask", NEW, epic.getId(), LocalDateTime.now(),
                Duration.ofMinutes(5));
        taskManager.createSubtask(subtask);

        int subtaskId = subtask.getId();
        assertTrue(epic.subtasksId.contains(subtaskId), "В эпик не добавился id его сабтаски");

        taskManager.removeSubtaskById(subtaskId);
        assertFalse(epic.subtasksId.contains(subtaskId), "Из эпика не удалился id его удаленной сабтаски");
    }

    @Test
    void shouldChangeTasksInManagerBySetters() {
        Task task1 = new Task("Task1", "Test task1", NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.createTask(task1);
        Status TaskStatusFirst = taskManager.getTaskById(task1.getId()).getStatus();
        task1.setStatus(IN_PROGRESS);
        Status TaskStatusAfterChangingBySetter = taskManager.getTaskById(task1.getId()).getStatus();

        assertNotEquals(TaskStatusFirst, TaskStatusAfterChangingBySetter, "Сеттер не изменил id задачи в менеджере");
    }

    @Test
    void shouldSetEpicStatusNewIfAllSubtasksHaveStatusNew() {
        Epic epic1 = new Epic("epic1", "Test epic1", NEW);
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("subtask1", "Test subtask1", NEW, epic1.getId(), LocalDateTime.now(),
                Duration.ofMinutes(5));
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("subtask2", "Test subtask2", NEW, epic1.getId(), LocalDateTime.now(),
                Duration.ofMinutes(5));
        taskManager.createSubtask(subtask2);

        assertEquals(NEW, epic1.getStatus(), "У epic1 должен быть статус NEW, а по факту " + epic1.getStatus());
    }

    @Test
    void shouldSetEpicStatusDoneIfAllSubtasksHaveStatusDone() {
        Epic epic1 = new Epic("epic1", "Test epic1", NEW);
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("subtask1", "Test subtask1", DONE, epic1.getId(), LocalDateTime.now(),
                Duration.ofMinutes(5));
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("subtask2", "Test subtask2", DONE, epic1.getId(), LocalDateTime.now(),
                Duration.ofMinutes(5));
        taskManager.createSubtask(subtask2);

        assertEquals(DONE, epic1.getStatus(), "У epic1 должен быть статус DONE, а по факту " + epic1.getStatus());
    }

    @Test
    void shouldSetEpicStatusInProgressIfSubtasksHaveStatusNewAndDone() {
        Epic epic1 = new Epic("epic1", "Test epic1", NEW);
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("subtask1", "Test subtask1", NEW, epic1.getId(), LocalDateTime.now(),
                Duration.ofMinutes(5));
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("subtask2", "Test subtask2", DONE, epic1.getId(), LocalDateTime.now(),
                Duration.ofMinutes(5));
        taskManager.createSubtask(subtask2);

        assertEquals(IN_PROGRESS, epic1.getStatus(), "У epic1 должен быть статус IN_PROGRESS, а по факту "
                + epic1.getStatus());
    }

    @Test
    void shouldSetEpicStatusInProgressIfAllSubtasksHaveStatusInProgress() {
        Epic epic1 = new Epic("epic1", "Test epic1", NEW);
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("subtask1", "Test subtask1", IN_PROGRESS, epic1.getId(),
                LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("subtask2", "Test subtask2", IN_PROGRESS, epic1.getId(),
                LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.createSubtask(subtask2);

        assertEquals(IN_PROGRESS, epic1.getStatus(), "У epic1 должен быть статус IN_PROGRESS, а по факту "
                + epic1.getStatus());
    }

    @Test
    void shouldSubtaskHaveEpic() {
        Epic epic1 = new Epic("epic1", "Test epic1", NEW);
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("subtask1", "Test subtask1", IN_PROGRESS, epic1.getId(),
                LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("subtask2", "Test subtask2", IN_PROGRESS, epic1.getId(),
                LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.createSubtask(subtask2);

        assertEquals(epic1.getId(), subtask1.getEpicId(), "ID эпика и его подзадачи №1 не равны, " +
                "возможно подзадача №1 не имеет эпика");
        assertEquals(epic1.getId(), subtask2.getEpicId(), "ID эпика и его подзадачи №2 не равны, " +
                "возможно подзадача №2 не имеет эпика");
    }

    @Test
    void testCrossTasksInTime() {

        LocalDateTime startTime1 = LocalDateTime.of(2024, 01, 01, 00, 00, 00);
        LocalDateTime startTime2 = LocalDateTime.of(2024, 01, 01, 00, 20, 00);

        Task task1 = new Task("Task1", "Test task1", NEW, startTime1, Duration.ofMinutes(15));
        taskManager.createTask(task1);
        Task task2 = new Task("Task1", "Test task1", NEW, startTime2, Duration.ofMinutes(15));
        taskManager.createTask(task2);

        assertFalse(taskManager.isTasksCrossInTime(task1, task2), "Задачи не должны пересекаться");
        assertFalse(taskManager.isTasksCrossInTime(task2, task1), "Задачи не должны пересекаться");


        startTime1 = LocalDateTime.of(2024, 01, 01, 00, 00, 00);
        startTime2 = LocalDateTime.of(2024, 01, 01, 00, 00, 00);

        task1 = new Task("Task1", "Test task1", NEW, startTime1, Duration.ofMinutes(15));
        taskManager.createTask(task1);
        task2 = new Task("Task1", "Test task1", NEW, startTime2, Duration.ofMinutes(15));
        taskManager.createTask(task2);

        assertTrue(taskManager.isTasksCrossInTime(task1, task2), "Задачи должны пересекаться, т.к. startTime задач равны");


        startTime1 = LocalDateTime.of(2024, 01, 01, 00, 00, 00);
        startTime2 = LocalDateTime.of(2024, 01, 01, 00, 15, 00);

        task1 = new Task("Task1", "Test task1", NEW, startTime1, Duration.ofMinutes(30));
        taskManager.createTask(task1);
        task2 = new Task("Task1", "Test task1", NEW, startTime2, Duration.ofMinutes(30));
        taskManager.createTask(task2);

        assertTrue(taskManager.isTasksCrossInTime(task1, task2), "Задачи должны пересекаться, " +
                "т.к. временные интервалы накладываются");
        assertTrue(taskManager.isTasksCrossInTime(task2, task1), "Задачи должны пересекаться, " +
                "т.к. временные интервалы накладываются");
    }
}
