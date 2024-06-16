package manager;

import tasks.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    Map<Integer, Node<Task>> history = new HashMap<>();
    TaskLinkedList<Task> taskLinkedList = new TaskLinkedList<>();

    // Добавление задачи в список истории
    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        int id = task.getId();
        if (history.containsKey(id)) {
            taskLinkedList.removeNode(history.get(id));
        }
        taskLinkedList.linkLast(task);
        history.put(id, taskLinkedList.last);
    }

    // Получение списка истории просмотров
    @Override
    public List<Task> getHistory() {
        return taskLinkedList.getTasks();
    }

    // Удаление задачи из истории
    @Override
    public void remove(int id) {
        if (!history.containsKey(id)) {
            return;
        }
        taskLinkedList.removeNode(history.get(id));
        history.remove(id);
    }
}

