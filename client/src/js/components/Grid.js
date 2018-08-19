import * as log from 'loglevel';
import React, {Component} from "react";
import PropTypes from 'prop-types';

import LoadState from '~/js/model/LoadState';
import Product from '~/js/components/Product';

import SearchService from "~/js/services/SearchService";

import Loading from '~/js/components/Loading';

class Grid extends Component {

    constructor(props) {
        super(props);
        this.service = new SearchService();

        this.state = {
            data: null,
            status: new LoadState()
        };

        this.filter = props.filter;

        log.debug("[GRID] constructor", props, this.state);

        this.reloadData(props, true);
    }

    isDataLoadRequired(newProps) {

        let filter = newProps.filter;

        let result = !this.filter && !filter ? false : this.filter !== filter;

        log.debug("[GRID] isDataLoadRequired: " + this.filter + " vs " + filter + " -> " + result);

        return result;
    }

    reloadData(props, force) {

        if (!force && !this.isDataLoadRequired(props)) {
            log.debug("[GRID] data load skipped", props);
            return;
        }

        let filter = trim(props.filter);

        log.debug("[GRID] reload data for filter:", filter);

        this.state.status.start(!filter ? "Loading trends . . ." : "Please wait . . .");

        this.filter = filter;

        this.service.search(filter).then(response => {

            let data = response.result;

            if (data && data.items && data.items.length) {
                this.state.status.onComplete();
            } else {
                this.state.status.onEmpty(this.emptyMessage());
            }
            log.info("[GRID] data", data);
            this.setState({data: data});
        }).catch(error => {
            log.error("[GRID] error", error);
            this.state.status.onError(error);
            this.setState({
                data: null,
                status: this.state.status
            });
        });

    }

    shouldComponentUpdate(props) {
        log.debug("[GRID] shouldComponentUpdate", props);
        this.reloadData(props);
        return true;
    }

    emptyMessage() {
        return !this.filter ? "No trends found" : "Nothing found";
    }

    render() {

        let status = this.state.status;

        let data = this.state.data;

        if (!status.isComplete()) {
            log.debug("[GRID] render status:", status);
            return (<Loading state={status}/>);
        }

        log.debug("[GRID] render data:", data);

        let products = [];
        for (let i = 0; i < data.items.length; i++) {
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
    return s.length === 0 ? null : s;
};


Grid.propTypes = {
    filter: PropTypes.string,
    openDetails: PropTypes.func.isRequired
};


export default Grid;