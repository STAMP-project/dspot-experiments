/**
 * Copyright 2016 2017 Fabio Collini.
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
package it.cosenonjaviste.daggermock.modulemethodsvisibility;


import dagger.Component;
import dagger.Module;
import dagger.Provides;
import it.cosenonjaviste.daggermock.DaggerMockRule;
import javax.inject.Singleton;
import org.junit.Assert;
import org.junit.Test;


public class FinalMethodTest {
    @Test
    public void testErrorOnFinalMethods() throws Throwable {
        try {
            DaggerMockRule<FinalMethodTest.MyComponent> rule = new DaggerMockRule(FinalMethodTest.MyComponent.class, new FinalMethodTest.MyModule());
            rule.apply(null, null, this).evaluate();
            Assert.fail();
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo(("The following methods must be non final:\n" + ("public final it.cosenonjaviste.daggermock.modulemethodsvisibility.MyService it.cosenonjaviste.daggermock.modulemethodsvisibility.FinalMethodTest$MyModule.provideMyService()\n" + " or using MockMaker plugin")));
        }
    }

    @Module
    public static class MyModule {
        @Provides
        public final MyService provideMyService() {
            return new MyService();
        }

        private void privateMethod() {
        }
    }

    @Singleton
    @Component(modules = FinalMethodTest.MyModule.class)
    public interface MyComponent {
        MainService mainService();
    }
}

