package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected static int count = 0;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Set<Task> sortedTasks = new TreeSet<>(new ComparatorByStartTime());
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    static class ComparatorByStartTime implements Comparator<Task> {
        @Override
        public int compare(Task o1, Task o2) {
            if (o1.getStartTime().isAfter(o2.getStartTime())) {
                return 1;
            }
            if (o1.getStartTime().isBefore(o2.getStartTime())) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    static class ComparatorByEndTime implements Comparator<Task> {
        @Override
        public int compare(Task o1, Task o2) {
            if (o1.getEndTime().isAfter(o2.getEndTime())) {
                return 1;
            }
            if (o1.getEndTime().isBefore(o2.getEndTime())) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(sortedTasks);
    }

    @Override
    public void removeTasks() {
        for (Integer id : tasks.keySet()) {
            historyManager.remove(id);
        }

        for (Task task : tasks.values()) {
            sortedTasks.remove(task);
        }

        tasks.clear();
    }

    @Override
    public void removeEpics() {
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
        }

        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
        }

        for (Subtask subtask : subtasks.values()) {
            sortedTasks.remove(subtask);
        }

        tasks.clear();
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void removeSubtasks() {
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
        }

        for (Subtask subtask : subtasks.values()) {
            sortedTasks.remove(subtask);
        }

        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.subtasksId.clear();
        }
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void createTask(Task task) {
        int id = getIdForNewTask();
        task.setId(id);
        tasks.put(id, task);

        boolean isNotCrossInTime = getPrioritizedTasks().stream()
                .allMatch(anyTask -> isTasksCrossInTime(anyTask, task));

        if (isNotCrossInTime && task.getStartTime() != null) {
            sortedTasks.add(task);
        }
    }

    @Override
    public void createEpic(Epic epic) {
        int id = getIdForNewTask();
        epic.setId(id);
        updateEpicStatus(epic);
        updateEpicStartAndEnd(epic);
        updateEpicDuration(epic);
        epics.put(id, epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        int id = getIdForNewTask();
        subtask.setId(id);
        subtasks.put(id, subtask);

        boolean isNotCrossInTime = getPrioritizedTasks().stream()
                .allMatch(anyTask -> isTasksCrossInTime(anyTask, subtask));

        if (isNotCrossInTime && subtask.getStartTime() != null) {
            sortedTasks.add(subtask);
        }

        int epicId = subtask.getEpicId();
        addSubtaskIdToEpic(id, epicId);
        updateEpicStatus(epics.get(epicId));
        updateEpicStartAndEnd(epics.get(epicId));
        updateEpicDuration(epics.get(epicId));
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
        sortedTasks.remove(task);

        boolean isNotCrossInTime = getPrioritizedTasks().stream()
                .allMatch(anyTask -> isTasksCrossInTime(anyTask, task));

        if (isNotCrossInTime && task.getStartTime() != null) {
            sortedTasks.add(task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
        updateEpicDuration(epic);
        updateEpicStartAndEnd(epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        sortedTasks.remove(subtask);

        boolean isNotCrossInTime = getPrioritizedTasks().stream()
                .allMatch(anyTask -> isTasksCrossInTime(anyTask, subtask));

        if (isNotCrossInTime && subtask.getStartTime() != null) {
            sortedTasks.add(subtask);
        }

        updateEpicStatus(epics.get(subtask.getEpicId()));
        updateEpicStartAndEnd(epics.get(subtask.getEpicId()));
        updateEpicDuration(epics.get(subtask.getEpicId()));
    }

    @Override
    public void removeTaskById(int id) {
        sortedTasks.remove(tasks.get(id));
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        for (Integer subtaskId : epic.subtasksId) {
            sortedTasks.remove(subtasks.get(id));
            subtasks.remove(subtaskId);
            historyManager.remove(subtaskId);
        }

        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) {
        int epicId = subtasks.get(id).getEpicId();
        epics.get(epicId).subtasksId.remove((Integer) id);
        updateEpicStatus(epics.get(epicId));
        updateEpicStartAndEnd(epics.get(epicId));
        updateEpicDuration(epics.get(epicId));

        sortedTasks.remove(subtasks.get(id));
        subtasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public List<Subtask> getSubtasksForEpic(int epicId) {
        return epics.get(epicId).subtasksId.stream()
                .map(subtasks::get)
                .collect(Collectors.toList());
    }

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
        if (isAllSubtasksOfEpicHaveStatusDone(epic)) {
            epic.setStatus(Status.DONE);
            return;
        }
        epic.setStatus(Status.IN_PROGRESS);
    }

    private boolean isAllSubtasksOfEpicHaveStatusNew(Epic epic) {
        return epic.subtasksId.stream()
                .allMatch(id -> subtasks.get(id).getStatus().equals(Status.NEW));
    }

    private boolean isAllSubtasksOfEpicHaveStatusDone(Epic epic) {
        return epic.subtasksId.stream()
                .allMatch(id -> subtasks.get(id).getStatus().equals(Status.DONE));
    }

    private void updateEpicStartAndEnd(Epic epic) {
        List<Subtask> subtasksForEpic = subtasks.values().stream()
                .filter(subtask -> epic.subtasksId.contains(subtask.getId()))
                .collect(Collectors.toList());

        if (subtasksForEpic.isEmpty()) {
            return;
        }

        subtasksForEpic.sort(new ComparatorByStartTime());
        epic.setStartTime(subtasksForEpic.getFirst().getStartTime());

        subtasksForEpic.sort(new ComparatorByEndTime());
        epic.setEndTime(subtasksForEpic.getLast().getEndTime());
    }

    private void updateEpicDuration(Epic epic) {
        long sumDuration = epic.subtasksId.stream()
                .mapToLong(id -> subtasks.get(id).getDuration().getSeconds())
                .sum();
        epic.setDuration(Duration.ofSeconds(sumDuration));
    }

    @Override
    public boolean isTasksCrossInTime(Task task1, Task task2) {
        LocalDateTime startTime1 = task1.getStartTime();
        LocalDateTime endTime1 = task1.getEndTime();
        LocalDateTime startTime2 = task2.getStartTime();
        LocalDateTime endTime2 = task2.getEndTime();

        return !endTime1.isBefore(startTime2) && !endTime2.isBefore(startTime1);
    }
}
