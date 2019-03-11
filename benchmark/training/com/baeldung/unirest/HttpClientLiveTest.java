package com.baeldung.unirest;


import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class HttpClientLiveTest {
    @Test
    public void shouldReturnStatusOkay() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse = Unirest.get("http://www.mocky.io/v2/5a9ce37b3100004f00ab5154").header("accept", "application/json").queryString("apiKey", "123").asJson();
        Assert.assertNotNull(jsonResponse.getBody());
        Assert.assertEquals(200, jsonResponse.getStatus());
    }

    @Test
    public void shouldReturnStatusAccepted() throws UnirestException {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("accept", "application/json");
        headers.put("Authorization", "Bearer 5a9ce37b3100004f00ab5154");
        Map<String, Object> fields = new HashMap<String, Object>();
        fields.put("name", "Sam Baeldung");
        fields.put("id", "PSP123");
        HttpResponse<JsonNode> jsonResponse = Unirest.put("http://www.mocky.io/v2/5a9ce7853100002a00ab515e").headers(headers).fields(fields).asJson();
        Assert.assertNotNull(jsonResponse.getBody());
        Assert.assertEquals(202, jsonResponse.getStatus());
    }

    @Test
    public void givenRequestBodyWhenCreatedThenCorrect() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse = Unirest.post("http://www.mocky.io/v2/5a9ce7663100006800ab515d").body("{\"name\":\"Sam Baeldung\", \"city\":\"viena\"}").asJson();
        Assert.assertEquals(201, jsonResponse.getStatus());
    }

    @Test
    public void givenArticleWhenCreatedThenCorrect() throws UnirestException {
        Article article = new Article("ID1213", "Guide to Rest", "baeldung");
        HttpResponse<JsonNode> jsonResponse = Unirest.post("http://www.mocky.io/v2/5a9ce7663100006800ab515d").body(article).asJson();
        Assert.assertEquals(201, jsonResponse.getStatus());
    }
}

