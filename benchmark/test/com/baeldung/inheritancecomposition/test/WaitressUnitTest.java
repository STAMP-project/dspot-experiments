package com.baeldung.inheritancecomposition.test;


import com.baeldung.inheritancecomposition.model.Waitress;
import org.junit.Test;


public class WaitressUnitTest {
    private static Waitress waitress;

    @Test
    public void givenWaitressInstance_whenCalledgetName_thenOneAssertion() {
        assertThat(WaitressUnitTest.waitress.getName()).isEqualTo("Mary");
    }

    @Test
    public void givenWaitressInstance_whenCalledgetEmail_thenOneAssertion() {
        assertThat(WaitressUnitTest.waitress.getEmail()).isEqualTo("mary@domain.com");
    }

    @Test
    public void givenWaitressInstance_whenCalledgetAge_thenOneAssertion() {
        assertThat(WaitressUnitTest.waitress.getAge()).isEqualTo(22);
    }

    @Test
    public void givenWaitressInstance_whenCalledserveStarter_thenOneAssertion() {
        assertThat(WaitressUnitTest.waitress.serveStarter("mixed salad")).isEqualTo("Serving a mixed salad");
    }

    @Test
    public void givenWaitressInstance_whenCalledserveMainCourse_thenOneAssertion() {
        assertThat(WaitressUnitTest.waitress.serveMainCourse("steak")).isEqualTo("Serving a steak");
    }

    @Test
    public void givenWaitressInstance_whenCalledserveDessert_thenOneAssertion() {
        assertThat(WaitressUnitTest.waitress.serveDessert("cup of coffee")).isEqualTo("Serving a cup of coffee");
    }
}

