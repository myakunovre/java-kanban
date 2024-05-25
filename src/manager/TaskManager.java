package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public interface TaskManager {

    // Получение списка всех задач (для каждого типа задач)
    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    // добавил новый метод возможности получения доступа из Main к методам класса InMemoryHistoryManager
    HistoryManager getHistoryManager();

    // Удаление всех задач (для каждого типа задач)
    void removeTasks();

    void removeEpics();

    void removeSubtasks();

    // Получение задачи по идентификатору (для каждого типа задач)
    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    //    Создание задач (для каждого типа задач)
    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    //    Обновление задач (для каждого типа задач)
    void updateTask(tasks.Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    //    Удаление задач по идентификатору (для каждого типа задач)
    void removeTaskById(int id);

    void removeEpicById(int id);

    void removeSubtaskById(int id);


    // Получение списка всех подзадач определённого эпика
    List<Subtask> getSubtasksForEpic(int epicId);

}
