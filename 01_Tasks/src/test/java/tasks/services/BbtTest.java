package tasks.services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tasks.controller.NewEditController;
import tasks.model.Task;
import tasks.repository.ArrayTaskList;

import java.util.Date;

public class BbtTest {
    private TasksService service;
    NewEditController add_ctrl;
    @BeforeEach
    public void setUp() {
        ArrayTaskList init_taskList = new ArrayTaskList();
        service = new TasksService(init_taskList);
        add_ctrl = new NewEditController();
        ObservableList<Task> taskList = service.getObservableList();
        add_ctrl.setService(service);
        add_ctrl.setTasksList(taskList);
    }

    // ECP
    @Test
    public void saveTaskWithValidTitle() {

        Date customStartDate = new Date(70, 1, 1);
        Date customEndDate = new Date(124, 11, 31);

        Task task = service.createTaskFromFields("Title", customStartDate, false, customEndDate, "Every 7 days", true);

        Assertions.assertDoesNotThrow(() -> add_ctrl.updateTaskList(task));
        Assertions.assertEquals(1, add_ctrl.getTasksListSize());
    }


    @Test
    public void saveTaskWithInvalidTitle() {
        Date customStartDate = new Date(123, 4, 12);
        Date customEndDate = new Date(124, 3, 28);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Task task = service.createTaskFromFields("    ", customStartDate,false, customEndDate, "Every 7 days", true);
            add_ctrl.updateTaskList(task);
        });
        Assertions.assertEquals(0, add_ctrl.getTasksListSize());
    }

    // BVA
    @Test
    public void saveTaskWithValidTitleBva() {
        Date customStartDate = new Date(123, 12, 2);
        Date customEndDate = new Date(124, 3, 28);
            Task task = service.createTaskFromFields("aaaaaaaaaabbbbbbbbbbcccccccccc", customStartDate,false, customEndDate, "Every 7 days", true);

        Assertions.assertDoesNotThrow(() -> {
            add_ctrl.updateTaskList(task);
        });

        Assertions.assertEquals(1,add_ctrl.getTasksListSize());
        Assertions.assertEquals(add_ctrl.getTasksList().get(0), task);
    }

    @Test
    public void saveTaskWithInvalidTitleBva() {
        Date customStartDate = new Date(123, 4, 12);
        Date customEndDate = new Date(124, 3, 28);
        Assertions.assertThrows(Exception.class, () -> {
            Task task = service.createTaskFromFields("aaaaaaaaaabbbbbbbbbbccccccccccd", customStartDate,false, customEndDate, "Every 7 days", true);
            add_ctrl.updateTaskList(task);
        });
        Assertions.assertEquals(0, add_ctrl.getTasksListSize());
    }

    @Test
    void testIncomingNullEndDate() {
        // Creăm un ObservableList și adăugăm un task activ
        ObservableList<Task> observableTasks = FXCollections.observableArrayList();
        Date now = new Date();
        // Task programat să ruleze la 30 de secunde după now
        Date scheduledTime = new Date(now.getTime() + 30000);
        Task task1 = new Task("Task1", scheduledTime);
        task1.setActive(true);
        observableTasks.add(task1);

        // Inițializăm TasksOperations cu lista creată
        TasksOperations operations = new TasksOperations(observableTasks);

        // Definim un interval care îl include pe task
        Date start = now;
        Date end = null; // 1 minut mai târziu

        Iterable<Task> result = operations.incoming(start, end);
        boolean found = false;
        for (Task t : result) {
            if ("Task1".equals(t.getTitle())) {
                found = true;
                break;
            }
        }
        Assertions.assertFalse(found, "Task1 ar trebui să fie prezent în rezultatul metodei incoming.");
    }

    @Test
    void testIncomingNoError() {
        // Creăm un ObservableList și adăugăm un task activ
        ObservableList<Task> observableTasks = FXCollections.observableArrayList();
        Date now = new Date();
        // Task programat să ruleze la 30 de secunde după now
        Date scheduledTime = new Date(now.getTime() + 30000);
        Task task1 = new Task("Task1", scheduledTime);
        task1.setActive(true);
        observableTasks.add(task1);

        // Inițializăm TasksOperations cu lista creată
        TasksOperations operations = new TasksOperations(observableTasks);

        // Definim un interval care îl include pe task
        Date start = now;
        Date end = new Date(now.getTime() + 60000); // 1 minut mai târziu

        Iterable<Task> result = operations.incoming(start, end);
        boolean found = false;
        for (Task t : result) {
            if ("Task1".equals(t.getTitle())) {
                found = true;
                break;
            }
        }
        Assertions.assertTrue(found, "Task1 ar trebui să fie prezent în rezultatul metodei incoming.");
    }

}