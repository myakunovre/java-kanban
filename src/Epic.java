import java.util.ArrayList;

public class Epic extends Task {
    ArrayList<Integer> subtasksId;

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        this.subtasksId = new ArrayList<>();
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                ", subtasksId=" + subtasksId.toString() +
                '}';
    }

}
