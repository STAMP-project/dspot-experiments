/**
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.bootique;


import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;
import java.util.Set;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Assert;
import org.junit.Test;


public class BootiqueTest {
    private Bootique bootique;

    @Test
    public void testCreateInjector_Modules_Instances() {
        Injector i = bootique.modules(new BootiqueTest.TestModule1(), new BootiqueTest.TestModule2()).createInjector();
        Set<String> strings = i.getInstance(Key.get(new com.google.inject.TypeLiteral<Set<String>>() {}));
        Assert.assertThat(strings, IsCollectionContaining.hasItems("tm1", "tm2"));
        Assert.assertEquals(2, strings.size());
    }

    @Test
    public void testCreateInjector_Modules_Types() {
        Injector i = bootique.modules(BootiqueTest.TestModule1.class, BootiqueTest.TestModule2.class).createInjector();
        Set<String> strings = i.getInstance(Key.get(new com.google.inject.TypeLiteral<Set<String>>() {}));
        Assert.assertThat(strings, IsCollectionContaining.hasItems("tm1", "tm2"));
        Assert.assertEquals(2, strings.size());
    }

    static class TestModule1 implements Module {
        @Override
        public void configure(Binder binder) {
            Multibinder.newSetBinder(binder, String.class).addBinding().toInstance("tm1");
        }
    }

    static class TestModule2 implements Module {
        @Override
        public void configure(Binder binder) {
            Multibinder.newSetBinder(binder, String.class).addBinding().toInstance("tm2");
        }
    }
}

