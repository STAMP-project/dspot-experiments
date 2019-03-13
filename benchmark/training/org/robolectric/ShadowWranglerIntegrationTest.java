package org.robolectric;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;
import org.robolectric.annotation.internal.Instrument;
import org.robolectric.internal.SandboxTestRunner;
import org.robolectric.internal.bytecode.SandboxConfig;
import org.robolectric.internal.bytecode.ShadowWrangler;
import org.robolectric.shadow.api.Shadow;
import org.robolectric.testing.Foo;
import org.robolectric.testing.ShadowFoo;


@RunWith(SandboxTestRunner.class)
public class ShadowWranglerIntegrationTest {
    private static final boolean YES = true;

    private String name;

    @Test
    @SandboxConfig(shadows = { ShadowWranglerIntegrationTest.ShadowForAClassWithDefaultConstructor_HavingNoConstructorDelegate.class })
    public void testConstructorInvocation_WithDefaultConstructorAndNoConstructorDelegateOnShadowClass() throws Exception {
        ShadowWranglerIntegrationTest.AClassWithDefaultConstructor instance = new ShadowWranglerIntegrationTest.AClassWithDefaultConstructor();
        assertThat(Shadow.<Object>extract(instance)).isInstanceOf(ShadowWranglerIntegrationTest.ShadowForAClassWithDefaultConstructor_HavingNoConstructorDelegate.class);
        assertThat(instance.initialized).isTrue();
    }

    @Test
    @SandboxConfig(shadows = { ShadowFoo.class })
    public void testConstructorInvocation() throws Exception {
        Foo foo = new Foo(name);
        Assert.assertSame(name, shadowOf(foo).name);
    }

    @Test
    @SandboxConfig(shadows = { ShadowFoo.class })
    public void testRealObjectAnnotatedFieldsAreSetBeforeConstructorIsCalled() throws Exception {
        Foo foo = new Foo(name);
        Assert.assertSame(name, shadowOf(foo).name);
        Assert.assertSame(foo, shadowOf(foo).realFooField);
        Assert.assertSame(foo, shadowOf(foo).realFooInConstructor);
        Assert.assertSame(foo, shadowOf(foo).realFooInParentConstructor);
    }

    @Test
    @SandboxConfig(shadows = { ShadowFoo.class })
    public void testMethodDelegation() throws Exception {
        Foo foo = new Foo(name);
        Assert.assertSame(name, foo.getName());
    }

    @Test
    @SandboxConfig(shadows = { ShadowWranglerIntegrationTest.WithEquals.class })
    public void testEqualsMethodDelegation() throws Exception {
        Foo foo1 = new Foo(name);
        Foo foo2 = new Foo(name);
        Assert.assertEquals(foo1, foo2);
    }

    @Test
    @SandboxConfig(shadows = { ShadowWranglerIntegrationTest.WithEquals.class })
    public void testHashCodeMethodDelegation() throws Exception {
        Foo foo = new Foo(name);
        Assert.assertEquals(42, foo.hashCode());
    }

    @Test
    @SandboxConfig(shadows = { ShadowWranglerIntegrationTest.WithToString.class })
    public void testToStringMethodDelegation() throws Exception {
        Foo foo = new Foo(name);
        Assert.assertEquals("the expected string", foo.toString());
    }

    @Test
    @SandboxConfig(shadows = { ShadowFoo.class })
    public void testShadowSelectionSearchesSuperclasses() throws Exception {
        ShadowWranglerIntegrationTest.TextFoo textFoo = new ShadowWranglerIntegrationTest.TextFoo(name);
        Assert.assertEquals(ShadowFoo.class, Shadow.extract(textFoo).getClass());
    }

    @Test
    @SandboxConfig(shadows = { ShadowFoo.class, ShadowWranglerIntegrationTest.ShadowTextFoo.class })
    public void shouldUseMostSpecificShadow() throws Exception {
        ShadowWranglerIntegrationTest.TextFoo textFoo = new ShadowWranglerIntegrationTest.TextFoo(name);
        assertThat(shadowOf(textFoo)).isInstanceOf(ShadowWranglerIntegrationTest.ShadowTextFoo.class);
    }

