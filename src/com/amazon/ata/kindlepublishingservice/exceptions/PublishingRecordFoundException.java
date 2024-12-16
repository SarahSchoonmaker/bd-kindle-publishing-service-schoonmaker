package com.amazon.ata.kindlepublishingservice.exceptions;

public class PublishingRecordFoundException extends RuntimeException {

    private static final long serialVersionUID = -8464012968476109262L;

    public PublishingRecordFoundException(String message) {
        super(message);
    }

    /**
     * Exception with message and cause.
     * @param message A descriptive message for this exception.
     * @param cause The original throwable resulting in this exception.
     */
    public PublishingRecordFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
