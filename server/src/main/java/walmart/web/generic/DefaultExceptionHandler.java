package walmart.web.generic;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.util.NestedServletException;
import walmart.model.generic.APIError;
import walmart.model.generic.APIException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static walmart.web.WebUtil.wrapError;

/**
 * Created by aawork on 8/13/18
 */
@ControllerAdvice
public class DefaultExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(DefaultExceptionHandler.class);

    @Value("${DEBUG_MODE:false}")
    private Boolean debugMode;

    private static boolean debug = true;

    @PostConstruct
    protected void init() {
        debug = BooleanUtils.isTrue(debugMode);
    }

    @ExceptionHandler(APIException.class)
    @ResponseBody
    public Object handleApiException(APIException e, HttpServletRequest request, HttpServletResponse response) {

        final APIError error = processAPIError(e, request, response);

        return wrapError(error);
    }

    private APIError processAPIError(APIException e, HttpServletRequest request, HttpServletResponse response) {

        final APIError error = e.toError();

        final int code = error.getCode();

        if (code == APIException.SERVER_ERROR_CODE || e.getCause() != null) {
            log.error("APIError: {} : {}", code, e.getMessage(), e.getCause());
        } else if (code == APIException.BAD_REQUEST_CODE) {
            log.debug("APIError: {} : {}", code, e.getMessage(), e.getCause());
        } else if (debug) {
            log.info("APIError: {} : {}", code, e.getMessage(), e.getCause());
        } else {
            log.debug("APIError: {}:{}", code, e.getMessage());
        }

        return error;
    }

//
//    @ExceptionHandler(MongoCommandException.class)
//    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
//    @ResponseBody
//    public Object handleMongoCommandException(final MongoCommandException exception, final HttpServletResponse response) {
//
//        final Throwable cause = exception.getCause();
//        if (cause instanceof DuplicateKeyException) {
//            return processDBDuplicateException((DuplicateKeyException) cause);
//        }
//
//        final ErrorCategory errorCategory = ErrorCategory.fromErrorCode(exception.getErrorCode());
//        if (errorCategory == ErrorCategory.DUPLICATE_KEY) {
//            return processDBDuplicateException(exception);
//        }
//
//        log.warn("DB error", exception);
//
//        DefaultExceptionHandler.logErrorWithTrace(exception);
//
//        final APIError error = new APIError(APIException.SERVER_ERROR_CODE, "DB error");
//
//        error.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//
//        final Throwable trouble = exception.getCause();
//
//        log.error("{} : {} {} : {}", error.getCode(), error.getMessage(), trouble != null ? trouble.getMessage() : "", exception.getClass());
//
//        return WebUtil.wrapError(error);
//    }
//
//    @ExceptionHandler(DuplicateKeyException.class)
//    @ResponseStatus(value = HttpStatus.OK)
//    @ResponseBody
//    public Object handleDBDuplicateException(final DuplicateKeyException exception, final HttpServletResponse response) {
//        return processDBDuplicateException(exception);
//    }
//
//    private Object processDBDuplicateException(MongoServerException exception) {
//        final APIError error = new APIError(APIException.ALREADY_EXISTS_CODE, "Already exists");
//
//        DefaultExceptionHandler.logErrorWithTrace(exception);
//
//        log.warn("Duplicate error:{} code:{} cause:{}", exception.getMessage(), exception.getCode(), exception.getCause());
//
//        return WebUtil.wrapError(error);
//    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public Object handleValidationException(MethodArgumentNotValidException e, HttpServletResponse response) {
        log.debug("MethodArgumentNotValidException: {}", e.getMessage());

        final APIError error = new APIError(APIException.BAD_REQUEST_CODE, "Invalid request");

