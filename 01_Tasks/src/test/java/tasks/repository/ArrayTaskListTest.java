package tasks.repository;

import org.junit.jupiter.api.Test;
import tasks.model.Task;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ArrayTaskListTest {
    @Test
    void testAddAndRemove() {
        ArrayTaskList list = new ArrayTaskList();
        Task t = new Task("A", new Date());
        list.add(t);
        assertEquals(1, list.size());
        assertTrue(list.remove(t));
        assertEquals(0, list.size());
    }

    @Test
    void testGetTaskOutOfBoundsThrows() {
        ArrayTaskList list = new ArrayTaskList();
        assertThrows(IndexOutOfBoundsException.class, () -> list.getTask(0));
    }

    @Test
    void testSpyAddInvocation() {
        ArrayTaskList spyList = spy(new ArrayTaskList());
        Task t = new Task("Spy", new Date());
        spyList.add(t);
        // Verify that add was invoked on the spy at least once
        verify(spyList, atLeastOnce()).add(t);
    }
}