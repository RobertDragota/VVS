package tasks.services;

import javafx.collections.ObservableList;
import tasks.model.Task;

import java.util.*;

public class TasksOperations {
    private final List<Task> tasks;

    public TasksOperations(ObservableList<Task> tasksList) {
        this.tasks = new ArrayList<>(tasksList);
    }

    /**
     * Retrieves tasks scheduled between start and end dates.
     * @param start Start date.
     * @param end End date.
     * @return Iterable list of tasks occurring within the given range.
     */
    public Iterable<Task> incoming(Date start, Date end) {
        return filterIncomingTasks(start, end);
    }

    /**
     * Filters tasks occurring between start and end dates.
     * @param start Start date.
     * @param end End date.
     * @return List of matching tasks.
     */
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

    /**
     * Builds a calendar of tasks scheduled between start and end dates.
     * @param start Start date.
     * @param end End date.
     * @return Sorted map with scheduled tasks per date.
     */
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
