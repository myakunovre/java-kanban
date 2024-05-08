import manager.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!" + System.lineSeparator());

        TaskManager taskManager = new TaskManager();

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

//        Выводим на экран созданные задачи:
        System.out.println("Созданы две задачи класса tasks.Task:");
        System.out.println(taskManager.tasks.values() + System.lineSeparator());
        System.out.println("Создан эпик класса tasks.Epic:");
        System.out.println(taskManager.epics.values() + System.lineSeparator());
        System.out.println("Созданы две подзадачи класса tasks.Subtask:");
        System.out.println(taskManager.subtasks.values() + System.lineSeparator());

//        Обновление статуса task1
        Task task3 = new Task("Завтрак", "Приготовить завтрак дома", Status.DONE);
        task3.setId(task1.getId());
        taskManager.updateTask(task3);

//        Обновление статуса task2
        Task task4 = new Task("Обед", "Сходить в ресторан", Status.IN_PROGRESS);
        task4.setId(task2.getId());
        taskManager.updateTask(task4);

//        Обновление статуса subtask1
        Subtask subtask3 = new Subtask("Проект", "Сделать проект", Status.DONE, epic.getId());
        subtask3.setId(subtask1.getId());
        taskManager.updateSubtask(subtask3);

//        Обновление статуса subtask2
        Subtask subtask4 = new Subtask("Материалы", "Закупить материалы", Status.DONE, epic.getId());
        subtask4.setId(subtask2.getId());
        taskManager.updateSubtask(subtask4);

//        Выводим на экран обновленные задачи:
        System.out.println("Обновлены статусы созданных задач и подзадач" + System.lineSeparator());
        System.out.println("Проверяем статусы задач после обновления:");
        System.out.println(taskManager.tasks.values() + System.lineSeparator());
        System.out.println("Проверяем статусы подзадач после обновления:");
        System.out.println(taskManager.subtasks.values() + System.lineSeparator());
        System.out.println("Проверяем статус эпика после обновления подзадач:");
        System.out.println(taskManager.epics.values() + System.lineSeparator());

//        Удаляем задачу по id
        taskManager.removeTaskById(task1.getId());

//        Удаляем эпик по id
        taskManager.removeEpicById(epic.getId());

//        Выводим на экран задачи и эпик после удаления:
        System.out.println("После удаления задачи должна остаться одна задача. Проверяем:");
        System.out.println("Содержимое мапы tasks:");
        System.out.println(taskManager.tasks.values() + System.lineSeparator());
        System.out.println("После удаления эпика не должно остаться подзадач и эпика. Проверяем:");
        System.out.println("Содержимое мапы subtasks:");
        System.out.println(taskManager.subtasks.values());
        System.out.println("Содержимое мапы epics:");
        System.out.println(taskManager.epics.values());
    }

}
