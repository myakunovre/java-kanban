public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

//        Создаем две задачи
        Task task1 = new Task("Завтрак", "Приготовить завтрак дома", Status.NEW);
        TaskManager.createTask(task1);
        Task task2 = new Task("Обед", "Сходить в ресторан", Status.NEW);
        TaskManager.createTask(task2);

//        Создаем эпик
        Epic epic = new Epic("Ремонт", "Сделать ремонт в гостиной", Status.NEW);
        TaskManager.createEpic(epic);

//        Создаем две подзадачи эпика
        Subtask subtask1 = new Subtask("Проект", "Сделать проект", Status.NEW, epic.getId());
        TaskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Материалы", "Закупить материалы", Status.NEW, epic.getId());
        TaskManager.createSubtask(subtask2);

//        Выводим на экран созданные задачи:
        System.out.println("Созданы две задачи класса Task:");
        System.out.println(TaskManager.tasks.values() + System.lineSeparator());
        System.out.println("Создан эпик класса Epic:");
        System.out.println(TaskManager.epics.values() + System.lineSeparator());
        System.out.println("Созданы две подзадачи класса Subtask:");
        System.out.println(TaskManager.subtasks.values() + System.lineSeparator());

//        Обновление статуса task1
        Task task3 = new Task("Завтрак", "Приготовить завтрак дома", Status.DONE);
        task3.setId(task1.getId());
        TaskManager.updateTask(task3);

//        Обновление статуса task2
        Task task4 = new Task("Обед", "Сходить в ресторан", Status.IN_PROGRESS);
        task4.setId(task2.getId());
        TaskManager.updateTask(task4);

//        Обновление статуса subtask1
        Subtask subtask3 = new Subtask("Проект", "Сделать проект", Status.DONE, epic.getId());
        subtask3.setId(subtask1.getId());
        TaskManager.updateSubtask(subtask3);

//        Обновление статуса subtask2
        Subtask subtask4 = new Subtask("Материалы", "Закупить материалы", Status.DONE, epic.getId());
        subtask4.setId(subtask2.getId());
        TaskManager.updateSubtask(subtask4);

//        Выводим на экран обновленные задачи:
        System.out.println("Обновляем статусы созданных задач и подзадач через обновление " + System.lineSeparator());
        System.out.println("Проверяем статусы задач после обновления:");
        System.out.println(TaskManager.tasks.values() + System.lineSeparator());
        System.out.println("Проверяем статусы подзадач после обновления:");
        System.out.println(TaskManager.subtasks.values() + System.lineSeparator());
        System.out.println("Проверяем статус эпика после обновления подзадач:");
        System.out.println(TaskManager.epics.values() + System.lineSeparator());

//        Удаляем задачу по id
        TaskManager.removeTaskById(task1.getId());

//        Удаляем эпик по id
        TaskManager.removeEpicById(epic.getId());

//        Выводим на экран задачи и эпик после удаления:
        System.out.println("После удаления задачи должна остаться одна задача. Проверяем:");
        System.out.println("Содержимое мапы tasks:");
        System.out.println(TaskManager.tasks.values() + System.lineSeparator());
        System.out.println("После удаления эпика не должно остаться подзадач и эпика. Проверяем:");
        System.out.println("Содержимое мапы subtasks:");
        System.out.println(TaskManager.subtasks.values() + System.lineSeparator());
        System.out.println("Содержимое мапы epics:");
        System.out.println(TaskManager.epics.values() + System.lineSeparator());
    }

}
