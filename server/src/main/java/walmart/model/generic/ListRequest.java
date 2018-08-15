package walmart.model.generic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by aawork on 8/13/18
 */
@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ListRequest {

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
