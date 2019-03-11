package org.robolectric;


import ShadowConstants.CONSTRUCTOR_METHOD_NAME;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.internal.Instrument;
import org.robolectric.internal.SandboxTestRunner;
import org.robolectric.internal.bytecode.SandboxConfig;
import org.robolectric.shadow.api.Shadow;
import org.robolectric.testing.AnUninstrumentedClass;
import org.robolectric.testing.Pony;


@RunWith(SandboxTestRunner.class)
public class ShadowingTest {
    @Test
    @SandboxConfig(shadows = { ShadowingTest.ShadowAccountManagerForTests.class })
    public void testStaticMethodsAreDelegated() throws Exception {
        Object arg = Mockito.mock(Object.class);
        ShadowingTest.AccountManager.get(arg);
        assertThat(ShadowingTest.ShadowAccountManagerForTests.wasCalled).isTrue();
        assertThat(ShadowingTest.ShadowAccountManagerForTests.arg).isSameAs(arg);
    }

    @Implements(ShadowingTest.AccountManager.class)
    public static class ShadowAccountManagerForTests {
        public static boolean wasCalled = false;

        public static Object arg;

        public static ShadowingTest.AccountManager get(Object arg) {
            ShadowingTest.ShadowAccountManagerForTests.wasCalled = true;
            ShadowingTest.ShadowAccountManagerForTests.arg = arg;
            return Mockito.mock(ShadowingTest.AccountManager.class);
        }
    }

    static class Context {}

    static class AccountManager {
        public static ShadowingTest.AccountManager get(Object arg) {
            return null;
        }
    }

    @Test
    @SandboxConfig(shadows = { ShadowingTest.ShadowClassWithProtectedMethod.class })
    public void testProtectedMethodsAreDelegated() throws Exception {
        ShadowingTest.ClassWithProtectedMethod overlay = new ShadowingTest.ClassWithProtectedMethod();
        Assert.assertEquals("shadow name", overlay.getName());
    }

    @Implements(ShadowingTest.ClassWithProtectedMethod.class)
    public static class ShadowClassWithProtectedMethod {
        @Implementation
        protected String getName() {
            return "shadow name";
        }
    }

    @Instrument
    public static class ClassWithProtectedMethod {
        protected String getName() {
            return "protected name";
        }
    }

    @Test
    @SandboxConfig(shadows = { ShadowingTest.ShadowPaintForTests.class })
    public void testNativeMethodsAreDelegated() throws Exception {
        ShadowingTest.Paint paint = new ShadowingTest.Paint();
        paint.setColor(1234);
        assertThat(paint.getColor()).isEqualTo(1234);
    }

    @Instrument
    static class Paint {
        public native void setColor(int color);

        public native int getColor();
    }

    @Implements(ShadowingTest.Paint.class)
    public static class ShadowPaintForTests {
        private int color;

        @Implementation
        protected void setColor(int color) {
            this.color = color;
        }

        @Implementation
        protected int getColor() {
            return color;
        }
    }

    @Implements(ShadowingTest.ClassWithNoDefaultConstructor.class)
    public static class ShadowForClassWithNoDefaultConstructor {
        public static boolean shadowDefaultConstructorCalled = false;

        public static boolean shadowDefaultConstructorImplementorCalled = false;

        public ShadowForClassWithNoDefaultConstructor() {
            ShadowingTest.ShadowForClassWithNoDefaultConstructor.shadowDefaultConstructorCalled = true;
        }

        @Implementation
        protected void __constructor__() {
            ShadowingTest.ShadowForClassWithNoDefaultConstructor.shadowDefaultConstructorImplementorCalled = true;
        }
    }

    @Instrument
    @SuppressWarnings({ "UnusedDeclaration" })
    public static class ClassWithNoDefaultConstructor {
        ClassWithNoDefaultConstructor(String string) {
        }
    }

