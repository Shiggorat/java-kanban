package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    public void epicsWitTheSameIdShouldBeEqual() {
        Subtask subtask1 = new Subtask(2, "Поездка в кино", "Теперь едем в кино", Status.IN_PROGRESS, 1);
        Subtask subtask2 = new Subtask(2, "Поймать котика", "Теперь едем с котиком к ветеринару", Status.NEW, 3);
        assertEquals(subtask1, subtask2, "Ошибка! Сабтаски должны быть равны, если их айди равны");
    }

}