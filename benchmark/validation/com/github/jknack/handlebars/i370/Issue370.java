package com.github.jknack.handlebars.i370;


import com.github.jknack.handlebars.Handlebars.Utils;
import org.junit.Assert;
import org.junit.Test;


public class Issue370 {
    @Test
    public void shouldEscapeSingleQuote() {
        Assert.assertEquals("&#x27;", Utils.escapeExpression("'").toString());
    }
}

