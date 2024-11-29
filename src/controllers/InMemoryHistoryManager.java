package controllers;

import model.Task;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


public class InMemoryHistoryManager implements HistoryManager {


    private Node<Task> head;
    private Node<Task> tail;
    private final Map<Integer, Node<Task>> history = new HashMap<>();

    public void add(Task task) {
        linkLast(task);
    }

    private void linkLast(Task task) {
        Node<Task> node = new Node<>(task);
        if (history.containsKey(task.getId())) {
            removeNode(history.get(task.getId()));
        }
        if (head == null) {
            tail = node;
            head = node;
        } else {
            node.setPrev(tail);
            tail.setNext(node);
            tail = node;
        }
        history.put(task.getId(), node);

    }

    private void removeNode(Node<Task> node) {
        if (node != null) {
            history.remove(node.getTask().getId());
            Node<Task> prev = node.getPrev();
            Node<Task> next = node.getNext();
            if (head == node) {
                head = node.getNext();
            }
            if (tail == node) {
                tail = node.getPrev();
            }
            if (prev != null) {
                prev.setNext(next);
            }
            if (next != null) {
                next.setPrev(prev);
            }
        }
    }

    public void remove(int id) {
        removeNode(history.get(id));
    }

    public List<Task> getTasks() {
        List<Task> list = new ArrayList<>();
        Node<Task> node = head;
        while (node != null) {
            list.add(node.getTask());
            node = node.getNext();
        }
        return list;
    }

    public List<Task> getHistory() {
        return getTasks();
    }

    class Node<Task> {
        private Task task;
        private Node<Task> prev;
        private Node<Task> next;

        public Node(Task task) {
            this.task = task;
            this.prev = null;
            this.next = null;
        }

        public Task getTask() {
            return this.task;
        }

        public Node<Task> getPrev() {
            return this.prev;
        }

        public Node<Task> getNext() {
            return this.next;
        }

        public void setTask(Task task) {
            this.task = task;
        }

        public void setPrev(Node<Task> prev) {
            this.prev = prev;
        }

        public void setNext(Node<Task> next) {
            this.next = next;
        }

    }
}
