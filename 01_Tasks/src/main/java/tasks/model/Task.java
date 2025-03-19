// java
package tasks.model;

import org.apache.log4j.Logger;
import tasks.utils.TaskIO;
import tasks.validator.Validation;
import tasks.validator.TaskValidator;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Task implements Serializable {
    private String title;
    private Date time;
    private Date start;
    private Date end;
    private int interval;
    private boolean active;

    private static final Logger log = Logger.getLogger(Task.class.getName());
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    // Static validator instance using the Validation interface.
    private static final Validation validator = new TaskValidator();

    public Task(String title, Date time) {
        validator.validateTime(time);
        this.title = title;
        this.time = this.start = this.end = time;
    }

    public Task(String title, Date start, Date end, int interval) {
        validator.validateDates(start, end);
        validator.validateInterval(interval);
        this.title = title;
        this.start = start;
        this.end = end;
        this.interval = interval;
        this.time = start;
    }

    public Task(Task other) {
        this.title = other.title;
        this.time = new Date(other.time.getTime());
        this.start = new Date(other.start.getTime());
        this.end = new Date(other.end.getTime());
        this.interval = other.interval;
        this.active = other.active;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Date getTime() { return time; }
    public Date getStartTime() { return start; }
    public Date getEndTime() { return end; }
    public int getRepeatInterval() { return Math.max(interval, 0); }
    public boolean isRepeated() { return interval > 0; }

    public void setTime(Date time) {
        validator.validateTime(time);
        this.time = this.start = this.end = time;
        this.interval = 0;
    }

    public void setTime(Date start, Date end, int interval) {
        validator.validateDates(start, end);
        validator.validateInterval(interval);
        this.start = start;
        this.end = end;
        this.interval = interval;
        this.time = start;
    }

    public Date nextTimeAfter(Date current) {
        if (!isActive() || current.after(end)) return null;
        if (!isRepeated() && current.before(time)) return time;

        Date nextTime = start;
        while (nextTime.before(current) || nextTime.equals(current)) {
            nextTime = new Date(nextTime.getTime() + interval * 1000);
        }
        return nextTime.before(end) ? nextTime : null;
    }

    public String getFormattedDateStart() { return sdf.format(start); }
    public String getFormattedDateEnd() { return sdf.format(end); }
    public String getFormattedRepeated() {
        return isRepeated() ? "Every " + TaskIO.getFormattedInterval(interval) : "No";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;
        return interval == task.interval && active == task.active &&
                title.equals(task.title) && time.equals(task.time) &&
                start.equals(task.start) && end.equals(task.end);
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + time.hashCode();
        result = 31 * result + start.hashCode();
        result = 31 * result + end.hashCode();
        result = 31 * result + interval;
        result = 31 * result + (active ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", time=" + time +
                ", start=" + start +
                ", end=" + end +
                ", interval=" + interval +
                ", active=" + active +
                '}';
    }
}