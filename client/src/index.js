import ReactDOM from "react-dom";

import React from "react";
import {HashRouter as Router, Switch, Route} from "react-router-dom";

import App from './js/App';
import * as log from "loglevel";

// ReactDOM.render((
//     <Router>
//         <div>
//             <Route path="/search" component={FormContainer} exact={true}/>
//             <Route path="/search/:id" component={FormContainer}/>
//             <Route path="/huy" component={FormContainer2}/>
//         </div>
//     </Router>
// ), document.getElementById('content'));
//
// ReactDOM.render((<Logo/>), document.getElementById('logo'));

ReactDOM.render((
    <Router>

        <Switch>

            <Route path="/product/:id" exact={true} render={({match}) =>
                <App id={parseInt(match.params.id)} />
            }/>

            <Route path="/search/:query" render={({match}) =>
                <App query={parseInt(match.params.query)} />
            }/>

            <App />

        </Switch>

    </Router>
), document.getElementById('application'));