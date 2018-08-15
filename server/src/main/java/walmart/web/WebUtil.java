package walmart.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import walmart.model.generic.APIError;
import walmart.model.generic.APIException;

import java.util.LinkedHashMap;
import java.util.Map;

public final class WebUtil {

    public static final String ERROR_WRAPPER = "error";

    public static final String API_ERROR_HEADER = "apiError";

    public static Map<String, Object> wrapResult(String container, Object result) {
        final Map<String, Object> map = new LinkedHashMap<>();
        if (result == null) {
            throw new IllegalArgumentException("Null result");
        }
        map.put(container, result);

        return map;
    }

    public static Map<String, Object> wrapResult(Object result) {
        if (result == null) {
            throw APIException.serverError("Not implemented");
        }
        return wrapResult("result", result);
    }

    public static ResponseEntity wrapError(APIError error) {

        final HttpHeaders headers = new HttpHeaders();

        headers.add(API_ERROR_HEADER, String.valueOf(error.getCode()));

        headers.setContentType(MediaType.APPLICATION_JSON);

        final HttpStatus status = HttpStatus.valueOf(error.getStatusCode());

        final Object result = wrapResult(ERROR_WRAPPER, error);

        return new ResponseEntity<>(result, headers, status);
    }

}