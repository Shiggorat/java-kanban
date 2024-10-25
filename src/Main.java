import controllers.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Сходить в магазин", "Купить бананы");
        taskManager.addTask(task1);
        System.out.println(taskManager.getTasks());

        Task task1upd = new Task(task1.getId(), "Поход в магазин", "Еще купить хлеб", Status.IN_PROGRESS);
        taskManager.updateTask(task1upd);
        System.out.println(taskManager.getTasks());


        Epic epic1 = new Epic("Поездка на природу", "Нужно выехать в лес");
        taskManager.addEpic(epic1);
        System.out.println(epic1);

        Subtask subtask1ep1 = new Subtask("Заправить машину", "Заехать на заправку", epic1.getId());
        Subtask subtask2ep1 = new Subtask("Купить еду", "Заехать на рынок за едой", epic1.getId());
        taskManager.addSubtask(subtask1ep1);
        taskManager.addSubtask(subtask2ep1);
        System.out.println(epic1.getSubtasks());

        Epic epic1upd = new Epic(epic1.getId(), "Поездка на природу", "Теперь едем на речку");
        taskManager.updateEpic(epic1upd);
        System.out.println(taskManager.getEpics());
        System.out.println(epic1upd.getSubtasks());

        subtask1ep1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1ep1);
        subtask2ep1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask2ep1);

        System.out.println(taskManager.getEpics());
        System.out.println(epic1upd.getSubtasks());



    }
}
