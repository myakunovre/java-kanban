package manager;

public class Node<Task> {
    public Task item;
    public Node<Task> next;
    public Node<Task> prev;

    public Node(Node<Task> prev, Task item, Node<Task> next) {
        this.item = item;
        this.next = next;
        this.prev = prev;
    }
}
