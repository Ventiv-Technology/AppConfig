exports.config = {
    seleniumAddress: 'http://localhost:4444/wd/hub',
    specs: ['spec.js'],
    baseUrl: 'http://localhost:9000',
    onPrepare: function() {
        browser.driver.manage().window().setSize(1280, 1024);

        // implicit and page load timeouts
        browser.manage().timeouts().pageLoadTimeout(40000);
        browser.manage().timeouts().implicitlyWait(25000);

        // for non-angular page
        browser.ignoreSynchronization = true;

        browser.get('/');

        // Wait until UI-Router redirects to /# to inform us that Angular has bootstrapped properly
        browser.driver.wait(function() {
            return browser.driver.getCurrentUrl().then(function(url) {
                return /\#\//.test(url);
            });
        });
    }
};