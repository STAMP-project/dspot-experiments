package com.baeldug.json;


import com.baeldung.json.Person;
import com.baeldung.json.PersonBuilder;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class JsonUnitTest {
    private Person person;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    private String personJson;

    private String petshopJson;

    @Test
    public void whenPersonIsConvertedToString_thenAValidJsonStringIsReturned() throws IOException {
        String generatedJsonString = write();
        Assert.assertEquals("Generated String has the expected format and content", personJson, generatedJsonString);
    }

    @Test
    public void whenJsonStringIsConvertedToPerson_thenAValidObjectIsReturned() throws IOException, ParseException {
        Person person = new PersonBuilder(personJson).build();
        Assert.assertEquals("First name has expected value", "Michael", person.getFirstName());
        Assert.assertEquals("Last name has expected value", "Scott", person.getLastName());
        Assert.assertEquals("Birthdate has expected value", dateFormat.parse("06/15/1978"), person.getBirthdate());
        Assert.assertThat("Email list has two items", person.getEmails(), CoreMatchers.hasItems("michael.scott@dd.com", "michael.scarn@gmail.com"));
    }

    @Test
    public void whenUsingObjectModelToQueryForSpecificProperty_thenExpectedValueIsReturned() throws IOException, ParseException {
        JsonReader reader = Json.createReader(new StringReader(petshopJson));
        JsonObject jsonObject = reader.readObject();
        Assert.assertEquals("The query should return the 'name' property of the third pet from the list", "Jake", jsonObject.getJsonArray("pets").getJsonObject(2).getString("name"));
    }

    @Test
    public void whenUsingStreamingApiToQueryForSpecificProperty_thenExpectedValueIsReturned() throws IOException, ParseException {
        JsonParser jsonParser = Json.createParser(new StringReader(petshopJson));
        int count = 0;
        String result = null;
        while (jsonParser.hasNext()) {
            Event e = jsonParser.next();
            if (e == (Event.KEY_NAME)) {
                if (jsonParser.getString().equals("name")) {
                    jsonParser.next();
                    if ((++count) == 3) {
                        result = jsonParser.getString();
                        break;
                    }
                }
            }
        } 
        Assert.assertEquals("The query should return the 'name' property of the third pet from the list", "Jake", result);
    }
}

