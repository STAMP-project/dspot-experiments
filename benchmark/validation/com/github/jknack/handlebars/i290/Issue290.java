package com.github.jknack.handlebars.i290;


import com.github.jknack.handlebars.AbstractTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class Issue290 extends AbstractTest {
    @Test
    public void identifiersContainingDigitsAndHyphenMustNotFail() throws IOException {
        Assert.assertNotNull(compile("{{#each article-1-column}}{{/each}}"));
        Assert.assertNotNull(compile("{{#each article-1column}}{{/each}}"));
    }
}

