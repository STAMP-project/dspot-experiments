package io.restassured.itest.java;


import com.google.gson.reflect.TypeToken;
import io.restassured.RestAssured;
import io.restassured.itest.java.objects.Greeting;
import io.restassured.itest.java.support.WithJetty;
import java.lang.reflect.Type;
import java.util.Map;
import org.junit.Test;


public class TypeObjectExceptionMappingITest extends WithJetty {
    @Test(expected = RuntimeException.class)
    public void shouldSeeExceptionWhenMappingTypeForJAXB() {
        final Greeting greeting = new Greeting();
        greeting.setFirstName("John");
        greeting.setLastName("Doe");
        Type type = new TypeToken<Map<String, String>>() {}.getType();
        RestAssured.given().contentType("application/xml").body(greeting, JAXB).post("/reflect").as(type, JAXB);
    }
}

