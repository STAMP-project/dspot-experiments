package com.baeldung.kong;


import HttpStatus.CONFLICT;
import HttpStatus.CREATED;
import HttpStatus.FORBIDDEN;
import HttpStatus.UNAUTHORIZED;
import com.baeldung.kong.domain.APIObject;
import com.baeldung.kong.domain.ConsumerObject;
import com.baeldung.kong.domain.KeyAuthObject;
import com.baeldung.kong.domain.PluginObject;
import java.net.URI;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static HttpMethod.GET;
import static HttpMethod.POST;
import static HttpStatus.CONFLICT;
import static HttpStatus.CREATED;


/**
 *
 *
 * @author aiet
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = DEFINED_PORT, classes = StockApp.class)
public class KongAdminAPILiveTest {
    @Autowired
    TestRestTemplate restTemplate;

    @Test
    public void givenEndpoint_whenQueryStockPrice_thenPriceCorrect() {
        String response = getStockPrice("btc");
        Assert.assertEquals("10000", response);
        response = getStockPrice("eth");
        Assert.assertEquals("N/A", response);
    }

    @Test
    public void givenKongAdminAPI_whenAddAPI_thenAPIAccessibleViaKong() throws Exception {
        restTemplate.delete("http://localhost:8001/apis/stock-api");
        APIObject stockAPI = new APIObject("stock-api", "stock.api", "http://localhost:9090", "/");
        HttpEntity<APIObject> apiEntity = new HttpEntity(stockAPI);
        ResponseEntity<String> addAPIResp = restTemplate.postForEntity("http://localhost:8001/apis", apiEntity, String.class);
        Assert.assertEquals(CREATED, addAPIResp.getStatusCode());
        addAPIResp = restTemplate.postForEntity("http://localhost:8001/apis", apiEntity, String.class);
        Assert.assertEquals(CONFLICT, addAPIResp.getStatusCode());
        String apiListResp = restTemplate.getForObject("http://localhost:8001/apis/", String.class);
        Assert.assertTrue(apiListResp.contains("stock-api"));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Host", "stock.api");
        RequestEntity<String> requestEntity = new RequestEntity(headers, GET, new URI("http://localhost:8000/springbootapp/stock/btc"));
        ResponseEntity<String> stockPriceResp = restTemplate.exchange(requestEntity, String.class);
        Assert.assertEquals("10000", stockPriceResp.getBody());
    }

    @Test
    public void givenKongAdminAPI_whenAddAPIConsumer_thenAdded() {
        restTemplate.delete("http://localhost:8001/consumers/eugenp");
        ConsumerObject consumer = new ConsumerObject("eugenp");
        HttpEntity<ConsumerObject> addConsumerEntity = new HttpEntity(consumer);
        ResponseEntity<String> addConsumerResp = restTemplate.postForEntity("http://localhost:8001/consumers/", addConsumerEntity, String.class);
        Assert.assertEquals(CREATED, addConsumerResp.getStatusCode());
        addConsumerResp = restTemplate.postForEntity("http://localhost:8001/consumers", addConsumerEntity, String.class);
        Assert.assertEquals(CONFLICT, addConsumerResp.getStatusCode());
        String consumerListResp = restTemplate.getForObject("http://localhost:8001/consumers/", String.class);
        Assert.assertTrue(consumerListResp.contains("eugenp"));
    }

    @Test
    public void givenAPI_whenEnableAuth_thenAnonymousDenied() throws Exception {
        String apiListResp = restTemplate.getForObject("http://localhost:8001/apis/", String.class);
        if (!(apiListResp.contains("stock-api"))) {
            givenKongAdminAPI_whenAddAPI_thenAPIAccessibleViaKong();
        }
        PluginObject authPlugin = new PluginObject("key-auth");
        ResponseEntity<String> enableAuthResp = restTemplate.postForEntity("http://localhost:8001/apis/stock-api/plugins", new HttpEntity(authPlugin), String.class);
        Assert.assertTrue((((CREATED) == (enableAuthResp.getStatusCode())) || ((CONFLICT) == (enableAuthResp.getStatusCode()))));
        String pluginsResp = restTemplate.getForObject("http://localhost:8001/apis/stock-api/plugins", String.class);
        Assert.assertTrue(pluginsResp.contains("key-auth"));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Host", "stock.api");
        RequestEntity<String> requestEntity = new RequestEntity(headers, GET, new URI("http://localhost:8000/stock/btc"));
        ResponseEntity<String> stockPriceResp = restTemplate.exchange(requestEntity, String.class);
        Assert.assertEquals(UNAUTHORIZED, stockPriceResp.getStatusCode());
    }

    @Test
    public void givenAPIAuthEnabled_whenAddKey_thenAccessAllowed() throws Exception {
        String apiListResp = restTemplate.getForObject("http://localhost:8001/apis/", String.class);
        if (!(apiListResp.contains("stock-api"))) {
            givenKongAdminAPI_whenAddAPI_thenAPIAccessibleViaKong();
        }
        String consumerListResp = restTemplate.getForObject("http://localhost:8001/consumers/", String.class);
        if (!(consumerListResp.contains("eugenp"))) {
            givenKongAdminAPI_whenAddAPIConsumer_thenAdded();
        }
        PluginObject authPlugin = new PluginObject("key-auth");
        ResponseEntity<String> enableAuthResp = restTemplate.postForEntity("http://localhost:8001/apis/stock-api/plugins", new HttpEntity(authPlugin), String.class);
        Assert.assertTrue((((CREATED) == (enableAuthResp.getStatusCode())) || ((CONFLICT) == (enableAuthResp.getStatusCode()))));
        final String consumerKey = "eugenp.pass";
        KeyAuthObject keyAuth = new KeyAuthObject(consumerKey);
        ResponseEntity<String> keyAuthResp = restTemplate.postForEntity("http://localhost:8001/consumers/eugenp/key-auth", new HttpEntity(keyAuth), String.class);
        Assert.assertTrue((((CREATED) == (keyAuthResp.getStatusCode())) || ((CONFLICT) == (keyAuthResp.getStatusCode()))));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Host", "stock.api");
        headers.set("apikey", consumerKey);
        RequestEntity<String> requestEntity = new RequestEntity(headers, GET, new URI("http://localhost:8000/springbootapp/stock/btc"));
        ResponseEntity<String> stockPriceResp = restTemplate.exchange(requestEntity, String.class);
        Assert.assertEquals("10000", stockPriceResp.getBody());
        headers.set("apikey", "wrongpass");
        requestEntity = new RequestEntity(headers, GET, new URI("http://localhost:8000/springbootapp/stock/btc"));
        stockPriceResp = restTemplate.exchange(requestEntity, String.class);
        Assert.assertEquals(FORBIDDEN, stockPriceResp.getStatusCode());
    }

    @Test
    public void givenAdminAPIProxy_whenAddAPIViaProxy_thenAPIAdded() throws Exception {
        APIObject adminAPI = new APIObject("admin-api", "admin.api", "http://localhost:8001", "/admin-api");
        HttpEntity<APIObject> apiEntity = new HttpEntity(adminAPI);
        ResponseEntity<String> addAPIResp = restTemplate.postForEntity("http://localhost:8001/apis", apiEntity, String.class);
        Assert.assertTrue((((CREATED) == (addAPIResp.getStatusCode())) || ((CONFLICT) == (addAPIResp.getStatusCode()))));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Host", "admin.api");
        APIObject baeldungAPI = new APIObject("baeldung-api", "baeldung.com", "http://ww.baeldung.com", "/");
        RequestEntity<APIObject> requestEntity = new RequestEntity(baeldungAPI, headers, POST, new URI("http://localhost:8000/admin-api/apis"));
        addAPIResp = restTemplate.exchange(requestEntity, String.class);
        Assert.assertTrue((((CREATED) == (addAPIResp.getStatusCode())) || ((CONFLICT) == (addAPIResp.getStatusCode()))));
    }
}

