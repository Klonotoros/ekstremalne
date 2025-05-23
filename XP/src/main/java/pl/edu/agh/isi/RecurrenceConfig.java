package pl.edu.agh.isi;

import java.time.LocalDateTime;

/**
 * Configuration for recurring tasks
 */
public class RecurrenceConfig {
    private RecurrenceInterval interval;
    private int occurrences;
    private LocalDateTime endDate;
    
    // Default constructor for Jackson
    public RecurrenceConfig() {
    }
    
    /**
     * Create a recurrence config with a specified interval and unlimited occurrences
     * @param interval the recurrence interval (daily, weekly, monthly)
     */
    public RecurrenceConfig(RecurrenceInterval interval) {
        this.interval = interval;
        this.occurrences = -1; // unlimited
        this.endDate = null;
    }
    
    /**
     * Create a recurrence config with a specified interval and number of occurrences
     * @param interval the recurrence interval (daily, weekly, monthly)
     * @param occurrences the number of recurrences (-1 for unlimited)
     */
    public RecurrenceConfig(RecurrenceInterval interval, int occurrences) {
        this.interval = interval;
        this.occurrences = occurrences;
        this.endDate = null;
    }
    
    /**
     * Create a recurrence config with a specified interval and end date
     * @param interval the recurrence interval (daily, weekly, monthly)
     * @param endDate the date after which no new tasks should be created
     */
    public RecurrenceConfig(RecurrenceInterval interval, LocalDateTime endDate) {
        this.interval = interval;
        this.occurrences = -1; // unlimited
        this.endDate = endDate;
    }
    
    /**
     * Create a recurrence config with a specified interval, occurrences, and end date
     * @param interval the recurrence interval (daily, weekly, monthly)
     * @param occurrences the number of recurrences (-1 for unlimited)
     * @param endDate the date after which no new tasks should be created
     */
    public RecurrenceConfig(RecurrenceInterval interval, int occurrences, LocalDateTime endDate) {
        this.interval = interval;
        this.occurrences = occurrences;
        this.endDate = endDate;
    }
    
    public RecurrenceInterval getInterval() {
        return interval;
    }
    
    public void setInterval(RecurrenceInterval interval) {
        this.interval = interval;
    }
    
    public int getOccurrences() {
        return occurrences;
    }
    
    public void setOccurrences(int occurrences) {
        this.occurrences = occurrences;
    }
    
    public LocalDateTime getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
    
    /**
     * Check if the recurrence is unlimited (no end date and no occurrence limit)
     * @return true if unlimited, false otherwise
     */
    public boolean isUnlimited() {
        return occurrences == -1 && endDate == null;
    }
    
    /**
     * Check if the recurrence is still valid based on current date and occurrence count
     * @param currentDate the current date
     * @param currentOccurrence the current occurrence number (1-based)
     * @return true if the recurrence should continue, false otherwise
     */
    public boolean isStillValid(LocalDateTime currentDate, int currentOccurrence) {
        if (occurrences != -1 && currentOccurrence >= occurrences) {
            return false;
        }
        
        if (endDate != null && currentDate.isAfter(endDate)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Calculate the next due date based on current due date and interval
     * @param currentDueDate the current due date
     * @return the next due date based on the recurrence interval
     */
    public LocalDateTime calculateNextDueDate(LocalDateTime currentDueDate) {
        if (currentDueDate == null) {
            return null;
        }
        
        switch (interval) {
            case DAILY:
                return currentDueDate.plusDays(1);
            case WEEKLY:
                return currentDueDate.plusWeeks(1);
            case MONTHLY:
                return currentDueDate.plusMonths(1);
            default:
                return null;
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(interval.getDisplayName());
        
        if (occurrences != -1) {
            sb.append(", ").append(occurrences).append(" occurrences");
        }
        
        if (endDate != null) {
            sb.append(", until ").append(endDate);
        }
        
        return sb.toString();
    }
} 