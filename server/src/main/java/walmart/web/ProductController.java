package walmart.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import walmart.model.Product;
import walmart.model.generic.ListRequest;
import walmart.services.ProductService;

import java.util.List;

/**
 * Created by aawork on 8/13/18
 */
@RestController
public class ProductController {

    @Autowired
    private ProductService service;

    @PostMapping("/products")
    public Object search(@RequestBody ListRequest request) {
        return WebUtil.wrapResult(service.search(request));
    }

    @GetMapping("/product/{id}")
    public Object details(@PathVariable("id") Long id) {

        final Product result = service.loadDetails(id);
        return WebUtil.wrapResult(result);
    }

    @GetMapping("/product/{id}/recommendations")
    public Object recommendations(@PathVariable("id") Long id) {
        final List<Product> result = service.getRecommendations(id);
        return WebUtil.wrapResult(result);
    }

}
