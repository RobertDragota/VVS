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
     *
     * @param start Start date.
     * @param end   End date.
     * @return Iterable list of tasks occurring within the given range.
     */
    public Iterable<Task> incoming(Date start, Date end) {
        System.out.println(start);
        System.out.println(end);
        ArrayList<Task> incomingTasks = new ArrayList<>();

        if (end != null && start != null) {
            for (Task t : tasks) {
                Date nextTime = t.nextTimeAfter(start);
                if (nextTime != null && (nextTime.before(end) || nextTime.equals(end))) {
                    incomingTasks.add(t);
                    System.out.println(t.getTitle());
                }
            }
        }

        if (incomingTasks.isEmpty()) {
            ArrayList<Task> emptyList = new ArrayList<>();
            emptyList.add(new Task("Empty", new Date()));
            return emptyList;
        }

        return incomingTasks;
    }


    /**
     * Builds a calendar of tasks scheduled between start and end dates.
     *
     * @param start Start date.
     * @param end   End date.
     * @return Sorted map with scheduled tasks per date.
     */
    public SortedMap<Date, Set<Task>> calendar(Date start, Date end) {
        SortedMap<Date, Set<Task>> calendar = new TreeMap<>();
        for (Task t : incoming(start, end)) {
            Date nextTimeAfter = t.nextTimeAfter(start);
            while (nextTimeAfter != null && !nextTimeAfter.after(end)) {
                calendar.computeIfAbsent(nextTimeAfter, k -> new HashSet<>()).add(t);
                nextTimeAfter = t.nextTimeAfter(nextTimeAfter);
            }
        }
        return calendar;
    }



}
