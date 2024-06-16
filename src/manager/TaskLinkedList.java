package manager;

import java.util.ArrayList;
import java.util.List;

public class TaskLinkedList<Task> {
    public Node<Task> first;
    public Node<Task> last;
    int size = 0;

    void linkLast(Task task) {
        final Node<Task> oldLast = last;
        final Node<Task> newNode = new Node<>(oldLast, task, null);
        last = newNode;
        if (oldLast == null)
            first = newNode;
        else
            oldLast.next = newNode;
        size++;
    }

    List<Task> getTasks() {
        List<Task> list = new ArrayList<>();

        for (Node<Task> x = first; x != null; x = x.next) {
            list.add(x.item);
        }

        return list;
    }

    void removeNode(Node<Task> node) {
        if (node.prev != null && node.next != null) { // если нода - не голова и не хвост списка
            Node<Task> prevNode = node.prev;
            Node<Task> nextNode = node.next;
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
            size--;
        }
        if (node.prev == null && node.next != null) { // если нода - голова списка
            Node<Task> nextNode = node.next;
            first = nextNode;
            nextNode.prev = null;
            size--;
        }
        if (node.prev != null && node.next == null) { // если нода - хвост списка
            Node<Task> prevNode = node.prev;
            last = prevNode;
            prevNode.next = null;
            size--;
        }
    }
}
