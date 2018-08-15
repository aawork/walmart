package walmart.web.generic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import walmart.model.generic.APIError;
import walmart.model.generic.APIException;

import javax.servlet.http.HttpServletRequest;

import static walmart.web.WebUtil.wrapError;

/**
 * Created by aawork on 8/13/18
 */
@RestController
public class ErrorController {

    private static final Logger log = LoggerFactory.getLogger(ErrorController.class);

    public static final String NOT_FOUND_MESSAGE = APIException.API_NOT_FOUND_MESSAGE;

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @RequestMapping("/error/404")
    public Object error404() {

        final APIError error = new APIError(APIException.NOT_FOUND_CODE, NOT_FOUND_MESSAGE);

        error.setStatusCode(HttpStatus.NOT_FOUND.value());

        return wrapError(error);
    }

    @ResponseStatus(code = HttpStatus.SERVICE_UNAVAILABLE)
    @RequestMapping("/error/503")
    public Object error503() {

        final APIError error = new APIError(APIException.SERVER_ERROR_CODE, "Service is not ready yet");

        error.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE.value());

        return wrapError(error);
    }

    @ResponseStatus(code = HttpStatus.GATEWAY_TIMEOUT)
    @RequestMapping("/error/504")
    public Object error504() {

        final APIError error = new APIError(APIException.SERVER_ERROR_CODE, "Service is not ready yet");

        error.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE.value());

        return wrapError(error);
    }


    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @RequestMapping("/error/other")
    public Object errorOther(HttpServletRequest request) throws Throwable {

        final APIError error = new APIError(APIException.SERVER_ERROR_CODE, "Internal server error");

        error.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

        return wrapError(error);
    }
}
