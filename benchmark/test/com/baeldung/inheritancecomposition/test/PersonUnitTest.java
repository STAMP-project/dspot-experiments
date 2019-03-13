package com.baeldung.inheritancecomposition.test;


import com.baeldung.inheritancecomposition.model.Person;
import org.junit.Test;


public class PersonUnitTest {
    private static Person person;

    @Test
    public void givenPersonInstance_whenCalledgetName_thenEqual() {
        assertThat(PersonUnitTest.person.getName()).isEqualTo("John");
    }

    @Test
    public void givenPersonInstance_whenCalledgetEmail_thenEqual() {
        assertThat(PersonUnitTest.person.getEmail()).isEqualTo("john@domain.com");
    }

    @Test
    public void givenPersonInstance_whenCalledgetAge_thenEqual() {
        assertThat(PersonUnitTest.person.getAge()).isEqualTo(35);
    }
}

