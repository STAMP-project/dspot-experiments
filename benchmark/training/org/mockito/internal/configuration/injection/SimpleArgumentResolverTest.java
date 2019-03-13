/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.configuration.injection;


import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.junit.Assert;
import org.junit.Test;


public class SimpleArgumentResolverTest {
    @Test
    public void should_return_object_matching_given_types() throws Exception {
        ConstructorInjection.SimpleArgumentResolver resolver = new ConstructorInjection.SimpleArgumentResolver(newSetOf(new HashSet<Long>(), new ByteArrayOutputStream(), new HashMap<String, String>()));
        Object[] resolvedInstance = resolver.resolveTypeInstances(Set.class, Map.class, OutputStream.class);
        Assert.assertEquals(3, resolvedInstance.length);
        Assert.assertTrue(((resolvedInstance[0]) instanceof Set));
        Assert.assertTrue(((resolvedInstance[1]) instanceof Map));
        Assert.assertTrue(((resolvedInstance[2]) instanceof OutputStream));
    }

    @Test
    public void should_return_null_when_match_is_not_possible_on_given_types() throws Exception {
        ConstructorInjection.SimpleArgumentResolver resolver = new ConstructorInjection.SimpleArgumentResolver(newSetOf(new HashSet<Float>(), new ByteArrayOutputStream()));
        Object[] resolvedInstance = resolver.resolveTypeInstances(Set.class, Map.class, OutputStream.class);
        Assert.assertEquals(3, resolvedInstance.length);
        Assert.assertTrue(((resolvedInstance[0]) instanceof Set));
        Assert.assertNull(resolvedInstance[1]);
        Assert.assertTrue(((resolvedInstance[2]) instanceof OutputStream));
    }

    @Test
    public void should_return_null_when_types_are_primitives() throws Exception {
        ConstructorInjection.SimpleArgumentResolver resolver = new ConstructorInjection.SimpleArgumentResolver(newSetOf(new HashMap<Integer, String>(), new TreeSet<Integer>()));
        Object[] resolvedInstance = resolver.resolveTypeInstances(Set.class, Map.class, Boolean.class);
        Assert.assertEquals(3, resolvedInstance.length);
        Assert.assertTrue(((resolvedInstance[0]) instanceof Set));
        Assert.assertTrue(((resolvedInstance[1]) instanceof Map));
        Assert.assertNull(resolvedInstance[2]);
    }
}

