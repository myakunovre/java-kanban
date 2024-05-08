package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private static int count = 0;
    public Map<Integer, Task> tasks = new HashMap<>();
    public Map<Integer, Epic> epics = new HashMap<>();
    public Map<Integer, Subtask> subtasks = new HashMap<>();


    // Получение списка всех задач (для каждого типа задач)
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    // Удаление всех задач (для каждого типа задач)
    public void removeTasks() {
        tasks.clear();
    }

    public void removeEpics() {
        subtasks.clear();
        epics.clear();
    }

    public void removeSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.subtasksId.clear();
        }
    }

    // Получение задачи по идентификатору (для каждого типа задач)
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubTaskById(int id) {
        return subtasks.get(id);
    }

    //    Создание задач (для каждого типа задач)
    public void createTask(Task task) {
        int id = getIdForNewTask();
        task.setId(id);
        tasks.put(id, task);
    }

    public void createEpic(Epic epic) {
        int id = getIdForNewTask();
        epic.setId(id);
        updateEpicStatus(epic);
        epics.put(id, epic);
    }

    public void createSubtask(Subtask subtask) {
        int id = getIdForNewTask();
        subtask.setId(id);
        subtasks.put(id, subtask);

        int epicId = subtask.getEpicId();
        addSubtaskIdToEpic(id, epicId);
        updateEpicStatus(epics.get(epicId));
    }

    //    Обновление задач (для каждого типа задач)
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epics.get(subtask.getEpicId()));
    }

    //    Удаление задач по идентификатору (для каждого типа задач)
    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        for (Integer subtaskId : epic.subtasksId) {
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
    }

    public void removeSubtaskById(int id) {
        int epicId = subtasks.get(id).getEpicId();
        epics.get(epicId).subtasksId.remove((Integer) id);
        updateEpicStatus(epics.get(epicId));
        subtasks.remove(id);
    }

    // Получение списка всех подзадач определённого эпика
    public List<Subtask> getSubtasksForEpic(int epicId) {
        List<Subtask> subtasksForEpic = new ArrayList<>();
        for (Integer subtaskId : epics.get(epicId).subtasksId) {
            subtasksForEpic.add(subtasks.get(subtaskId));
        }
        return subtasksForEpic;
    }

    // Вспомогательные методы класса
    public int getIdForNewTask() {
        count++;
        return count;
    }

    private void addSubtaskIdToEpic(int subtaskId, int epicId) {
        Epic epic = epics.get(epicId);
        epic.subtasksId.add(subtaskId);
    }

    public void updateEpicStatus(Epic epic) {
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

    private boolean isAllSubtasksOfEpicHaveStatusNew(Epic epic) {
        for (Integer subtaskId : epic.subtasksId) {
            if (!subtasks.get(subtaskId).getStatus().equals(Status.NEW)) {
                return false;
            }
        }
        return true;
    }

    private boolean isAllSubtasksOfEpicHaveStatusDONE(Epic epic) {
        for (Integer subtaskId : epic.subtasksId) {
            if (!subtasks.get(subtaskId).getStatus().equals(Status.DONE)) {
                return false;
            }
        }
        return true;
    }

}
