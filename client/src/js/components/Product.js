import * as log from 'loglevel';
import PropTypes from 'prop-types';
import React, {Component} from "react";

import Rating from '~/js/components/Rating';

import {Link} from "react-router-dom";

class Product extends Component {

    onSelected(event) {
        //event.preventDefault();
        log.info("=== open ", this.props.item)
        this.props.onClick(this.props.item);
    }

    render() {
        log.debug("render Product")
        let item = this.props.item;
        return (
            <div className="cell">
                <div className="product">
                    <Link to={'/product/' + item.id} onClick={this.onSelected.bind(this)}>
                        <img alt="Force Factor Test X180 Test Booster Capsules, 60 Ct"
                             src={item.thumbnailURL}/>
                        <div className="title">{item.name}</div>
                        <Rating value={item.rating} count={item.ratingsCount}/>
                        <div className="finance">
                            <span className="price">${item.price}</span>
                        </div>

                    </Link>
                </div>
            </div>
        );
    }
}

Product.propTypes = {
    onClick: PropTypes.func.isRequired
};


export default Product;