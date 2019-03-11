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
package ninja.utils;


import com.google.inject.Guice;
import com.google.inject.Injector;
import ninja.BaseAndClassicModules;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static NinjaMode.test;


public class ImplFromPropertiesFactoryTest {
    private static final Logger logger = LoggerFactory.getLogger(ImplFromPropertiesFactoryTest.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public static interface MockInterface {}

    public static class MockInterfaceImpl implements ImplFromPropertiesFactoryTest.MockInterface {}

    @Test
    public void defaultImplementation() {
        NinjaPropertiesImpl ninjaProperties = new NinjaPropertiesImpl(test);
        // ninjaProperties.setProperty("i.am.a.test.implementation", null);
        Injector injector = Guice.createInjector(new BaseAndClassicModules(ninjaProperties));
        // inner class requires $ symbol
        ImplFromPropertiesFactory<ImplFromPropertiesFactoryTest.MockInterface> factory = new ImplFromPropertiesFactory(injector, ninjaProperties, "i.am.a.test.implementation", ImplFromPropertiesFactoryTest.MockInterface.class, "ninja.utils.ImplFromPropertiesFactoryTest$MockInterfaceImpl", false, ImplFromPropertiesFactoryTest.logger);
        ImplFromPropertiesFactoryTest.MockInterface mockObject = factory.create();
        Assert.assertThat(mockObject, CoreMatchers.instanceOf(ImplFromPropertiesFactoryTest.MockInterfaceImpl.class));
    }

    @Test
    public void missingImplementation() {
        NinjaPropertiesImpl ninjaProperties = new NinjaPropertiesImpl(test);
        ninjaProperties.setProperty("i.am.a.test.implementation", "does_not_exist");
        Injector injector = Guice.createInjector(new BaseAndClassicModules(ninjaProperties));
        // this will not work => we expect a runtime exception with the impl missing
        thrown.expect(RuntimeException.class);
        ImplFromPropertiesFactory<ImplFromPropertiesFactoryTest.MockInterface> factory = new ImplFromPropertiesFactory(injector, ninjaProperties, "i.am.a.test.implementation", ImplFromPropertiesFactoryTest.MockInterface.class, null, false, ImplFromPropertiesFactoryTest.logger);
    }

    @Test
    public void missingImplementationDeferredUntilGet() {
        NinjaPropertiesImpl ninjaProperties = new NinjaPropertiesImpl(test);
        ninjaProperties.setProperty("i.am.a.test.implementation", "does_not_exist");
        Injector injector = Guice.createInjector(new BaseAndClassicModules(ninjaProperties));
        // this should be okay since we want to defer the resolution until a 'get'
        ImplFromPropertiesFactory<ImplFromPropertiesFactoryTest.MockInterface> factory = new ImplFromPropertiesFactory(injector, ninjaProperties, "i.am.a.test.implementation", ImplFromPropertiesFactoryTest.MockInterface.class, null, true, ImplFromPropertiesFactoryTest.logger);
        // this will not work => we expect a runtime exception with the impl missing
        thrown.expect(RuntimeException.class);
        factory.create();
    }

    @Test
    public void configuredImplementation() {
        NinjaPropertiesImpl ninjaProperties = new NinjaPropertiesImpl(test);
        ninjaProperties.setProperty("i.am.a.test.implementation", "ninja.utils.ImplFromPropertiesFactoryTest$MockInterfaceImpl");
        Injector injector = Guice.createInjector(new BaseAndClassicModules(ninjaProperties));
        ImplFromPropertiesFactory<ImplFromPropertiesFactoryTest.MockInterface> factory = new ImplFromPropertiesFactory(injector, ninjaProperties, "i.am.a.test.implementation", ImplFromPropertiesFactoryTest.MockInterface.class, null, false, ImplFromPropertiesFactoryTest.logger);
        ImplFromPropertiesFactoryTest.MockInterface mockObject = factory.create();
        Assert.assertThat(mockObject, CoreMatchers.instanceOf(ImplFromPropertiesFactoryTest.MockInterfaceImpl.class));
    }

    @Test
    public void implementationNotAnInstanceOfTarget() {
        NinjaPropertiesImpl ninjaProperties = new NinjaPropertiesImpl(test);
        ninjaProperties.setProperty("i.am.a.test.implementation", "java.lang.Object");
        Injector injector = Guice.createInjector(new BaseAndClassicModules(ninjaProperties));
        ImplFromPropertiesFactory<ImplFromPropertiesFactoryTest.MockInterface> factory = new ImplFromPropertiesFactory(injector, ninjaProperties, "i.am.a.test.implementation", ImplFromPropertiesFactoryTest.MockInterface.class, null, true, ImplFromPropertiesFactoryTest.logger);
        thrown.expect(RuntimeException.class);
        ImplFromPropertiesFactoryTest.MockInterface mockObject = factory.create();
    }
}

