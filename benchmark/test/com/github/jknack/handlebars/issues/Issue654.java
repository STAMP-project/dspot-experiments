package com.github.jknack.handlebars.issues;


import JavaBeanValueResolver.INSTANCE;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.v4Test;
import org.junit.Assert;
import org.junit.Test;


public class Issue654 extends v4Test {
    @Test
    public void makeSureMapResolverIsAlwaysPresent() throws Exception {
        Context ctx = Context.newBuilder(v4Test.$("foo", "bar")).resolver(INSTANCE).build();
        Assert.assertEquals("bar", ctx.get("foo"));
    }

    @Test
    public void ignoreMapResolverWhenItIsProvided() throws Exception {
        Context ctx = Context.newBuilder(v4Test.$("foo", "bar")).resolver(MapValueResolver.INSTANCE).build();
        Assert.assertEquals("bar", ctx.get("foo"));
    }
}

