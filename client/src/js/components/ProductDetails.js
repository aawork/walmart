import * as log from 'loglevel';
import PropTypes from 'prop-types';
import React, {Component} from "react";

import DetailsService from "~/js/services/DetailsService"
import Loading from '~/js/components/Loading';
import LoadState from '~/js/model/LoadState';
import Product from '~/js/components/Product';
import Rating from '~/js/components/Rating';

class ProductDetails extends Component {

    constructor(props) {
        super(props);
        this.service = new DetailsService();
        log.info("ProductDetails", props)

        this.state = {
            item: props.item,
            status: new LoadState()
        }

        this.loadDetails(props)
    }

    static getDerivedStateFromProps(props, state) {
        if (props.item) {
            if (state.item && props.item.id == state.item.id) {
                return null
            }
            log.debug("[DETAILS] getDerivedStateFromProps: item from props: " + props.item.id)
            // return {item: props.item}
        }
        return null
    }

    shouldComponentUpdate(nextProps, nextState) {

        log.debug("[DETAILS] shouldComponentUpdate: nextProps:" + nextProps.id + " vs " + this.props.id)

        log.debug("[DETAILS] shouldComponentUpdate: nextProps.item:" , nextProps.item)

        if (this.props.id != nextProps.id) {
            this.loadDetails(nextProps, true);
        } else {
            log.debug("[DETAILS] load skipped")
        }


        return true
    }

    // componentDidMount() {
    //     // this.state = {item: null}
    //     this.loadDetails(this.props);
    // }

    onClose(event) {
        // event.preventDefault();
        this.props.onClose(this.props.item);
    }

    loadDetails(props, force) {

        let id = props.id;

        let item = this.state ? this.state.item : null;

        let status = this.state.status

        // log.debug("[DETAILS] this.state", this.state)
        // log.debug("[DETAILS] this.props", this.props)
        // log.debug("[DETAILS] props", props)

        // status.start("loadDetails:" + id, item)

        if (force || !item || item.id != id) {

            if (force) {
                log.debug("[DETAILS] force => loading...")
            } else if (!item) {
                log.debug("[DETAILS] no item => loading...")
            } else {
                log.debug("[DETAILS] id changed: => loading... " + id + " vs " + item.id)
            }

            log.debug("[DETAILS] loading details... by " + id)

            this.service.loadDetails(id).then(response => {
                status.onComplete()
                let data = response.result
                log.debug("[DETAILS] data", data)
                log.debug("[DETAILS] current props", this.props.id)
                log.debug("[DETAILS] recommendations", data.recommendations)
                if (data.id == id) {
                    //this.setState({item: data})
                    this.setState((prevState, props) => ({
                        item: data
                    }));
                }
            }).catch(error => {
                this.state.status.onError(error)
            });

        } else {

            status.onComplete()

            if (!item.recommendations) {

                log.debug("[DETAILS] loading recommendations... " + item.recommendations)

                this.service.loadDetails(id).then(response => {
                    //this.state.status.onComplete()
                    let data = response.result
                    log.debug("[DETAILS] recommendations", data.recommendations)
                    if (data.id == id) {
                        //this.setState({item: data})
                        this.setState((prevState, props) => ({
                            item: data
                        }));
                    } else {
                        log.debug("[DETAILS] recommendations discrepancy: " + id + " vs " + data.id)
                    }
                }).catch(error => {
                    log.error("[DETAILS] error", error)
                    //this.state.status.onError(error)
                });
            } else {
                log.debug("do nothing")
            }

        }
    }

    render() {

        let status = this.state.status

        let item = this.state ? this.state.item : null;

        // if (!status.isComplete()) {
        //     status.onError({code: 404, message: "Product is not found"}) // this is actually not possible
        // }

        if (!status.isComplete()) {
            log.debug("[DETAILS] render status:", status)
            return (<Loading state={status}/>);
        }

        log.debug("[DETAILS] render details")

        let recommendations = [];
        if (item && item.recommendations) {
            for (var i = 0; i < item.recommendations.length; i++) {
                let recomendation = item.recommendations[i];
                recommendations.push(<Product key={recomendation.id}
                                              item={recomendation}
                                              onClick={this.props.openDetails}/>);
            }
        }

        if (item != null) {

            return (
                <div className="productDetails">

                    <div className="details">
                        <div>
                            <img alt={item.name} src={item.imageURL}/>
                        </div>
                        <div className="info">
                            <div className="title">{item.name}</div>
                            <Rating value={item.rating} count={item.ratingsCount}/>
                            <div className="finance">
                                <span className="price">${item.price}</span>
                            </div>
                            {item.description && (
                                <div className="description" dangerouslySetInnerHTML={{__html: item.description}}></div>
                            )}
                            {item.walmartURL && (
                                <p><a href={item.walmartURL} target="_blank">Walmart Link</a></p>
                            )}
                        </div>
                    </div>
                    {recommendations.length > 0 && (
                        <div>
                            <h2>Recommendations</h2>
                            <div className="recommendations">
                                <div className="swipeView">
                                    {recommendations}
                                </div>
                            </div>
                        </div>
                    )}
                </div>
            );
        }

        return (<h1>LOADING</h1>)
    }
}

ProductDetails.propTypes = {
    onClose: PropTypes.func.isRequired,
    item: PropTypes.object,
    id: PropTypes.number
};


export default ProductDetails;