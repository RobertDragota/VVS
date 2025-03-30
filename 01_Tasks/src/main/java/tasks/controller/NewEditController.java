package tasks.controller;

import javafx.collections.FXCollections;
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

    // Remove previous interval controls and add three ComboBoxes for days, weeks, and months
    @FXML
    private ComboBox<String> comboBoxDay;
    @FXML
    private ComboBox<String> comboBoxWeek;
    @FXML
    private ComboBox<String> comboBoxMonth;

    @FXML
    private CheckBox checkBoxActive;
    @FXML
    private CheckBox checkBoxRepeated;

    private static final String DEFAULT_START_TIME = "08:00";
    private static final String DEFAULT_END_TIME = "10:00";
    // Default selections for interval; for a repeated task only one should be non-zero.
    private static final String DEFAULT_DAY = "0";
    private static final String DEFAULT_WEEK = "0";
    private static final String DEFAULT_MONTH = "0";

    public static void setClickedButton(Button clickedButton) {
        NewEditController.clickedButton = clickedButton;
    }

    public static void setCurrentStage(Stage stage) {
        NewEditController.currentStage = stage;
    }

    public void setTasksList(ObservableList<Task> tasksList) {
        this.tasksList = tasksList;
    }
    public int getTasksListSize() {
        return tasksList.size();
    }
    public ObservableList<Task> getTasksList() {
        return tasksList;
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
        // Initialize combo boxes for day, week and month values.
        if (comboBoxDay != null) {
            comboBoxDay.setItems(getNumberOptions(0, 31));
            comboBoxDay.setValue(DEFAULT_DAY);
        }
        if (comboBoxWeek != null) {
            comboBoxWeek.setItems(getNumberOptions(0, 5));
            comboBoxWeek.setValue(DEFAULT_WEEK);
        }
        if (comboBoxMonth != null) {
            comboBoxMonth.setItems(getNumberOptions(0, 13));
            comboBoxMonth.setValue(DEFAULT_MONTH);
        }
    }

    // Utility method for generating number options as strings.
    private ObservableList<String> getNumberOptions(int start, int end) {
        ObservableList<String> options = FXCollections.observableArrayList();
        for (int i = start; i < end; i++) {
            options.add(String.valueOf(i));
        }
        return options;
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
     * Collects task data from UI fields. For repeated tasks the method reads the end date and requires
     * at least one interval unit (days, weeks, or months) to be non-zero.
     */
    private Task collectTaskData() {
        incorrectInputMade = false;
        try {
            Date start = dateService.getDateMergedWithTime(

                    dateService.getDateValueFromLocalDate(datePickerStart.getValue()),
                    txtFieldTimeStart.getText()
            );
            if (checkBoxRepeated.isSelected()) {
                Date end = dateService.getDateMergedWithTime(

                        dateService.getDateValueFromLocalDate(datePickerEnd.getValue()),
                        txtFieldTimeEnd.getText()
                );
                // Parse selections from the three ComboBoxes.
                int day = Integer.parseInt(comboBoxDay.getValue());
                int week = Integer.parseInt(comboBoxWeek.getValue());
                int month = Integer.parseInt(comboBoxMonth.getValue());
                // Ensure that at least one interval unit is non-zero.
                if (day == 0 && week == 0 && month == 0) {
                    throw new IllegalArgumentException(
                            "Select at least one non-zero interval unit (days, weeks, or months)."
                    );
                }
                // Construct the interval message.
                StringBuilder intervalMessage = new StringBuilder("Every ");

                if (month > 0) {
                    intervalMessage.append(month).append(month == 1 ? " month " : " months ");
                }
                if (week > 0) {
                    intervalMessage.append(week).append(week == 1 ? " week " : " weeks ");
                }
                if (day > 0 || (month == 0 && week == 0)) {
                    intervalMessage.append(day).append(day == 1 ? " day" : " days");
                }

                return service.createTaskFromFields(
                        fieldTitle.getText(),
                        start,
                        true,
                        end,
                        intervalMessage.toString(),
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

    public void updateTaskList(Task collectedTask) {
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
        // Explicitly set currentTask to null so the task is added to the list
        currentTask = null;
        datePickerStart.setValue(LocalDate.now());
        txtFieldTimeStart.setText(DEFAULT_START_TIME);
        if (comboBoxDay != null) {
            comboBoxDay.setValue(DEFAULT_DAY);
        }
        if (comboBoxWeek != null) {
            comboBoxWeek.setValue(DEFAULT_WEEK);
        }
        if (comboBoxMonth != null) {
            comboBoxMonth.setValue(DEFAULT_MONTH);
        }
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
            // For editing provide default interval values.
            if (comboBoxDay != null) {
                comboBoxDay.setValue(DEFAULT_DAY);
            }
            if (comboBoxWeek != null) {
                comboBoxWeek.setValue(DEFAULT_WEEK);
            }
            if (comboBoxMonth != null) {
                comboBoxMonth.setValue(DEFAULT_MONTH);
            }
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
        txtFieldTimeEnd.setDisable(disableFields);
        if (comboBoxDay != null) {
            comboBoxDay.setDisable(disableFields);
        }
        if (comboBoxWeek != null) {
            comboBoxWeek.setDisable(disableFields);
        }
        if (comboBoxMonth != null) {
            comboBoxMonth.setDisable(disableFields);
        }
        if (!disableFields) {
            datePickerEnd.setValue(LocalDate.now());
            txtFieldTimeEnd.setText(DEFAULT_END_TIME);
            if (comboBoxDay != null) {
                comboBoxDay.setValue(DEFAULT_DAY);
            }
            if (comboBoxWeek != null) {
                comboBoxWeek.setValue(DEFAULT_WEEK);
            }
            if (comboBoxMonth != null) {
                comboBoxMonth.setValue(DEFAULT_MONTH);
            }
        }
    }
}