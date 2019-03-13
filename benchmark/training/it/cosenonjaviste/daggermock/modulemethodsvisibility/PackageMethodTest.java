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
import org.junit.Assert;
import org.junit.Test;


public class PackageMethodTest {
    @Test
    public void testErrorOnPackageMethods() throws Throwable {
        try {
            DaggerMockRule<PackageMethodTest.MyComponent> rule = new DaggerMockRule(PackageMethodTest.MyComponent.class, new PackageMethodTest.MyModule());
            rule.apply(null, null, this).evaluate();
            Assert.fail();
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo(("The following methods must be declared public or protected:\n" + "it.cosenonjaviste.daggermock.modulemethodsvisibility.MyService it.cosenonjaviste.daggermock.modulemethodsvisibility.PackageMethodTest$MyModule.provideMyService()"));
        }
    }

    @Module
    public static class MyModule {
        @Provides
        MyService provideMyService() {
            return new MyService();
        }

        private void privateMethod() {
        }
    }

    @Singleton
    @Component(modules = PackageMethodTest.MyModule.class)
    public interface MyComponent {
        MainService mainService();
    }
}

