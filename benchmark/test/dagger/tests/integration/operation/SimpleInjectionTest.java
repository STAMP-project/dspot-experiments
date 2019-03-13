/**
 * Copyright (C) 2013 Google, Inc.
 * Copyright (C) 2013 Square, Inc.
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
package dagger.tests.integration.operation;


import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class SimpleInjectionTest {
    abstract static class AbstractFoo {
        @Inject
        String blah;
    }

    static class Foo extends SimpleInjectionTest.AbstractFoo {}

    @Module(injects = SimpleInjectionTest.Foo.class)
    static class FooModule {
        @Provides
        String string() {
            return "blah";
        }
    }

    @Module(injects = SimpleInjectionTest.Foo.class)
    static class ProvidingFooModule {
        @Provides
        String string() {
            return "blah";
        }

        @Provides
        SimpleInjectionTest.Foo foo(String blah) {
            SimpleInjectionTest.Foo foo = new SimpleInjectionTest.Foo();
            foo.blah = blah;
            return foo;
        }
    }

    @Test
    public void memberInject_WithoutProvidesMethod() {
        SimpleInjectionTest.Foo foo = new SimpleInjectionTest.Foo();
        ObjectGraph.create(SimpleInjectionTest.FooModule.class).inject(foo);
        assertThat(foo.blah).isEqualTo("blah");
    }

    @Test
    public void membersInject_WithProvidesMethod() {
        SimpleInjectionTest.Foo foo = new SimpleInjectionTest.Foo();
        ObjectGraph.create(SimpleInjectionTest.ProvidingFooModule.class).inject(foo);
        assertThat(foo.blah).isEqualTo("blah");
    }

    @Test
    public void get_WithProvidesMethod() {
        SimpleInjectionTest.Foo foo = ObjectGraph.create(SimpleInjectionTest.ProvidingFooModule.class).get(SimpleInjectionTest.Foo.class);
        assertThat(foo.blah).isEqualTo("blah");
    }

    static class Bar {}

    @Module(injects = SimpleInjectionTest.Bar.class)
    static class BarModule {}

    @Test
    public void membersInject_WithNonInjectable() {
        SimpleInjectionTest.Bar bar = new SimpleInjectionTest.Bar();
        ObjectGraph.create(SimpleInjectionTest.BarModule.class).inject(bar);
    }

    @Module(injects = SimpleInjectionTest.Bar.class)
    static class ProvidingBarModule {
        @Provides
        public SimpleInjectionTest.Bar bar() {
            return new SimpleInjectionTest.Bar();
        }
    }

    @Test
    public void membersInject_WithProvidedNonInjectable() {
        SimpleInjectionTest.Bar bar = ObjectGraph.create(SimpleInjectionTest.ProvidingBarModule.class).get(SimpleInjectionTest.Bar.class);
        assertThat(bar).isNotNull();
    }
}

