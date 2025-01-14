package controllers;

import model.Epic;
import model.Subtask;
import model.Task;

import org.junit.jupiter.api.Test;

import java.io.File;


import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    private static FileBackedTaskManager manager ;
    private static File file;

    @Override
    protected FileBackedTaskManager getNewTaskManager() {
        try {
            file = File.createTempFile("file", ".csv");
            manager = new FileBackedTaskManager(file);
        } catch (Exception e) {
            System.out.println("No file created");
        }
        return new FileBackedTaskManager(file);
    }

    @Test
    void shouldSaveLoadEmptyFile() {
        manager.save();
        manager = FileBackedTaskManager.loadFromFile(file);
        assertTrue(manager.taskList.isEmpty());
        assertTrue(manager.epicList.isEmpty());
        assertTrue(manager.subtaskList.isEmpty());
    }

    @Test
    void shouldSaveLoad() {
        Task task1 = createTask();
        manager.addTask(task1);
        Epic epic1 = createEpic();
        manager.addEpic(epic1);
        Subtask subtask1 = createSubtask1(epic1);
        manager.addSubtask(subtask1);
        Subtask subtask2 = createSubtask1(epic1);
        manager.addSubtask(subtask2);

        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(file);
        assertEquals(manager.getTasks(), manager2.getTasks());
        assertEquals(manager.getEpics(), manager2.getEpics());
        assertEquals(manager.getSubtasks(), manager2.getSubtasks());
    }

}