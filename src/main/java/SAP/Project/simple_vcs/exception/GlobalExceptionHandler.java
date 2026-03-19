package SAP.Project.simple_vcs.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
        return new ResponseEntity<>(
                Map.of("error", "403 Forbidden", "message", "Access denied from the security system."),
                HttpStatus.FORBIDDEN
        );
    }

//    @ExceptionHandler(AuthenticationException.class)
//    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex) {
//        return new ResponseEntity<>(
//                Map.of("error", "401 Unauthorized", "message", "Wrong username or password."),
//                HttpStatus.UNAUTHORIZED
//        );
//    }
}