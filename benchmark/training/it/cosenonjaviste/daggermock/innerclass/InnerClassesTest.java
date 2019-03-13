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
package it.cosenonjaviste.daggermock.innerclass;


import dagger.Component;
import dagger.Module;
import dagger.Provides;
import it.cosenonjaviste.daggermock.DaggerMockRule;
import javax.inject.Singleton;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;


public class InnerClassesTest {
    @Rule
    public final DaggerMockRule<InnerClassesTest.MyComponent> rule = new DaggerMockRule(InnerClassesTest.MyComponent.class, new InnerClassesTest.MyModule()).set(new DaggerMockRule.ComponentSetter<InnerClassesTest.MyComponent>() {
        @Override
        public void setComponent(it.cosenonjaviste.daggermock.innerclass.MyComponent component) {
            it.cosenonjaviste.daggermock.innerclass.mainService = component.mainService();
        }
    });

    @Mock
    MyService myService;

    private MainService mainService;

    @Test
    public void testInnerClasses() {
        // it.cosenonjaviste.daggermock.innerclass.InnerClassesTest$MyComponent
        assertThat(mainService).isNotNull();
        assertThat(mainService.getMyService()).isSameAs(myService);
    }

    @Singleton
    @Component(modules = InnerClassesTest.MyModule.class)
    public interface MyComponent {
        MainService mainService();
    }

    @Module
    public static class MyModule {
        @Provides
        public MyService provideMyService() {
            return new MyService();
        }
    }
}

