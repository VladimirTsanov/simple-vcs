package SAP.Project.simple_vcs.exception;

public class InvalidStatusTransitionException extends Exception {
    public InvalidStatusTransitionException(String message) {
        super(message);
    }
}
