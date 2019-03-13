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
package it.cosenonjaviste.daggermock.demo.robolectric;


import it.cosenonjaviste.daggermock.DaggerMockRule;
import it.cosenonjaviste.daggermock.demo.MainActivity;
import it.cosenonjaviste.daggermock.demo.MyComponent;
import it.cosenonjaviste.daggermock.demo.MyModule;
import it.cosenonjaviste.daggermock.demo.MyPrinter;
import it.cosenonjaviste.daggermock.demo.RestService;
import it.cosenonjaviste.daggeroverride.BuildConfig;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class MainActivityTest {
    @Rule
    public final DaggerMockRule<MyComponent> rule = new DaggerMockRule(MyComponent.class, new MyModule()).set(new DaggerMockRule.ComponentSetter<MyComponent>() {
        @Override
        public void setComponent(MyComponent component) {
            ((it.cosenonjaviste.daggermock.demo.App) (RuntimeEnvironment.application)).setComponent(component);
        }
    });

    @Mock
    RestService restService;

    @Mock
    MyPrinter myPrinter;

    @Test
    public void testCreateActivity() {
        Mockito.when(restService.getSomething()).thenReturn("abc");
        Robolectric.setupActivity(MainActivity.class);
        Mockito.verify(myPrinter).print("ABC");
    }
}

