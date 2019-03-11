package org.jsoup.parser;


import org.junit.Assert;
import org.junit.Test;


public class ParserTest {
    @Test
    public void unescapeEntities() {
        String s = Parser.unescapeEntities("One &amp; Two", false);
        Assert.assertEquals("One & Two", s);
    }

    @Test
    public void unescapeEntitiesHandlesLargeInput() {
        StringBuilder longBody = new StringBuilder(500000);
        do {
            longBody.append("SomeNonEncodedInput");
        } while ((longBody.length()) < (64 * 1024) );
        String body = longBody.toString();
        Assert.assertEquals(body, Parser.unescapeEntities(body, false));
    }
}

