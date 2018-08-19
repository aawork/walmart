import AbstractService from "~/js/services/AbstractService";

class DetailsService extends AbstractService {

    loadDetails(id) {

        let url = "api/product/" + id;

        return this.get(url);
    }
}

export default DetailsService;