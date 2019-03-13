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
package it.cosenonjaviste.daggermock.injectfromsubcomponent;


import dagger.Component;
import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;
import it.cosenonjaviste.daggermock.DaggerMockRule;
import it.cosenonjaviste.daggermock.InjectFromComponent;
import org.junit.Rule;
import org.junit.Test;


public class InjectFromSubComponent {
    @Rule
    public final DaggerMockRule<InjectFromSubComponent.MyComponent> rule = new DaggerMockRule(InjectFromSubComponent.MyComponent.class);

    String s1 = "test1";

    @InjectFromComponent({ MyActivity.class, MainService.class, Service1.class, Service2.class })
    Service3 service3;

    @Test
    public void testInjectFromSubComponentWithMultipleParameters() {
        assertThat(service3).isNotNull();
        assertThat(service3.get()).isEqualTo("test1");
    }

    @Module
    public static class MyModule {
        @Provides
        public String provideS1() {
            return "s1";
        }
    }

    @Subcomponent(modules = InjectFromSubComponent.MyModule.class)
    public interface MySubComponent {
        void inject(MyActivity myActivity);
    }

    @Component
    public interface MyComponent {
        InjectFromSubComponent.MySubComponent subcomponent(InjectFromSubComponent.MyModule module);
    }
}

