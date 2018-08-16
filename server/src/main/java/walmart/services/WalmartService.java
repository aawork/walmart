package walmart.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import walmart.model.generic.APIException;
import walmart.model.walmart.WLItem;
import walmart.model.walmart.WLSearchResponse;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by aawork on 8/13/18
 */

@Service
public class WalmartService {

    private static final Logger log = LoggerFactory.getLogger(WalmartService.class);

    private static final RestTemplate rest = new RestTemplate();

    private static final ObjectMapper mapper = new ObjectMapper();

    @Value("${WL_BASE_URL}")
    private String baseURL = "http://api.walmartlabs.com/v1";

    @Value("${WL_API_KEY}")
    private String apiKey = "[walmartlabs API key]";

    @PostConstruct
    public void construct() {
        log.info("URL: {}", baseURL);
        log.info("KEY: {}", apiKey);
    }

    public WLSearchResponse search(String query) {

        if (query == null) {
            throw new IllegalStateException();
        }

        if (query.length() > 200) {
            throw APIException.badRequest("Query is too long");
        }

        query = query.replaceAll("#", "%23");

        final String url = url("/search?query=" + query);

        log.debug("url: {}", url);

        try {
            return rest.getForObject(url, WLSearchResponse.class);
        } catch (Exception ex) {
            throw APIException.serverError(ex);
        }
    }

    public WLSearchResponse trends() {
        final String url = url("/trends");

        log.debug("url: {}", url);

        return rest.getForObject(url, WLSearchResponse.class);
    }

    public List<WLItem> recommendations(Long id) {

        if (id == null) {
            throw new IllegalStateException();
        }

        final String url = url("/nbp?itemId=" + id);

        log.debug("url: {}", url);

        final Object response = rest.getForObject(url, Object.class);

        if (response instanceof Map) {
            Map errorData = (Map) response;
            log.debug("error data: {}", errorData);
            return Collections.emptyList();
        }

        if (!(response instanceof List)) {
            log.warn("incorrect response: {}", response);
            return Collections.emptyList();
        }

        final List list = (List) response;

        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        final List<WLItem> result = new ArrayList<>(list.size());

        for (Object entry : list) {
            if (entry instanceof Map) {
                final WLItem item = mapper.convertValue(entry, WLItem.class);
                if (item.getItemId() != null) {
                    result.add(item);
                }
            }
        }

        return result;
    }

    public WLItem getById(Long id) {

        if (id == null) {
            throw new IllegalStateException();
        }

        final String url = url("/items/" + id);

        log.debug("url: {}", url);

        try {
            return rest.getForObject(url, WLItem.class);
        } catch (Exception ex) {
            throw APIException.notFound();
        }
    }

    private String url(String path) {
        final char ch = path.indexOf('?') < 0 ? '?' : '&';
        return baseURL + path + ch + "format=json&apiKey=" + apiKey;
    }
}
