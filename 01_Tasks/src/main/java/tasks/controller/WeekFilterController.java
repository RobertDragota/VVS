// File: src/main/java/tasks/controller/WeekFilterController.java
package tasks.controller;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import java.time.LocalDate;
import tasks.model.Task;
import tasks.services.TasksService;

public class WeekFilterController {

    @FXML
    private DatePicker datePickerFilter;

    @FXML
    private ListView<Task> listViewTasks;

    private TasksService tasksService;

    public void setTasksService(TasksService tasksService) {
        this.tasksService = tasksService;
        ObservableList<Task> observableTasks = tasksService.getObservableList();
        observableTasks.addListener((ListChangeListener<Task>) change -> {
            if (datePickerFilter.getValue() != null) {
                updateTaskList();
            }
        });
    }

    @FXML
    public void handleDateSelection(ActionEvent event) {
        updateTaskList();
    }

    private void updateTaskList() {
        LocalDate selectedDate = datePickerFilter.getValue();
        if (selectedDate == null) {
            listViewTasks.setItems(FXCollections.observableArrayList());
            return;
        }
        ObservableList<Task> tasksForWeek =
                FXCollections.observableArrayList(tasksService.getTasksForWeek(selectedDate));
        listViewTasks.setItems(tasksForWeek);
    }
}