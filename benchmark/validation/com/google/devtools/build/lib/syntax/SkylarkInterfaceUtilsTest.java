/**
 * Copyright 2016 The Bazel Authors. All Rights Reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.syntax;


import com.google.devtools.build.lib.skylarkinterface.SkylarkCallable;
import com.google.devtools.build.lib.skylarkinterface.SkylarkInterfaceUtils;
import com.google.devtools.build.lib.skylarkinterface.SkylarkModule;
import com.google.devtools.build.lib.testutil.MoreAsserts;
import java.lang.reflect.Method;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test Skylark interface annotations and utilities.
 */
@RunWith(JUnit4.class)
public class SkylarkInterfaceUtilsTest {
    /**
     * MockClassA
     */
    @SkylarkModule(name = "MockClassA", doc = "MockClassA")
    public static class MockClassA {
        @SkylarkCallable(name = "foo", doc = "MockClassA#foo")
        public void foo() {
        }

        @SkylarkCallable(name = "bar", doc = "MockClassA#bar")
        public void bar() {
        }

        public void baz() {
        }
    }

    /**
     * MockInterfaceB1
     */
    @SkylarkModule(name = "MockInterfaceB1", doc = "MockInterfaceB1")
    public static interface MockInterfaceB1 {
        @SkylarkCallable(name = "foo", doc = "MockInterfaceB1#foo")
        void foo();

        @SkylarkCallable(name = "bar", doc = "MockInterfaceB1#bar")
        void bar();

        @SkylarkCallable(name = "baz", doc = "MockInterfaceB1#baz")
        void baz();
    }

    /**
     * MockInterfaceB2
     */
    @SkylarkModule(name = "MockInterfaceB2", doc = "MockInterfaceB2")
    public static interface MockInterfaceB2 {
        @SkylarkCallable(name = "baz", doc = "MockInterfaceB2#baz")
        void baz();

        @SkylarkCallable(name = "qux", doc = "MockInterfaceB2#qux")
        void qux();
    }

    /**
     * MockClassC
     */
    @SkylarkModule(name = "MockClassC", doc = "MockClassC")
    public static class MockClassC extends SkylarkInterfaceUtilsTest.MockClassA implements SkylarkInterfaceUtilsTest.MockInterfaceB1 , SkylarkInterfaceUtilsTest.MockInterfaceB2 {
        @Override
        @SkylarkCallable(name = "foo", doc = "MockClassC#foo")
        public void foo() {
        }

        @Override
        public void bar() {
        }

        @Override
        public void baz() {
        }

        @Override
        public void qux() {
        }
    }

    /**
     * MockClassD
     */
    public static class MockClassD extends SkylarkInterfaceUtilsTest.MockClassC {
        @Override
        @SkylarkCallable(name = "foo", doc = "MockClassD#foo")
        public void foo() {
        }
    }

    /**
     * A mock class that implements two unrelated module interfaces. This is invalid as the skylark
     * type of such an object is ambiguous.
     */
    public static class ImplementsTwoUnrelatedInterfaceModules implements SkylarkInterfaceUtilsTest.MockInterfaceB1 , SkylarkInterfaceUtilsTest.MockInterfaceB2 {
        @Override
        public void foo() {
        }

        @Override
        public void bar() {
        }

        @Override
        public void baz() {
        }

        @Override
        public void qux() {
        }
    }

    /**
     * ClassAModule test class
     */
    @SkylarkModule(name = "ClassAModule", doc = "ClassAModule")
    public static class ClassAModule {}

    /**
     * ExtendsClassA test class
     */
    public static class ExtendsClassA extends SkylarkInterfaceUtilsTest.ClassAModule {}

    /**
     * InterfaceBModule test interface
     */
    @SkylarkModule(name = "InterfaceBModule", doc = "InterfaceBModule")
    public static interface InterfaceBModule {}

    /**
     * ExtendsInterfaceB test interface
     */
    public static interface ExtendsInterfaceB extends SkylarkInterfaceUtilsTest.InterfaceBModule {}

    /**
     * A mock class which has two transitive superclasses ({@link ClassAModule} and
     * {@link InterfaceBModule})) which are unrelated modules. This is invalid as the skylark type
     * of such an object is ambiguous.
     *
     * In other words:
     *   AmbiguousClass -> ClassAModule
     *   AmbiguousClass -> InterfaceBModule
     *   ... but ClassAModule and InterfaceBModule have no relation.
     */
    public static class AmbiguousClass extends SkylarkInterfaceUtilsTest.ExtendsClassA implements SkylarkInterfaceUtilsTest.ExtendsInterfaceB {}

