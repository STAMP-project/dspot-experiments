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
package it.cosenonjaviste.daggermock.injectfromcomponentwithparams;


import dagger.Component;
import dagger.Module;
import dagger.Provides;
import it.cosenonjaviste.daggermock.DaggerMockRule;
import it.cosenonjaviste.daggermock.InjectFromComponent;
import org.junit.Rule;
import org.junit.Test;


public class InjectFromComponentWithMultipleParametersTest {
    @Rule
    public final DaggerMockRule<InjectFromComponentWithMultipleParametersTest.MyComponent> rule = new DaggerMockRule(InjectFromComponentWithMultipleParametersTest.MyComponent.class, new InjectFromComponentWithMultipleParametersTest.MyModule());

    String s1 = "test1";

    @InjectFromComponent({ MyActivity.class, MainService.class, Service1.class, Service2.class })
    Service3 service3;

    @Test
    public void testInjectFromComponentWithMultipleParameters() {
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

    @Component(modules = InjectFromComponentWithMultipleParametersTest.MyModule.class)
    public interface MyComponent {
        void inject(MyActivity myActivity);
    }
}

