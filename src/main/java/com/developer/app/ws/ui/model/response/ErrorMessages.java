package com.developer.app.ws.ui.model.response;

import lombok.Getter;

@Getter
public enum ErrorMessages {

    MISSING_REQUIRED_FIELD("Missing required field. Please check documentation for required fields"),
    RECORD_ALREADY_EXISTS("Record already exists"),
    INTERNAL_SERVER_ERROR("Internal server error"),
    NO_RECORD_FOUND("Record with provided id is not found"),
    AUTHENTICATION_FAILED("Authetication failed"),
    COULD_NOT_UPDATE_RECORD("Could not update record"),
    COULD_NOT_DELETE_RECORD("Could not delete record"),
    EMAIL_ADDRESS_NOT_VERIFIED("Email address could not be verified");

    /**
     * -- GETTER --
     *
     * @return the errorMessage
     */
    private String errorMessage;

    ErrorMessages(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
