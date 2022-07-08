package managers;

import tasks.Task;

public class Node {
    private Node next;
    private Node prev;
    private Task task;

    public Node (Node prev, Task task, Node next) {
        this.prev = prev;
        this.task = task;
        this.next = next;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public Task getData() {
        return task;
    }

    public void setData(Task task) {
        this.task = task;
    }
}
