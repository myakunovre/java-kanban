package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    List<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {

    }

    // Получение списка последних просмотренных 10 задач
    @Override
    public List<Task> getHistory() {
        return history;
    }
}
