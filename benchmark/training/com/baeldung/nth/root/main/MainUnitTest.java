package com.baeldung.nth.root.main;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;


public class MainUnitTest {
    @InjectMocks
    private Main main;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    private final PrintStream originalOut = System.out;

    @Test
    public void givenThatTheBaseIs125_andTheExpIs3_whenMainIsCalled_thenTheCorrectResultIsPrinted() {
        main.main(new String[]{ "125.0", "3.0" });
        Assert.assertEquals("The 3.0 root of 125.0 equals to 5.0.\n", outContent.toString().replaceAll("\r", ""));
    }
}

