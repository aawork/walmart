import App from './js/App';
import React from "react";
import ReactDOM from "react-dom";
import {HashRouter as Router, Route, Switch} from "react-router-dom";

ReactDOM.render((
    <Router>

        <Switch>

            <Route path="/product/:id" exact={true} render={({match}) =>
                <App id={parseInt(match.params.id)}/>
            }/>

            <Route path="/search/:query" render={({match}) =>
                <App query={match.params.query}/>
            }/>

            <App/>

        </Switch>

    </Router>
), document.getElementById('application'));