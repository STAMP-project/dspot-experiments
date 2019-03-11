package com.baeldung.jackson.deserialization.immutable;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class ImmutableObjectDeserializationUnitTest {
    @Test
    public void whenPublicConstructorIsUsed_thenObjectIsDeserialized() throws IOException {
        final String json = "{\"name\":\"Frank\",\"id\":5000}";
        Employee employee = new ObjectMapper().readValue(json, Employee.class);
        Assert.assertEquals("Frank", employee.getName());
        Assert.assertEquals(5000, employee.getId());
    }

    @Test
    public void whenBuilderIsUsedAndFieldIsNull_thenObjectIsDeserialized() throws IOException {
        final String json = "{\"name\":\"Frank\"}";
        Person person = new ObjectMapper().readValue(json, Person.class);
        Assert.assertEquals("Frank", person.getName());
        Assert.assertNull(person.getAge());
    }

    @Test
    public void whenBuilderIsUsedAndAllFieldsPresent_thenObjectIsDeserialized() throws IOException {
        final String json = "{\"name\":\"Frank\",\"age\":50}";
        Person person = new ObjectMapper().readValue(json, Person.class);
        Assert.assertEquals("Frank", person.getName());
        Assert.assertEquals(50, ((int) (person.getAge())));
    }
}