    @Test
    public void testPrimitiveArrays() throws Exception {
        Class<?> objArrayClass = ShadowWrangler.loadClass("java.lang.Object[]", getClass().getClassLoader());
        Assert.assertTrue(objArrayClass.isArray());
        Assert.assertEquals(Object.class, objArrayClass.getComponentType());
        Class<?> intArrayClass = ShadowWrangler.loadClass("int[]", getClass().getClassLoader());
        Assert.assertTrue(intArrayClass.isArray());
        Assert.assertEquals(Integer.TYPE, intArrayClass.getComponentType());
    }

    @Test
    @SandboxConfig(shadows = ShadowWranglerIntegrationTest.ShadowThrowInShadowMethod.class)
    public void shouldRemoveNoiseFromShadowedStackTraces() throws Exception {
        ShadowWranglerIntegrationTest.ThrowInShadowMethod instance = new ShadowWranglerIntegrationTest.ThrowInShadowMethod();
        Exception e = null;
        try {
            instance.method();
        } catch (Exception e1) {
            e = e1;
        }
        Assert.assertNotNull(e);
        Assert.assertEquals(IOException.class, e.getClass());
        Assert.assertEquals("fake exception", e.getMessage());
        StackTraceElement[] stackTrace = e.getStackTrace();
        assertThat(stackTrace[0].getClassName()).isEqualTo(ShadowWranglerIntegrationTest.ShadowThrowInShadowMethod.class.getName());
        assertThat(stackTrace[0].getMethodName()).isEqualTo("method");
        assertThat(stackTrace[0].getLineNumber()).isGreaterThan(0);
        assertThat(stackTrace[1].getClassName()).isEqualTo(ShadowWranglerIntegrationTest.ThrowInShadowMethod.class.getName());
        assertThat(stackTrace[1].getMethodName()).isEqualTo("method");
        assertThat(stackTrace[1].getLineNumber()).isLessThan(0);
        assertThat(stackTrace[2].getClassName()).isEqualTo(ShadowWranglerIntegrationTest.class.getName());
        assertThat(stackTrace[2].getMethodName()).isEqualTo("shouldRemoveNoiseFromShadowedStackTraces");
        assertThat(stackTrace[2].getLineNumber()).isGreaterThan(0);
    }

    @Instrument
    public static class ThrowInShadowMethod {
        public void method() throws IOException {
        }
    }

    @Implements(ShadowWranglerIntegrationTest.ThrowInShadowMethod.class)
    public static class ShadowThrowInShadowMethod {
        public void method() throws IOException {
            throw new IOException("fake exception");
        }
    }

    @Test
    @SandboxConfig(shadows = ShadowWranglerIntegrationTest.ShadowThrowInRealMethod.class)
    public void shouldRemoveNoiseFromUnshadowedStackTraces() throws Exception {
        ShadowWranglerIntegrationTest.ThrowInRealMethod instance = new ShadowWranglerIntegrationTest.ThrowInRealMethod();
        Exception e = null;
        try {
            instance.method();
        } catch (Exception e1) {
            e = e1;
        }
        Assert.assertNotNull(e);
        Assert.assertEquals(IOException.class, e.getClass());
        Assert.assertEquals("fake exception", e.getMessage());
        StackTraceElement[] stackTrace = e.getStackTrace();
        assertThat(stackTrace[0].getClassName()).isEqualTo(ShadowWranglerIntegrationTest.ThrowInRealMethod.class.getName());
        assertThat(stackTrace[0].getMethodName()).isEqualTo("method");
        assertThat(stackTrace[0].getLineNumber()).isGreaterThan(0);
        assertThat(stackTrace[1].getClassName()).isEqualTo(ShadowWranglerIntegrationTest.class.getName());
        assertThat(stackTrace[1].getMethodName()).isEqualTo("shouldRemoveNoiseFromUnshadowedStackTraces");
        assertThat(stackTrace[1].getLineNumber()).isGreaterThan(0);
    }

    @Instrument
    public static class ThrowInRealMethod {
        public void method() throws IOException {
            throw new IOException("fake exception");
        }
    }

