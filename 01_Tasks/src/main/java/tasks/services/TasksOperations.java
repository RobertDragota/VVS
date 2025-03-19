package tasks.services;

import javafx.collections.ObservableList;
import tasks.model.Task;

import java.util.*;

public class TasksOperations {
    private final List<Task> tasks;

    public TasksOperations(ObservableList<Task> tasksList) {
        this.tasks = new ArrayList<>(tasksList);
    }

    public Iterable<Task> incoming(Date start, Date end) {
        return filterIncomingTasks(start, end);
    }

    private List<Task> filterIncomingTasks(Date start, Date end) {
        List<Task> incomingTasks = new ArrayList<>();
        for (Task t : tasks) {
            Date nextTime = t.nextTimeAfter(start);
            if (nextTime != null && !nextTime.after(end)) {
                incomingTasks.add(t);
            }
        }
        return incomingTasks;
    }

    public SortedMap<Date, Set<Task>> calendar(Date start, Date end) {
        SortedMap<Date, Set<Task>> calendar = new TreeMap<>();
        for (Task t : filterIncomingTasks(start, end)) {
            Date nextTimeAfter = t.nextTimeAfter(start);
            while (nextTimeAfter != null && !nextTimeAfter.after(end)) {
                calendar.computeIfAbsent(nextTimeAfter, k -> new HashSet<>()).add(t);
                nextTimeAfter = t.nextTimeAfter(nextTimeAfter);
            }
        }
        return calendar;
    }
}
