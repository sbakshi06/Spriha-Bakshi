
# heroku-rest-booking-api-test
Automation Test Suite for Restful Booking API by Heroku

### Summary:
This automation framework is to test the [Booking APIs] provided by [Heroku].

 Technologies/Frameworks used
* TestNG for test driven development approach.
* RestAssured framework for HTTP client calls.


### How to run tests:
Install the Heroku App locally using instructions mentioned [here]
OR Configure the **api.base-url** in config.properties to https://restful-booker.herokuapp.com value.

Import the project as a Maven project and execute following command.

```sh
$ mvn test
```
The test report can be found under the generated **target** folder at the following path:
```
./target/surefire-reports/html/index.html
```

### How to generate TestNG reports manually:
```
Run the testng.xml file
```
The testng report is generated under **./test-output/html** folder.

[Booking APIs]: https://restful-booker.herokuapp.com/apidoc/index.html#api-Booking
[Heroku]: https://www.heroku.com
[here]: https://github.com/mwinteringham/restful-booker/blob/master/README.md


