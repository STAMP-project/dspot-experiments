/**
 * Copyright (C) 2013 Google Inc.
 * Copyright (C) 2013 Square Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dagger;


import dagger.internal.TestingLoader;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static Type.SET;


@RunWith(JUnit4.class)
public final class ExtensionWithSetBindingsTest {
    private static final AtomicInteger counter = new AtomicInteger(0);

    @Singleton
    static class RealSingleton {
        @Inject
        Set<Integer> ints;
    }

    @Singleton
    static class Main {
        @Inject
        Set<Integer> ints;
    }

    @Module(injects = ExtensionWithSetBindingsTest.RealSingleton.class)
    static class RootModule {
        @Provides(type = SET)
        @Singleton
        Integer provideA() {
            return ExtensionWithSetBindingsTest.counter.getAndIncrement();
        }

        @Provides(type = SET)
        @Singleton
        Integer provideB() {
            return ExtensionWithSetBindingsTest.counter.getAndIncrement();
        }
    }

    @Module(addsTo = ExtensionWithSetBindingsTest.RootModule.class, injects = ExtensionWithSetBindingsTest.Main.class)
    static class ExtensionModule {
        @Provides(type = Type.SET)
        @Singleton
        Integer provideC() {
            return ExtensionWithSetBindingsTest.counter.getAndIncrement();
        }

        @Provides(type = Type.SET)
        @Singleton
        Integer provideD() {
            return ExtensionWithSetBindingsTest.counter.getAndIncrement();
        }
    }

    @Module
    static class EmptyModule {}

    @Module(library = true)
    static class DuplicateModule {
        @Provides
        @Singleton
        String provideFoo() {
            return "foo";
        }

        @Provides
        @Singleton
        String provideBar() {
            return "bar";
        }
    }

    @Test
    public void basicInjectionWithExtension() {
        ObjectGraph root = ObjectGraph.createWith(new TestingLoader(), new ExtensionWithSetBindingsTest.RootModule());
        ExtensionWithSetBindingsTest.RealSingleton rs = root.get(ExtensionWithSetBindingsTest.RealSingleton.class);
        assertThat(rs.ints).containsExactly(0, 1);
        ObjectGraph extension = root.plus(new ExtensionWithSetBindingsTest.ExtensionModule());
        ExtensionWithSetBindingsTest.Main main = extension.get(ExtensionWithSetBindingsTest.Main.class);
        assertThat(main.ints).containsExactly(0, 1, 2, 3);
        // Second time around.
        ObjectGraph extension2 = root.plus(new ExtensionWithSetBindingsTest.ExtensionModule());
        ExtensionWithSetBindingsTest.Main main2 = extension2.get(ExtensionWithSetBindingsTest.Main.class);
        assertThat(main2.ints).containsExactly(0, 1, 4, 5);
    }

    @Module(includes = ExtensionWithSetBindingsTest.ExtensionModule.class, overrides = true)
    static class TestModule {
        @Provides(type = Type.SET)
        @Singleton
        Integer provide9999() {
            return 9999;
        }
    }

    @Test
    public void basicInjectionWithExtensionAndOverrides() {
        try {
            ObjectGraph.createWith(new TestingLoader(), new ExtensionWithSetBindingsTest.RootModule()).plus(new ExtensionWithSetBindingsTest.TestModule());
            Assert.fail("Should throw exception.");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("TestModule: Module overrides cannot contribute set bindings.", e.getMessage());
        }
    }

    @Test
    public void duplicateBindingsInSecondaryModule() {
        try {
            ObjectGraph.createWith(new TestingLoader(), new ExtensionWithSetBindingsTest.EmptyModule(), new ExtensionWithSetBindingsTest.DuplicateModule());
            Assert.fail("Should throw exception.");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().startsWith("DuplicateModule: Duplicate"));
        }
    }
}

