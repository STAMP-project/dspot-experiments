/**
 * Copyright (C) 2013 Google Inc.
 * Copyright (C) 2013 Square Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dagger;


import dagger.internal.TestingLoader;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class ExtensionWithStateTest {
    static class A {}

    static class B {
        @Inject
        ExtensionWithStateTest.A a;
    }

    @Module(injects = ExtensionWithStateTest.A.class// for testing
    , complete = false)
    static class RootModule {
        final ExtensionWithStateTest.A a;

        RootModule(ExtensionWithStateTest.A a) {
            this.a = a;
        }

        @Provides
        ExtensionWithStateTest.A provideA() {
            return a;
        }
    }

    @Module(addsTo = ExtensionWithStateTest.RootModule.class, injects = { ExtensionWithStateTest.B.class })
    static class ExtensionModule {}

    @Test
    public void basicInjectionWithExtension() {
        ExtensionWithStateTest.A a = new ExtensionWithStateTest.A();
        ObjectGraph root = ObjectGraph.createWith(new TestingLoader(), new ExtensionWithStateTest.RootModule(a));
        assertThat(root.get(ExtensionWithStateTest.A.class)).isSameAs(a);
        // Extension graph behaves as the root graph would for root-ish things.
        ObjectGraph extension = root.plus(new ExtensionWithStateTest.ExtensionModule());
        assertThat(extension.get(ExtensionWithStateTest.A.class)).isSameAs(a);
        assertThat(extension.get(ExtensionWithStateTest.B.class).a).isSameAs(a);
    }
}

