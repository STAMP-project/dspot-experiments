package com.github.jknack.handlebars;


import TagType.AMP_VAR;
import TagType.SECTION;
import TagType.SUB_EXPRESSION;
import TagType.TRIPLE_VAR;
import TagType.VAR;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class TagTypeTest extends AbstractTest {
    AbstractTest.Hash helpers = AbstractTest.$("tag", new Helper<Object>() {
        @Override
        public Object apply(final Object context, final Options options) throws IOException {
            return options.tagType.name();
        }
    }, "vowels", new Helper<Object>() {
        @Override
        public Object apply(final Object context, final Options options) throws IOException {
            return options.helperName;
        }
    });

    @Test
    public void varTag() throws IOException {
        shouldCompileTo("{{tag}}", AbstractTest.$, helpers, "VAR");
    }

    @Test
    public void unescapeVarTag() throws IOException {
        shouldCompileTo("{{&tag}}", AbstractTest.$, helpers, "AMP_VAR");
    }

    @Test
    public void tripleVarTag() throws IOException {
        shouldCompileTo("{{{tag}}}", AbstractTest.$, helpers, "TRIPLE_VAR");
    }

    @Test
    public void sectionTag() throws IOException {
        shouldCompileTo("{{#tag}}{{/tag}}", AbstractTest.$, helpers, "SECTION");
    }

    @Test
    public void inline() {
        Assert.assertTrue(VAR.inline());
        Assert.assertTrue(AMP_VAR.inline());
        Assert.assertTrue(TRIPLE_VAR.inline());
        Assert.assertTrue(SUB_EXPRESSION.inline());
    }

    @Test
    public void block() {
        Assert.assertTrue((!(SECTION.inline())));
    }

    @Test
    public void collectVar() throws IOException {
        assertSetEquals(Arrays.asList("a", "z", "k"), compile("{{#hello}}{{a}}{{&b}}{{z}}{{/hello}}{{k}}").collect(VAR));
    }

    @Test
    public void collectSubexpression() throws IOException {
        assertSetEquals(Arrays.asList("tag"), compile("{{vowels (tag)}}", helpers).collect(SUB_EXPRESSION));
    }

    @Test
    public void collectAmpVar() throws IOException {
        assertSetEquals(Arrays.asList("b"), compile("{{#hello}}{{a}}{{&b}}{{z}}{{/hello}}{{k}}").collect(AMP_VAR));
    }

    @Test
    public void collectTripleVar() throws IOException {
        assertSetEquals(Arrays.asList("tvar"), compile("{{{tvar}}}{{#hello}}{{a}}{{&b}}{{z}}{{/hello}}{{k}}").collect(TRIPLE_VAR));
    }

    @Test
    public void collectSection() throws IOException {
        assertSetEquals(Arrays.asList("hello"), compile("{{#hello}}{{a}}{{&b}}{{z}}{{/hello}}{{k}}").collect(SECTION));
    }

    @Test
    public void collectSectionWithSubExpression() throws IOException {
        assertSetEquals(Arrays.asList("tag"), compile("{{#hello}}{{vowels (tag)}}{{/hello}}", helpers).collect(SUB_EXPRESSION));
    }

    @Test
    public void collectSectionAndVars() throws IOException {
        assertSetEquals(Arrays.asList("hello", "a", "b", "z", "k", "vowels", "tag"), compile("{{#hello}}{{a}}{{&b}}{{z}}{{/hello}}{{k}}{{vowels (tag)}}", helpers).collect(SECTION, VAR, TRIPLE_VAR, AMP_VAR, SUB_EXPRESSION));
    }
}

