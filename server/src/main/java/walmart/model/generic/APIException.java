package walmart.model.generic;

import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by aawork on 8/13/18
 */
public class APIException extends RuntimeException {

    public static final int BAD_REQUEST_CODE;

    public static final int NOT_FOUND_CODE;

    public static final int SERVER_ERROR_CODE;

    public static final Set<Integer> errorsCode = Collections.unmodifiableSet(Sets.newHashSet(
            BAD_REQUEST_CODE = 1,
            NOT_FOUND_CODE = 4,
            SERVER_ERROR_CODE = 5));

    public static final int DEFAULT_HTTP_STATUS_CODE = 200;

    public static final int SERVER_ERROR_HTTP_STATUS_CODE = 500;

    public static final String API_NOT_FOUND_MESSAGE = "API endpoint is not found";

    private int code;

    private int httpCode = DEFAULT_HTTP_STATUS_CODE;

    public final APIError toError() {
        APIError error = new APIError(this.code, this.getMessage());
        error.setStatusCode(this.httpCode);
        return error;
    }

    private void check() {
        if (errorsCode.contains(code) && (SERVER_ERROR_CODE != code)) {
            final Map<String, Object> info = new HashMap<>(2, 1f);
            info.put("code", code);
            info.put("message", this.getMessage());
            throw new APIException(SERVER_ERROR_CODE, "Do not use reserved system error codes", DEFAULT_HTTP_STATUS_CODE);
        }
    }

    private APIException(int code, String message, int httpCode, Throwable ex) {
        super(message, ex);
        this.code = code;
        this.httpCode = httpCode;
    }

    public APIException(Throwable cause) {
        this("Server error", cause);
    }

    public APIException(String message, Throwable cause) {
        this(SERVER_ERROR_CODE, message, SERVER_ERROR_HTTP_STATUS_CODE, cause);
    }

    public APIException(int code, String message) {
        this(code, message, DEFAULT_HTTP_STATUS_CODE, null);
        check();
    }

    public APIException(int code, String message, int httpCode) {
        this(code, message, httpCode, null);
        check();
    }

    private static APIException exception(int code, String message) {
        return new APIException(code, message, DEFAULT_HTTP_STATUS_CODE, null);
    }

    public int getCode() {
        return code;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public static APIException badRequest(String message) {
        return exception(BAD_REQUEST_CODE, message);
    }

    public static APIException serverError(String message) {
        return new APIException(SERVER_ERROR_CODE, message, SERVER_ERROR_HTTP_STATUS_CODE, null);
    }

    public static APIException serverError(Exception ex) {
        return new APIException(SERVER_ERROR_CODE, ex.getMessage(), SERVER_ERROR_HTTP_STATUS_CODE, null);
    }

    public static APIException notFound() {
        return exception(NOT_FOUND_CODE, "Not found");
    }
}
