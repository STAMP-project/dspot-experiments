package com.baeldung.annotation;


import org.junit.Assert;
import org.junit.Test;


public class PersonBuilderUnitTest {
    @Test
    public void whenBuildPersonWithBuilder_thenObjectHasPropertyValues() {
        Person person = new PersonBuilder().setAge(25).setName("John").build();
        Assert.assertEquals(25, person.getAge());
        Assert.assertEquals("John", person.getName());
    }
}

