/**
 * Copyright (C) 2012 Google Inc.
 * Copyright (C) 2012 Square Inc.
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
import java.util.Arrays;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class ExtensionTest {
    @Singleton
    static class A {
        @Inject
        A() {
        }
    }

    static class B {
        @Inject
        ExtensionTest.A a;
    }

    @Singleton
    static class C {
        @Inject
        ExtensionTest.A a;

        @Inject
        ExtensionTest.B b;
    }

    static class D {
        @Inject
        ExtensionTest.A a;

        @Inject
        ExtensionTest.B b;

        @Inject
        ExtensionTest.C c;
    }

    @Module(injects = { ExtensionTest.A.class, ExtensionTest.B.class })
    static class RootModule {}

    @Module(addsTo = ExtensionTest.RootModule.class, injects = { ExtensionTest.C.class, ExtensionTest.D.class })
    static class ExtensionModule {}

    @Test
    public void basicExtension() {
        Assert.assertNotNull(ObjectGraph.createWith(new TestingLoader(), new ExtensionTest.RootModule()).plus(new ExtensionTest.ExtensionModule()));
    }

    @Test
    public void basicInjection() {
        ObjectGraph root = ObjectGraph.createWith(new TestingLoader(), new ExtensionTest.RootModule());
        assertThat(root.get(ExtensionTest.A.class)).isNotNull();
        assertThat(root.get(ExtensionTest.A.class)).isSameAs(root.get(ExtensionTest.A.class));// Present and Singleton.

        assertThat(root.get(ExtensionTest.B.class)).isNotSameAs(root.get(ExtensionTest.B.class));// Not singleton.

        assertFailInjectNotRegistered(root, ExtensionTest.C.class);// Not declared in RootModule.

        assertFailInjectNotRegistered(root, ExtensionTest.D.class);// Not declared in RootModule.

        // Extension graph behaves as the root graph would for root-ish things.
        ObjectGraph extension = root.plus(new ExtensionTest.ExtensionModule());
        assertThat(root.get(ExtensionTest.A.class)).isSameAs(extension.get(ExtensionTest.A.class));
        assertThat(root.get(ExtensionTest.B.class)).isNotSameAs(extension.get(ExtensionTest.B.class));
        assertThat(root.get(ExtensionTest.B.class).a).isSameAs(extension.get(ExtensionTest.B.class).a);
        assertThat(extension.get(ExtensionTest.C.class).a).isNotNull();
        assertThat(extension.get(ExtensionTest.D.class).c).isNotNull();
    }

    @Test
    public void scopedGraphs() {
        ObjectGraph app = ObjectGraph.createWith(new TestingLoader(), new ExtensionTest.RootModule());
        assertThat(app.get(ExtensionTest.A.class)).isNotNull();
        assertThat(app.get(ExtensionTest.A.class)).isSameAs(app.get(ExtensionTest.A.class));
        assertThat(app.get(ExtensionTest.B.class)).isNotSameAs(app.get(ExtensionTest.B.class));
        assertFailInjectNotRegistered(app, ExtensionTest.C.class);
        assertFailInjectNotRegistered(app, ExtensionTest.D.class);
        ObjectGraph request1 = app.plus(new ExtensionTest.ExtensionModule());
        ObjectGraph request2 = app.plus(new ExtensionTest.ExtensionModule());
        for (ObjectGraph request : Arrays.asList(request1, request2)) {
            assertThat(request.get(ExtensionTest.A.class)).isNotNull();
            assertThat(request.get(ExtensionTest.A.class)).isSameAs(request.get(ExtensionTest.A.class));
            assertThat(request.get(ExtensionTest.B.class)).isNotSameAs(request.get(ExtensionTest.B.class));
            assertThat(request.get(ExtensionTest.C.class)).isNotNull();
            assertThat(request.get(ExtensionTest.C.class)).isSameAs(request.get(ExtensionTest.C.class));
            assertThat(request.get(ExtensionTest.D.class)).isNotSameAs(request.get(ExtensionTest.D.class));
        }
        // Singletons are one-per-graph-instance where they are declared.
        assertThat(request1.get(ExtensionTest.C.class)).isNotSameAs(request2.get(ExtensionTest.C.class));
        // Singletons that come from common roots should be one-per-common-graph-instance.
        assertThat(request1.get(ExtensionTest.C.class).a).isSameAs(request2.get(ExtensionTest.C.class).a);
    }
}

