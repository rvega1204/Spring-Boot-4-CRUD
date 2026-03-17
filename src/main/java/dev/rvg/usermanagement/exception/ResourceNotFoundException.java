package dev.rvg.usermanagement.exception;

/**
 * Exception thrown when a requested resource is not found.
 * Maps to HTTP 404 status automatically.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

}
