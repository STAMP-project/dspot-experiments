package com.termux.app;


import junit.framework.TestCase;


public class TermuxActivityTest extends TestCase {
    public void testExtractUrls() {
        assertUrlsAre("hello http://example.com world", "http://example.com");
        assertUrlsAre("http://example.com\nhttp://another.com", "http://example.com", "http://another.com");
        assertUrlsAre("hello http://example.com world and http://more.example.com with secure https://more.example.com", "http://example.com", "http://more.example.com", "https://more.example.com");
    }
}

