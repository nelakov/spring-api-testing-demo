package ru.nelakov.libraryapi.listeners;

import io.qameta.allure.restassured.AllureRestAssured;

/**
 * Builds the Allure REST-assured filter with the project's custom request/response
 * templates ({@code src/test/resources/tpl}). Returns a fresh instance per call so no
 * mutable filter state is shared across (possibly parallel) tests.
 */
public final class CustomAllureListener {

    private CustomAllureListener() {
    }

    public static AllureRestAssured withCustomTemplates() {
        AllureRestAssured filter = new AllureRestAssured();
        filter.setRequestTemplate("request.ftl");
        filter.setResponseTemplate("response.ftl");
        return filter;
    }
}
