/**
 * Copyright 2016 Fabio Collini.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.cosenonjaviste.daggermock.injectfromcomponent;


import dagger.Component;
import dagger.Module;
import dagger.Provides;
import it.cosenonjaviste.daggermock.DaggerMockRule;
import it.cosenonjaviste.daggermock.InjectFromComponent;
import javax.inject.Named;
import org.junit.Rule;
import org.junit.Test;


public class InjectFromComponentWithNamedAnnotationTest {
    @Rule
    public final DaggerMockRule<InjectFromComponentWithNamedAnnotationTest.MyComponent> rule = new DaggerMockRule(InjectFromComponentWithNamedAnnotationTest.MyComponent.class, new InjectFromComponentWithNamedAnnotationTest.MyModule());

    @InjectFromComponent
    @Named("s1")
    String s1;

    @InjectFromComponent
    @Named("s2")
    String s2;

    @Test
    public void testInjectFromComponentWithQualifiers() {
        assertThat(s1).isEqualTo("s1");
        assertThat(s2).isEqualTo("s2");
    }

    @Module
    public static class MyModule {
        @Named("s1")
        @Provides
        public String provideS1() {
            return "s1";
        }

        @Named("s2")
        @Provides
        public String provideS2() {
            return "s2";
        }
    }

    @Component(modules = InjectFromComponentWithNamedAnnotationTest.MyModule.class)
    public interface MyComponent {
        @Named("s1")
        String s1();

        @Named("s2")
        String s2();
    }
}

