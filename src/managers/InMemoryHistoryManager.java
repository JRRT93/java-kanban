package managers;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList customLinkedList;
    private final Map<Integer, Node> nodesMap = new HashMap<>();

    public InMemoryHistoryManager() {
        this.customLinkedList = new CustomLinkedList();
    }

    @Override
    public void add(Task task) {
        customLinkedList.linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return customLinkedList.getTasks();
    }

    @Override
    public void remove(int id) {
        if (nodesMap.containsKey(id)) {
            customLinkedList.removeNode(nodesMap.remove(id));
        }
    }

    public class CustomLinkedList {
        private Node tail;
        private Node head;
        private int size;

        public CustomLinkedList() {

        }

        public void linkLast(Task task) {
            int id = task.getIdentifier();
            if (!nodesMap.containsKey(id)) {
                setNodesLinks(task);
            } else {
                removeNode(nodesMap.get(id));
                setNodesLinks(task);
            }
        }

        private void setNodesLinks(Task task) {
            Node oldTail = tail;
            Node newNode = new Node(oldTail, task, null);
            tail = newNode;
            if (oldTail == null) {
                head = tail;
            } else {
                oldTail.setNext(newNode);
            }
            size++;
            nodesMap.put(task.getIdentifier(), tail);
        }

        public void removeNode(Node nodeToCut) {
            if (tail.equals(head)) {
                tail = null;
                head = null;
            } else if (nodeToCut.equals(tail)) {
                tail = nodeToCut.getPrev();
                tail.setNext(null);
            } else if (nodeToCut.equals(head)) {
                head = nodeToCut.getNext();
                head.setPrev(null);
            } else {
                Node nextNode = nodeToCut.getNext();
                Node prevNode = nodeToCut.getPrev();
                nextNode.setPrev(prevNode);
                prevNode.setNext(nextNode);
            }
            size--;
        }

        public List<Task> getTasks() {
            List<Task> history = new ArrayList<>();
            if (tail != null && head != null) {
                Node prevNode = tail.getPrev();
                history.add(tail.getData());
                for (int i = 1; i < size; i++) {
                    history.add(prevNode.getData());
                    prevNode = prevNode.getPrev();
                }
                Collections.reverse(history);
                return history;
            }
            return history;
        }

        public int size() {
            return  this.size;
        }
    }
}