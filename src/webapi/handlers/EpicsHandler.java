package webapi.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import manager.TaskManager;
import tasks.Epic;
import webapi.Endpoint;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    public EpicsHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        Endpoint endpoint = getEndpoint(httpExchange.getRequestURI().getPath(), httpExchange.getRequestMethod());

        switch (endpoint) {
            case GET_ALL:
                handleGetEpics(httpExchange);
                break;
            case GET_BY_ID:
                handleGetEpicById(httpExchange);
                break;
            case GET_SUBTASKS:
                handleGetEpicSubtasks(httpExchange);
            case CREATE:
                handleCreateEpic(httpExchange);
                break;
            case UPDATE:
                handleUpdateEpic(httpExchange);
                break;
            case DELETE:
                handleDeleteEpic(httpExchange);
            case UNKNOWN:
                sendNotFound(httpExchange, "Указанный путь не найден. Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    private void handleGetEpics(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, gson.toJson(taskManager.getEpics()), 200);
    }

    private void handleGetEpicById(HttpExchange httpExchange) throws IOException {
        try {
            sendText(httpExchange, gson.toJson(getEpic(httpExchange)), 200);
        } catch (NotFoundException e) {
            sendNotFound(httpExchange, e.getMessage());
        }
    }

    private void handleGetEpicSubtasks(HttpExchange httpExchange) throws IOException {
        try {
            sendText(httpExchange, gson.toJson(taskManager.getSubtasksForEpic(getEpic(httpExchange).getId())), 200);
        } catch (NotFoundException e) {
            sendNotFound(httpExchange, e.getMessage());
        }
    }

    private void handleCreateEpic(HttpExchange httpExchange) throws IOException {
        Epic epic = getEpicFromJson(httpExchange);

        taskManager.createEpic(epic);
        sendText(httpExchange, "Эпик создан с id = " + epic.getId() + ".", 201);
    }

    private void handleUpdateEpic(HttpExchange httpExchange) throws IOException {
        int id = Integer.parseInt(httpExchange.getRequestURI().getPath().split("/")[2]);
        Epic epic = getEpicFromJson(httpExchange);
        taskManager.updateEpic(epic);
        sendText(httpExchange, "Эпик с id = " + id + " обновлен.", 201);
    }

    private void handleDeleteEpic(HttpExchange httpExchange) throws IOException {
        int id = Integer.parseInt(httpExchange.getRequestURI().getPath().split("/")[2]);
        taskManager.removeEpicById(id);
        sendText(httpExchange, "Эпик c id = " + id + " удален.", 200);
    }

    private Epic getEpicFromJson(HttpExchange httpExchange) throws IOException {
        InputStream inputStream = httpExchange.getRequestBody();
        String epicJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(epicJson, Epic.class);
        if (epic.subtasksId == null) {
            epic.setSubtasksId(new ArrayList<>());
        }
        return epic;
    }

    private Epic getEpic(HttpExchange httpExchange) {
        int id = Integer.parseInt(httpExchange.getRequestURI().getPath().split("/")[2]);
        return taskManager.getEpicById(id);
    }

    private static Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals("epics")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_ALL;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.CREATE;
            }
        }
        if (pathParts.length == 3 && pathParts[1].equals("epics")) {
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
        if (pathParts.length == 4 && pathParts[1].equals("epics") && pathParts[3].equals("subtasks")
                && requestMethod.equals("GET")) {
            return Endpoint.GET_SUBTASKS;
        }
        return Endpoint.UNKNOWN;
    }
}
