package manager;

import tasks.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    Map<Integer, Node<Task>> history = new HashMap<>();
    TaskLinkedList<Task> taskLinkedList = new TaskLinkedList<>();

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

    @Override
    public List<Task> getHistory() {
        return taskLinkedList.getTasks();
    }

    @Override
    public void remove(int id) {
        if (!history.containsKey(id)) {
            return;
        }
        taskLinkedList.removeNode(history.get(id));
        history.remove(id);
    }
}

