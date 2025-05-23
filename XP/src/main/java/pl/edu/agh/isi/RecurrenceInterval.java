package pl.edu.agh.isi;

/**
 * Enum representing task recurrence intervals
 */
public enum RecurrenceInterval {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly");
    
    private final String displayName;
    
    RecurrenceInterval(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Get interval from string name (case-insensitive)
     * @param name the string name of the interval
     * @return the matching interval or null if not found
     */
    public static RecurrenceInterval fromString(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
} 