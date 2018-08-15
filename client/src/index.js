import ReactDOM from "react-dom";

import React from "react";
import {HashRouter as Router} from "react-router-dom";

import App from './js/App';

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
        <App />
    </Router>
), document.getElementById('application'));