    @Implements(ShadowWranglerIntegrationTest.ThrowInRealMethod.class)
    public static class ShadowThrowInRealMethod {}

    @Test
    @SandboxConfig(shadows = { ShadowWranglerIntegrationTest.Shadow2OfChild.class, ShadowWranglerIntegrationTest.ShadowOfParent.class })
    public void whenShadowMethodIsOverriddenInShadowWithSameShadowedClass_shouldUseOverriddenMethod() throws Exception {
        assertThat(new ShadowWranglerIntegrationTest.Child().get()).isEqualTo("get from Shadow2OfChild");
    }

    @Test
    @SandboxConfig(shadows = { ShadowWranglerIntegrationTest.Shadow22OfChild.class, ShadowWranglerIntegrationTest.ShadowOfParent.class })
    public void whenShadowMethodIsNotOverriddenInShadowWithSameShadowedClass_shouldUseOverriddenMethod() throws Exception {
        assertThat(new ShadowWranglerIntegrationTest.Child().get()).isEqualTo("get from Shadow2OfChild");
    }

    @Test
    @SandboxConfig(shadows = { ShadowWranglerIntegrationTest.Shadow3OfChild.class, ShadowWranglerIntegrationTest.ShadowOfParent.class })
    public void whenShadowMethodIsOverriddenInShadowOfAnotherClass_shouldNotUseShadowSuperclassMethods() throws Exception {
        assertThat(new ShadowWranglerIntegrationTest.Child().get()).isEqualTo("from child (from shadow of parent)");
    }

    @Test
    @SandboxConfig(shadows = { ShadowWranglerIntegrationTest.ShadowOfParentWithPackageImpl.class })
    public void whenShadowMethodIsntCorrectlyVisible_shouldNotUseShadowMethods() throws Exception {
        assertThat(new ShadowWranglerIntegrationTest.Parent().get()).isEqualTo("from parent");
    }

    @Instrument
    public static class Parent {
        public String get() {
            return "from parent";
        }
    }

    @Instrument
    public static class Child extends ShadowWranglerIntegrationTest.Parent {
        @Override
        public String get() {
            return ("from child (" + (super.get())) + ")";
        }
    }

    @Implements(ShadowWranglerIntegrationTest.Parent.class)
    public static class ShadowOfParent {
        @Implementation
        protected String get() {
            return "from shadow of parent";
        }
    }

    @Implements(ShadowWranglerIntegrationTest.Parent.class)
    public static class ShadowOfParentWithPackageImpl {
        @Implementation
        String get() {
            return "from ShadowOfParentWithPackageImpl";
        }
    }

    @Implements(ShadowWranglerIntegrationTest.Child.class)
    public static class ShadowOfChild extends ShadowWranglerIntegrationTest.ShadowOfParent {
        @Implementation
        @Override
        protected String get() {
            return "get from ShadowOfChild";
        }
    }

    @Implements(ShadowWranglerIntegrationTest.Child.class)
    public static class Shadow2OfChild extends ShadowWranglerIntegrationTest.ShadowOfChild {
        @Implementation
        @Override
        protected String get() {
            return "get from Shadow2OfChild";
        }
    }

    @Implements(ShadowWranglerIntegrationTest.Child.class)
    public static class Shadow22OfChild extends ShadowWranglerIntegrationTest.Shadow2OfChild {}

    public static class SomethingOtherThanChild extends ShadowWranglerIntegrationTest.Child {}

    @Implements(ShadowWranglerIntegrationTest.SomethingOtherThanChild.class)
    public static class Shadow3OfChild extends ShadowWranglerIntegrationTest.ShadowOfChild {
        @Implementation
        @Override
        protected String get() {
            return "get from Shadow3OfChild";
        }
    }

    @Implements(Foo.class)
    public static class WithEquals {
        @Implementation
        protected void __constructor__(String s) {
        }

        @Override
        @Implementation
        public boolean equals(Object o) {
            return true;
        }

        @Override
        @Implementation
        public int hashCode() {
            return 42;
        }
    }

    @Implements(Foo.class)
    public static class WithToString {
        @Implementation
        protected void __constructor__(String s) {
        }

