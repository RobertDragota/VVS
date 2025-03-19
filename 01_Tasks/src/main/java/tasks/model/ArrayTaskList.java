package tasks.model;

import org.apache.log4j.Logger;
import java.util.*;

public class ArrayTaskList extends TaskList {
    private Task[] tasks;
    private int numberOfTasks;
    private int currentCapacity;
    private static final Logger log = Logger.getLogger(ArrayTaskList.class.getName());

    public ArrayTaskList() {
        currentCapacity = 10;
        this.tasks = new Task[currentCapacity];
    }

    @Override
    public Iterator<Task> iterator() {
        return new ArrayTaskListIterator(this);
    }

    private void ensureCapacity() {
        if (numberOfTasks >= currentCapacity) {
            currentCapacity *= 2;
            tasks = Arrays.copyOf(tasks, currentCapacity);
        }
    }

    public void trimToSize() {
        if (numberOfTasks < currentCapacity) {
            currentCapacity = numberOfTasks;
            tasks = Arrays.copyOf(tasks, numberOfTasks);
        }
    }

    @Override
    public void add(Task task) {
        if (task == null) throw new NullPointerException("Task shouldn't be null");
        ensureCapacity();
        tasks[numberOfTasks++] = task;
    }

    @Override
    public boolean remove(Task task) {
        if (task == null) return false;
        int indexOfTaskToDelete = -1;
        for (int i = 0; i < numberOfTasks; i++) {
            if (task.equals(tasks[i])) {
                indexOfTaskToDelete = i;
                break;
            }
        }
        if (indexOfTaskToDelete >= 0) {
            System.arraycopy(tasks, indexOfTaskToDelete + 1, tasks, indexOfTaskToDelete, numberOfTasks - indexOfTaskToDelete - 1);
            tasks[--numberOfTasks] = null;
            return true;
        }
        return false;
    }

    @Override
    public int size() {
        return this.numberOfTasks;
    }

    @Override
    public Task getTask(int index) {
        if (index < 0 || index >= size()) {
            log.error("not existing index");
            throw new IndexOutOfBoundsException("Index not found");
        }
        return tasks[index];
    }

    @Override
    public List<Task> getAll() {
        return Arrays.asList(Arrays.copyOf(tasks, numberOfTasks));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArrayTaskList that = (ArrayTaskList) o;
        return numberOfTasks == that.numberOfTasks && Arrays.equals(Arrays.copyOf(tasks, numberOfTasks), Arrays.copyOf(that.tasks, that.numberOfTasks));
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(Arrays.copyOf(tasks, numberOfTasks)), numberOfTasks, currentCapacity);
    }

    @Override
    public String toString() {
        return "ArrayTaskList{" +
                "tasks=" + Arrays.toString(Arrays.copyOf(tasks, numberOfTasks)) +
                ", numberOfTasks=" + numberOfTasks +
                ", currentCapacity=" + currentCapacity +
                '}';
    }

    public ArrayTaskList(ArrayTaskList arrayTaskList) {
        this.currentCapacity = arrayTaskList.currentCapacity;
        this.numberOfTasks = arrayTaskList.numberOfTasks;
        this.tasks = Arrays.copyOf(arrayTaskList.tasks, currentCapacity);
    }
}
