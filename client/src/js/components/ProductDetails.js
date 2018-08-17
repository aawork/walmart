import * as log from 'loglevel';
import PropTypes from 'prop-types';
import React, {Component} from "react";

import DetailsService from "~/js/services/DetailsService"
import Loading from '~/js/components/Loading';
import LoadState from '~/js/model/LoadState';
import Product from '~/js/components/Product';
import Rating from '~/js/components/Rating';

const loadingMessage = "Loading product . . .";

class ProductDetails extends Component {

    constructor(props) {
        super(props);
        this.service = new DetailsService();
        log.info("[DETAILS] constructor", props)

        let status = new LoadState();

        status.start(loadingMessage)

        this.state = {
            id: props.id,
            item: props.item,
            status: status
        }

        this.loadDetails(props)
    }

    static getDerivedStateFromProps(props, state) {

        let nextId = props.id

        let nextItem = props.item

        let currentItem = state.item;

        let currentId = state.id

        let status = state.status

        if (currentId == null) {
            log.error("[DETAILS] null currentId")
            return null;
        }

        // URL navigation : item not loaded yet
        if (!nextItem) {

            if (currentItem) {
                if (currentItem.id != nextId) {
                    log.debug("[DETAILS] Item changed : Loading...")
                    status.start(loadingMessage)
                    return {item: null, id: nextId, status: status, stop: false}
                }
            } else {
                if (currentId != nextId) {
                    log.debug("[DETAILS] ID changed : Loading...")
                    status.start(loadingMessage)
                    return {item: null, id: nextId, status: status, stop: false}
                }
            }

            return null;
        }

        if (nextItem.id == currentId) {
            return null;
        }

        // status.start(loadingMessage)
        return {item: nextItem, id: nextId, status: status, stop: false}
    }


    shouldComponentUpdate(nextProps, nextState) {

        if (nextState.stop) {
            log.debug("[DETAILS] stopped")
            return true
        }

        let result = this.props.id != nextProps.id;

        log.debug("[DETAILS] shouldComponentUpdate: nextProps:" + nextProps.id + " vs " + this.props.id + " >>> " + result)

        let nextId = nextProps.id;

        if (!this.state.item || this.state.item.id != nextId || this.props.id != nextId) {
            this.loadDetails(nextProps, false);
        }

        return true
    }

    onClose(event) {
        this.props.onClose(this.props.item);
    }

    loadDetails(props, force) {

        let id = props.id;

        let item = this.state ? this.state.item : null;

        let status = this.state.status

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
                log.debug("[DETAILS] recommendations", data.recommendations)

                if (data.id == id) {

                    this.setState((prevState, props) => ({
                        id: id,
                        item: data,
                        status: status
                    }));

                    log.info("[DETAILS] new state:", this.state)
                } else {
                    log.error("[DETAILS] ignore: ID changed: " + data.id + " vs " + id)
                }
            }).catch(error => {

                this.state.status.onError(error)

                log.warn("[DETAILS] error", error)

                this.setState((prevState, props) => ({
                    id: id,
                    item: null,
                    status: status,
                    stop: true
                }));
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
                        this.setState((prevState, props) => ({
                            item: data
                        }));
                    } else {
                        log.debug("[DETAILS] recommendations discrepancy: " + id + " vs " + data.id)
                    }
                }).catch(error => {
                    log.error("[DETAILS] error", error)
                    // process recommendations error is not needed : not critical for UX: avoid extra error messages
                });
            } else {
                log.debug("[DETAILS] do nothing")
            }
        }
    }

    openRecommendation(recommendation) {

        document.body.scrollTop = 0;            // Safari
        document.documentElement.scrollTop = 0; // Chrome, Firefox

        if (this.props.openDetails) {
            this.props.openDetails(recommendation);
        }
    }

    render() {

        let status = this.state.status

        let item = this.state ? this.state.item : null;

        if (!status.isComplete()) {
            log.debug("[DETAILS] render status:", status)
            return (<Loading state={status}/>);
        }

        // TODO: remove
        if (!item) {
            status.onError({code: 4, message: "Incorrect State"});
            return (<Loading state={status}/>);
        }

        log.debug("[DETAILS] render details")

        let recommendations = [];
        if (item && item.recommendations) {
            for (var i = 0; i < item.recommendations.length; i++) {
                let recommendation = item.recommendations[i];
                recommendations.push(
                    <Product key={recommendation.id}
                             item={recommendation}
                             onClick={this.props.openDetails.bind(this)}/>
                );
            }
        }

        return (
            <div className="productDetails">

                <div className="details">

                    <div className="bigImage">
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
}

ProductDetails.propTypes = {
    id: PropTypes.number.isRequired,
    item: PropTypes.object,
    onClose: PropTypes.func.isRequired
};


export default ProductDetails;