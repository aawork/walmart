import * as log from 'loglevel';
import React, {Component} from "react";
import PropTypes from 'prop-types';

// import LoadState from '~/js/model/LoadState';

import LoadingIcon from '~/images/loading.svg';
import ErrorIcon from '~/images/error.svg';
import EmptyIcon from '~/images/empty.svg';

class Loading extends Component {

    render() {

        let state = this.props.state

        log.debug("[Loading] render state:", state)

        if (state.isInitial()) {
            return (<p>should never be visible</p>)
        }

        if (state.isLoading()) {
            return (
                <div className="loading">
                    <div className="message">{state.message}</div>
                    <div className="icon"><LoadingIcon/></div>
                </div>
            )
        }

        if (state.isFailed()) {
            return (
                <div className="error">
                    <div className="message">{state.message}</div>
                    <div className="icon"><ErrorIcon/></div>
                </div>
            )
        }

        if (state.isEmpty()) {
            return (
                <div className="empty">
                    <div className="message">{state.message}</div>
                    <div className="icon"><EmptyIcon/></div>
                </div>
            )
        }

        return null
    }
}


Loading.propTypes = {
    state: PropTypes.object.isRequired
};


export default Loading;