//        Map<String, Object> validationErrors = new HashMap<>();
//        for (ObjectError issue : e.getBindingResult().getAllErrors()) {
//            if (issue instanceof FieldError) {
//                validationErrors.put(((FieldError) issue).getField(), issue.getDefaultMessage());
//            } else {
//                validationErrors.put(issue.getObjectName(), issue.getDefaultMessage());
//            }
//        }

        // error.setInfo(validationErrors);

        return wrapError(error);
    }

    @ExceptionHandler({IllegalArgumentException.class, UnrecognizedPropertyException.class})
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public Object handleIllegalArgumentException(Exception e) {
        log.debug("handleIllegalArgumentException: {}", e.getMessage());

        final APIError error = new APIError(APIException.BAD_REQUEST_CODE, e.getMessage());

        return wrapError(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public Object handleNoBodyException(HttpMessageNotReadableException e, HttpServletResponse response) {
        log.debug("Invalid body : {}", e.getClass());

        String errorMessage = e.getMessage();

        if (StringUtils.startsWith(errorMessage, "Required request body is missing")) {
            errorMessage = "Request body is missing";
        } else {
            log.debug("{} : {} : {}", e.getClass(), e.getCause().getClass(), e.getCause().getMessage());
            errorMessage = "Invalid request, can not read request body";
            if (e.getCause() instanceof com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException) {
                UnrecognizedPropertyException cause = (UnrecognizedPropertyException) e.getCause();
                errorMessage = String.format("Invalid request, unrecognized field '%s'", cause.getPropertyName());
                // cause.getReferringClass().getSimpleName())
            } else if (e.getCause() instanceof JsonMappingException) {
                JsonMappingException cause = (JsonMappingException) e.getCause();
                List<JsonMappingException.Reference> path = cause.getPath();
                if (path != null) {
                    StringBuilder r = new StringBuilder();
                    for (int i = 0; i < path.size(); i++) {
                        JsonMappingException.Reference ref = path.get(i);
                        r.append(ref.getFieldName());
                        if (ref.getIndex() >= 0) {
                            r.append("[" + ref.getIndex() + "]");
                        }
                        if (i < path.size() - 1) {
                            r.append(".");
                        }
                    }
                    errorMessage = "Incorrect JSON format: " + r;
                }
            }
        }

        //TODO: fix all the smoke tests after uncomment of this and other occurrences of code 400
        // response.setStatus(HttpStatus.BAD_REQUEST.value());

        final APIError error = new APIError(APIException.BAD_REQUEST_CODE, errorMessage);

        this.debugError(error, e);

        return wrapError(error);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public Object handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {

        log.warn("Method {} is not one of the supported:{} for path:{}", e.getMethod(), e.getSupportedHttpMethods(), request.getPathInfo());

        final APIError error = new APIError(APIException.NOT_FOUND_CODE, e.getMethod() + " method is not supported");

        error.setStatusCode(HttpStatus.NOT_FOUND.value());

        return wrapError(error);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public Object handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request, HttpServletResponse response) {

        final APIError error = new APIError(APIException.NOT_FOUND_CODE, ErrorController.NOT_FOUND_MESSAGE);

        error.setStatusCode(HttpStatus.NOT_FOUND.value());

        return wrapError(error);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public Object handleMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException e, HttpServletRequest request, HttpServletResponse response) {
        log.error("MediaTypeNotAcceptable {}:{} : exception:{} nested:{}",
                request.getMethod(), request.getPathInfo(),
                e.getMessage(), e.getCause() == null ? null : e.getCause().getMessage());

        final APIError error = new APIError(APIException.BAD_REQUEST_CODE, e.getMessage());

        // response.setStatus(HttpStatus.BAD_REQUEST.value());

        return wrapError(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public Object handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request, HttpServletResponse response) {

        log.warn("MethodArgumentTypeMismatchException {} MismatchException: {} nested: {}",
                request.getPathInfo(),
                e.getMessage(), e.getCause() == null ? null : e.getCause().getMessage());

        final APIError error = new APIError(APIException.BAD_REQUEST_CODE, e.getMessage());

        this.debugError(error, e);

        Throwable cause = e.getCause();

        while (cause instanceof Exception) {
            error.setMessage(cause.getMessage());
            cause = cause.getCause();
        }

        // response.setStatus(HttpStatus.BAD_REQUEST.value());

        return wrapError(error);
    }

//    @ExceptionHandler({MongoSocketException.class, MongoTimeoutException.class})
//    @ResponseStatus(value = HttpStatus.OK)
//    @ResponseBody
//    public Object handleMongoConnectionException(final Throwable throwable,
//                                                 final HttpServletResponse httpServletResponse) {
//        log.debug(throwable.getMessage(), throwable);
//        final APIError apiError = new APIError();
//        apiError.setCode(APIException.SERVER_NOT_READY);
//
//        debugError(apiError, throwable);
//
//        apiError.setMessage(throwable.getMessage());
//
//        log.error(apiError.getMessage());
//
//        Throwable cause = throwable.getCause();
//
//        while (cause instanceof Exception) {
//            apiError.setMessage(cause.getMessage());
//            cause = cause.getCause();
//        }
//        //applicationStateDispatcher.fireApplicationStateEvent(false, null, null, null);
//        return wrapError(httpServletResponse, apiError);
//    }

    @ExceptionHandler(NestedServletException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Object handleNestedServletException(final NestedServletException exception, final HttpServletRequest request, final HttpServletResponse response) {

        final Throwable cause = exception.getCause();

        if (cause instanceof APIException) {
            return handleApiException((APIException) cause, request, response);
        }

        final APIError error = new APIError(APIException.SERVER_ERROR_CODE, "Internal server error");

        error.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

        debugError(error, exception);

        return wrapError(error);
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Object handleGenericException(final Throwable throwable, final HttpServletRequest request, final HttpServletResponse response) {

        log.error("GenericException {} Exception: {}:{}", request.getPathInfo(), throwable.getClass(), throwable.getMessage(), throwable);

        final APIError error = new APIError(APIException.SERVER_ERROR_CODE, "Internal server error");

        error.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

        debugError(error, throwable);

        return wrapError(error);
    }

    private void debugError(final APIError apiError, final Throwable throwable) {
        if (!debug) {
            return;
        }

        // log.debug("Error: {}, {}", apiError.getCode(), apiError.getMessage(), throwable);
    }
}