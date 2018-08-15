import * as log from 'loglevel';
import PropTypes from 'prop-types';
import React, {Component} from "react";

import SearchIcon from '~/images/search.svg';
import ClearIcon from '~/images/clear.svg';

class SearchInput extends Component {

    constructor(props) {
        super(props);
        this.state = {value: ''};
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.handleCleanup = this.handleCleanup.bind(this);
    }

    handleChange(event) {
        this.setState({value: event.target.value});
    }

    handleSubmit(event) {
        event.preventDefault();
        this.props.handleSearch(this.state.value)
    }

    handleCleanup(event) {
        log.info("cleanup");
        event.preventDefault();
        this.setState({value: ''});
    }

    render() {
        log.debug("render SearchInput")
        return (
            <div className="searchInput">
                <form onSubmit={this.handleSubmit}>
                    <button className="close"></button>
                    <div className="inputContainer">
                        <input type="text" placeholder="Search..." name="search" value={this.state.value}
                               onChange={this.handleChange}/>
                    </div>
                    <button className="clear" onClick={this.handleCleanup}>
                        <ClearIcon/>
                    </button>
                    <button className="search">
                        <SearchIcon/>
                    </button>
                </form>
            </div>
        );
    }
}

SearchInput.propTypes = {
    onChange: PropTypes.func,
    handleSearch: PropTypes.func.isRequired
};

export default SearchInput;