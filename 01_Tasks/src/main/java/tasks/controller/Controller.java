// File: src/main/java/tasks/controller/Controller.java
package tasks.controller;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import tasks.model.Task;
import tasks.services.DateService;
import tasks.services.TasksService;
import tasks.utils.TaskIO;
import tasks.view.Main;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Controller {
    private static final Logger log = Logger.getLogger(Controller.class.getName());
    private ObservableList<Task> tasksList;
    private TasksService service;
    private DateService dateService;

    private static Stage editNewStage;
    private static Stage infoStage;
    private static TableView<Task> mainTable;

    @FXML
    private TableView<Task> tasks;
    @FXML
    private TableColumn<Task, String> columnTitle;
    @FXML
    private TableColumn<Task, String> columnTime;
    @FXML
    private TableColumn<Task, String> columnRepeated;
    @FXML
    private Label labelCount;
    @FXML
    private DatePicker datePickerFrom;
    @FXML
    private TextField fieldTimeFrom;
    @FXML
    private DatePicker datePickerTo;
    @FXML
    private TextField fieldTimeTo;

    public void setService(TasksService service) {
        this.service = service;
        this.dateService = new DateService(service);
        this.tasksList = service.getObservableList();
        updateCountLabel();
        tasks.setItems(tasksList);
        mainTable = tasks;
        tasksList.addListener((ListChangeListener.Change<? extends Task> c) -> updateCountLabel());
    }

    @FXML
    public void initialize() {
        log.info("Main controller initializing");
        columnTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        columnTime.setCellValueFactory(new PropertyValueFactory<>("formattedDateStart"));
        columnRepeated.setCellValueFactory(new PropertyValueFactory<>("formattedRepeated"));
    }

    private void updateCountLabel() {
        labelCount.setText(tasksList.size() + " elements");
    }

    @FXML
    public void showTaskDialog(ActionEvent actionEvent) {
        NewEditController.setClickedButton((Button) actionEvent.getSource());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/new-edit-task.fxml"));
            Parent root = loader.load();
            NewEditController editCtrl = loader.getController();
            editCtrl.setService(service);
            editCtrl.setTasksList(tasksList);
            editNewStage = new Stage();
            NewEditController.setCurrentStage(editNewStage);
            editCtrl.setCurrentTask(mainTable.getSelectionModel().getSelectedItem());
            editNewStage.setScene(new Scene(root, 600, 350));
            editNewStage.setResizable(false);
            editNewStage.setTitle("Edit Task");
            editNewStage.initModality(Modality.APPLICATION_MODAL);
            editNewStage.initOwner(Main.primaryStage);
            editNewStage.show();
        } catch (IOException e) {
            log.error("Error loading /fxml/new-edit-task.fxml", e);
        }
    }

    // New method to open the Week Filter window.
    public void openWeekFilter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/WeekFilter.fxml"));
            Parent root = loader.load();
            WeekFilterController controller = loader.getController();
            controller.setTasksService(this.service);
            Stage stage = new Stage();
            stage.setScene(new Scene(root, 400, 300));
            stage.setTitle("Week Filter Tasks");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void deleteTask() {
        Task toDelete = tasks.getSelectionModel().getSelectedItem();
        if (toDelete != null) {
            tasksList.remove(toDelete);
            TaskIO.rewriteFile(tasksList);
            updateCountLabel();
        }
    }

    @FXML
    public void showDetailedInfo() {
        Task selectedTask = tasks.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            log.error("No task selected for displaying info");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/task-info.fxml"));
            Parent root = loader.load();
            TaskInfoController infoCtrl = loader.getController();
            infoCtrl.setTask(selectedTask);
            infoStage = new Stage();
            infoStage.setScene(new Scene(root, 550, 350));
            infoStage.setResizable(false);
            infoStage.setTitle("Task Info");
            infoStage.initModality(Modality.APPLICATION_MODAL);
            infoStage.initOwner(Main.primaryStage);
            infoStage.show();
        } catch (IOException e) {
            log.error("Error loading /fxml/task-info.fxml", e);
        }
    }

    @FXML
    public void showFilteredTasks() {
        Date start = getDateFromFilterField(datePickerFrom.getValue(), fieldTimeFrom.getText());
        Date end = getDateFromFilterField(datePickerTo.getValue(), fieldTimeTo.getText());
        List<Task> filteredTasks = new ArrayList<>();
        service.filterTasks(start, end).forEach(filteredTasks::add);
        ObservableList<Task> observableTasks = FXCollections.observableList(filteredTasks);
        tasks.setItems(observableTasks);
        updateCountLabel();
    }

    private Date getDateFromFilterField(LocalDate localDate, String time) {
        Date date = dateService.getDateValueFromLocalDate(localDate);
        return dateService.getDateMergedWithTime(date, time);
    }

    @FXML
    public void resetFilteredTasks() {
        tasks.setItems(tasksList);
        updateCountLabel();
    }

    public static Stage getInfoStage() {
        return infoStage;
    }
}