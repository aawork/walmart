package walmart.services;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import walmart.model.Product;
import walmart.model.generic.APIException;
import walmart.model.generic.ListRequest;
import walmart.model.generic.ListResponse;
import walmart.model.walmart.WLImage;
import walmart.model.walmart.WLItem;
import walmart.model.walmart.WLSearchResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by aawork on 8/13/18
 */
@Service
public class ProductService {

    private static final int MAX_RECOMMENDATIONS_COUNT = 10;

    @Autowired
    private WalmartService service;

    private LoadingCache<String, ListResponse<Product>> searchCache = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.MINUTES)
            .maximumSize(50000)
            .build(new CacheLoader<String, ListResponse<Product>>() {
                @Override
                public ListResponse<Product> load(String text) {
                    return find(text);
                }
            });

    private LoadingCache<Long, Product> detailsCache = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .maximumSize(100000)
            .build(new CacheLoader<Long, Product>() {
                @Override
                public Product load(Long id) {
                    return loadProduct(id);
                }
            });

    private LoadingCache<String, ListResponse<Product>> trendsCache = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .maximumSize(1)
            .build(new CacheLoader<String, ListResponse<Product>>() {
                @Override
                public ListResponse<Product> load(String text) {
                    return getCurrentTrends();
                }
            });


    public Product loadDetails(Long id) {
        if (id == null) {
            throw APIException.badRequest("missed ID");
        }

        return getFromCache(detailsCache, id);
    }

    public ListResponse<Product> search(ListRequest request) {

        if (request == null) {
            throw APIException.badRequest("missed request object");
        }

        final String query = validateNormalizeQuery(request.getText());

        if (query == null) {
            // key 'TRENDS' could be replaced/used for trends in different product categories
            return getFromCache(trendsCache, "TRENDS");
        }

        return getFromCache(searchCache, query);
    }

    private <K, R> R getFromCache(LoadingCache<K, R> cache, K key) {
        try {
            return cache.getUnchecked(key);
        } catch (Exception ex) {
            if (!(ex.getCause() instanceof APIException)) {
                throw APIException.serverError(ex);
            }
            throw APIException.notFound("Product is not found");
        }
    }

    public List<Product> getRecommendations(Long id) {
        List<WLItem> recommendations = service.recommendations(id);

        if (recommendations.size() > MAX_RECOMMENDATIONS_COUNT) {
            recommendations = recommendations.subList(0, MAX_RECOMMENDATIONS_COUNT);
        }

        return convertToProducts(recommendations);
    }

    private Product loadProduct(Long id) {
        final WLItem item = service.getById(id);
        final Product product = convert(item);
        product.setRecommendations(getRecommendations(id));
        return product;
    }

    private ListResponse<Product> getCurrentTrends() {
        return convert(service.trends());
    }

    private ListResponse<Product> find(String query) {

        if (StringUtils.isEmpty(query)) {
            throw APIException.badRequest("Query parameter missed");
        }

        return convert(service.search(query));
    }

    private ListResponse<Product> convert(WLSearchResponse wlResponse) {
        final ListResponse<Product> result = new ListResponse();
        result.setItems(convertToProducts(wlResponse.getItems()));
        result.setTotal(wlResponse.getTotalResults());

        return result;
    }

    private String validateNormalizeQuery(String query) {

        if (query == null) {
            return null;
        }

        query = query.replaceAll("#", "%23");

        query = query.replaceAll("&", " ");

        query = StringUtils.trim(query);

        if (query.length() == 0) {
            return null;
        }

        return query;
    }

    private static List<Product> convertToProducts(List<WLItem> items) {

        if (CollectionUtils.isEmpty(items)) {
            return Collections.emptyList();
        }

        final List<Product> result = new ArrayList<>(items.size());

        for (WLItem item : items) {
            result.add(convert(item));
        }

        return result;
    }

    private static Product convert(WLItem item) {
        final Product product = new Product();
        product.setId(item.getItemId());
        product.setName(item.getName());
        product.setDescription(StringEscapeUtils.unescapeHtml4(item.getLongDescription()));
        product.setPrice(item.getSalePrice());
        product.setRating(item.getCustomerRating());
        product.setRatingsCount(item.getNumReviews());
        product.setThumbnailURL(item.getLargeImage());

        product.setWalmartURL(item.getProductUrl());

        String imageURL = item.getLargeImage();
        if (CollectionUtils.isNotEmpty(item.getImageEntities())) {
            for (WLImage image : item.getImageEntities()) {
                if ("PRIMARY".equalsIgnoreCase(image.getEntityType())) {
                    imageURL = image.getLargeImage();
                    break;
                }
            }
        }
        product.setImageURL(imageURL);

        return product;
    }


}