        @Override
        @Implementation
        public String toString() {
            return "the expected string";
        }
    }

    @Implements(ShadowWranglerIntegrationTest.TextFoo.class)
    public static class ShadowTextFoo extends ShadowFoo {}

    @Instrument
    public static class TextFoo extends Foo {
        public TextFoo(String s) {
            super(s);
        }
    }

    @Implements(Foo.class)
    public static class ShadowFooParent {
        @RealObject
        private Foo realFoo;

        public Foo realFooInParentConstructor;

        @Implementation
        protected void __constructor__(String name) {
            realFooInParentConstructor = realFoo;
        }
    }

    @Instrument
    public static class AClassWithDefaultConstructor {
        public boolean initialized;

        public AClassWithDefaultConstructor() {
            initialized = true;
        }
    }

    @Implements(ShadowWranglerIntegrationTest.AClassWithDefaultConstructor.class)
    public static class ShadowForAClassWithDefaultConstructor_HavingNoConstructorDelegate {}

    @SandboxConfig(shadows = ShadowWranglerIntegrationTest.ShadowAClassWithDifficultArgs.class)
    @Test
    public void shouldAllowLooseSignatureMatches() throws Exception {
        assertThat(new ShadowWranglerIntegrationTest.AClassWithDifficultArgs().aMethod("bc")).isEqualTo("abc");
    }

    @Implements(value = ShadowWranglerIntegrationTest.AClassWithDifficultArgs.class, looseSignatures = true)
    public static class ShadowAClassWithDifficultArgs {
        @Implementation
        protected Object aMethod(Object s) {
            return "a" + s;
        }
    }

    @Instrument
    public static class AClassWithDifficultArgs {
        public CharSequence aMethod(CharSequence s) {
            return s;
        }
    }

    @Test
    @SandboxConfig(shadows = ShadowWranglerIntegrationTest.ShadowOfAClassWithStaticInitializer.class)
    public void classesWithInstrumentedShadowsDontDoubleInitialize() throws Exception {
        // if we didn't reject private shadow methods, __staticInitializer__ on the shadow
        // would be executed twice.
        new ShadowWranglerIntegrationTest.AClassWithStaticInitializer();
        assertThat(ShadowWranglerIntegrationTest.ShadowOfAClassWithStaticInitializer.initCount).isEqualTo(1);
        assertThat(ShadowWranglerIntegrationTest.AClassWithStaticInitializer.initCount).isEqualTo(1);
    }

    @Instrument
    public static class AClassWithStaticInitializer {
        static int initCount;

        static {
            (ShadowWranglerIntegrationTest.AClassWithStaticInitializer.initCount)++;
        }
    }

    // because it's fairly common that people accidentally instrument their own shadows
    @Instrument
    @Implements(ShadowWranglerIntegrationTest.AClassWithStaticInitializer.class)
    public static class ShadowOfAClassWithStaticInitializer {
        static int initCount;

        static {
            (ShadowWranglerIntegrationTest.ShadowOfAClassWithStaticInitializer.initCount)++;
        }
    }

    @Test
    @SandboxConfig(shadows = ShadowWranglerIntegrationTest.Shadow22OfAClassWithBrokenStaticInitializer.class)
    public void staticInitializerShadowMethodsObeySameRules() throws Exception {
        new ShadowWranglerIntegrationTest.AClassWithBrokenStaticInitializer();
    }

    @Instrument
    public static class AClassWithBrokenStaticInitializer {
        static {
            if (ShadowWranglerIntegrationTest.YES)
                throw new RuntimeException("broken!");

        }
    }

    @Implements(ShadowWranglerIntegrationTest.AClassWithBrokenStaticInitializer.class)
    public static class Shadow2OfAClassWithBrokenStaticInitializer {
        @Implementation
        protected static void __staticInitializer__() {
            // don't call real static initializer
        }
    }

    @Implements(ShadowWranglerIntegrationTest.AClassWithBrokenStaticInitializer.class)
    public static class Shadow22OfAClassWithBrokenStaticInitializer extends ShadowWranglerIntegrationTest.Shadow2OfAClassWithBrokenStaticInitializer {}
}

