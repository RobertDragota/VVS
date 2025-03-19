package tasks.controller;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import tasks.model.Task;
import tasks.services.DateService;
import tasks.utils.TaskIO;
import tasks.services.TasksService;
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

    /**
     * Sets the service and initializes the observable task list.
     * @param service Task management service.
     */
    public void setService(TasksService service) {
        this.service = service;
        this.dateService = new DateService(service);
        this.tasksList = service.getObservableList();

        setupTaskTable();
        addTaskListListener();
    }

    @FXML
    public void initialize() {
        log.info("Main controller initializing");
        initializeTableColumns();
    }

    /**
     * Initializes table columns with property mappings.
     */
    private void initializeTableColumns() {
        columnTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        columnTime.setCellValueFactory(new PropertyValueFactory<>("formattedDateStart"));
        columnRepeated.setCellValueFactory(new PropertyValueFactory<>("formattedRepeated"));
    }

    /**
     * Sets up the task table with initial data.
     */
    private void setupTaskTable() {
        updateCountLabel();
        tasks.setItems(tasksList);
        mainTable = tasks;
    }

    /**
     * Adds a listener to track changes in the task list and update UI.
     */
    private void addTaskListListener() {
        tasksList.addListener((ListChangeListener.Change<? extends Task> c) -> updateCountLabel());
    }

    /**
     * Updates the count label to reflect the number of tasks.
     */
    private void updateCountLabel() {
        labelCount.setText(tasksList.size() + " elements");
    }

    @FXML
    public void showTaskDialog(ActionEvent actionEvent) {
        openTaskEditDialog((Button) actionEvent.getSource());
    }

    /**
     * Opens the task edit dialog.
     * @param clickedButton The button that triggered the action.
     */
    private void openTaskEditDialog(Button clickedButton) {
        NewEditController.setClickedButton(clickedButton);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/new-edit-task.fxml"));
            Parent root = loader.load();
            NewEditController editCtrl = loader.getController();

            editCtrl.setService(service);
            editCtrl.setTasksList(tasksList);

            editNewStage = createDialogStage(root, "Edit Task", 600, 350);
            NewEditController.setCurrentStage(editNewStage);
            editCtrl.setCurrentTask(mainTable.getSelectionModel().getSelectedItem());

            editNewStage.show();
        } catch (IOException e) {
            log.error("Error loading /fxml/new-edit-task.fxml", e);
        }
    }

    @FXML
    public void deleteTask() {
        removeSelectedTask();
    }

    /**
     * Removes the currently selected task.
     */
    private void removeSelectedTask() {
        Task toDelete = tasks.getSelectionModel().getSelectedItem();
        if (toDelete != null) {
            tasksList.remove(toDelete);
            TaskIO.rewriteFile(tasksList);
            updateCountLabel();
        }
    }

    @FXML
    public void showDetailedInfo() {
        infoStage = createDialogStage("/fxml/task-info.fxml", "Task Info", 550, 350);
        if (infoStage != null) {
            infoStage.show();
        }
    }

    @FXML
    public void showFilteredTasks() {
        filterTasksByDateRange();
    }

    /**
     * Filters tasks based on the selected date range.
     */
    private void filterTasksByDateRange() {
        Date start = getDateFromFilterField(datePickerFrom.getValue(), fieldTimeFrom.getText());
        Date end = getDateFromFilterField(datePickerTo.getValue(), fieldTimeTo.getText());

        List<Task> filteredTasks = new ArrayList<>();
        service.filterTasks(start, end).forEach(filteredTasks::add);

        ObservableList<Task> observableTasks = FXCollections.observableList(filteredTasks);
        tasks.setItems(observableTasks);
        updateCountLabel();
    }

    /**
     * Converts date and time fields into a Date object.
     * @param localDate The LocalDate from the date picker.
     * @param time The time entered in the text field.
     * @return The corresponding Date object.
     */
    private Date getDateFromFilterField(LocalDate localDate, String time) {
        Date date = dateService.getDateValueFromLocalDate(localDate);
        return dateService.getDateMergedWithTime(time, date);
    }

    @FXML
    public void resetFilteredTasks() {
        restoreOriginalTaskList();
    }

    /**
     * Resets the task table to show all tasks.
     */
    private void restoreOriginalTaskList() {
        tasks.setItems(tasksList);
        updateCountLabel();
    }

    /**
     * Creates and returns a dialog stage with the specified properties.
     * @param fxmlPath Path to the FXML file.
     * @param title Title of the dialog.
     * @param width Dialog width.
     * @param height Dialog height.
     * @return The created Stage instance.
     */
    private Stage createDialogStage(String fxmlPath, String title, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            return createDialogStage(root, title, width, height);
        } catch (IOException e) {
            log.error("Error loading " + fxmlPath, e);
            return null;
        }
    }

    /**
     * Creates a dialog stage from a given root node.
     * @param root The loaded Parent node.
     * @param title The dialog title.
     * @param width Dialog width.
     * @param height Dialog height.
     * @return The created Stage instance.
     */
    private Stage createDialogStage(Parent root, String title, int width, int height) {
        Stage stage = new Stage();
        stage.setScene(new Scene(root, width, height));
        stage.setResizable(false);
        stage.setTitle(title);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(Main.primaryStage);
        return stage;
    }

    public static Stage getInfoStage() {
        return infoStage;
    }
}
