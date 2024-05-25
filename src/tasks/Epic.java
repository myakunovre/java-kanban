package tasks;

import java.util.ArrayList;

public class Epic extends Task {

    public final ArrayList<Integer> subtasksId;

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        this.subtasksId = new ArrayList<>();
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "tasks.Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                ", subtasksId=" + subtasksId.toString() +
                '}';
    }
}
