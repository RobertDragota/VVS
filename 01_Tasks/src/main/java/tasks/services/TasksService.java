package tasks.services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tasks.repository.ArrayTaskList;
import tasks.model.Task;

import java.util.Date;

public class TasksService {
    private final ArrayTaskList tasks;

    public TasksService(ArrayTaskList tasks) {
        this.tasks = tasks;
    }

    /**
     * Retrieves the list of tasks as an observable list for JavaFX components.
     * @return ObservableList of tasks.
     */
    public ObservableList<Task> getObservableList() {
        return FXCollections.observableArrayList(tasks.getAll());
    }

    /**
     * Formats the repeat interval of a task in HH:MM format.
     * @param task The task to extract interval from.
     * @return Formatted interval in HH:MM.
     */
    public String getIntervalInHours(Task task) {
        int seconds = task.getRepeatInterval();
        int minutes = seconds / DateService.SECONDS_IN_MINUTE;
        int hours = minutes / DateService.MINUTES_IN_HOUR;
        minutes %= DateService.MINUTES_IN_HOUR;
        return formatTimeUnit(hours) + ":" + formatTimeUnit(minutes);
    }

    /**
     * Ensures time unit values have two-digit formatting.
     * @param timeUnit The time unit value.
     * @return Formatted string with two-digit representation.
     */
    public String formatTimeUnit(int timeUnit) {
        return (timeUnit < 10 ? "0" : "") + timeUnit;
    }

    /**
     * Converts a string time format (HH:MM) to seconds.
     * @param timeString The time string (e.g., "02:30").
     * @return Time in seconds.
     */
    public int convertTimeToSeconds(String timeString) {
        String[] units = timeString.split(":");
        if (units.length != 2) throw new IllegalArgumentException("Invalid time format. Use HH:MM");

        int hours = Integer.parseInt(units[0]);
        int minutes = Integer.parseInt(units[1]);
        return (hours * DateService.MINUTES_IN_HOUR + minutes) * DateService.SECONDS_IN_MINUTE;
    }

    /**
     * Filters tasks that have scheduled occurrences between the given dates.
     * @param start Start date.
     * @param end End date.
     * @return Iterable containing filtered tasks.
     */
    public Iterable<Task> filterTasks(Date start, Date end) {
        return new TasksOperations(getObservableList()).incoming(start, end);
    }

    /**
     * Creates a Task object based on provided user inputs.
     * @param title The task title.
     * @param startDate The start date.
     * @param isRepeated Whether the task is repeated.
     * @param endDate The end date (if repeated).
     * @param interval The interval (if repeated).
     * @param isActive Whether the task is active.
     * @return A new Task object.
     */
    public Task createTaskFromFields(String title, Date startDate, boolean isRepeated, Date endDate, String interval, boolean isActive) {
        if (isRepeated) {
            int newInterval = convertTimeToSeconds(interval);
            if (startDate.after(endDate)) throw new IllegalArgumentException("Start date should be before end");
            Task task = new Task(title, startDate, endDate, newInterval);
            task.setActive(isActive);
            return task;
        } else {
            Task task = new Task(title, startDate);
            task.setActive(isActive);
            return task;
        }
    }
}