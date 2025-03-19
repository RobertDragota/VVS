package tasks.validator;

import java.util.Date;
import tasks.model.Task;

public interface Validation {
    // Validates that the title is not null or empty.
    void validateTitle(String title) throws IllegalArgumentException;

    // Validates a single date (for non-repeated tasks).
    void validateTime(Date time) throws IllegalArgumentException;

    // Validates that start and end dates are valid and that start is not after end.
    void validateDates(Date start, Date end) throws IllegalArgumentException;

    // Validates that the repeat interval is greater than zero.
    void validateInterval(int interval) throws IllegalArgumentException;

    // Validates all elements of the given task.
    void validateTask(Task task) throws IllegalArgumentException;
}