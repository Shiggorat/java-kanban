package controllers;


class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    private static TaskManager taskManager;

    @Override
    protected InMemoryTaskManager getNewTaskManager() {
        return new InMemoryTaskManager();
    }
}