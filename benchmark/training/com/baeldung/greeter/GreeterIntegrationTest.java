package com.baeldung.greeter;


import com.baeldung.greeter.library.Greeter;
import com.baeldung.greeter.library.GreetingConfig;
import java.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Test;


public class GreeterIntegrationTest {
    private static GreetingConfig greetingConfig;

    @Test
    public void givenMorningTime_ifMorningMessage_thenSuccess() {
        String expected = "Hello World, Good Morning";
        Greeter greeter = new Greeter(GreeterIntegrationTest.greetingConfig);
        String actual = greeter.greet(LocalDateTime.of(2017, 3, 1, 6, 0));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void givenAfternoonTime_ifAfternoonMessage_thenSuccess() {
        String expected = "Hello World, Good Afternoon";
        Greeter greeter = new Greeter(GreeterIntegrationTest.greetingConfig);
        String actual = greeter.greet(LocalDateTime.of(2017, 3, 1, 13, 0));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void givenEveningTime_ifEveningMessage_thenSuccess() {
        String expected = "Hello World, Good Evening";
        Greeter greeter = new Greeter(GreeterIntegrationTest.greetingConfig);
        String actual = greeter.greet(LocalDateTime.of(2017, 3, 1, 19, 0));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void givenNightTime_ifNightMessage_thenSuccess() {
        String expected = "Hello World, Good Night";
        Greeter greeter = new Greeter(GreeterIntegrationTest.greetingConfig);
        String actual = greeter.greet(LocalDateTime.of(2017, 3, 1, 21, 0));
        Assert.assertEquals(expected, actual);
    }
}

