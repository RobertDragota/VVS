package tasks.task;

import org.junit.jupiter.api.Test;
import tasks.model.Task;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void testNonRepeatedNextTimeAfter() {
        Date now = new Date();
        Task task = new Task("Test", now);
        task.setActive(true);

        // For a non-repeated active task, nextTimeAfter before time should return the time
        Date before = new Date(now.getTime() - 1000);
        assertEquals(now, task.nextTimeAfter(before));

        // After or at the time, there should be no next occurrence
        assertNull(task.nextTimeAfter(now));
    }

    @Test
    void testRepeatedNextTimeAfter() {
        Date start = new Date();
        Date end = new Date(start.getTime() + 3600_000); // +1h
        int intervalSeconds = 1800; // 30min
        Task task = new Task("Repeat", start, end, intervalSeconds);
        task.setActive(true);

        // After one second from start, next should be start + interval
        Date after = new Date(start.getTime() + 1000);
        Date expectedNext = new Date(start.getTime() + intervalSeconds * 1000L);
        assertEquals(expectedNext, task.nextTimeAfter(after));
    }

    @Test
    void testInvalidDatesInConstructorThrows() {
        Date now = new Date();
        // end before start should throw
        Date endBefore = new Date(now.getTime() - 1000);
        assertThrows(IllegalArgumentException.class,
                () -> new Task("Bad", now, endBefore, 60));
    }
}