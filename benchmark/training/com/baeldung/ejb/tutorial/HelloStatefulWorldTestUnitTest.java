package com.baeldung.ejb.tutorial;


import org.junit.Test;


public class HelloStatefulWorldTestUnitTest {
    private HelloStatefulWorldBean statefulBean;

    @Test
    public void whenGetHelloWorld_thenHelloStatefulWorldIsReturned() {
        String helloWorld = statefulBean.getHelloWorld();
        assertThat(helloWorld).isEqualTo("Hello Stateful World!");
    }

    @Test
    public void whenGetHelloWorldIsCalledTwice_thenCounterIs2() {
        statefulBean.getHelloWorld();
        statefulBean.getHelloWorld();
        assertThat(statefulBean.howManyTimes()).isEqualTo(2);
    }
}

