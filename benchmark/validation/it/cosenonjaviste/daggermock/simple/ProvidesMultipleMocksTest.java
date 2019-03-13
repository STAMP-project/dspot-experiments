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
import org.mockito.Mockito;


public class ProvidesMultipleMocksTest {
    @Rule
    public final DaggerMockRule<MyComponent> rule = new DaggerMockRule(MyComponent.class, new MyModule()).providesMock(MyService.class, MyService2.class).set(new DaggerMockRule.ComponentSetter<MyComponent>() {
        @Override
        public void setComponent(MyComponent component) {
            it.cosenonjaviste.daggermock.simple.mainService = component.mainService();
        }
    });

    private MainService mainService;

    @Test
    public void testProvideMultipleMock() {
        Mockito.when(mainService.getMyService().get()).thenReturn("mock");
        Mockito.when(mainService.myService2.get()).thenReturn("!!!");
        assertThat(mainService.get2()).isEqualTo("mock!!!");
    }
}

