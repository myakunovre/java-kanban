package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public interface TaskManager {

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    HistoryManager getHistoryManager();

    void removeTasks();

    void removeEpics();

    void removeSubtasks();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    void updateTask(tasks.Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void removeTaskById(int id);

    void removeEpicById(int id);

    void removeSubtaskById(int id);

    List<Subtask> getSubtasksForEpic(int epicId);

    boolean isTasksCrossInTime(Task task1, Task task2);

    boolean isNotCrossInTime(Task task);

    List<Task> getPrioritizedTasks();

    Status convertStringToStatus(String string);
}
