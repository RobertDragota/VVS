package tasks.repository;

import tasks.model.Task;

import java.io.Serializable;
import java.util.List;

public abstract class TaskList implements Iterable<Task>, Serializable  {
    public abstract void add(Task task);
    public abstract boolean remove(Task task);
    public abstract int size();
    public abstract Task getTask(int index);
    public abstract List<Task> getAll();
    

}
