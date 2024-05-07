import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private static int count = 0;
    public static HashMap<Integer, Task> tasks = new HashMap<>();
    public static HashMap<Integer, Epic> epics = new HashMap<>();
    public static HashMap<Integer, Subtask> subtasks = new HashMap<>();


    // Получение списка всех задач (для каждого типа задач)
    public static List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.addAll(TaskManager.tasks.values());
        return tasks;
    }

    public static List<Epic> getEpics() {
        List<Epic> epics = new ArrayList<>();
        epics.addAll(TaskManager.epics.values());
        return epics;
    }

    public static List<Subtask> getSubtasks() {
        List<Subtask> subtasks = new ArrayList<>();
        subtasks.addAll(TaskManager.subtasks.values());
        return subtasks;
    }

    // Удаление всех задач (для каждого типа задач)
    public static void removeTasks() {
        tasks.clear();
    }

    public static void removeEpics() {
        subtasks.clear();
        epics.clear();
    }

    public static void removeSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.subtasksId.clear();
        }
    }

    // Получение задачи по идентификатору (для каждого типа задач)
    public static Task getTaskById(int id) {
        return tasks.get(id);
    }

    public static Epic getEpicById(int id) {
        return epics.get(id);
    }

    public static Subtask getSubTaskById(int id) {
        return subtasks.get(id);
    }

    //    Создание задач (для каждого типа задач)
    public static void createTask(Task task) {
        int id = getIdForNewTask();
        task.setId(id);
        tasks.put(id, task);
    }

    public static void createEpic(Epic epic) {
        int id = getIdForNewTask();
        epic.setId(id);
        updateEpicStatus(epic);
        epics.put(id, epic);
    }

    public static void createSubtask(Subtask subtask) {
        int id = getIdForNewTask();
        subtask.setId(id);
        subtasks.put(id, subtask);

        int epicId = subtask.getEpicId();
        addSubtaskIdToEpic(id, epicId);
        updateEpicStatus(epics.get(epicId));
    }

    //    Обновление задач (для каждого типа задач)
    public static void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public static void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
    }

    public static void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epics.get(subtask.getEpicId()));
    }

    //    Удаление задач по идентификатору (для каждого типа задач)
    public static void removeTaskById(int id) {
        tasks.remove(id);
    }

    public static void removeEpicById(int id) {
        Epic epic = epics.get(id);
        for (Integer subtaskId : epic.subtasksId) {
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
    }

    public static void removeSubtaskById(int id) {
        int epicId = subtasks.get(id).getEpicId();
        epics.get(epicId).subtasksId.remove((Integer) id);
        updateEpicStatus(epics.get(epicId));
        subtasks.remove(id);
    }

    // Получение списка всех подзадач определённого эпика
    public static List<Subtask> getSubtasksForEpic(int epicId) {
        List<Subtask> subtasksForEpic = new ArrayList<>();
        for (Integer subtaskId : epics.get(epicId).subtasksId) {
            subtasksForEpic.add(subtasks.get(subtaskId));
        }
        return subtasksForEpic;
    }

    // Вспомогательные методы класса
    public static int getIdForNewTask() {
        count++;
        return count;
    }

    private static void addSubtaskIdToEpic(int subtaskId, int epicId) {
        Epic epic = epics.get(epicId);
        epic.subtasksId.add(subtaskId);
    }

    public static void updateEpicStatus(Epic epic) {
        if (epic.subtasksId.isEmpty() || isAllSubtasksOfEpicHaveStatusNew(epic)) {
            epic.setStatus(Status.NEW);
            return;
        }
        if (isAllSubtasksOfEpicHaveStatusDONE(epic)) {
            epic.setStatus(Status.DONE);
            return;
        }
        epic.setStatus(Status.IN_PROGRESS);
    }

    private static boolean isAllSubtasksOfEpicHaveStatusNew(Epic epic) {
        for (Integer subtaskId : epic.subtasksId) {
            if (!subtasks.get(subtaskId).getStatus().equals(Status.NEW)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isAllSubtasksOfEpicHaveStatusDONE(Epic epic) {
        for (Integer subtaskId : epic.subtasksId) {
            if (!subtasks.get(subtaskId).getStatus().equals(Status.DONE)) {
                return false;
            }
        }
        return true;
    }

}
