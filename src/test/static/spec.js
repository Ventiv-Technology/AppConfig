var waitForElement = function(elementId) {
    browser.driver.wait(function() {
        return element(by.id(elementId)).isPresent();
    });
};

var waitForAndClick = function(elementId) {
    waitForElement(elementId);
    expect(element(by.id(elementId)).isPresent()).toBe(true);
    element(by.id(elementId)).click();
};

var waitForModalToClose = function() {
    browser.driver.sleep(2000);
};

var enterXEditable = function(elementId, value, isTextArea) {
    // Click on the x-editable link for the property key
    expect(element(by.id(elementId)).isPresent()).toBe(true);
    element(by.id(elementId)).click();

    // Enter data into the text box, and submit
    var inputElement = element(by.id(elementId + '-input'));
    expect(inputElement.isPresent()).toBe(true);
    inputElement.sendKeys(value);

    if (isTextArea)
        inputElement.sendKeys(protractor.Key.chord(protractor.Key.CONTROL, protractor.Key.ENTER));
    else
        inputElement.sendKeys(protractor.Key.ENTER);
};

describe('appconfig landing page', function() {
    it('should have a title', function() {
        browser.get('/');

        expect(browser.getTitle()).toEqual('Application Configuration');
    });
});

describe('add new application', function() {
    it('should pop modal and enter values', function() {
        element(by.id('add-application-btn')).click();

        element(by.id('applicationId')).sendKeys('John_Test');
        element(by.id('applicationName')).sendKeys("John's Test Application");

        element(by.id('add-application-save')).click();

        waitForModalToClose();

        waitForAndClick('John_Test-application');
        waitForAndClick('John_Test-1');

        expect(browser.getLocationAbsUrl()).toBe("/John_Test/Default");
    });
});

describe('environment page', function() {
    it('should add a property group', function() {
        element(by.id('addPropertyGroup')).click();

        element(by.model('addingObject.name')).sendKeys('Main');

        element(by.id('add-property-group-save')).click();

        waitForModalToClose();
    });

    it('should add a property', function() {
        waitForAndClick('1-add-property');      // Click the + on 'Main'
        element(by.cssContainingText('span', 'Main')).click();       // Click on "Main" to re-expand,  shouldn't be necesary

        enterXEditable('-property-key', 'testing.property.1', false);
        enterXEditable('testing.property.1-property-value', 'Value for property: testing.property.1', true);

        expect(element(by.id('testing.property.1-property-key')).getText()).toBe("testing.property.1");
        expect(element(by.id('testing.property.1-property-value')).getText()).toBe("Value for property: testing.property.1");
    });
});

describe('extending an environment', function() {
    it('should add a new environment', function() {
        waitForAndClick('John_Test-add-environment');       // Click the + on 'John's Test Environment'

        element(by.model('addingObject.name')).sendKeys('Test_Environment');
        element(by.cssContainingText('option', 'Default')).click();
        element(by.id('add-environment-save')).click();

        waitForModalToClose();
    });

    it('should inherit the properties from Default', function() {
        browser.get('/#/John_Test/Test_Environment');

        expect(element(by.id('testing.property.1-property-key')).getText()).toBe("testing.property.1");
        expect(element(by.id('testing.property.1-property-value')).getText()).toBe("Value for property: testing.property.1");
    });
});