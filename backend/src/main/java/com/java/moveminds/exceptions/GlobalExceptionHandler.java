package com.java.moveminds.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import com.java.moveminds.dto.response.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Handle BadCredentialsException
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(HttpServletRequest request, BadCredentialsException exception) {
        return buildErrorResponse(request, HttpStatus.UNAUTHORIZED, "The credentials are incorrect.", exception);
    }

    // Handle AccountStatusException
    @ExceptionHandler(AccountStatusException.class)
    public ResponseEntity<ErrorResponse> handleAccountStatusException(HttpServletRequest request, AccountStatusException exception) {
        return buildErrorResponse(request, HttpStatus.FORBIDDEN, "Account blocked", exception);
    }

    // Handle AccessDeniedException
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(HttpServletRequest request, AccessDeniedException exception) {
        return buildErrorResponse(request, HttpStatus.FORBIDDEN, "You do not have permission to access this resource.", exception);
    }

    // Handle JWT Exceptions
    @ExceptionHandler({SignatureException.class, ExpiredJwtException.class})
    public ResponseEntity<ErrorResponse> handleJwtException(HttpServletRequest request, Exception exception) {
        String message = exception instanceof SignatureException ? "JWT token is not valid" : "JWT token has expired";
        return buildErrorResponse(request, HttpStatus.UNAUTHORIZED, message, exception);
    }

    // Handle IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(HttpServletRequest request, IllegalArgumentException exception) {
        return buildErrorResponse(request, HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
    }

    // Handle Custom Exceptions
    @ExceptionHandler({
            UserAlreadyExistsException.class,
            UserNotFoundException.class,
            InvalidTokenException.class,
            EmailSendException.class,
            AccountActivationException.class,
            InvalidOldPasswordException.class,
            RssFeedException.class,
            ExerciseFetchException.class,
            CategoryNotFoundException.class,
            LocationNotFoundException.class,
            CategoryAlreadyExistsException.class,
            LocationAlreadyExistsException.class,
            ProgramAlreadyExistsException.class,
            ImageUploadException.class,
            AttributeValueNotFoundException.class,
            ProgramNotFoundException.class,
            UnauthorizedAccessException.class
    })
    public ResponseEntity<ErrorResponse> handleCustomExceptions(HttpServletRequest request, Exception exception) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (exception instanceof UserAlreadyExistsException ||
                exception instanceof CategoryAlreadyExistsException ||
                exception instanceof LocationAlreadyExistsException ||
                exception instanceof ProgramAlreadyExistsException) {
            status = HttpStatus.CONFLICT;
        } else if (exception instanceof UserNotFoundException ||
                exception instanceof CategoryNotFoundException ||
                exception instanceof LocationNotFoundException ||
                exception instanceof AttributeValueNotFoundException ||
                exception instanceof ProgramNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else if (exception instanceof InvalidTokenException ||
                exception instanceof AccountActivationException ||
                exception instanceof InvalidOldPasswordException) {
            status = HttpStatus.BAD_REQUEST;
        } else if (exception instanceof UnauthorizedAccessException) {
            status = HttpStatus.UNAUTHORIZED;
        }

        return buildErrorResponse(request, status, exception.getMessage(), exception);
    }

    // Handle MethodArgumentTypeMismatchException
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(HttpServletRequest request, MethodArgumentTypeMismatchException exception) {
        String message = "Invalid parameter: " + exception.getName();
        return buildErrorResponse(request, HttpStatus.NOT_FOUND, message, exception);
    }

    // Handle HttpRequestMethodNotSupportedException
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupportedException(HttpServletRequest request, HttpRequestMethodNotSupportedException exception) {
        String message = "The requested method '" + exception.getMethod() + "' not held";
        return buildErrorResponse(request, HttpStatus.METHOD_NOT_ALLOWED, message, exception);
    }

    // Handle all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(HttpServletRequest request, Exception exception) {
        return buildErrorResponse(request, HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while processing the request.", exception);
    }

    // Helper method to build error response
    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpServletRequest request, HttpStatus status, String message, Exception exception) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(status.value());
        errorResponse.setError(status.getReasonPhrase());
        errorResponse.setMessage(message);
        errorResponse.setPath(request.getRequestURI());

        if (status.is5xxServerError()) {
            logger.error("{}: {}", status.getReasonPhrase(), exception.getMessage());
        } else {
            logger.warn("{}: {}", status.getReasonPhrase(), exception.getMessage());
        }

        return ResponseEntity
                .status(status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(errorResponse);
    }
}
