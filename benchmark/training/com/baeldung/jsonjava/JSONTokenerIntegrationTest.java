package com.baeldung.jsonjava;


import org.json.JSONTokener;
import org.junit.Assert;
import org.junit.Test;


public class JSONTokenerIntegrationTest {
    @Test
    public void givenString_convertItToJSONTokens() {
        String str = "Sample String";
        JSONTokener jt = new JSONTokener(str);
        char[] expectedTokens = str.toCharArray();
        int index = 0;
        while (jt.more()) {
            Assert.assertEquals(expectedTokens[(index++)], jt.next());
        } 
    }
}

