package com.neb.exception;
/**
 * Custom exception class for handling file storage-related errors.
 * -- Thrown when an issue occurs during file upload, saving, or retrieval.--
 */
public class FileStorageException extends RuntimeException{
	 /**
     * Constructs a new FileStorageException with a specified message.
     */
	public FileStorageException(String message) {
        super(message);
    }
	
	/**
     * Parameterized Constructs a new FileStorageException with a specified message and cause.
     */
    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
