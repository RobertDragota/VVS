package tasks.services;

import javafx.collections.ObservableList;
import org.junit.jupiter.api.*;
import tasks.controller.NewEditController;
import tasks.model.Task;
import tasks.repository.ArrayTaskList;

import java.util.Date;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Pentru controlul ordinii testelor
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
    @Tag("Regression") // Adăugat pentru suita de regression
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
        @Order(1) // Rulează primul
        @Timeout(2) // Timeout de 2 secunde
        @DisplayName("Save task with a valid description")
        public void saveTaskWithValidTitle() {
            Assertions.assertDoesNotThrow(() -> {
                Task task = service.createTaskFromFields("Sample", new Date(), false, new Date(), "Every 1 day", true);
                add_ctrl.updateTaskList(task);
            });
            Assertions.assertEquals(1, add_ctrl.getTasksListSize());
        }

        @Test
        @Disabled("Dezactivat temporar - necesită investigație") // Test dezactivat
        @DisplayName("Save task with an invalid description")
        public void saveTaskWithInvalidTitle() {
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                service.createTaskFromFields("   ", new Date(), false, new Date(), "Every 7 days", true);
            });
            Assertions.assertEquals(0, add_ctrl.getTasksListSize());
        }

        @RepeatedTest(3) // Rulează de 3 ori
        @DisplayName("Save task with a valid date (repeated)")
        public void saveTaskWithValidDateRepeated() {
            Assertions.assertDoesNotThrow(() -> {
                Task task = service.createTaskFromFields("Title", new Date(124, 4, 21), false, new Date(), "Every 1 day", true);
                add_ctrl.updateTaskList(task);
            });
            Assertions.assertEquals(1, add_ctrl.getTasksListSize());
        }

        @ParameterizedTest
        @ValueSource(strings = {"A", "Valid Title", "123456789012345678901234567890"}) // Date dinamice
        @DisplayName("Save task with parameterized valid titles")
        public void saveTaskWithParameterizedTitles(String title) {
            Assertions.assertDoesNotThrow(() -> {
                Task task = service.createTaskFromFields(title, new Date(), false, new Date(), "Every 1 day", true);
                add_ctrl.updateTaskList(task);
            });
            Assertions.assertEquals(1, add_ctrl.getTasksListSize());
        }
    }

    @Nested
    @Tag("BVA")
    @Tag("Regression") // Adăugat pentru suita de regression
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
        @Timeout(1) // Timeout de 1 secundă
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