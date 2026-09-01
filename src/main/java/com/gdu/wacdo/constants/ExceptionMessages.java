package com.gdu.wacdo.constants;

public final class ExceptionMessages {

    private ExceptionMessages() {}

    public static final String ADMIN_ALREADY_EXISTS = "Initial administrator already exists.";

    // RESOURCE NOT FOUND
    public static final String COLLABORATOR_NOT_FOUND = "No collaborator found with id : ";
    public static final String RESTAURANT_NOT_FOUND = "No restaurant found with id : ";
    public static final String JOB_NOT_FOUND = "No job found with id : ";
    public static final String ASSIGNMENT_NOT_FOUND = "No assignment found with id : ";

    // AUTHENTICATION
    public static final String NO_COLLABORATOR_WITH_EMAIL = "No collaborator found with email : ";
    public static final String WRONG_EMAIL_OR_PASSWORD = "Incorrect email or password";
    public static final String MUST_LOG_TO_ACCESS = "You must be logged in to access this resource.";
    public static final String NOT_ENOUGH_PRIVILEGE = "You do not have the necessary permissions to access this resource.";
    public static final String INVALID_JWT_TOKEN = "Invalid JWT token.";
    public static final String EXPIRED_JWT_TOKEN = "JWT token has expired.";
}
// TODO See to make a message.properties