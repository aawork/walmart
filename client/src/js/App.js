import * as log from 'loglevel';

import React, {Component} from "react";

import {Route, Switch, withRouter} from "react-router-dom";

import NavBar from '~/js/components/NavBar';

import Grid from '~/js/components/Grid';

import ProductDetails from '~/js/components/ProductDetails';
import '~/styles/app.scss';

class App extends Component {

    constructor(props) {
        super();
        let params = props.match.params
        this.state = {
            query: params && params.query ? params.query : ''
        }
        log.enableAll()
        log.setDefaultLevel("debug")
        //log.disableAll()

        log.info("[APP] constructor", props.match)
    }

    static getDerivedStateFromProps(props, state) {

        let params = props.match.params
        if (params == null) {
            if (state.query != '') {
                log.debug("[APP] cleanup query")
                return {query: ''}
            }
        } else {
            if (params.query != state.query) {
                log.debug("[APP] set query: " + params.query)
                return {query: params.query}
            }
        }

        return null
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
        log.debug("[APP] query:" + query)

        if (this.state.query != query) {

            this.query = query;

            query = query.trim()

            query = query.replaceAll(/\s/g, "+");

            query = query.replace(/#/g, '%23');

            this.props.history.push("/search/" + encodeURI(query))
        }
    }

    parseQuery(query) {
        if (!query) {
            return null
        }
        query = query.replaceAll("\\+", " ")
        query = query.replaceAll("%23", "#")
        query = query.trim()
        return query
    }

    render() {

        let product = this.state.product

        return (
            <div>

                <NavBar query={this.props.match.params.query} callback={this.search.bind(this)}/>

                <Switch>

                    <Route path="/product/:id" exact={true} render={({match}) => {

                        log.debug("[APP] product:" + (product ? product.id : null) + " match:" + match.params.id)

                        let productId = match.params.id

                        let productItem = product && product.id == productId ? product : null

                        return (
                            <ProductDetails id={parseInt(match.params.id)}
                                            item={productItem}
                                            openDetails={this.openDetails.bind(this)}
                                            onClose={() => this.closeDetails()}/>);
                    }
                    }/>

                    <Route path="/search/:query" render={({match}) => {
                        let query = match.params.query;
                        let filter = this.parseQuery(query)
                        return (
                            <Grid filter={filter} openDetails={this.openDetails.bind(this)}/>
                        )
                    }}/>

                    <Route path="/" exact={true} render={({match}) => {
                        return (
                            <Grid openDetails={this.openDetails.bind(this)}/>
                        )
                    }}/>

                </Switch>
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

String.prototype.replaceAll = function (search, replacement) {
    var target = this;
    return target.replace(new RegExp(search, 'g'), replacement);
};