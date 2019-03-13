package io.fabric8.maven.docker.sample.jolokia;


import com.jayway.restassured.RestAssured;
import com.jayway.restassured.parsing.Parser;
import com.jayway.restassured.path.json.JsonPath;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author roland
 * @since 15.05.14
 */
public class VersionIT {
    @Test
    public void testVersion() {
        String versionExpected = System.getProperty("jolokia.version");
        String jolokiaUrl = System.getProperty("jolokia.url");
        RestAssured.baseURI = jolokiaUrl;
        RestAssured.defaultParser = Parser.JSON;
        System.out.println(("Checking URL: " + jolokiaUrl));
        // Need to do it that way since Jolokia doesnt return application/json as mimetype by default
        JsonPath json = with(get("/version").asString());
        json.prettyPrint();
        Assert.assertEquals(versionExpected, json.get("value.agent"));
        // Alternatively, set the mime type before, then Rest-assured's fluent API can be used
        get("/version").then().assertThat().header("content-type", containsString("application/json")).body("value.agent", equalTo(versionExpected)).body("timestamp", lessThanOrEqualTo(((int) ((System.currentTimeMillis()) / 1000)))).body("status", equalTo(200)).body("value.protocol", equalTo("7.2")).body("value.config", notNullValue());
    }
}

