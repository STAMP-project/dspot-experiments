package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.ValueResolver;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class Issue490 {
    @Test
    public void pushResolver() throws IOException {
        ValueResolver resolver = new ValueResolver() {
            @Override
            public Object resolve(final Object context) {
                return 1;
            }

            @Override
            public Object resolve(final Object context, final String name) {
                return 1;
            }

            @Override
            public Set<Map.Entry<String, Object>> propertySet(final Object context) {
                return null;
            }
        };
        Map<String, Object> hash = new HashMap<>();
        hash.put("foo", "bar");
        Context ctx = Context.newBuilder(hash).push(resolver).build();
        Assert.assertEquals("bar", ctx.get("foo"));
        Assert.assertEquals(1, ctx.get("bar"));
    }

    @Test
    public void setResolver() throws IOException {
        ValueResolver resolver = new ValueResolver() {
            @Override
            public Object resolve(final Object context) {
                return 1;
            }

            @Override
            public Object resolve(final Object context, final String name) {
                return 1;
            }

            @Override
            public Set<Map.Entry<String, Object>> propertySet(final Object context) {
                return null;
            }
        };
        Map<String, Object> hash = new HashMap<>();
        hash.put("foo", "bar");
        Context ctx = Context.newBuilder(hash).resolver(resolver).build();
        Assert.assertEquals(1, ctx.get("foo"));
        Assert.assertEquals(1, ctx.get("bar"));
    }
}

