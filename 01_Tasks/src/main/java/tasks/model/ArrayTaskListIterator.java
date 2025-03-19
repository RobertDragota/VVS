package tasks.model;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.log4j.Logger;

public class ArrayTaskListIterator implements Iterator<Task> {
    private static final Logger log = Logger.getLogger(ArrayTaskListIterator.class.getName());
    private final ArrayTaskList taskList;
    private int cursor;
    private int lastCalled = -1;

    public ArrayTaskListIterator(ArrayTaskList taskList) {
        this.taskList = taskList;
    }

    @Override
    public boolean hasNext() {
        return cursor < taskList.size();
    }

    @Override
    public Task next() {
        if (!hasNext()) {
            log.error("next iterator element doesn't exist");
            throw new NoSuchElementException("No next element");
        }
        lastCalled = cursor;
        return taskList.getTask(cursor++);
    }

    @Override
    public void remove() {
        if (lastCalled == -1) {
            throw new IllegalStateException();
        }
        taskList.remove(taskList.getTask(lastCalled));
        cursor = lastCalled;
        lastCalled = -1;
    }
}
