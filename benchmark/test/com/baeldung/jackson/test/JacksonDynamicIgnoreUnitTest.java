package com.baeldung.jackson.test;


import com.baeldung.jackson.dynamicIgnore.Address;
import com.baeldung.jackson.dynamicIgnore.Person;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class JacksonDynamicIgnoreUnitTest {
    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void whenNotHidden_thenCorrect() throws JsonProcessingException {
        final Address ad = new Address("ny", "usa", false);
        final Person person = new Person("john", ad, false);
        final String result = mapper.writeValueAsString(person);
        Assert.assertTrue(result.contains("name"));
        Assert.assertTrue(result.contains("john"));
        Assert.assertTrue(result.contains("address"));
        Assert.assertTrue(result.contains("usa"));
        System.out.println(("Not Hidden = " + result));
    }

    @Test
    public void whenAddressHidden_thenCorrect() throws JsonProcessingException {
        final Address ad = new Address("ny", "usa", true);
        final Person person = new Person("john", ad, false);
        final String result = mapper.writeValueAsString(person);
        Assert.assertTrue(result.contains("name"));
        Assert.assertTrue(result.contains("john"));
        Assert.assertFalse(result.contains("address"));
        Assert.assertFalse(result.contains("usa"));
        System.out.println(("Address Hidden = " + result));
    }

    @Test
    public void whenAllHidden_thenCorrect() throws JsonProcessingException {
        final Address ad = new Address("ny", "usa", false);
        final Person person = new Person("john", ad, true);
        final String result = mapper.writeValueAsString(person);
        Assert.assertTrue(((result.length()) == 0));
        System.out.println(("All Hidden = " + result));
    }

    @Test
    public void whenSerializeList_thenCorrect() throws JsonProcessingException {
        final Address ad1 = new Address("tokyo", "jp", true);
        final Address ad2 = new Address("london", "uk", false);
        final Address ad3 = new Address("ny", "usa", false);
        final Person p1 = new Person("john", ad1, false);
        final Person p2 = new Person("tom", ad2, true);
        final Person p3 = new Person("adam", ad3, false);
        final String result = mapper.writeValueAsString(Arrays.asList(p1, p2, p3));
        System.out.println(result);
    }
}

