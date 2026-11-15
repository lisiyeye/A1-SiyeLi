/**
 * Outcome of one operation on the appointment collection.
 *
 * <p>The brief asks every failed operation to "return a warning message"
 * (part 5). Returning a small result object instead of printing inside the
 * manager keeps the manager free of console code, lets the caller decide how to
 * display the message, and makes the difference between success and failure
 * explicit in the type instead of being hidden in a boolean or a null.</p>
 */
public final class OperationResult {

    /** True when the operation completed. */
    private final boolean success;

    /** Message describing what happened. */
    private final String message;

    /**
     * Creates a result; use the factory methods instead of this constructor.
     *
     * @param success true when the operation completed
     * @param message description of the outcome
     */
    private OperationResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    /**
     * Creates a successful result carrying a confirmation message.
     *
     * @param message description of the successful operation, not blank
     * @return successful result
     * @throws IllegalArgumentException if the message is null or blank
     */
    public static OperationResult success(String message) {
        return new OperationResult(true, requireMessage(message));
    }

    /**
     * Creates a failed result carrying a warning message.
     *
     * @param message warning explaining why the operation could not be done, not blank
     * @return failed result carrying a warning message
     * @throws IllegalArgumentException if the message is null or blank
     */
    public static OperationResult warning(String message) {
        return new OperationResult(false, requireMessage(message));
    }

    /**
     * Validates and trims a result message.
     *
     * @param message candidate message
     * @return the trimmed message
     * @throws IllegalArgumentException if the message is null or blank
     */
    private static String requireMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("An operation result needs a message.");
        }
        return message.trim();
    }

    /**
     * Tells whether the operation completed.
     *
     * @return true when the operation completed
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Tells whether the operation failed.
     *
     * @return true when the operation failed and {@link #getMessage()} holds a warning
     */
    public boolean isWarning() {
        return !success;
    }

    /**
     * Returns the description of the outcome.
     *
     * @return description of the outcome
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return single line with a prefix that shows success or warning
     */
    @Override
    public String toString() {
        return (success ? "[OK]      " : "[WARNING] ") + message;
    }
}
