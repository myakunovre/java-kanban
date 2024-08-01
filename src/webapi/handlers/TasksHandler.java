package webapi.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import manager.TaskManager;
import tasks.Task;
import webapi.Endpoint;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {

    public TasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Endpoint endpoint = getEndpoint(httpExchange.getRequestURI().getPath(), httpExchange.getRequestMethod());

        switch (endpoint) {
            case GET_ALL:
                handleGetTasks(httpExchange);
                break;
            case GET_BY_ID:
                handleGetTaskById(httpExchange);
                break;
            case CREATE:
                handleCreateTask(httpExchange);
                break;
            case UPDATE:
                handleUpdateTask(httpExchange);
                break;
            case DELETE:
                handleDeleteTask(httpExchange);
            case UNKNOWN:
                sendNotFound(httpExchange, "Указанный путь не найден. Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    private void handleGetTasks(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, gson.toJson(taskManager.getTasks()), 200);
    }

    private void handleGetTaskById(HttpExchange httpExchange) throws IOException {
        int id = Integer.parseInt(httpExchange.getRequestURI().getPath().split("/")[2]);
        try {
            sendText(httpExchange, gson.toJson(taskManager.getTaskById(id)), 200);
        } catch (NotFoundException e) {
            sendNotFound(httpExchange, e.getMessage());
        }
    }

    private void handleCreateTask(HttpExchange httpExchange) throws IOException {
        Task task = getTaskFromJson(httpExchange);
        if (!taskManager.isNotCrossInTime(task)) {
            sendHasInteractions(httpExchange, "Задача пересекается с существующими задачами, создание не возможно!");
            return;
        }
        taskManager.createTask(task);
        sendText(httpExchange, "Задача создана с id = " + task.getId() + ".", 201);
    }

    private void handleUpdateTask(HttpExchange httpExchange) throws IOException {
        int id = Integer.parseInt(httpExchange.getRequestURI().getPath().split("/")[2]);
        Task task = getTaskFromJson(httpExchange);
        if (!taskManager.isNotCrossInTime(task)) {
            sendHasInteractions(httpExchange, "Задача с id = " + id + " пересекается с существующими " +
                    "задачами, обновление не возможно!");
            return;
        }
        taskManager.updateTask(task);
        sendText(httpExchange, "Задача с id = " + id + " обновлена.", 201);
    }

    private void handleDeleteTask(HttpExchange httpExchange) throws IOException {
        int id = Integer.parseInt(httpExchange.getRequestURI().getPath().split("/")[2]);
        taskManager.removeTaskById(id);
        sendText(httpExchange, "Задача c id = " + id + " удалена.", 200);
    }

    private Task getTaskFromJson(HttpExchange httpExchange) throws IOException {
        InputStream inputStream = httpExchange.getRequestBody();
        String taskJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        return gson.fromJson(taskJson, Task.class);
    }

    private static Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals("tasks")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_ALL;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.CREATE;
            }
        }
        if (pathParts.length == 3 && pathParts[1].equals("tasks")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_BY_ID;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.UPDATE;
            }
            if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE;
            }
        }
        return Endpoint.UNKNOWN;
    }
}