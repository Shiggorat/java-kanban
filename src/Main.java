import controllers.InMemoryTaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

        Task task1 = new Task("Сходить в магазин", "Купить бананы");
        inMemoryTaskManager.addTask(task1);
        System.out.println(inMemoryTaskManager.getTasks());

        Task task1upd = new Task(task1.getId(), "Поход в магазин", "Еще купить хлеб", Status.IN_PROGRESS);
        inMemoryTaskManager.updateTask(task1upd);
        System.out.println(inMemoryTaskManager.getTasks());


        Epic epic1 = new Epic("Поездка на природу", "Нужно выехать в лес");
        inMemoryTaskManager.addEpic(epic1);
        System.out.println(epic1);

        Subtask subtask1ep1 = new Subtask("Заправить машину", "Заехать на заправку", epic1.getId());
        Subtask subtask2ep1 = new Subtask("Купить еду", "Заехать на рынок за едой", epic1.getId());
        inMemoryTaskManager.addSubtask(subtask1ep1);
        inMemoryTaskManager.addSubtask(subtask2ep1);
        System.out.println(epic1.getSubtasks());

        Epic epic1upd = new Epic(epic1.getId(), "Поездка на природу", "Теперь едем на речку");
        inMemoryTaskManager.updateEpic(epic1upd);
        System.out.println(inMemoryTaskManager.getEpics());
        System.out.println(epic1upd.getSubtasks());

        subtask1ep1.setStatus(Status.DONE);
        inMemoryTaskManager.updateSubtask(subtask1ep1);
        subtask2ep1.setStatus(Status.DONE);
        inMemoryTaskManager.updateSubtask(subtask2ep1);

        System.out.println(inMemoryTaskManager.getEpics());
        System.out.println(epic1upd.getSubtasks());



    }
}
