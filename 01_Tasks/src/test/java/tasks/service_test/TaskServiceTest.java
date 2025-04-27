// File: src/test/java/tasks/service_test/TaskServiceTest.java
package tasks.service_test;

import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.repository.ArrayTaskList;
import tasks.model.Task;
import tasks.services.TasksService;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {
    private ArrayTaskList repository;
    private TasksService service;

    @BeforeEach
    void init() {
        repository = mock(ArrayTaskList.class);
        service = new TasksService(repository);
    }

    @Test
    void testGetObservableListDelegatesToRepository() {
        Date now = new Date();
        Task t = new Task("X", now);
        when(repository.getAll()).thenReturn(Collections.singletonList(t));
        ObservableList<Task> list = service.getObservableList();
        assertEquals(1, list.size());
        assertEquals(t, list.get(0));
        verify(repository).getAll();
    }

    @Test
    void testGetIntervalInHoursFormatsCorrectly() {
        Date start = new Date();
        Date end = new Date(start.getTime() + 3660_000); // +1h1m
        Task t = new Task("I", start, end, 3660);
        t.setActive(true);
        assertEquals("01:01", service.getIntervalInHours(t));
    }

    @Test
    void testConvertTimeToSeconds_HHMM() {
        assertEquals(90 * 60, service.convertTimeToSeconds("1:30"));
    }

    @Test
    void testCreateNonRepeatedTask() {
        Date now = new Date();
        Task t = service.createTaskFromFields("Title", now, false, null, null, true);
        assertEquals("Title", t.getTitle());
        assertFalse(t.isRepeated());
        assertTrue(t.isActive());
    }

    @Test
    void testCreateRepeatedTask() {
        Date start = new Date();
        Date end = new Date(start.getTime() + 3600_000);
        Task t = service.createTaskFromFields("T", start, true, end, "1:00", false);
        assertTrue(t.isRepeated());
        assertFalse(t.isActive());
        assertEquals(end, t.getEndTime());
    }
}
