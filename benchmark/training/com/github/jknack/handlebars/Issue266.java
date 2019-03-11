package com.github.jknack.handlebars;


import com.github.jknack.handlebars.cache.TemplateCache;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import org.junit.Assert;
import org.junit.Test;


public class Issue266 {
    @Test
    public void prettyPrint() {
        Handlebars handlebars = new Handlebars().prettyPrint(false);
        Assert.assertNotNull(handlebars);
    }

    @Test
    public void stringParams() {
        Handlebars handlebars = new Handlebars().stringParams(false);
        Assert.assertNotNull(handlebars);
    }

    @Test
    public void infiniteLoops() {
        Handlebars handlebars = new Handlebars().infiniteLoops(false);
        Assert.assertNotNull(handlebars);
    }

    @Test
    public void endDelimiter() {
        Handlebars handlebars = new Handlebars().endDelimiter(">>");
        Assert.assertNotNull(handlebars);
    }

    @Test
    public void startDelimiter() {
        Handlebars handlebars = new Handlebars().startDelimiter("<<");
        Assert.assertNotNull(handlebars);
    }

    @Test
    public void withTemplateLoader() {
        Handlebars handlebars = new Handlebars().with(new ClassPathTemplateLoader());
        Assert.assertNotNull(handlebars);
    }

    @Test
    public void withParserFactory() {
        ParserFactory parserFactory = createMock(ParserFactory.class);
        replay(parserFactory);
        Handlebars handlebars = new Handlebars().with(parserFactory);
        Assert.assertNotNull(handlebars);
        verify(parserFactory);
    }

    @Test
    public void withTemplateCache() {
        TemplateCache cache = createMock(TemplateCache.class);
        replay(cache);
        Handlebars handlebars = new Handlebars().with(cache);
        Assert.assertNotNull(handlebars);
        verify(cache);
    }

    @Test
    public void withHelperRegistry() {
        HelperRegistry registry = createMock(HelperRegistry.class);
        replay(registry);
        Handlebars handlebars = new Handlebars().with(registry);
        Assert.assertNotNull(handlebars);
        verify(registry);
    }

    @Test
    public void withEscapingStrategy() {
        EscapingStrategy escapingStrategy = createMock(EscapingStrategy.class);
        replay(escapingStrategy);
        Handlebars handlebars = new Handlebars().with(escapingStrategy);
        Assert.assertNotNull(handlebars);
        verify(escapingStrategy);
    }
}

