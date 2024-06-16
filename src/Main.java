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
        System.out.println("Дергаем созданные задачи в порядке id 1...5" + System.lineSeparator());
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getEpicById(3);
        taskManager.getSubtaskById(4);
        taskManager.getSubtaskById(5);

        System.out.println("Выводим на экран все созданные задачи и историю просмотров:");
        printAllTasks(taskManager);

        System.out.println("Повторно делаем еще 5 запросов этих же тасок, но уже в обратном порядке...");
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(4);
        taskManager.getEpicById(3);
        taskManager.getTaskById(2);
        taskManager.getTaskById(1);

        System.out.println("Повторно выводим на экран все задачи и историю, обращаем внимание на порядок задач в истории"
                + System.lineSeparator());
        printAllTasks(taskManager);

//        Удаляем задачу по id
        taskManager.removeTaskById(task1.getId());
        System.out.println("Удалили задачу task1" + System.lineSeparator());

//        Удаляем эпик по id
        taskManager.removeEpicById(epic.getId());
        System.out.println("Удалили эпик" + System.lineSeparator());

        System.out.println("После удаления задачи должна остаться одна задача и не должно остаться подзадач и эпика.");
        System.out.println("Выводим на экран все оставшиеся задачи:");
        printAllTasks(taskManager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpics()) {
            System.out.println(epic);

            for (Task task : manager.getSubtasksForEpic(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }
        System.out.println("История:");
        for (Task task : manager.getHistoryManager().getHistory()) {
            System.out.println(task);
        }
        System.out.println();
    }
}
