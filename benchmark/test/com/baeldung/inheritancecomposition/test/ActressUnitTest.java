package com.baeldung.inheritancecomposition.test;


import com.baeldung.inheritancecomposition.model.Actress;
import org.junit.Test;


public class ActressUnitTest {
    private static Actress actress;

    @Test
    public void givenActressInstance_whenCalledgetName_thenEqual() {
        assertThat(ActressUnitTest.actress.getName()).isEqualTo("Susan");
    }

    @Test
    public void givenActressInstance_whenCalledgetEmail_thenEqual() {
        assertThat(ActressUnitTest.actress.getEmail()).isEqualTo("susan@domain.com");
    }

    @Test
    public void givenActressInstance_whenCalledgetAge_thenEqual() {
        assertThat(ActressUnitTest.actress.getAge()).isEqualTo(30);
    }

    @Test
    public void givenActressInstance_whenCalledreadScript_thenEqual() {
        assertThat(ActressUnitTest.actress.readScript("Psycho")).isEqualTo("Reading the script of Psycho");
    }

    @Test
    public void givenActressInstance_whenCalledperfomRole_thenEqual() {
        assertThat(ActressUnitTest.actress.performRole()).isEqualTo("Performing a role");
    }
}

