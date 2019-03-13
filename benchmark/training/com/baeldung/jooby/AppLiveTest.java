package com.baeldung.jooby;


import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.jooby.test.JoobyRule;
import org.junit.ClassRule;
import org.junit.Test;


public class AppLiveTest {
    @ClassRule
    public static JoobyRule app = new JoobyRule(new App());

    @Test
    public void given_defaultUrl_expect_fixedString() {
        RestAssured.get("/").then().assertThat().body(Matchers.equalTo("Hello World!")).statusCode(200).contentType("text/html;charset=UTF-8");
    }
}

