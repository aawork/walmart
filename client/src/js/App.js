import '~/styles/app.scss';
import * as log from 'loglevel';
import Grid from '~/js/components/Grid';
import NavBar from '~/js/components/NavBar';
import ProductDetails from '~/js/components/ProductDetails';
import React, {Component} from "react";
import {withRouter} from "react-router-dom";

String.prototype.replaceAll = function(search, replacement) {
    var target = this;
    return target.replace(new RegExp(search, 'g'), replacement);
};

class App extends Component {

    constructor(props) {
        super();

        // log.enableAll()
        // log.setDefaultLevel("debug")

        log.disableAll()

        log.info("[APP] constructor", props)

        this.state = {
            product: null
        }
    }

    componentDidMount() {
        log.debug("[APP] componentDidMount", this.props)
    }

    componentWillUnmount() {
        log.debug("[APP] componentWillUnmount")
    }

    shouldComponentUpdate(props) {
        log.debug("[APP] shouldComponentUpdate", this.props.match.params, props)
        return true;
    }

    componentDidUpdate(prevProps, prevState, snapshot) {
        log.debug("[APP] componentDidUpdate", this.props)
    }

    search(query) {


        log.info(this.props)

        query = query.trim()

        // query = query.replaceAll(/\s/g, "+");

        query = query.replaceAll("%", "percent");   // TODO(AA): incomplete implementation (bug in react router)

        // query = query.replaceAll("#", '%23');

        log.debug("[APP] query:" + query)

        query = encodeURIComponent(query)

        log.info("[APP] current:" + this.props.location.pathname)
        log.info("[APP] query:" + query)
        log.info("[APP] props", this.props)

        if (this.query != query) {

            this.query = query

            this.props.history.push("/search/" + query)

            // history.push("#/search/" + query)

        } else {
            log.debug("ignore search request")
        }
    }

    parseQuery(query) {
        if (!query) {
            return null
        }

        // query = query.replaceAll("%25", "%")

        query = decodeURIComponent(query)

        // query = query.replaceAll("\\+", " ")

        // query = query.replaceAll("%23", "#")

        query = query.trim()

        log.warn("[APP] query: ", query)

        return query
    }

    render() {

        let query = this.parseQuery(this.props.query)

        let productId = this.props.id

        let product = this.state.product

        let productItem = product && product.id == productId ? product : null

        log.info("[APP] render Q:'" + this.props.query + "' ID:" + this.props.id, productItem)

        return (
            <div>
                <NavBar query={query} callback={this.search.bind(this)}/>

                {productId != null && (
                    <ProductDetails id={productId}
                                    item={productItem}
                                    openDetails={this.openDetails.bind(this)}
                                    onClose={() => this.closeDetails()}/>
                )}

                {productId == null && (
                    <Grid filter={query} openDetails={this.openDetails.bind(this)}/>
                )}
            </div>
        );
    }

    openDetails(item) {
        log.info("[APP] open details", item);

        this.setState((prevState, props) => {
            return {product: item};
        });
    }

    closeDetails() {
        this.setState({product: null})
    }
}

export default withRouter(App);