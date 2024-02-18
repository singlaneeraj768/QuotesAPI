package com.example.definitions;

import io.cucumber.java.en.Given;
import pages.APITesting;

public class QuotesStepDefinitions extends APITesting{
@Given("fav Quotes {string}, {string}, {string}")
    public void fav_quotes(String filter, String value, String valueExpected){
    filter(filter, value, valueExpected);
}

    @Given("fav {string}")
    public void fav(String isfav){
        getQuotesfav(isfav);
    }
}
