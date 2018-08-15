class DetailsService {

    loadDetails(id) {
        let url = "api/product/"+id;
        let options = {
            method: 'GET',
            headers: {
                "Content-Type": "application/json; charset=utf-8"
            }
        };

        return fetch(url, options).then(result => {
            return result.json();
        });
    }
}

export default DetailsService;