    @Test
    @SandboxConfig(shadows = { Pony.ShadowPony.class })
    public void directlyOn_shouldCallThroughToOriginalMethodBody() throws Exception {
        Pony pony = new Pony();
        Assert.assertEquals("Fake whinny! You're on my neck!", pony.ride("neck"));
        Assert.assertEquals("Whinny! You're on my neck!", Shadow.directlyOn(pony, Pony.class).ride("neck"));
        Assert.assertEquals("Fake whinny! You're on my haunches!", pony.ride("haunches"));
    }

    @Test
    @SandboxConfig(shadows = { Pony.ShadowPony.class })
    public void shouldCallRealForUnshadowedMethod() throws Exception {
        Assert.assertEquals("Off I saunter to the salon!", new Pony().saunter("the salon"));
    }

    static class TextView {}

    static class ColorStateList {
        public ColorStateList(int[][] ints, int[] ints1) {
        }
    }

    static class TypedArray {}

    @Implements(ShadowingTest.TextView.class)
    public static class TextViewWithDummyGetTextColorsMethod {
        public static ShadowingTest.ColorStateList getTextColors(ShadowingTest.Context context, ShadowingTest.TypedArray attrs) {
            return new ShadowingTest.ColorStateList(new int[0][0], new int[0]);
        }
    }

    @Test
    @SandboxConfig(shadows = ShadowingTest.ShadowOfClassWithSomeConstructors.class)
    public void shouldGenerateSeparatedConstructorBodies() throws Exception {
        ShadowingTest.ClassWithSomeConstructors o = new ShadowingTest.ClassWithSomeConstructors("my name");
        Assert.assertNull(o.name);
        Method realConstructor = o.getClass().getDeclaredMethod(CONSTRUCTOR_METHOD_NAME, String.class);
        realConstructor.setAccessible(true);
        realConstructor.invoke(o, "my name");
        Assert.assertEquals("my name", o.name);
    }

    @Instrument
    public static class ClassWithSomeConstructors {
        public String name;

        public ClassWithSomeConstructors(String name) {
            this.name = name;
        }
    }

    @Implements(ShadowingTest.ClassWithSomeConstructors.class)
    public static class ShadowOfClassWithSomeConstructors {
        @Implementation
        protected void __constructor__(String s) {
        }
    }

    @Test
    @SandboxConfig(shadows = { ShadowingTest.ShadowApiImplementedClass.class })
    public void withNonApiSubclassesWhichExtendApi_shouldStillBeInvoked() throws Exception {
        Assert.assertEquals("did foo", new ShadowingTest.NonApiSubclass().doSomething("foo"));
    }

    public static class NonApiSubclass extends ShadowingTest.ApiImplementedClass {
        public String doSomething(String value) {
            return "did " + value;
        }
    }

    @Instrument
    public static class ApiImplementedClass {}

    @Implements(ShadowingTest.ApiImplementedClass.class)
    public static class ShadowApiImplementedClass {}

    @Test
    public void shouldNotInstrumentClassIfNotAddedToConfig() {
        Assert.assertEquals(1, new ShadowingTest.NonInstrumentedClass().plus(0));
    }

    @Test
    @SandboxConfig(shadows = { ShadowingTest.ShadowNonInstrumentedClass.class })
    public void shouldInstrumentClassIfAddedToConfig() {
        Assert.assertEquals(2, new ShadowingTest.NonInstrumentedClass().plus(0));
    }

    public static class NonInstrumentedClass {
        public int plus(int x) {
            return x + 1;
        }
    }

    @Implements(ShadowingTest.NonInstrumentedClass.class)
    public static class ShadowNonInstrumentedClass {
        @Implementation
        protected int plus(int x) {
            return x + 2;
        }
    }

    @Test
    public void shouldNotInstrumentPackageIfNotAddedToConfig() throws Exception {
        Class<?> clazz = Class.forName(AnUninstrumentedClass.class.getName());
        Assert.assertTrue(Modifier.isFinal(clazz.getModifiers()));
    }

    @Test
    @SandboxConfig(instrumentedPackages = { "org.robolectric.testing" })
    public void shouldInstrumentPackageIfAddedToConfig() throws Exception {
        Class<?> clazz = Class.forName(AnUninstrumentedClass.class.getName());
        Assert.assertFalse(Modifier.isFinal(clazz.getModifiers()));
    }
}

