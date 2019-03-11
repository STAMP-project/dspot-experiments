package com.baeldung.stringtokenizer;


import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class TokenizerUnitTest {
    private final MyTokenizer myTokenizer = new MyTokenizer();

    private final List<String> expectedTokensForString = Arrays.asList("Welcome", "to", "baeldung.com");

    private final List<String> expectedTokensForFile = Arrays.asList("1", "IND", "India", "2", "MY", "Malaysia", "3", "AU", "Australia");

    @Test
    public void givenString_thenGetListOfString() {
        String str = "Welcome,to,baeldung.com";
        List<String> actualTokens = myTokenizer.getTokens(str);
        Assert.assertEquals(expectedTokensForString, actualTokens);
    }

    @Test
    public void givenFile_thenGetListOfString() {
        List<String> actualTokens = myTokenizer.getTokensFromFile("data.csv", "|");
        Assert.assertEquals(expectedTokensForFile, actualTokens);
    }
}

