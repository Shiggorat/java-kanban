package controllers;

import model.Task;
import java.util.List;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int MAX_HISTORY_MEMORY = 10;
    private final List<Task> history = new ArrayList<>();

    public void add(Task task) {
        if (history.size() == MAX_HISTORY_MEMORY) {
            history.removeFirst();
        }
        history.add(task);
    }

    public List<Task> getHistory() {
        return history;
    }
}
