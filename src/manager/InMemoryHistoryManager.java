package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new ArrayList<>();

    // Добавление задачи в список последних просмотренных 10 задач
    @Override
    public void add(Task task) {
        Task taskCopy = new Task(task.getName(), task.getDescription(), task.getStatus());
        taskCopy.setId(task.getId());
        history.add(taskCopy);
        if (history.size() <= 10) {
            return;
        }
        history.removeFirst();
    }

    // Получение списка последних просмотренных 10 задач
    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
