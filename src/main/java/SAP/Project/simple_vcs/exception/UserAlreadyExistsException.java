package SAP.Project.simple_vcs.exception;

public class UserAlreadyExistsException extends  RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}