import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали! Создаем две задачи, эпик и две подзадачи эпика, делаем 5 запросов по ID..."
                + System.lineSeparator());

        TaskManager taskManager = Managers.getDefault();

//        Создаем две задачи
        Task task1 = new Task("Завтрак", "Приготовить завтрак дома", Status.NEW);
        taskManager.createTask(task1);
        Task task2 = new Task("Обед", "Сходить в ресторан", Status.NEW);
        taskManager.createTask(task2);

//        Создаем эпик
        Epic epic = new Epic("Ремонт", "Сделать ремонт в гостиной", Status.NEW);
        taskManager.createEpic(epic);

//        Создаем две подзадачи эпика
        Subtask subtask1 = new Subtask("Проект", "Сделать проект", Status.NEW, epic.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Материалы", "Закупить материалы", Status.NEW, epic.getId());
        taskManager.createSubtask(subtask2);

//        Тестируем новый функционал метода getHistory():
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getEpicById(3);
        taskManager.getSubtaskById(4);
        taskManager.getSubtaskById(5);

        System.out.println("Выводим на экран все созданные задачи и историю просмотров:");
        printAllTasks(taskManager);

//        Обновление статуса task1
        Task task3 = new Task("Завтрак", "Приготовить завтрак дома", Status.DONE);
        task3.setId(task1.getId());
        taskManager.updateTask(task3);
        System.out.println("Обновили статус task1 на DONE");

//        Обновление статуса task2
        Task task4 = new Task("Обед", "Сходить в ресторан", Status.IN_PROGRESS);
        task4.setId(task2.getId());
        taskManager.updateTask(task4);
        System.out.println("Обновили статус task2 на IN_PROGRESS");

//        Обновление статуса subtask1
        Subtask subtask3 = new Subtask("Проект", "Сделать проект", Status.DONE, epic.getId());
        subtask3.setId(subtask1.getId());
        taskManager.updateSubtask(subtask3);
        System.out.println("Обновили статус subtask1 на DONE");

//        Обновление статуса subtask2
        Subtask subtask4 = new Subtask("Материалы", "Закупить материалы", Status.DONE, epic.getId());
        subtask4.setId(subtask2.getId());
        taskManager.updateSubtask(subtask4);
        System.out.println("Обновили статус subtask2 на DONE" + System.lineSeparator());

        System.out.println("Делаем еще 5 запросов...");
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getEpicById(3);
        taskManager.getSubtaskById(4);
        taskManager.getSubtaskById(5);

        System.out.println("Выводим на экран все задачи и историю, обращаем внимание на статусы повторно вызванных " +
                "задач в истории:");
        printAllTasks(taskManager);

        System.out.println("Делаем еще 1 запрос, чтобы проверить максимальный размер списка задач в истории...");
        taskManager.getTaskById(1);

        System.out.println("Выводим на экран все задачи:");
        printAllTasks(taskManager);

//        Удаляем задачу по id
        taskManager.removeTaskById(task1.getId());
        System.out.println("Удалили задачу task1");

//        Удаляем эпик по id
        taskManager.removeEpicById(epic.getId());
        System.out.println("Удалили эпик");

        System.out.println("После удаления задачи должна остаться одна задача и не должно остаться подзадач и эпика.");
        System.out.println("Выводим на экран все оставшиеся задачи:");
        printAllTasks(taskManager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println();
        System.out.println("Эпики:");
        for (Task epic : manager.getEpics()) {
            System.out.println(epic);

            for (Task task : manager.getSubtasksForEpic(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println();
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }
        System.out.println();
        System.out.println("История:");
        for (Task task : manager.getHistoryManager().getHistory()) {
            System.out.println(task);
        }
        System.out.println();
    }
}
