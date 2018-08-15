package walmart.web.filters;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by aawork on 8/12/18
 */
@Component("CacheControlFilter")
public final class CacheControlFilter extends BaseFilter {

    // with hash-based client build html/js/css could be removed
    private Set<String> noCacheUrls = Sets.newHashSet("/", "/index.html", "/main.js", "/main.css", "/api");

    @Autowired
    @Qualifier("allowCachePatterns")
    private Pattern[] allowCachePatterns;

    @Override
    protected void doFilterUnsafe(final HttpServletRequest request, final HttpServletResponse httpServletResponse, final FilterChain filterChain)
            throws Exception {
        String servletPath = request.getServletPath().toLowerCase();

        if (noCacheUrls.contains(servletPath) && !cacheAllowed(request)) {
            httpServletResponse.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
            httpServletResponse.setHeader(HttpHeaders.PRAGMA, "no-cache");
            httpServletResponse.setHeader(HttpHeaders.EXPIRES, "0");
        }
        filterChain.doFilter(request, httpServletResponse);
    }

    private boolean cacheAllowed(final HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            return true;
        }
        pathInfo = pathInfo.toLowerCase();
        for (Pattern p : allowCachePatterns) {
            if (p.matcher(pathInfo).matches()) {
                return true;
            }
        }
        return false;
    }
}