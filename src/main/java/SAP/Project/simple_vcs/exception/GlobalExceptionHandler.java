package SAP.Project.simple_vcs.exception;

import org.apache.coyote.Response;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AccessDeniedException.class)
    public org.springframework.web.servlet.ModelAndView handleAccessDeniedException(AccessDeniedException ex) {
        org.springframework.web.servlet.ModelAndView mav = new org.springframework.web.servlet.ModelAndView("error");
        mav.addObject("errorCode", "403 Forbidden");
        mav.addObject("errorMessage", "Access denied from the security system.");
        return mav;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public org.springframework.web.servlet.ModelAndView handleNotFound(ResourceNotFoundException ex) {
        org.springframework.web.servlet.ModelAndView mav = new org.springframework.web.servlet.ModelAndView("error");
        mav.addObject("errorCode", "404 Not Found");
        mav.addObject("errorMessage", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public org.springframework.web.servlet.ModelAndView handleUserAlreadyExists(UserAlreadyExistsException ex) {
        org.springframework.web.servlet.ModelAndView mav = new org.springframework.web.servlet.ModelAndView("error");
        mav.addObject("errorCode", "409 Conflict");
        mav.addObject("errorMessage", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(UserNotFoundException.class)
    public org.springframework.web.servlet.ModelAndView handleUserNotFound(UserNotFoundException ex) {
        org.springframework.web.servlet.ModelAndView mav = new org.springframework.web.servlet.ModelAndView("error");
        mav.addObject("errorCode", "404 Not Found");
        mav.addObject("errorMessage", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(VersionNotFoundException.class)
    public org.springframework.web.servlet.ModelAndView handleVersionNotFound(VersionNotFoundException ex) {
        org.springframework.web.servlet.ModelAndView mav = new org.springframework.web.servlet.ModelAndView("error");
        mav.addObject("errorCode", "404 Not Found");
        mav.addObject("errorMessage", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(DocumentNotFoundException.class)
    public org.springframework.web.servlet.ModelAndView handleDocumentNotFound(DocumentNotFoundException ex) {
        org.springframework.web.servlet.ModelAndView mav = new org.springframework.web.servlet.ModelAndView("error");
        mav.addObject("errorCode", "404 Not Found");
        mav.addObject("errorMessage", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public org.springframework.web.servlet.ModelAndView handleAll(Exception ex) {
        org.springframework.web.servlet.ModelAndView mav = new org.springframework.web.servlet.ModelAndView("error");
        mav.addObject("errorCode", "500 Internal Server Error");
        mav.addObject("errorMessage", "Something went wrong. Try again later: " + ex.getMessage());
        return mav;
    }

}