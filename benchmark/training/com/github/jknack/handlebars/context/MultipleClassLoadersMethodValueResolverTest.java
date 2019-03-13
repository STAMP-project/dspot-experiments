package com.github.jknack.handlebars.context;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class MultipleClassLoadersMethodValueResolverTest {
    private static final String CLASS_NAME = "TestClass";

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private MethodValueResolver resolver = new MethodValueResolver();

    @Test
    public void canResolveMethodsFromTheSameClassLoadedByDistinctClassLoaders() throws Exception {
        Assert.assertEquals(resolver.resolve(loadTestClassWithDistinctClassLoader().newInstance(), "getField"), "value");
        Assert.assertEquals(resolver.resolve(loadTestClassWithDistinctClassLoader().newInstance(), "getField"), "value");
    }
}