    /**
     * SubclassOfBoth test interface
     */
    @SkylarkModule(name = "SubclassOfBoth", doc = "SubclassOfBoth")
    public static class SubclassOfBoth extends SkylarkInterfaceUtilsTest.ExtendsClassA implements SkylarkInterfaceUtilsTest.ExtendsInterfaceB {}

    /**
     * A mock class similar to {@link AmbiugousClass} in that it has two separate superclass-paths
     * to skylark modules, but is resolvable.
     *
     * Concretely:
     *   UnambiguousClass -> SubclassOfBoth
     *   UnambiguousClass -> InterfaceBModule
     *   SubclassOfBoth -> InterfaceBModule
     *
     * ... so UnambiguousClass is of type SubclassOfBoth.
     */
    public static class UnambiguousClass extends SkylarkInterfaceUtilsTest.SubclassOfBoth implements SkylarkInterfaceUtilsTest.ExtendsInterfaceB {}

    /**
     * MockClassZ
     */
    public static class MockClassZ {}

    // The tests for getSkylarkModule() double as tests for getParentWithSkylarkModule(),
    // since they share an implementation.
    @Test
    public void testGetSkylarkModuleBasic() throws Exception {
        // Normal case.
        SkylarkModule ann = SkylarkInterfaceUtils.getSkylarkModule(SkylarkInterfaceUtilsTest.MockClassA.class);
        Class<?> cls = SkylarkInterfaceUtils.getParentWithSkylarkModule(SkylarkInterfaceUtilsTest.MockClassA.class);
        assertThat(ann).isNotNull();
        assertThat(ann.doc()).isEqualTo("MockClassA");
        assertThat(cls).isNotNull();
        assertThat(cls).isEqualTo(SkylarkInterfaceUtilsTest.MockClassA.class);
    }

    @Test
    public void testGetSkylarkModuleSubclass() throws Exception {
        // Subclass's annotation is used.
        SkylarkModule ann = SkylarkInterfaceUtils.getSkylarkModule(SkylarkInterfaceUtilsTest.MockClassC.class);
        Class<?> cls = SkylarkInterfaceUtils.getParentWithSkylarkModule(SkylarkInterfaceUtilsTest.MockClassC.class);
        assertThat(ann).isNotNull();
        assertThat(ann.doc()).isEqualTo("MockClassC");
        assertThat(cls).isNotNull();
        assertThat(cls).isEqualTo(SkylarkInterfaceUtilsTest.MockClassC.class);
    }

    @Test
    public void testGetSkylarkModuleSubclassNoSubannotation() throws Exception {
        // Falls back on superclass's annotation.
        SkylarkModule ann = SkylarkInterfaceUtils.getSkylarkModule(SkylarkInterfaceUtilsTest.MockClassD.class);
        Class<?> cls = SkylarkInterfaceUtils.getParentWithSkylarkModule(SkylarkInterfaceUtilsTest.MockClassD.class);
        assertThat(ann).isNotNull();
        assertThat(ann.doc()).isEqualTo("MockClassC");
        assertThat(cls).isNotNull();
        assertThat(cls).isEqualTo(SkylarkInterfaceUtilsTest.MockClassC.class);
    }

    @Test
    public void testGetSkylarkModuleNotFound() throws Exception {
        // Doesn't exist.
        SkylarkModule ann = SkylarkInterfaceUtils.getSkylarkModule(SkylarkInterfaceUtilsTest.MockClassZ.class);
        Class<?> cls = SkylarkInterfaceUtils.getParentWithSkylarkModule(SkylarkInterfaceUtilsTest.MockClassZ.class);
        assertThat(ann).isNull();
        assertThat(cls).isNull();
    }

    @Test
    public void testGetSkylarkModuleAmbiguous() throws Exception {
        MoreAsserts.assertThrows(IllegalArgumentException.class, () -> SkylarkInterfaceUtils.getSkylarkModule(SkylarkInterfaceUtilsTest.ImplementsTwoUnrelatedInterfaceModules.class));
    }

    @Test
    public void testGetSkylarkModuleTransitivelyAmbiguous() throws Exception {
        MoreAsserts.assertThrows(IllegalArgumentException.class, () -> SkylarkInterfaceUtils.getSkylarkModule(SkylarkInterfaceUtilsTest.AmbiguousClass.class));
    }

