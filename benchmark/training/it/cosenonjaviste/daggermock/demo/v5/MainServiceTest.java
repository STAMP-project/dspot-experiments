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
package it.cosenonjaviste.daggermock.demo.v5;


import it.cosenonjaviste.daggermock.DaggerMockRule;
import it.cosenonjaviste.daggermock.InjectFromComponent;
import it.cosenonjaviste.daggermock.demo.MainActivity;
import it.cosenonjaviste.daggermock.demo.MainService;
import it.cosenonjaviste.daggermock.demo.MyComponent;
import it.cosenonjaviste.daggermock.demo.MyModule;
import it.cosenonjaviste.daggermock.demo.MyPrinter;
import it.cosenonjaviste.daggermock.demo.RestService;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class MainServiceTest {
    @Rule
    public final DaggerMockRule<MyComponent> rule = new DaggerMockRule(MyComponent.class, new MyModule());

    @Mock
    RestService restService;

    @Mock
    MyPrinter myPrinter;

    @InjectFromComponent(MainActivity.class)
    MainService mainService;

    @Test
    public void testDoSomething() {
        Mockito.when(restService.getSomething()).thenReturn("abc");
        mainService.doSomething();
        Mockito.verify(myPrinter).print("ABC");
    }
}

