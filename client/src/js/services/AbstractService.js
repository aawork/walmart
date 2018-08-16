import * as log from "loglevel";

const defaultHeaders = {
    "Content-Type": "application/json; charset=utf-8"
}

class AbstractService {

    processResponse(response) {

        log.debug("[REST] response:", response);

        if (response.status != 200) {
            throw {code: response.status, message: response.statusText}
        }

        let json = response.json();

        return json.then((value => {

            log.debug("[REST] json:", value);

            let error = value.error

            if (error) {
                log.error("REST error:", error)
                throw error;
            }

            return value;
        }))
    }

    get(url) {
        let options = {
            method: 'GET',
            headers: defaultHeaders
        };

        return fetch(url, options).then(response => {
            return this.processResponse(response)
        });
    }

    post(url, body) {
        let options = {
            method: 'POST',
            body: JSON.stringify(body),
            headers: defaultHeaders
        };

        return fetch(url, options).then(response => {
            return this.processResponse(response)
        });
    }
}

export default AbstractService;