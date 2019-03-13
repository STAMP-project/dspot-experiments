package com.baeldung.maven.it;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import org.junit.Assert;
import org.junit.Test;


public class RestIntegrationTest {
    @Test
    public void whenSendingGet_thenMessageIsReturned() throws IOException {
        String url = "http://localhost:8999";
        URLConnection connection = new URL(url).openConnection();
        try (InputStream response = connection.getInputStream();Scanner scanner = new Scanner(response)) {
            String responseBody = scanner.nextLine();
            Assert.assertEquals("Welcome to Baeldung!", responseBody);
        }
    }
}

