package com.baeldung.ejb.tutorial;


import org.junit.Test;


public class HelloStatelessWorldTestUnitTest {
    private HelloStatelessWorldBean statelessBean;

    @Test
    public void whenGetHelloWorld_thenHelloStatelessWorldIsReturned() {
        String helloWorld = statelessBean.getHelloWorld();
        assertThat(helloWorld).isEqualTo("Hello Stateless World!");
    }
}

