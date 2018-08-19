import * as log from 'loglevel';
import PropTypes from 'prop-types';
import React, {Component} from "react";

import SearchIcon from '~/images/search.svg';
import ClearIcon from '~/images/clear.svg';

class SearchInput extends Component {

    constructor(props) {
        super(props);
        this.state = {value: props.query ? props.query : ''};
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.handleCleanup = this.handleCleanup.bind(this);
    }

    static getDerivedStateFromProps(props, state) {
        let original = props.query ? props.query : '';
        if(props.query != state.query && state.original != original) {
            return  {
                original: original,
                value: original
            };
        }

        return null;
    }

    handleChange(event) {
        this.setState({value: event.target.value});
    }

    handleSubmit(event) {
        // log.debug("[Search] handleSubmit", event)
        event.preventDefault();
        let q = this.state.value.trim();
        if (q.length > 0) {
            this.props.handleSearch(this.state.value);
        }
    }

    handleCleanup(event) {
        log.info("cleanup");
        event.preventDefault();
        this.setState({value: ''});
    }

    render() {
        log.debug("render SearchInput");
        return (
            <div className="searchInput">
                <form onSubmit={this.handleSubmit}>
                    <button className="close"></button>
                    <div className="inputContainer">
                        <input
                            autofocus="true"
                            maxLength="200"
                            name="search"
                            onChange={this.handleChange.bind(this)}
                            placeholder="Search..."
                            type="text"
                            value={this.state.value}/>
                    </div>
                    <button type="submit" className="search">
                        <SearchIcon/>
                    </button>
                    <button className="clear" onClick={this.handleCleanup.bind(this)}>
                        <ClearIcon/>
                    </button>
                </form>
            </div>
        );
    }
}

SearchInput.propTypes = {
    query: PropTypes.string,
    onChange: PropTypes.func,
    handleSearch: PropTypes.func.isRequired
};

export default SearchInput;