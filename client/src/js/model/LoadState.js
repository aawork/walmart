const COMPLETE = 'complete';
const EMPTY = 'empty';
const ERROR = 'error';
const INITIAL = 'init';
const LOADING = 'loading';
const SERVER_ERROR = 'failed';

class LoadState {

    constructor() {
        this.status = INITIAL;
    }

    isInitial() {
        return this.status === INITIAL
    }

    isLoading() {
        return this.status === LOADING
    }

    isFailed() {
        return this.status === ERROR || this.status === SERVER_ERROR
    }

    isEmpty() {
        return this.status === EMPTY
    }

    isComplete() {
        return this.status === COMPLETE
    }

    start(message) {
        this.message = message;
        this.status = LOADING
    }

    onError(error) {
        this.status = !error || error.code >= 500 ? SERVER_ERROR : ERROR

        if (this.status === SERVER_ERROR) {
            this.message = "Server communication failed"
        } else {
            this.message = error && error.message ? error.message : null
        }
    }

    onEmpty(emptyMessage) {
        this.message = emptyMessage;
        this.status = EMPTY
    }

    onComplete() {
        this.message = null;
        this.status = COMPLETE
    }

}

export default LoadState;