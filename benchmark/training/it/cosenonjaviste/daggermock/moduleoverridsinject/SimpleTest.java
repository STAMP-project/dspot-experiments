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
package it.cosenonjaviste.daggermock.moduleoverridsinject;


import it.cosenonjaviste.daggermock.DaggerMockRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;


public class SimpleTest {
    @Rule
    public final DaggerMockRule<MyTestComponent> rule = new DaggerMockRule(MyTestComponent.class, new MyModule()).set(new DaggerMockRule.ComponentSetter<MyTestComponent>() {
        @Override
        public void setComponent(MyTestComponent component) {
            it.cosenonjaviste.daggermock.moduleoverridsinject.mainService = component.mainService();
        }
    });

    @Mock
    MyService myService;

    private MainService mainService;

    @Test
    public void testConstructorArgs() {
        assertThat(mainService).isNotNull();
        assertThat(mainService.getMyService()).isSameAs(myService);
    }
}

