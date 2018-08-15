package walmart.web.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by aawork on 8/12/18
 */

public abstract class BaseFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(BaseFilter.class);

    private PathMatcher pathMatcher = new AntPathMatcher();

    private List<String> exclude;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            doFilterUnsafe(request, response, filterChain);
        } catch (Exception e) {
            request.setAttribute("javax.servlet.error.exception", e);
            request.setAttribute("javax.servlet.error.message", e.getMessage());
            try {
                request.getRequestDispatcher("/api/error/other").forward(request, response);
            } catch (Exception ex) {
                log.error("failed to forward", ex);
            }
        }
    }

    protected abstract void doFilterUnsafe(HttpServletRequest request,
                                           HttpServletResponse response,
                                           FilterChain filterChain) throws Exception;


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (pathMatcher == null || exclude == null) {
            return false;
        }
        String path = request.getServletPath() + request.getPathInfo();
        for (String pattern : exclude) {
            if (pathMatcher.match(pattern, path)) {
                log.debug("Do not filter pattern: {}, path: {}", pattern, path);
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return true;
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return true;
    }

    public PathMatcher getPathMatcher() {
        return pathMatcher;
    }

    public void setPathMatcher(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
    }

    public List<String> getExclude() {
        return exclude;
    }

    public void setExclude(List<String> exclude) {
        this.exclude = exclude;
    }
}
