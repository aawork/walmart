package walmart.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Created by aawork on 8/13/18
 */
@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Product {

    private Long id;
    private String name;

    private Float price;
    private Float rating;
    private Integer ratingsCount;
    private String description;
    private String imageURL;
    private String thumbnailURL;
    private String walmartURL;

    private List<Product> recommendations;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public Integer getRatingsCount() {
        return ratingsCount;
    }

    public void setRatingsCount(Integer ratingsCount) {
        this.ratingsCount = ratingsCount;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public void setRecommendations(List<Product> recommendations) {
        this.recommendations = recommendations;
    }

    public List<Product> getRecommendations() {
        return recommendations;
    }


    public void setWalmartURL(String walmartURL) {
        this.walmartURL = walmartURL;
    }

    public String getWalmartURL() {
        return walmartURL;
    }

}


