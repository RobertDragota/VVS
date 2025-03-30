package tasks.validator;

import java.util.Date;
import tasks.model.Task;

public class TaskValidator implements Validation {

    @Override
    public void validateTitle(String title) throws IllegalArgumentException {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title must be non-null and not empty");
        }
        if (title.length() > 30) {
            throw new IllegalArgumentException("Title length must be less than 30 characters.");
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

        // Interval permis: 1970-01-01 până la 2024-12-31
        Date minDate = new Date(70, 0, 1);  // 1970-01-01 (year=70, month=0, day=1)
        Date maxDate = new Date(124, 11, 31); // 2024-12-31 (year=124, month=11, day=31)
        // Notă: Constructorul Date(int year, int month, int date) consideră year = an - 1900.

        if (start.before(minDate) || start.after(maxDate)) {
            throw new IllegalArgumentException("Start date must be between 1970-01-01 and 2024-12-31");
        }
        if (end.before(minDate) || end.after(maxDate)) {
            throw new IllegalArgumentException("End date must be between 1970-01-01 and 2024-12-31");
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