import AbstractService from "~/js/services/AbstractService"

class SearchService extends AbstractService {

    search(query) {
        return this.post("api/products", {text: query})
    }

}

export default SearchService;