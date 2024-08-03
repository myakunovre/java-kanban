package manager;

import exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("resources", "save.csv");
        if (!Files.exists(path)) {
            Files.createFile(path);
        }

        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(path.toFile());

        Task task1 = new Task("Task1", "description Task1", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        fileBackedTaskManager.createTask(task1);
        Task task2 = new Task("Task1", "description Task2", Status.NEW, LocalDateTime.now().plusMinutes(5), Duration.ofMinutes(5));
        fileBackedTaskManager.createTask(task2);

        Epic epic = new Epic("Epic", "description Epic", Status.NEW);
        fileBackedTaskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask1", "description Subtask1", Status.NEW, epic.getId(), LocalDateTime.now().plusMinutes(10), Duration.ofMinutes(5));
        fileBackedTaskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask2", "description Subtask2", Status.NEW, epic.getId(), LocalDateTime.now().plusMinutes(15), Duration.ofMinutes(5));
        fileBackedTaskManager.createSubtask(subtask2);

        FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(path.toFile());

        System.out.println("Равны ли таски исходного и восстановленного менеджеров?");
        System.out.println("Ответ: " + fileBackedTaskManager.tasks.equals(fileBackedTaskManager1.tasks) + System.lineSeparator());
        System.out.println("Равны ли эпики исходного и восстановленного менеджеров?");
        System.out.println("Ответ: " + fileBackedTaskManager.epics.equals(fileBackedTaskManager1.epics) + System.lineSeparator());
        System.out.println("Равны ли сабтаски исходного и восстановленного менеджеров? ");
        System.out.println("Ответ: " + fileBackedTaskManager.subtasks.equals(fileBackedTaskManager1.subtasks) + System.lineSeparator());
    }

    private final Path path;
    private static final String TASK = "TASK";
    private static final String EPIC = "EPIC";
    private static final String SUBTASK = "SUBTASK";
    private static final String delimiter = ",";
    private static final String COUNT = "count";

    public FileBackedTaskManager(Path path) {
        this.path = path;
    }

    void save() {
        try (Writer fileWriter = new FileWriter(String.valueOf(path), false)) {
            if (!tasks.isEmpty() || !epics.isEmpty() || !subtasks.isEmpty()) {
                fileWriter.write("id,type,name,status,description,epic,startTime,duration\n");
                for (Task task : tasks.values()) {
                    fileWriter.write(taskToString(task) + "\n");
                }
                for (Epic epic : epics.values()) {
                    fileWriter.write(epicToString(epic) + "\n");
                }
                for (Subtask subtask : subtasks.values()) {
                    fileWriter.write(subtaskToString(subtask) + "\n");
                }
                fileWriter.write(count + delimiter + COUNT);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время записи файла.");
        }
    }

    static FileBackedTaskManager loadFromFile(File file) throws IOException {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file.toPath());
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {

            while (fileReader.ready()) {
                String string = fileReader.readLine();
                String[] split = string.split(delimiter);

                switch (split[1]) {
                    case TASK -> {
                        Task task = fileBackedTaskManager.fromString(string);
                        if (task != null) {
                            fileBackedTaskManager.tasks.put(task.getId(), task);
                            if (task.getStartTime() != null) {
                                fileBackedTaskManager.sortedTasks.add(task);
                            }
                        }
                    }
                    case EPIC -> {
                        Epic epic = (Epic) fileBackedTaskManager.fromString(string);
                        if (epic != null) {
                            fileBackedTaskManager.epics.put(epic.getId(), epic);
                        }
                    }
                    case SUBTASK -> {
                        Subtask subtask = (Subtask) fileBackedTaskManager.fromString(string);
                        if (subtask != null) {
                            fileBackedTaskManager.subtasks.put(subtask.getId(), subtask);
                            fileBackedTaskManager.epics.get(subtask.getEpicId()).subtasksId.add(subtask.getId());
                            if (subtask.getStartTime() != null) {
                                fileBackedTaskManager.sortedTasks.add(subtask);
                            }
                        }
                    }
                    case COUNT -> {
                        count = Integer.parseInt(split[0]);
                    }
                }
            }
        }
        return fileBackedTaskManager;
    }

    private String taskToString(Task task) {
        return task.getId() + delimiter + TASK + delimiter +
                task.getName() + delimiter +
                task.getStatus() + delimiter +
                task.getDescription() + delimiter +
                task.getStartTime() + delimiter +
                task.getDuration().toMinutes();
    }

    private String epicToString(Epic epic) {
        long duration;
        if (epic.getDuration() == null) {
            duration = 0;
        } else {
            duration = epic.getDuration().toMinutes();
        }
        return epic.getId() + delimiter + EPIC + delimiter +
                epic.getName() + delimiter +
                epic.getStatus() + delimiter +
                epic.getDescription() + delimiter +
                epic.getStartTime() + delimiter +
                epic.getEndTime() + delimiter +
                duration;
    }

    private String subtaskToString(Subtask subtask) {
        return subtask.getId() + delimiter + SUBTASK + delimiter +
                subtask.getName() + delimiter +
                subtask.getStatus() + delimiter +
                subtask.getDescription() + delimiter +
                subtask.getEpicId() + delimiter +
                subtask.getStartTime() + delimiter +
                subtask.getDuration().toMinutes();
    }

    private Task fromString(String value) {
        String[] split = value.split(delimiter);
        switch (split[1]) {
            case TASK -> {
                int id = Integer.parseInt(split[0]);
                String name = split[2];
                Status status = convertStringToStatus(split[3]);
                String description = split[4];
                LocalDateTime startTime = LocalDateTime.parse(split[5]);
                Duration duration = Duration.ofMinutes(Long.parseLong(split[6]));

                Task task = new Task(name, description, status, startTime, duration);
                task.setId(id);
                return task;
            }
            case EPIC -> {
                int id = Integer.parseInt(split[0]);
                String name = split[2];
                Status status = convertStringToStatus(split[3]);
                String description = split[4];
                LocalDateTime startTime = LocalDateTime.parse(split[5]);
                LocalDateTime endTime = LocalDateTime.parse(split[6]);
                Duration duration = Duration.ofMinutes(Long.parseLong(split[7]));

                Epic epic = new Epic(name, description, status);
                epic.setId(id);
                epic.setStartTime(startTime);
                epic.setEndTime(endTime);
                epic.setDuration(duration);
                return epic;
            }
            case SUBTASK -> {
                int id = Integer.parseInt(split[0]);
                String name = split[2];
                Status status = convertStringToStatus(split[3]);
                String description = split[4];
                int epicId = Integer.parseInt(split[5]);
                LocalDateTime startTime = LocalDateTime.parse(split[6]);
                Duration duration = Duration.ofMinutes(Long.parseLong(split[7]));

                Subtask subtask = new Subtask(name, description, status, epicId, startTime, duration);
                subtask.setId(id);
                return subtask;
            }
            default -> {
                return null;
            }
        }
    }



    @Override
    public void removeTasks() {
        super.removeTasks();
        save();
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
        save();
    }

    @Override
    public void removeSubtasks() {
        super.removeSubtasks();
        save();
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }
}
