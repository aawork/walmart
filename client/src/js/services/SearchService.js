import * as log from "loglevel";

class SearchService {

    search(query) {
        let url = "api/products";
        let options = {
            method: 'POST',
            body: JSON.stringify({text: query}),
            headers: {
                "Content-Type": "application/json; charset=utf-8"
            }
        };

        return fetch(url, options).then(result => {
            return result.json();
        });
    }
}

export default SearchService;