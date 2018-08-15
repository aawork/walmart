package walmart;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.regex.Pattern;

/**
 * Created by aawork on 8/12/18
 */
@ComponentScan(basePackages = {"walmart.services", "walmart.web"})
@Configuration
@EnableAsync
@EnableWebMvc
@PropertySource(value = {"classpath:/application.properties"}, ignoreResourceNotFound = false)
public class AppConfiguration {


    @Bean
    @Qualifier("allowCachePatterns")
    public Pattern[] allowCachePatterns() {
        return new Pattern[]{
                Pattern.compile("/product/[0-9a-f]{24}/logo/[0-9]{1,20}")
        };
    }

//    @Bean(name = "CacheControlFilter")
//    public CacheControlFilter cacheControlFilter() {
//        return new CacheControlFilter();
//    }

}
