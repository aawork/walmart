import * as log from 'loglevel';
import React, {Component} from "react";
import PropTypes from 'prop-types';

import LoadState from '~/js/model/LoadState';
import Product from '~/js/components/Product';

import SearchService from "~/js/services/SearchService"

import Loading from '~/js/components/Loading';

// import LoadingIcon from '~/images/loading.svg';
// import ErrorIcon from '~/images/error.svg';
// import EmptyIcon from '~/images/empty.svg';

class Grid extends Component {

    constructor(props) {
        super(props);
        this.service = new SearchService();

        this.state = {
            data: null,
            //filter: props.filter,
            status: new LoadState()
        }

        this.filter = props.filter

        // this.state.status.start('test');
        // this.state.status.onError({code: 1, message: "failed to load any data"});
        // this.state.status.onComplete();

        log.debug("[GRID] constructor", props, this.state)

        this.reloadData(props, true);
    }

    // static getDerivedStateFromProps(props, state) {
    //     if(state.filter == props.filter) {
    //         return null;
    //     }
    //     state.filter = props.filter;
    //     return state;
    // }

    isDataLoadRequired(newProps) {
        //log.debug("[GRID] isDataLoadRequired: ", newProps);
        //log.debug("[GRID] isDataLoadRequired: " + this.state.filter+" vs "+newProps.filter);
        let filter = newProps.filter;

        let result = !this.filter && !filter ? false : this.filter != filter;

        log.debug("[GRID] isDataLoadRequired: " + this.filter + " vs " + filter + " -> " + result);

        return result
    }

    reloadData(props, force) {

        if (!force && !this.isDataLoadRequired(props)) {
            log.debug("[GRID] data load skipped", props)
            return
        }

        let filter = trim(props.filter)

        log.debug("[GRID] reload data for filter:", filter)

        this.state.status.start(!filter ? "Loading Trends..." : "Searching for " + filter + " ...");

        this.filter = filter

        this.service.search(filter).then(response => {

            let data = response.result;

            //TODO: compare data.query with this.filter and skip

            if (data && data.items && data.items.length) {
                this.state.status.onComplete();
            } else {
                status.onEmpty(this.emptyMessage())
            }
            log.info("[GRID] data", data)
            this.setState({data: data});
        }).catch(error => {
            //this.setState({data: null});
            this.state.status.onError(error);
        });

        // this.setState({filter:filter})
    }

    shouldComponentUpdate(props) {
        log.debug("[GRID] shouldComponentUpdate", props)
        this.reloadData(props);
        return true;
    }

    emptyMessage() {
        return this.filter ? "No trends found" : "Nothing found for '" + this.filter + "'"
    }

    render() {

        let status = this.state.status

        let data = this.state.data

        // let noData = !data || !data.items

        if (!status.isComplete()) {
            log.debug("[GRID] render status:", status)
            return (<Loading state={status}/>);
        }

        log.debug("[GRID] render data:", data)

        let products = [];
        for (var i = 0; i < data.items.length; i++) {
            let item = data.items[i];
            products.push(<Product key={item.id} item={item} onClick={this.props.openDetails}/>);
        }

        return (
            <div className="grid">
                {products}
            </div>
        );
    }
}

const trim = (s) => {
    if (!s) return null;
    s = s.trim();
    return s.length == 0 ? null : s;
};


Grid.propTypes = {
    filter: PropTypes.string,
    openDetails: PropTypes.func.isRequired
};


export default Grid;