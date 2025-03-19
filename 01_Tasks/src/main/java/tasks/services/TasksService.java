// Java
package tasks.services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tasks.repository.ArrayTaskList;
import tasks.model.Task;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * Converts a time string to seconds.
     * Supports HH:MM format or a numeric value with a unit (days, weeks, months, hours, minutes).
     * For months, 30 days are assumed.
     *
     * @param timeString The time string (e.g., "02:30", "1 day", "2 weeks", "3 months").
     * @return Time in seconds.
     */
    public int convertTimeToSeconds(String timeString) {
        if (timeString == null || timeString.trim().isEmpty()) {
            throw new IllegalArgumentException("Time string cannot be empty");
        }
        timeString = timeString.trim();
        // Check if time is in HH:MM format
        if (timeString.matches("\\d{1,2}:\\d{2}")) {
            String[] parts = timeString.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            return (hours * DateService.MINUTES_IN_HOUR + minutes) * DateService.SECONDS_IN_MINUTE;
        }
        // Otherwise, check for numeric value with unit (e.g., "1 day", "2 weeks")
        Pattern pattern = Pattern.compile("(\\d+)\\s*(day|days|week|weeks|month|months|hour|hours|minute|minutes)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(timeString);
        if (matcher.find()) {
            int value = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2).toLowerCase();
            switch(unit) {
                case "day":
                case "days":
                    return value * 24 * 3600;
                case "week":
                case "weeks":
                    return value * 7 * 24 * 3600;
                case "month":
                case "months":
                    return value * 30 * 24 * 3600;
                case "hour":
                case "hours":
                    return value * 3600;
                case "minute":
                case "minutes":
                    return value * 60;
                default:
                    break;
            }
        }
        throw new IllegalArgumentException("Invalid time format. Use HH\\:MM or specify unit (days, weeks, months, hours, minutes)");
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

    /**
     * Returns tasks whose start date fall on even or odd weeks.
     * @param filterDate If true, returns tasks in even weeks; if false, returns tasks in odd weeks.
     * @return List of tasks filtered by week parity.
     */
    public List<Task> getTasksForWeek(LocalDate filterDate) {
        List<Task> filteredTasks = new ArrayList<>();
        for (Task task : tasks) {
            LocalDate taskStart = task.getStartTime().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate taskWeekStart = taskStart.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate filterWeekStart = filterDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            long weeksDiff = ChronoUnit.WEEKS.between(taskWeekStart, filterWeekStart);
            if (weeksDiff < 0) {
                continue;
            }
            if (!task.isRepeated() && weeksDiff == 0) {
                filteredTasks.add(task);
            } else if (task.isRepeated()) {
                int recurrenceWeeks = task.getRepeatInterval() / 604800; // 604800 seconds in a week
                if (recurrenceWeeks <= 0) {
                    recurrenceWeeks = 1;
                }
                if (weeksDiff % recurrenceWeeks == 0) {
                    filteredTasks.add(task);
                }
            }
        }
        return filteredTasks;
    }
}