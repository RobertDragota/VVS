// Java
package tasks.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import tasks.model.Task;
import tasks.services.DateService;
import tasks.services.TasksService;
import tasks.utils.TaskIO;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;

public class NewEditController {
    private static final Logger log = Logger.getLogger(NewEditController.class.getName());

    private static Button clickedButton;
    private static Stage currentStage;

    private Task currentTask;
    private ObservableList<Task> tasksList;
    private TasksService service;
    private DateService dateService;
    private boolean incorrectInputMade;

    @FXML
    private TextField fieldTitle;
    @FXML
    private DatePicker datePickerStart;
    @FXML
    private TextField txtFieldTimeStart;
    @FXML
    private DatePicker datePickerEnd;
    @FXML
    private TextField txtFieldTimeEnd;
    @FXML
    private TextField fieldInterval;
    @FXML
    private CheckBox checkBoxActive;
    @FXML
    private CheckBox checkBoxRepeated;

    private static final String DEFAULT_START_TIME = "08:00";
    private static final String DEFAULT_END_TIME = "10:00";
    private static final String DEFAULT_INTERVAL_TIME = "00:30";

    public static void setClickedButton(Button clickedButton) {
        NewEditController.clickedButton = clickedButton;
    }

    public static void setCurrentStage(Stage stage) {
        NewEditController.currentStage = stage;
    }

    public void setTasksList(ObservableList<Task> tasksList) {
        this.tasksList = tasksList;
    }

    public void setService(TasksService service) {
        this.service = service;
        this.dateService = new DateService(service);
    }

    public void setCurrentTask(Task task) {
        this.currentTask = task;
        setupWindow();
    }

    @FXML
    public void initialize() {
        log.info("New/Edit window initializing");
        if (fieldTitle != null && fieldTitle.getScene() != null) {
            currentStage = (Stage) fieldTitle.getScene().getWindow();
        }
    }

    @FXML
    public void saveChanges() {
        Task collectedTask = collectTaskData();
        if (incorrectInputMade || collectedTask == null) return;
        updateTaskList(collectedTask);
        TaskIO.rewriteFile(tasksList);
        closeDialogWindow();
    }

    /**
     * Collects the task data from UI fields.
     * For repeated tasks, end date and interval are read.
     * For non-repeated tasks, those fields are ignored.
     */
    private Task collectTaskData() {
        incorrectInputMade = false;
        try {
            Date start = dateService.getDateMergedWithTime(
                    txtFieldTimeStart.getText(),
                    dateService.getDateValueFromLocalDate(datePickerStart.getValue())
            );
            if (checkBoxRepeated.isSelected()) {
                Date end = dateService.getDateMergedWithTime(
                        txtFieldTimeEnd.getText(),
                        dateService.getDateValueFromLocalDate(datePickerEnd.getValue())
                );
                return service.createTaskFromFields(
                        fieldTitle.getText(),
                        start,
                        true,
                        end,
                        fieldInterval.getText(),
                        checkBoxActive.isSelected()
                );
            } else {
                return service.createTaskFromFields(
                        fieldTitle.getText(),
                        start,
                        false,
                        null,
                        null,
                        checkBoxActive.isSelected()
                );
            }
        } catch (RuntimeException e) {
            incorrectInputMade = true;
            showValidationErrorDialog();
            return null;
        }
    }

    private void updateTaskList(Task collectedTask) {
        if (currentTask == null) {
            tasksList.add(collectedTask);
        } else {
            int index = tasksList.indexOf(currentTask);
            if (index != -1) {
                tasksList.set(index, collectedTask);
            }
            currentTask = null;
        }
    }

    private void showValidationErrorDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/field-validator.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root, 350, 150));
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException ioe) {
            log.error("Error loading /fxml/field-validator.fxml", ioe);
        }
    }

    private void setupWindow() {
        if (clickedButton == null || currentStage == null) {
            return;
        }
        switch (clickedButton.getId()) {
            case "btnNew":
                currentStage.setTitle("New Task");
                initializeNewTask();
                break;
            case "btnEdit":
                currentStage.setTitle("Edit Task");
                initializeTaskFields();
                break;
            default:
                log.error("Unknown button id: " + clickedButton.getId());
                break;
        }
    }

    private void initializeNewTask() {
        datePickerStart.setValue(LocalDate.now());
        txtFieldTimeStart.setText(DEFAULT_START_TIME);
    }

    private void initializeTaskFields() {
        if (currentTask == null) {
            return;
        }
        fieldTitle.setText(currentTask.getTitle());
        datePickerStart.setValue(dateService.getLocalDateValueFromDate(currentTask.getStartTime()));
        txtFieldTimeStart.setText(dateService.getTimeOfTheDayFromDate(currentTask.getStartTime()));
        if (currentTask.isRepeated()) {
            checkBoxRepeated.setSelected(true);
            datePickerEnd.setValue(dateService.getLocalDateValueFromDate(currentTask.getEndTime()));
            txtFieldTimeEnd.setText(dateService.getTimeOfTheDayFromDate(currentTask.getEndTime()));
            fieldInterval.setText(service.getIntervalInHours(currentTask));
        }
        checkBoxActive.setSelected(currentTask.isActive());
    }

    @FXML
    public void closeDialogWindow() {
        if (currentStage != null) {
            currentStage.close();
        }
    }

    @FXML
    public void switchRepeatedCheckbox(ActionEvent actionEvent) {
        boolean isChecked = ((CheckBox) actionEvent.getSource()).isSelected();
        toggleRepeatedTaskFields(!isChecked);
    }

    private void toggleRepeatedTaskFields(boolean disableFields) {
        datePickerEnd.setDisable(disableFields);
        fieldInterval.setDisable(disableFields);
        txtFieldTimeEnd.setDisable(disableFields);
        if (!disableFields) {
            datePickerEnd.setValue(LocalDate.now());
            txtFieldTimeEnd.setText(DEFAULT_END_TIME);
            fieldInterval.setText(DEFAULT_INTERVAL_TIME);
        }
    }
}