    @Test
    public void testGetSkylarkModuleUnambiguousComplex() throws Exception {
        assertThat(SkylarkInterfaceUtils.getSkylarkModule(SkylarkInterfaceUtilsTest.SubclassOfBoth.class)).isEqualTo(SkylarkInterfaceUtilsTest.SubclassOfBoth.class.getAnnotation(SkylarkModule.class));
        assertThat(SkylarkInterfaceUtils.getSkylarkModule(SkylarkInterfaceUtilsTest.UnambiguousClass.class)).isEqualTo(SkylarkInterfaceUtilsTest.SubclassOfBoth.class.getAnnotation(SkylarkModule.class));
    }

    @Test
    public void testGetSkylarkCallableBasic() throws Exception {
        // Normal case. Ensure two-arg form is consistent with one-arg form.
        Method method = SkylarkInterfaceUtilsTest.MockClassA.class.getMethod("foo");
        SkylarkCallable ann = SkylarkInterfaceUtils.getSkylarkCallable(method);
        assertThat(ann).isNotNull();
        assertThat(ann.doc()).isEqualTo("MockClassA#foo");
        SkylarkCallable ann2 = SkylarkInterfaceUtils.getSkylarkCallable(SkylarkInterfaceUtilsTest.MockClassA.class, method);
        assertThat(ann2).isEqualTo(ann);
    }

    @Test
    public void testGetSkylarkCallableSubclass() throws Exception {
        // Subclass's annotation is used.
        Method method = SkylarkInterfaceUtilsTest.MockClassC.class.getMethod("foo");
        SkylarkCallable ann = SkylarkInterfaceUtils.getSkylarkCallable(method);
        assertThat(ann).isNotNull();
        assertThat(ann.doc()).isEqualTo("MockClassC#foo");
    }

    @Test
    public void testGetSkylarkCallableSubclassNoSubannotation() throws Exception {
        // Falls back on superclass's annotation. Superclass takes precedence over interface.
        Method method = SkylarkInterfaceUtilsTest.MockClassC.class.getMethod("bar");
        SkylarkCallable ann = SkylarkInterfaceUtils.getSkylarkCallable(method);
        assertThat(ann).isNotNull();
        assertThat(ann.doc()).isEqualTo("MockClassA#bar");
    }

    @Test
    public void testGetSkylarkCallableTwoargForm() throws Exception {
        // Ensure that when passing superclass in directly, we bypass subclass's annotation.
        Method method = SkylarkInterfaceUtilsTest.MockClassC.class.getMethod("foo");
        SkylarkCallable ann = SkylarkInterfaceUtils.getSkylarkCallable(SkylarkInterfaceUtilsTest.MockClassA.class, method);
        assertThat(ann).isNotNull();
        assertThat(ann.doc()).isEqualTo("MockClassA#foo");
    }

    @Test
    public void testGetSkylarkCallableNotFound() throws Exception {
        // Null result when no annotation present...
        Method method = SkylarkInterfaceUtilsTest.MockClassA.class.getMethod("baz");
        SkylarkCallable ann = SkylarkInterfaceUtils.getSkylarkCallable(method);
        assertThat(ann).isNull();
        // ... including when it's only present in a subclass that was bypassed...
        method = SkylarkInterfaceUtilsTest.MockClassC.class.getMethod("baz");
        ann = SkylarkInterfaceUtils.getSkylarkCallable(SkylarkInterfaceUtilsTest.MockClassA.class, method);
        assertThat(ann).isNull();
        // ... or when the method itself is only in the subclass that was bypassed.
        method = SkylarkInterfaceUtilsTest.MockClassC.class.getMethod("qux");
        ann = SkylarkInterfaceUtils.getSkylarkCallable(SkylarkInterfaceUtilsTest.MockClassA.class, method);
        assertThat(ann).isNull();
    }

    @Test
    public void testGetSkylarkCallableInterface() throws Exception {
        // Search through parent interfaces. First interface takes priority.
        Method method = SkylarkInterfaceUtilsTest.MockClassC.class.getMethod("baz");
        SkylarkCallable ann = SkylarkInterfaceUtils.getSkylarkCallable(method);
        assertThat(ann).isNotNull();
        assertThat(ann.doc()).isEqualTo("MockInterfaceB1#baz");
        // Make sure both are still traversed.
        method = SkylarkInterfaceUtilsTest.MockClassC.class.getMethod("qux");
        ann = SkylarkInterfaceUtils.getSkylarkCallable(method);
        assertThat(ann).isNotNull();
        assertThat(ann.doc()).isEqualTo("MockInterfaceB2#qux");
    }
}

