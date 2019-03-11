package io.restassured.itest.java;


import com.google.gson.reflect.TypeToken;
import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.itest.java.objects.Message;
import io.restassured.itest.java.support.WithJetty;
import io.restassured.mapper.ObjectMapperType;
import java.lang.reflect.Type;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TypeObjectMappingITest extends WithJetty {
    @Parameterized.Parameter
    public ObjectMapperType mapperType;

    @Test
    public void shouldUseMapTypeWithObjectMappers() {
        String expected = "A message";
        final Message message = new Message();
        message.setMessage(expected);
        RestAssured.config = RestAssuredConfig.config().objectMapperConfig(new io.restassured.config.ObjectMapperConfig(mapperType));
        Type type = new TypeToken<Map<String, String>>() {}.getType();
        final Map<String, String> returnedMessage = RestAssured.given().body(message).when().post("/reflect").as(type);
        Assert.assertThat(returnedMessage.get("message"), Matchers.equalTo(expected));
    }
}

