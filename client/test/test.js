function shot(name) {
    browser.saveScreenshot("logs/" + name);
}

describe('URL based navigation', () => {

    let baseURL = browser.options.baseUrl;

    it('smoke test', () => {

        browser.url(baseURL);

        browser.getTitle().should.be.equal('Walmart');

        let inputPath = 'input[type="text"]';

        browser.waitForExist(inputPath, 50000);

        browser.hasFocus(inputPath).should.be.equal(true);

        browser.setValue(inputPath, "Doctor Who");

        shot("1-trends.png");

        browser.keys(["Return"]);

        let loading = "div[class='loading']";

        browser.waitForExist(loading, 50000);

        shot("2-grid-loading.png");

        let tardis = "a[href='#/product/126725106']";

        browser.waitForExist(tardis, 50000);

        browser.click(tardis);

        let productDetails = "div[class='productDetails']";

        browser.waitForExist(productDetails, 50000);

        shot("3-tardis-details.png");

        browser.url(baseURL + "#/product/46700585");

        browser.waitForExist(loading, 50000);

        shot("4-loading-details.png");

        browser.waitForExist(productDetails, 50000);

        let priceTag = "#application > div > div > div.details > div.info > div.finance > span[class='price']";

        browser.getText(priceTag).should.be.equal("$44.95");

    });
});