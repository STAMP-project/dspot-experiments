package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.v4Test;
import org.junit.Assert;
import org.junit.Test;


public class Issue596 extends v4Test {
    @Test
    public void shouldSupportNoneCharSequenceReturnsTypeFromHelperClass() throws Exception {
        String text = compile("{{> partial root=this name=\"Han\"}}").text();
        Assert.assertEquals("{{>partial root=this name=\"Han\"}}", text);
    }
}

