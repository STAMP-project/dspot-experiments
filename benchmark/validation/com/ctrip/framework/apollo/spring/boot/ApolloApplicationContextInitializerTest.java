package com.ctrip.framework.apollo.spring.boot;


import ConfigConsts.APOLLO_CLUSTER_KEY;
import ConfigConsts.APOLLO_META_KEY;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.env.ConfigurableEnvironment;


public class ApolloApplicationContextInitializerTest {
    private ApolloApplicationContextInitializer apolloApplicationContextInitializer;

    @Test
    public void testFillFromEnvironment() throws Exception {
        String someAppId = "someAppId";
        String someCluster = "someCluster";
        String someCacheDir = "someCacheDir";
        String someApolloMeta = "someApolloMeta";
        ConfigurableEnvironment environment = Mockito.mock(ConfigurableEnvironment.class);
        Mockito.when(environment.getProperty("app.id")).thenReturn(someAppId);
        Mockito.when(environment.getProperty(APOLLO_CLUSTER_KEY)).thenReturn(someCluster);
        Mockito.when(environment.getProperty("apollo.cacheDir")).thenReturn(someCacheDir);
        Mockito.when(environment.getProperty(APOLLO_META_KEY)).thenReturn(someApolloMeta);
        apolloApplicationContextInitializer.initializeSystemProperty(environment);
        Assert.assertEquals(someAppId, System.getProperty("app.id"));
        Assert.assertEquals(someCluster, System.getProperty(APOLLO_CLUSTER_KEY));
        Assert.assertEquals(someCacheDir, System.getProperty("apollo.cacheDir"));
        Assert.assertEquals(someApolloMeta, System.getProperty(APOLLO_META_KEY));
    }

    @Test
    public void testFillFromEnvironmentWithSystemPropertyAlreadyFilled() throws Exception {
        String someAppId = "someAppId";
        String someCluster = "someCluster";
        String someCacheDir = "someCacheDir";
        String someApolloMeta = "someApolloMeta";
        System.setProperty("app.id", someAppId);
        System.setProperty(APOLLO_CLUSTER_KEY, someCluster);
        System.setProperty("apollo.cacheDir", someCacheDir);
        System.setProperty(APOLLO_META_KEY, someApolloMeta);
        String anotherAppId = "anotherAppId";
        String anotherCluster = "anotherCluster";
        String anotherCacheDir = "anotherCacheDir";
        String anotherApolloMeta = "anotherApolloMeta";
        ConfigurableEnvironment environment = Mockito.mock(ConfigurableEnvironment.class);
        Mockito.when(environment.getProperty("app.id")).thenReturn(anotherAppId);
        Mockito.when(environment.getProperty(APOLLO_CLUSTER_KEY)).thenReturn(anotherCluster);
        Mockito.when(environment.getProperty("apollo.cacheDir")).thenReturn(anotherCacheDir);
        Mockito.when(environment.getProperty(APOLLO_META_KEY)).thenReturn(anotherApolloMeta);
        apolloApplicationContextInitializer.initializeSystemProperty(environment);
        Assert.assertEquals(someAppId, System.getProperty("app.id"));
        Assert.assertEquals(someCluster, System.getProperty(APOLLO_CLUSTER_KEY));
        Assert.assertEquals(someCacheDir, System.getProperty("apollo.cacheDir"));
        Assert.assertEquals(someApolloMeta, System.getProperty(APOLLO_META_KEY));
    }

    @Test
    public void testFillFromEnvironmentWithNoPropertyFromEnvironment() throws Exception {
        ConfigurableEnvironment environment = Mockito.mock(ConfigurableEnvironment.class);
        apolloApplicationContextInitializer.initializeSystemProperty(environment);
        Assert.assertNull(System.getProperty("app.id"));
        Assert.assertNull(System.getProperty(APOLLO_CLUSTER_KEY));
        Assert.assertNull(System.getProperty("apollo.cacheDir"));
        Assert.assertNull(System.getProperty(APOLLO_META_KEY));
    }
}

