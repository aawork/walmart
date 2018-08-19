const selenium = require('selenium-download');

console.log("==== selenium.ensure");

selenium.ensure('./test/selenium', function (error) {
    console.log("==== error:", error);
    if (error) {
        console.error(error.stack);
    }
    process.exit(0);
});