package pl.edu.agh.isi;

/**
 * Enum representing task priority levels
 */
public enum TaskPriority {
    LOW(1, "Low"),
    MEDIUM(2, "Medium"),
    HIGH(3, "High");
    
    private final int level;
    private final String displayName;
    
    TaskPriority(int level, String displayName) {
        this.level = level;
        this.displayName = displayName;
    }
    
    public int getLevel() {
        return level;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Get priority by level number
     * @param level the level number (1-3)
     * @return the corresponding priority or MEDIUM if invalid
     */
    public static TaskPriority fromLevel(int level) {
        for (TaskPriority priority : values()) {
            if (priority.level == level) {
                return priority;
            }
        }
        return MEDIUM; // Default to MEDIUM for invalid levels
    }
    
    /**
     * Get priority by level number as string
     * @param levelStr the level number as string
     * @return the corresponding priority or MEDIUM if invalid
     */
    public static TaskPriority fromLevelString(String levelStr) {
        try {
            int level = Integer.parseInt(levelStr);
            return fromLevel(level);
        } catch (NumberFormatException e) {
            return MEDIUM; // Default to MEDIUM for parsing errors
        }
    }
    
    /**
     * Get a symbol representation for the priority
     * @return a symbol indicating the priority level
     */
    public String getSymbol() {
        switch (this) {
            case LOW: return "!";
            case MEDIUM: return "!!";
            case HIGH: return "!!!";
            default: return "";
        }
    }
} 