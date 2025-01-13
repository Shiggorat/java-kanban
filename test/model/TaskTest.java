package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    public void epicsWitTheSameIdShouldBeEqual() {
        Task task1 = new Task(2, "Поездка на вокзал", "Теперь едем на вокзал", Status.IN_PROGRESS);
        Task task2 = new Task(2, "Поймать белочку", "Теперь едем с белочкой в лес", Status.NEW);
        assertEquals(task1, task2, "Ошибка! Таски должны быть равны, если их айди равны");
    }


}