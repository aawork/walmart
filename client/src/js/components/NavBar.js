import * as log from 'loglevel';
import PropTypes from 'prop-types';
import React, {Component} from "react";

import GitHubLogo from '~/images/github.svg';
import Logo from '~/images/logo.svg';
import Spark from '~/images/spark.svg';

import SearchInput from '~/js/components/SearchInput';

class NavBar extends Component {

    render() {

        log.debug("render NavBar", this.props);

        return (
            <nav className="sticky-top">
                <a href="#" className="nav-brand">
                    <Logo/>
                </a>

                <div className="nav-spark">
                    <a href="#"><Spark/></a>
                </div>

                <SearchInput query={this.props.query} handleSearch={this.props.callback} />

                <a className="github" href="https://github.com/aawork/walmart" target="_blank" rel="noopener" aria-label="GitHub">
                    <GitHubLogo />
                </a>
            </nav>
        );
    }
}

NavBar.propTypes = {
    query: PropTypes.string,
    callback: PropTypes.func.isRequired
};

export default NavBar;
