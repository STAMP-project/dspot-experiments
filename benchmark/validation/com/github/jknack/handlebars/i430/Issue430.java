package com.github.jknack.handlebars.i430;


import Handlebars.Utils;
import com.github.jknack.handlebars.v4Test;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class Issue430 extends v4Test {
    @Test
    public void shouldEscapeEqualsSignInHtml() throws IOException {
        Assert.assertEquals("foo&#x3D;", Utils.escapeExpression("foo=").toString());
    }
}

