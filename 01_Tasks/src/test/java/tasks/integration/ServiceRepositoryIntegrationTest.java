package tasks.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.model.Task;
import tasks.repository.ArrayTaskList;
import tasks.services.TasksService;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServiceRepositoryIntegrationTest {
    private ArrayTaskList repository;
    private TasksService service;
    private Task mockTask;

    @BeforeEach
    void init() {
        repository = new ArrayTaskList();
        service = new TasksService(repository);
        mockTask = mock(Task.class);
    }

    @Test
    void testGetObservableListWithMockTask() {
        // Adăugăm un Task mock în repository și verificăm că service îl returnează
        repository.add(mockTask);
        var list = service.getObservableList();
        assertEquals(1, list.size());
        assertSame(mockTask, list.get(0));
    }

    @Test
    void testFilterTasksInvokesNextTimeAfterOnMockTask() {
        // Configurăm intervalul de filtrare și stub pentru nextTimeAfter
        Date start = new Date(1000);
        Date end = new Date(5000);
        when(mockTask.nextTimeAfter(start)).thenReturn(new Date(2000));
        repository.add(mockTask);

        var filtered = service.filterTasks(start, end);
        // Ar trebui să includă mockTask deoarece nextTimeAfter returnează o dată în interval
        assertTrue(filtered.iterator().hasNext());
        assertSame(mockTask, filtered.iterator().next());
        verify(mockTask).nextTimeAfter(start);
    }
}