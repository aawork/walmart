package walmart.model.generic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Created by aawork on 8/13/18
 */
@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ListResponse<T> {

    private List<T> items;

    private int total;

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
