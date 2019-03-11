/**
 * Copyright (C) 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ninja;


import NinjaConstant.APPLICATION_MODULES_BASE_PACKAGE;
import com.example.controllers.DummyApplication;
import conf.Module;
import ninja.cache.Cache;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaPropertiesImpl;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 *
 * @author ra
 */
@RunWith(MockitoJUnitRunner.class)
public class BootstrapTest {
    NinjaPropertiesImpl ninjaPropertiesImpl;

    @Test
    public void testInitializeWithAllUserSpecifiedThingsInConfDirectory() {
        ninjaPropertiesImpl = new NinjaPropertiesImpl(NinjaMode.test);
        Bootstrap bootstrap = new Bootstrap(ninjaPropertiesImpl);
        bootstrap.boot();
        Assert.assertThat("Ninja Boostrap process picks up user supplied conf.Ninja definition", bootstrap.getInjector().getInstance(Ninja.class), CoreMatchers.is(CoreMatchers.instanceOf(conf.Ninja.class)));
        Assert.assertThat("Ninja Boostrap process picks up user supplied Guice module in conf.Module", bootstrap.getInjector().getInstance(Module.DummyInterfaceForTesting.class), CoreMatchers.is(CoreMatchers.instanceOf(Module.DummyClassForTesting.class)));
        Router router = bootstrap.getInjector().getInstance(Router.class);
        Route route = router.getRouteFor("GET", "/");
        Assert.assertThat("conf.Routes initialized properly. We get back the class we defined by the route.", route.getControllerClass(), CoreMatchers.is(CoreMatchers.instanceOf(DummyApplication.class.getClass())));
    }

    @Test
    public void noUserSuppliedThingsInConfDirectory() {
        // since we needed to supply conf.Ninja, etc. for our other tests, we'll
        // test a user NOT supplying these by configuring the application base package
        // a bit of a hack, but will work to force NOT finding anything
        ninjaPropertiesImpl = Mockito.spy(new NinjaPropertiesImpl(NinjaMode.test));
        Mockito.when(ninjaPropertiesImpl.get(APPLICATION_MODULES_BASE_PACKAGE)).thenReturn("com.doesnotexist");
        Bootstrap bootstrap = new Bootstrap(ninjaPropertiesImpl);
        bootstrap.boot();
        Router router = bootstrap.getInjector().getInstance(Router.class);
        try {
            Route route = router.getRouteFor("GET", "/");
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("routes not compiled"));
        }
    }

    @Test
    public void testInitializeWithAllUserSpecifiedThingsInShiftedConfDirectory() {
        ninjaPropertiesImpl = Mockito.spy(new NinjaPropertiesImpl(NinjaMode.test));
        Mockito.when(ninjaPropertiesImpl.get(APPLICATION_MODULES_BASE_PACKAGE)).thenReturn("com.example");
        Bootstrap bootstrap = new Bootstrap(ninjaPropertiesImpl);
        bootstrap.boot();
        Assert.assertThat("Ninja Boostrap process picks up user supplied conf.Ninja definition", bootstrap.getInjector().getInstance(Ninja.class), CoreMatchers.is(CoreMatchers.instanceOf(com.example.conf.Ninja.class)));
        Assert.assertThat("Ninja Boostrap process picks up user supplied Guice module in conf.Module", bootstrap.getInjector().getInstance(com.example.conf.Module.DummyInterfaceForTesting.class), CoreMatchers.is(CoreMatchers.instanceOf(com.example.conf.Module.DummyClassForTesting.class)));
        Router router = bootstrap.getInjector().getInstance(Router.class);
        Route route = router.getRouteFor("GET", "/");
        Assert.assertThat("conf.Routes initialized properly. We get back the class we defined by the route.", route.getControllerClass(), CoreMatchers.is(CoreMatchers.instanceOf(DummyApplication.class.getClass())));
    }

    @Test
    public void frameworkModuleSkipsNinjaClassicModule() {
        ninjaPropertiesImpl = Mockito.spy(new NinjaPropertiesImpl(NinjaMode.test));
        Mockito.when(ninjaPropertiesImpl.get(APPLICATION_MODULES_BASE_PACKAGE)).thenReturn("com.example.frameworkmodule");
        Bootstrap bootstrap = new Bootstrap(ninjaPropertiesImpl);
        bootstrap.boot();
        try {
            Cache cache = bootstrap.getInjector().getInstance(Cache.class);
            Assert.fail("cache should not have been found");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("No implementation for ninja.cache.Cache was bound"));
        }
    }
}

