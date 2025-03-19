package tasks.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.apache.log4j.Logger;
import tasks.model.Task;

public class TaskInfoController {
    private static final Logger log = Logger.getLogger(TaskInfoController.class.getName());

    @FXML
    private Label labelTitle;
    @FXML
    private Label labelStart;
    @FXML
    private Label labelEnd;
    @FXML
    private Label labelInterval;
    @FXML
    private Label labelIsActive;

    private Task currentTask;

    public void setTask(Task task) {
        this.currentTask = task;
        updateTaskDetails();
    }

    private void updateTaskDetails() {
        if (currentTask == null) {
            log.error("No task selected for info display");
            return;
        }
        labelTitle.setText("Title: " + currentTask.getTitle());
        labelStart.setText("Start time: " + currentTask.getFormattedDateStart());
        labelEnd.setText("End time: " + currentTask.getFormattedDateEnd());
        labelInterval.setText("Interval: " + currentTask.getFormattedRepeated());
        labelIsActive.setText("Is active: " + (currentTask.isActive() ? "Yes" : "No"));
    }

    @FXML
    public void closeWindow() {
        if (Controller.getInfoStage() != null) {
            Controller.getInfoStage().close();
        }
    }
}