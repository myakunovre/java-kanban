package manager;

import java.util.ArrayList;
import java.util.List;

public class TaskLinkedList<T> {
    public Node<T> first;
    public Node<T> last;

    void linkLast(T task) {
        final Node<T> oldLast = last;
        final Node<T> newNode = new Node<>(oldLast, task, null);
        last = newNode;
        if (oldLast == null) {
            first = newNode;
        } else {
            oldLast.next = newNode;
        }
    }

    public List<T> getTasks() {
        List<T> list = new ArrayList<>();

        for (Node<T> x = first; x != null; x = x.next) {
            list.add(x.item);
        }

        return list;
    }

    void removeNode(Node<T> node) {
        if (node.prev != null && node.next != null) {
            Node<T> prevNode = node.prev;
            Node<T> nextNode = node.next;
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
        }
        if (node.prev == null && node.next != null) {
            Node<T> nextNode = node.next;
            first = nextNode;
            nextNode.prev = null;
        }
        if (node.prev != null && node.next == null) {
            Node<T> prevNode = node.prev;
            last = prevNode;
            prevNode.next = null;
        }
    }
}
