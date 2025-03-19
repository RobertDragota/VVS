package tasks.validator;

import java.util.Date;
import tasks.model.Task;

public class TaskValidator implements Validation {

    @Override
    public void validateTitle(String title) throws IllegalArgumentException {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title must be non-null and not empty");
        }
    }

    @Override
    public void validateTime(Date time) throws IllegalArgumentException {
        if (time == null) {
            throw new IllegalArgumentException("Time cannot be null");
        }
        if (time.getTime() < 0) {
            throw new IllegalArgumentException("Time cannot be negative");
        }
    }

    @Override
    public void validateDates(Date start, Date end) throws IllegalArgumentException {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start and end dates cannot be null");
        }
        if (start.getTime() < 0 || end.getTime() < 0) {
            throw new IllegalArgumentException("Dates cannot be negative");
        }
        if (start.after(end)) {
            throw new IllegalArgumentException("Start date should not be after end date");
        }
    }

    @Override
    public void validateInterval(int interval) throws IllegalArgumentException {
        if (interval < 1) {
            throw new IllegalArgumentException("Interval must be greater than 0");
        }
    }

    @Override
    public void validateTask(Task task) throws IllegalArgumentException {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        // Validate title.
        validateTitle(task.getTitle());

        // If the task is repeated validate start, end, and interval.
        if (task.isRepeated()) {
            validateDates(task.getStartTime(), task.getEndTime());
            validateInterval(task.getRepeatInterval());
        } else {
            // For non-repeated tasks, validate single time.
            validateTime(task.getTime());
        }
    }
}