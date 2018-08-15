import PropTypes from 'prop-types';
import React, {Component} from "react";

class Rating extends Component {

    render() {
        let v = this.props.value;

        let stars = [];

        for (var i = 1; i <= 5; i++) {
            let type = v >= i ? "star-rated" : v >= i - 1 && v < i ? "star-partial" : "star-empty";
            stars.push(
                <span className={"star " + type} key={i}>&nbsp;</span>
            );
        }

        return (
            <div className="rating">
                {stars}
                {this.props.count > 0 &&
                    <span className="reviews">{this.props.count}</span>
                }
            </div>
        );
    }
}

Rating.propTypes = {
    value: PropTypes.number,
    count: PropTypes.number.isRequired
};

export default Rating;