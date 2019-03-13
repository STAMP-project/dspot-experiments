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
package it.cosenonjaviste.daggermock.modulemethodsvisibility;


import dagger.Component;
import dagger.Module;
import dagger.Provides;
import it.cosenonjaviste.daggermock.DaggerMockRule;
import javax.inject.Singleton;
import org.junit.Rule;
import org.junit.Test;


public class ProtectedMethodTest {
    private MainService mainService;

    @Rule
    public final DaggerMockRule<ProtectedMethodTest.MyComponent> rule = new DaggerMockRule(ProtectedMethodTest.MyComponent.class, new ProtectedMethodTest.MyModule()).provides(MyService.class, new javax.inject.Provider<MyService>() {
        @Override
        public MyService get() {
            return new MyService();
        }
    }).set(new DaggerMockRule.ComponentSetter<ProtectedMethodTest.MyComponent>() {
        @Override
        public void setComponent(it.cosenonjaviste.daggermock.modulemethodsvisibility.MyComponent component) {
            mainService = component.mainService();
        }
    });

    @Test
    public void testErrorOnPackageMethods() throws Throwable {
        assertThat(mainService.getMyService()).isNotNull();
    }

    @Module
    public static class MyModule {
        @Provides
        protected MyService provideMyService() {
            return new MyService();
        }

        private void privateMethod() {
        }
    }

    @Singleton
    @Component(modules = ProtectedMethodTest.MyModule.class)
    public interface MyComponent {
        MainService mainService();
    }
}

