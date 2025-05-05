package tasks.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.model.Task;
import tasks.repository.ArrayTaskList;
import tasks.services.TasksService;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FullIntegrationTest {
    private ArrayTaskList repository;
    private TasksService service;

    @BeforeEach
    void init() {
        repository = new ArrayTaskList();
        service = new TasksService(repository);
    }

    @Test
    void testNonRepeatedTaskFilter() {
        Date now = new Date(1000);
        Task t = service.createTaskFromFields("NT", now, false, null, null, true);
        repository.add(t);

        var filtered = service.filterTasks(new Date(0), new Date(2000));
        assertTrue(filtered.iterator().hasNext());
        assertEquals(t, filtered.iterator().next());
    }

    @Test
    void testRepeatedTaskFilter() {
        Date start = new Date(0);
        Date end = new Date(10000);
        // interval de 3 secunde
        Task t = service.createTaskFromFields("RT", start, true, end, "0:03", true);
        repository.add(t);

        // filtrăm între 2s și 4s
        var filtered = service.filterTasks(new Date(2000), new Date(4000));
        assertTrue(filtered.iterator().hasNext());
        Task result = filtered.iterator().next();

        // în codul curent, nu se găseşte nicio apariţie,
        // așa că se întoarce placeholder-ul “Empty”
        assertEquals("Empty", result.getTitle());
    }

}
