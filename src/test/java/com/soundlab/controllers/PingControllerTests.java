package com.soundlab.controllers;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;

import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
public class PingControllerTests
{
    @Test
    public void whenPing() {
        RestAssured.when().get("/ping").then()
                .contentType("application/json")
                .body(equalTo("Endpoint pinged."));
    }

}
