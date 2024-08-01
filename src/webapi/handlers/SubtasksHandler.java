package webapi.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import manager.TaskManager;
import tasks.Subtask;
import webapi.Endpoint;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    public SubtasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Endpoint endpoint = getEndpoint(httpExchange.getRequestURI().getPath(), httpExchange.getRequestMethod());

        switch (endpoint) {
            case GET_ALL:
                handleGetSubtasks(httpExchange);
                break;
            case GET_BY_ID:
                handleGetSubtaskById(httpExchange);
                break;
            case CREATE:
                handleCreateSubtask(httpExchange);
                break;
            case UPDATE:
                handleUpdateSubtask(httpExchange);
                break;
            case DELETE:
                handleDeleteSubtask(httpExchange);
            case UNKNOWN:
                sendNotFound(httpExchange, "Указанный путь не найден. Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    private void handleGetSubtasks(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, gson.toJson(taskManager.getSubtasks()), 200);
    }

    private void handleGetSubtaskById(HttpExchange httpExchange) throws IOException {
        int id = Integer.parseInt(httpExchange.getRequestURI().getPath().split("/")[2]);
        try {
            sendText(httpExchange, gson.toJson(taskManager.getSubtaskById(id)), 200);
        } catch (NotFoundException e) {
            sendNotFound(httpExchange, e.getMessage());
        }
    }

    private void handleCreateSubtask(HttpExchange httpExchange) throws IOException {
        Subtask subtask = getSubtaskFromJson(httpExchange);
        if (!taskManager.isNotCrossInTime(subtask)) {
            sendHasInteractions(httpExchange, "Подзадача пересекается с существующими задачами, создание не возможно!");
            return;
        }
        taskManager.createSubtask(subtask);
        sendText(httpExchange, "Подзадача создана с id = " + subtask.getId() + ".", 201);
    }

    private void handleUpdateSubtask(HttpExchange httpExchange) throws IOException {
        int id = Integer.parseInt(httpExchange.getRequestURI().getPath().split("/")[2]);
        Subtask subtask = getSubtaskFromJson(httpExchange);
        if (!taskManager.isNotCrossInTime(subtask)) {
            sendHasInteractions(httpExchange, "Подзадача с id = " + id + " пересекается с существующими " +
                    "подзадачами, обновление не возможно!");
            return;
        }
        taskManager.updateSubtask(subtask);
        sendText(httpExchange, "Подзадача с id = " + id + " обновлена.", 201);
    }

    private void handleDeleteSubtask(HttpExchange httpExchange) throws IOException {
        int id = Integer.parseInt(httpExchange.getRequestURI().getPath().split("/")[2]);
        taskManager.removeSubtaskById(id);
        sendText(httpExchange, "Подзадача c id = " + id + " удалена.", 200);
    }

    private Subtask getSubtaskFromJson(HttpExchange httpExchange) throws IOException {
        InputStream inputStream = httpExchange.getRequestBody();
        String subtaskJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        return gson.fromJson(subtaskJson, Subtask.class);
    }

    private static Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals("subtasks")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_ALL;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.CREATE;
            }
        }
        if (pathParts.length == 3 && pathParts[1].equals("subtasks")) {
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