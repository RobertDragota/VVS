package tasks.services;

import javafx.collections.ObservableList;
import org.junit.jupiter.api.*;
import tasks.controller.NewEditController;
import tasks.model.Task;
import tasks.repository.ArrayTaskList;

import java.util.Date;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TaskServiceTest {
    static private TasksService service;
    static NewEditController add_ctrl;

    @BeforeAll
    static void generalSetUp() {
        ArrayTaskList init_taskList = new ArrayTaskList();
        service = new TasksService(init_taskList);
        add_ctrl = new NewEditController();
        ObservableList<Task> taskList = service.getObservableList();
        add_ctrl.setService(service);
        add_ctrl.setTasksList(taskList);
    }

    @AfterAll
    static void bigCleaning() {
        service = null;
    }

    @Nested
    @Tag("ECP")
    @DisplayName("ECP Tests")
    class EcpTesting {
        @BeforeEach
        void testSetup() {

            ArrayTaskList init_taskList = new ArrayTaskList();
            service = new TasksService(init_taskList);
            add_ctrl = new NewEditController();
            ObservableList<Task> taskList = service.getObservableList();
            add_ctrl.setService(service);
            add_ctrl.setTasksList(taskList);
        }



        @Test
        @DisplayName("Save task with a valid description")
        public void saveTaskWithValidTitle() {
            Assertions.assertDoesNotThrow(() -> {
                Task task = service.createTaskFromFields("Sample", new Date(), false, new Date(), "Every 1 day", true);
                add_ctrl.updateTaskList(task);
            });
            Assertions.assertEquals(1, add_ctrl.getTasksListSize());
        }

        @Test
        @DisplayName("Save task with an invalid description")
        public void saveTaskWithInvalidTitle() {
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                service.createTaskFromFields("   ", new Date(), false, new Date(), "Every 7 days", true);
            });
            Assertions.assertEquals(0, add_ctrl.getTasksListSize());
        }

        @Test
        @DisplayName("Save task with a valid date")
        public void saveTaskWithValidDate() {
            Assertions.assertDoesNotThrow(() -> {
                Task task = service.createTaskFromFields("Title", new Date(124, 4, 21), false, new Date(), "Every 1 day", true);
                add_ctrl.updateTaskList(task);
            });
            Assertions.assertEquals(1, add_ctrl.getTasksListSize());
        }
    }

    @Nested
    @Tag("BVA")
    @DisplayName("BVA Tests")
    class BvaTesting {
        @BeforeEach
        void testSetup() {

            ArrayTaskList init_taskList = new ArrayTaskList();
            service = new TasksService(init_taskList);
            add_ctrl = new NewEditController();
            ObservableList<Task> taskList = service.getObservableList();
            add_ctrl.setService(service);
            add_ctrl.setTasksList(taskList);
        }


        @Test
        @DisplayName("Save task with a valid title (lower bound)")
        public void saveTaskWithValidTitleLowerBound() {
            Assertions.assertDoesNotThrow(() -> {
                Task task = service.createTaskFromFields("T", new Date(), false, new Date(), "Every 1 day", true);
                add_ctrl.updateTaskList(task);
            });
            Assertions.assertEquals(1, add_ctrl.getTasksListSize());
        }

        @Test
        @DisplayName("Save task with an invalid title (empty)")
        public void saveTaskWithInvalidTitleEmpty() {
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                service.createTaskFromFields("", new Date(), false, new Date(), "Every 7 days", true);
            });
            Assertions.assertEquals(0, add_ctrl.getTasksListSize());
        }

        @Test
        @DisplayName("Save task with an invalid title (upper bound)")
        public void saveTaskWithInvalidTitleUpperBound() {
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                service.createTaskFromFields("1234567890123456789012345678901", new Date(), false, new Date(), "Every 7 days", true);
            });
            Assertions.assertEquals(0, add_ctrl.getTasksListSize());
        }

        @Test
        @DisplayName("Save task with a valid title (upper bound)")
        public void saveTaskWithValidTitleUpperBound() {
            Assertions.assertDoesNotThrow(() -> {
                Task task = service.createTaskFromFields("123456789012345678901234567890", new Date(), false, new Date(), "Every 7 days", true);
                add_ctrl.updateTaskList(task);
            });
            Assertions.assertEquals(1, add_ctrl.getTasksListSize());
        }

        @Test
        @DisplayName("Save task with a valid date (lower bound)")
        public void saveTaskWithValidDateLowerBound() {
            Assertions.assertDoesNotThrow(() -> {
                Task task = service.createTaskFromFields("Title", new Date(), false, new Date(), "Every 1 day", true);
                add_ctrl.updateTaskList(task);
            });
            Assertions.assertEquals(1, add_ctrl.getTasksListSize());
        }

        @Test
        @DisplayName("Save task with invalid date (end before start)")
        public void saveTaskWithInvalidDateLowerBound() {
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                service.createTaskFromFields("Title", new Date(124, 5, 1), true, new Date(124, 4, 30), "Every 1 day", true);
            });
            Assertions.assertEquals(0, add_ctrl.getTasksListSize());
        }

    }
}