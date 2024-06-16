package manager;

import java.util.ArrayList;
import java.util.List;

public class TaskLinkedList<T> {
    public Node<T> first;
    public Node<T> last;
    int size = 0;

    void linkLast(T task) {
        final Node<T> oldLast = last;
        final Node<T> newNode = new Node<>(oldLast, task, null);
        last = newNode;
        if (oldLast == null)
            first = newNode;
        else
            oldLast.next = newNode;
        size++;
    }

    List<T> getTasks() {
        List<T> list = new ArrayList<>();

        for (Node<T> x = first; x != null; x = x.next) {
            list.add(x.item);
        }

        return list;
    }

    void removeNode(Node<T> node) {
        if (node.prev != null && node.next != null) { // если нода - не голова и не хвост списка
            Node<T> prevNode = node.prev;
            Node<T> nextNode = node.next;
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
            size--;
        }
        if (node.prev == null && node.next != null) { // если нода - голова списка
            Node<T> nextNode = node.next;
            first = nextNode;
            nextNode.prev = null;
            size--;
        }
        if (node.prev != null && node.next == null) { // если нода - хвост списка
            Node<T> prevNode = node.prev;
            last = prevNode;
            prevNode.next = null;
            size--;
        }
    }
}
