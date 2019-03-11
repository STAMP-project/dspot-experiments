package org.mockserver.serialization.java;


import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.model.Cookie;


public class CookieToJavaSerializerTest {
    @Test
    public void shouldSerializeCookie() throws IOException {
        Assert.assertEquals(((NEW_LINE) + "        new Cookie(\"requestCookieNameOne\", \"requestCookieValueOne\")"), new CookieToJavaSerializer().serialize(1, new Cookie("requestCookieNameOne", "requestCookieValueOne")));
    }

    @Test
    public void shouldSerializeMultipleCookies() throws IOException {
        Assert.assertEquals(((((NEW_LINE) + "        new Cookie(\"requestCookieNameOne\", \"requestCookieValueOne\"),") + (NEW_LINE)) + "        new Cookie(\"requestCookieNameTwo\", \"requestCookieValueTwo\")"), new CookieToJavaSerializer().serializeAsJava(1, new Cookie("requestCookieNameOne", "requestCookieValueOne"), new Cookie("requestCookieNameTwo", "requestCookieValueTwo")));
    }

    @Test
    public void shouldSerializeListOfCookies() throws IOException {
        Assert.assertEquals(((((NEW_LINE) + "        new Cookie(\"requestCookieNameOne\", \"requestCookieValueOne\"),") + (NEW_LINE)) + "        new Cookie(\"requestCookieNameTwo\", \"requestCookieValueTwo\")"), new CookieToJavaSerializer().serializeAsJava(1, Arrays.asList(new Cookie("requestCookieNameOne", "requestCookieValueOne"), new Cookie("requestCookieNameTwo", "requestCookieValueTwo"))));
    }
}

