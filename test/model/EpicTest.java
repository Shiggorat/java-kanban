package model;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    public void epicsWitTheSameIdShouldBeEqual() {
        Epic epic1 = new Epic(2, "Поездка на природу", "Теперь едем на речку");
        Epic epic2 = new Epic(2, "Поймать кукуху", "Теперь едем с кукухой в магазин");
        epic2.setStatus(Status.IN_PROGRESS);
        assertEquals(epic1, epic2, "Ошибка! Эпики должны быть равны, если их айди равны");
    }

}