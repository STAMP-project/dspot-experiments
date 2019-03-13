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
import it.cosenonjaviste.daggermock.InjectFromComponent;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;


public class DecoratorTest {
    @Rule
    public final DaggerMockRule<MyComponent> rule = new DaggerMockRule(MyComponent.class, new MyModule()).decorates(MyService.class, new DaggerMockRule.ObjectDecorator<MyService>() {
        @Override
        public MyService decorate(MyService obj) {
            return Mockito.spy(obj);
        }
    });

    @InjectFromComponent
    MainService mainService;

    @Test
    public void testObjectIsDecorated() {
        mainService.get();
        Mockito.verify(mainService.getMyService()).get();
    }
}

