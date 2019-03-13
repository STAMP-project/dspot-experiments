/**
 * Copyright 2016 Fabio Collini.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package it.cosenonjaviste.daggermock.dependency;


import it.cosenonjaviste.daggermock.DaggerMockRule;
import it.cosenonjaviste.daggermock.InjectFromComponent;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;


public class InjectFromSubComponentTest {
    @Rule
    public final DaggerMockRule<MyComponent> rule = new DaggerMockRule(MyComponent.class, new MyModule()).addComponentDependency(MyComponent2.class, new MyModule2());

    @Mock
    MyService myService;

    @InjectFromComponent
    MyService2 myService2;

    @Test
    public void testInjectFromComponentOnObjectDeclaredInDependentComponent() {
        assertThat(myService2).isNotNull();
    }
}

