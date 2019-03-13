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
package it.cosenonjaviste.daggermock.simple;


import it.cosenonjaviste.daggermock.DaggerMockRule;
import org.junit.Rule;
import org.junit.Test;


public class ProvidesNotSingletonTest {
    @Rule
    public final DaggerMockRule<MyComponent> rule = new DaggerMockRule(MyComponent.class, new MyModule()).provides(MyService.class, new javax.inject.Provider<MyService>() {
        @Override
        public MyService get() {
            return new MyService();
        }
    }).set(new DaggerMockRule.ComponentSetter<MyComponent>() {
        @Override
        public void setComponent(MyComponent component) {
            it.cosenonjaviste.daggermock.simple.myService1 = component.myService();
            it.cosenonjaviste.daggermock.simple.myService2 = component.myService();
        }
    });

    private MyService myService1;

    private MyService myService2;

    @Test
    public void testProvidesDifferentObjects() {
        assertThat(myService1).isNotSameAs(myService2);
    }
}

