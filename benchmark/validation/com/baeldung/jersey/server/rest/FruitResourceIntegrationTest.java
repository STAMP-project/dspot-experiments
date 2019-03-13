package com.baeldung.jersey.server.rest;


import MediaType.APPLICATION_FORM_URLENCODED;
import MediaType.APPLICATION_JSON_TYPE;
import Status.CREATED;
import com.baeldung.jersey.server.model.Fruit;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class FruitResourceIntegrationTest extends JerseyTest {
    @Test
    public void givenGetAllFruit_whenCorrectRequest_thenAllTemplateInvoked() {
        final String response = target("/fruit/all").request().get(String.class);
        Assert.assertThat(response, CoreMatchers.allOf(CoreMatchers.containsString("banana"), CoreMatchers.containsString("apple"), CoreMatchers.containsString("kiwi")));
    }

    @Test
    public void givenGetFruit_whenCorrectRequest_thenIndexTemplateInvoked() {
        final String response = target("/fruit").request().get(String.class);
        Assert.assertThat(response, CoreMatchers.containsString("Welcome Fruit Index Page!"));
    }

    @Test
    public void givenGetFruitByName_whenFruitUnknown_thenErrorTemplateInvoked() {
        final String response = target("/fruit/orange").request().get(String.class);
        Assert.assertThat(response, CoreMatchers.containsString("Error -  Fruit not found: orange!"));
    }

    @Test
    public void givenCreateFruit_whenFormContainsNullParam_thenResponseCodeIsBadRequest() {
        Form form = new Form();
        form.param("name", "apple");
        form.param("colour", null);
        Response response = target("fruit/create").request(APPLICATION_FORM_URLENCODED).post(Entity.form(form));
        Assert.assertEquals("Http Response should be 400 ", 400, response.getStatus());
        Assert.assertThat(response.readEntity(String.class), CoreMatchers.containsString("Fruit colour must not be null"));
    }

    @Test
    public void givenCreateFruit_whenJsonIsCorrect_thenResponseCodeIsCreated() {
        Response response = target("fruit/created").request().post(Entity.json("{\"name\":\"strawberry\",\"weight\":20}"));
        Assert.assertEquals("Http Response should be 201 ", CREATED.getStatusCode(), response.getStatus());
        Assert.assertThat(response.readEntity(String.class), CoreMatchers.containsString("Fruit saved : Fruit [name: strawberry colour: null]"));
    }

    @Test
    public void givenUpdateFruit_whenFormContainsBadSerialParam_thenResponseCodeIsBadRequest() {
        Form form = new Form();
        form.param("serial", "2345-2345");
        Response response = target("fruit/update").request(APPLICATION_FORM_URLENCODED).put(Entity.form(form));
        Assert.assertEquals("Http Response should be 400 ", 400, response.getStatus());
        Assert.assertThat(response.readEntity(String.class), CoreMatchers.containsString("Fruit serial number is not valid"));
    }

    @Test
    public void givenCreateFruit_whenFruitIsInvalid_thenResponseCodeIsBadRequest() {
        Fruit fruit = new Fruit("Blueberry", "purple");
        fruit.setWeight(1);
        Response response = target("fruit/create").request(APPLICATION_JSON_TYPE).post(Entity.entity(fruit, APPLICATION_JSON_TYPE));
        Assert.assertEquals("Http Response should be 400 ", 400, response.getStatus());
        Assert.assertThat(response.readEntity(String.class), CoreMatchers.containsString("Fruit weight must be 10 or greater"));
    }

    @Test
    public void givenFruitExists_whenSearching_thenResponseContainsFruit() {
        Fruit fruit = new Fruit();
        fruit.setName("strawberry");
        fruit.setWeight(20);
        Response response = target("fruit/create").request(APPLICATION_JSON_TYPE).post(Entity.entity(fruit, APPLICATION_JSON_TYPE));
        Assert.assertEquals("Http Response should be 204 ", 204, response.getStatus());
        final String json = target("fruit/search/strawberry").request().get(String.class);
        Assert.assertThat(json, CoreMatchers.containsString("{\"name\":\"strawberry\",\"weight\":20}"));
    }

    @Test
    public void givenFruitExists_whenSearching_thenResponseContainsFruitEntity() {
        Fruit fruit = new Fruit();
        fruit.setName("strawberry");
        fruit.setWeight(20);
        Response response = target("fruit/create").request(APPLICATION_JSON_TYPE).post(Entity.entity(fruit, APPLICATION_JSON_TYPE));
        Assert.assertEquals("Http Response should be 204 ", 204, response.getStatus());
        final Fruit entity = target("fruit/search/strawberry").request().get(Fruit.class);
        Assert.assertEquals("Fruit name: ", "strawberry", entity.getName());
        Assert.assertEquals("Fruit weight: ", Integer.valueOf(20), entity.getWeight());
    }

    @Test
    public void givenFruit_whenFruitIsInvalid_thenReponseContainsCustomExceptions() {
        final Response response = target("fruit/exception").request().get();
        Assert.assertEquals("Http Response should be 400 ", 400, response.getStatus());
        String responseString = response.readEntity(String.class);
        Assert.assertThat(responseString, CoreMatchers.containsString("exception.<return value>.colour size must be between 5 and 200"));
        Assert.assertThat(responseString, CoreMatchers.containsString("exception.<return value>.name size must be between 5 and 200"));
    }
}

