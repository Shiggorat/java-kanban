package controllers;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    private static FileBackedTaskManager manager;
    private static File file;


    @BeforeEach
    void tempFileCreation() {
        try {
            file = File.createTempFile("file", ".csv");
            manager = new FileBackedTaskManager(file);
        } catch (Exception e) {
            System.out.println("No file created");
        }
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
        Task task1 = new Task("TASK1", "TaskDescr1");
        manager.addTask(task1);
        Epic epic1 = new Epic("EPIC1", "EpicDescr1");
        manager.addEpic(epic1);
        Subtask subtask1 = new Subtask("subtask1", "SubtaskDescr1", epic1.getId());
        manager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "SubtaskDescr2", epic1.getId());
        manager.addSubtask(subtask2);

        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(file);
        assertEquals(manager.getTasks(), manager2.getTasks());
        assertEquals(manager.getEpics(), manager2.getEpics());
        assertEquals(manager.getSubtasks(), manager2.getSubtasks());
    }
}