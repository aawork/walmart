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
        this.state = {}
        log.enableAll()
        log.setDefaultLevel("debug")
        //log.disableAll()

        log.info(props.match)
    }

    static getDerivedStateFromProps(props, state) {
        log.debug("[APP] getDerivedStateFromProps", props.match)
        return state
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
        log.debug("componentDidUpdate", this.props)
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
        if(!query) {
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

                    <Route path="/product/:id" exact={true} render={({match}) => (
                        <ProductDetails id={parseInt(match.params.id)}
                                        item={product}
                                        openDetails={this.openDetails.bind(this)}
                                        onClose={() => this.closeDetails()}/>
                    )}/>

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
        log.info("open details", item);
        //this.setState({product:item})

        this.setState((prevState, props) => {
            return {product: item};
        });
    }

    closeDetails() {
        this.setState({product: null})
    }
}

export default withRouter(App);

String.prototype.replaceAll = function(search, replacement) {
    var target = this;
    return target.replace(new RegExp(search, 'g'), replacement);
};