package manager;

public class Node<T> {
    public T item;
    public Node<T> next;
    public Node<T> prev;

    public Node(Node<T> prev, T item, Node<T> next) {
        this.item = item;
        this.next = next;
        this.prev = prev;
    }
}
