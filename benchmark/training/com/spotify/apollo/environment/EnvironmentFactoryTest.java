/**
 * -\-\-
 * Spotify Apollo API Implementations
 * --
 * Copyright (C) 2013 - 2015 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.apollo.environment;


import EnvironmentFactory.Resolver;
import com.google.common.collect.Iterables;
import com.google.common.io.Closer;
import com.spotify.apollo.Client;
import com.spotify.apollo.Environment;
import com.spotify.apollo.Response;
import com.spotify.apollo.route.AsyncHandler;
import com.spotify.apollo.route.Route;
import com.typesafe.config.Config;
import okio.ByteString;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit-tests for EnvironmentImpl class.
 */
@RunWith(MockitoJUnitRunner.class)
public class EnvironmentFactoryTest {
    private static final String BACKEND_DOMAIN = "dummydomain";

    private static final String SERVICE_NAME = "dummy-service";

    @Mock
    Client client;

    @Mock
    EnvironmentConfigResolver configResolver;

    @Mock
    Resolver resolver;

    @Mock
    EnvironmentFactory.RoutingContext routingContext;

    @Mock
    Config configNode;

    @Mock
    ClassLoader classLoader;

    Closer closer = Closer.create();

    EnvironmentFactoryBuilder sut;

    @Test
    public void shouldResolveThroughResolver() throws Exception {
        Mockito.when(resolver.resolve(String.class)).thenReturn("hello world");
        final Environment environment = sut.build().create(EnvironmentFactoryTest.SERVICE_NAME, routingContext);
        final String resolve = environment.resolve(String.class);
        Assert.assertEquals("hello world", resolve);
    }

    @Test
    public void verifyDummyConfig() {
        final Environment environment = sut.build().create(EnvironmentFactoryTest.SERVICE_NAME, routingContext);
        final Config config = environment.config();
        Assert.assertNotNull(config);
        Assert.assertEquals("propertyBiValue", config.getString("propertyBi"));
    }

    @Test
    public void customConfigResolverShouldWork() throws Exception {
        Mockito.when(configResolver.getConfig(EnvironmentFactoryTest.SERVICE_NAME)).thenReturn(configNode);
        final Environment environment = sut.withConfigResolver(configResolver).build().create(EnvironmentFactoryTest.SERVICE_NAME, routingContext);
        Assert.assertEquals(configNode, environment.config());
    }

    @Test
    public void staticConfigShouldWork() throws Exception {
        final Environment environment = sut.withStaticConfig(configNode).build().create(EnvironmentFactoryTest.SERVICE_NAME, routingContext);
        Assert.assertEquals(configNode, environment.config());
    }

    @Test
    public void shouldCollectRegisteredRoutes() throws Exception {
        final EnvironmentFactory factory = sut.build();
        final EnvironmentFactory.RoutingContext routingContext = factory.createRoutingContext();
        final Environment environment = factory.create(EnvironmentFactoryTest.SERVICE_NAME, routingContext);
        final Route<AsyncHandler<Response<ByteString>>> route1 = Route.sync("GET", "/f1", handler());
        final Route<AsyncHandler<Response<ByteString>>> route2 = Route.sync("GET", "/2", handler());
        final Route<AsyncHandler<Response<ByteString>>> route3 = Route.sync("GET", "/3", handler());
        environment.routingEngine().registerRoute(route1).registerRoute(route2);
        environment.routingEngine().registerRoute(route3);
        final Iterable<Object> objects = routingContext.endpointObjects();
        Assert.assertTrue(Iterables.contains(objects, route1));
        Assert.assertTrue(Iterables.contains(objects, route2));
        Assert.assertTrue(Iterables.contains(objects, route3));
    }

    @Test
    public void shouldThrowIfUsedAfterInit() throws Exception {
        final EnvironmentFactory factory = sut.build();
        final EnvironmentFactory.RoutingContext routingContext = factory.createRoutingContext();
        final Environment environment = factory.create(EnvironmentFactoryTest.SERVICE_NAME, routingContext);
        final Route<AsyncHandler<Response<ByteString>>> route = Route.sync("GET", "/f1", handler());
        environment.routingEngine().registerRoute(route);
        final Iterable<Object> objects = routingContext.endpointObjects();
        Assert.assertTrue(Iterables.contains(objects, route));
        try {
            environment.routingEngine().registerRoute(route);
            Assert.fail("should throw");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("already been initialized"));
        }
    }

    @Test(expected = IllegalStateException.class)
    public void settingMultipleConfigResolversShouldFail1() throws Exception {
        sut.withConfigResolver(configResolver).withStaticConfig(configNode).build();
    }

    @Test(expected = IllegalStateException.class)
    public void settingMultipleConfigResolversShouldFail2() throws Exception {
        sut.withStaticConfig(configNode).withConfigResolver(configResolver).build();
    }

    @Test(expected = IllegalStateException.class)
    public void settingMultipleConfigResolversShouldFail3() throws Exception {
        sut.withStaticConfig(configNode).withClassLoader(classLoader).build();
    }

    @Test(expected = IllegalStateException.class)
    public void settingMultipleConfigResolversShouldFail4() throws Exception {
        sut.withClassLoader(classLoader).withStaticConfig(configNode).build();
    }

    @Test(expected = IllegalStateException.class)
    public void settingMultipleConfigResolversShouldFail5() throws Exception {
        sut.withConfigResolver(configResolver).withClassLoader(classLoader).build();
    }

    @Test(expected = IllegalStateException.class)
    public void settingMultipleConfigResolversShouldFail6() throws Exception {
        sut.withClassLoader(classLoader).withConfigResolver(configResolver).build();
    }
}

