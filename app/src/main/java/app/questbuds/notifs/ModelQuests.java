package app.questbuds.notifs;

import java.util.ArrayList;

public class ModelQuests {
    String taskId, text;
    int hour, min;
    boolean done;
    ArrayList<String> daysWeek = new ArrayList<>();

    public ModelQuests() {
    }

    public ModelQuests(String taskId, String text, int hour, int min, boolean done, ArrayList<String> daysWeek) {
        this.taskId = taskId;
        this.text = text;
        this.hour = hour;
        this.min = min;
        this.done = done;
        this.daysWeek = daysWeek;
    }

    @Override
    public String toString() {
        return "ModelTasks{" +
                "taskId='" + taskId + '\'' +
                ", text='" + text + '\'' +
                ", hour=" + hour +
                ", min=" + min +
                ", done=" + done +
                ", daysWeek=" + daysWeek +
                '}';
    }
}
