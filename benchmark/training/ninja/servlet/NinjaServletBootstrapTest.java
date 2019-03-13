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
package ninja.servlet;


import NinjaConstant.APPLICATION_MODULES_BASE_PACKAGE;
import conf.ServletModule;
import controller.DummyControllerForTesting;
import ninja.Bootstrap;
import ninja.Context;
import ninja.Route;
import ninja.Router;
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
public class NinjaServletBootstrapTest {
    NinjaPropertiesImpl ninjaPropertiesImpl;

    @Test
    public void userSuppliedServletModuleInConfDirectory() {
        ninjaPropertiesImpl = new NinjaPropertiesImpl(NinjaMode.test);
        Bootstrap bootstrap = new NinjaServletBootstrap(ninjaPropertiesImpl);
        bootstrap.boot();
        Assert.assertThat(bootstrap.getInjector().getInstance(Context.class), CoreMatchers.is(CoreMatchers.instanceOf(NinjaServletContext.class)));
        Assert.assertThat("Ninja Boostrap process picks up user supplied Guice servlet module in conf.ServletModule", bootstrap.getInjector().getInstance(ServletModule.DummyInterfaceForTesting.class), CoreMatchers.is(CoreMatchers.instanceOf(ServletModule.DummyClassForTesting.class)));
        Router router = bootstrap.getInjector().getInstance(Router.class);
        Route route = router.getRouteFor("GET", "/");
        Assert.assertThat("conf.Routes initialized properly. We get back the class we defined by the route.", route.getControllerClass(), CoreMatchers.is(CoreMatchers.instanceOf(DummyControllerForTesting.class.getClass())));
    }

    @Test
    public void userSuppliedServletModuleInShiftedConfDirectory() {
        ninjaPropertiesImpl = Mockito.spy(new NinjaPropertiesImpl(NinjaMode.test));
        Mockito.when(ninjaPropertiesImpl.get(APPLICATION_MODULES_BASE_PACKAGE)).thenReturn("custom_base_package");
        Bootstrap bootstrap = new NinjaServletBootstrap(ninjaPropertiesImpl);
        bootstrap.boot();
        Assert.assertThat("Ninja Boostrap process picks up user supplied Guice servlet module in custom_base_package.conf.ServletModule", bootstrap.getInjector().getInstance(custom_base_package.conf.ServletModule.DummyInterfaceForTesting.class), CoreMatchers.is(CoreMatchers.instanceOf(custom_base_package.conf.ServletModule.DummyClassForTesting.class)));
        Router router = bootstrap.getInjector().getInstance(Router.class);
        Route route = router.getRouteFor("GET", "/custom_base_package");
        Assert.assertThat("custom_base_package.conf.Routes initialized properly. We get back the class we defined by the route.", route.getControllerClass(), CoreMatchers.is(CoreMatchers.instanceOf(DummyControllerForTesting.class.getClass())));
    }
}

