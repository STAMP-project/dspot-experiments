package org.robolectric.util.reflector;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class ReflectorTest {
    private ReflectorTest.SomeClass someClass;

    private ReflectorTest._SomeClass_ reflector;

    private ReflectorTest._SomeClass_ staticReflector;

    @Test
    public void reflector_shouldCallPrivateMethod() throws Exception {
        assertThat(reflector.someMethod("a", "b")).isEqualTo("a-b-c (someMethod)");
    }

    @Test
    public void reflector_shouldHonorWithTypeAnnotationForParams() throws Exception {
        assertThat(reflector.anotherMethod("a", "b")).isEqualTo("a-b-c (anotherMethod)");
    }

    @Test
    public void reflector_defaultMethodsShouldWork() throws Exception {
        assertThat(reflector.defaultMethod("someMethod", "a", "b")).isEqualTo("a-b-c (someMethod)");
        assertThat(reflector.defaultMethod("anotherMethod", "a", "b")).isEqualTo("a-b-c (anotherMethod)");
    }

    @Test
    public void reflector_shouldUnboxReturnValues() throws Exception {
        assertThat(reflector.returnLong()).isEqualTo(1234L);
    }

    @Test
    public void reflector_shouldCallStaticMethod() throws Exception {
        assertThat(reflector.someStaticMethod("a", "b")).isEqualTo("a-b (someStaticMethod)");
        assertThat(staticReflector.someStaticMethod("a", "b")).isEqualTo("a-b (someStaticMethod)");
    }

    @Test
    public void reflector_fieldAccessors() throws Exception {
        assertThat(reflector.getC()).isEqualTo("c");
        reflector.setC("c++");
        assertThat(reflector.getC()).isEqualTo("c++");
    }

    @Test
    public void reflector_primitiveFieldAccessors() throws Exception {
        assertThat(reflector.getD()).isEqualTo(0);
        reflector.setD(1234);
        assertThat(reflector.getD()).isEqualTo(1234);
    }

    @Test
    public void reflector_staticFieldAccessors() throws Exception {
        assertThat(reflector.getEStatic()).isEqualTo(null);
        reflector.setEStatic("eee!");
        assertThat(reflector.getEStatic()).isEqualTo("eee!");
    }

    @Test
    public void reflector_throwsCorrectExceptions() throws Exception {
        Throwable expected = new ArrayIndexOutOfBoundsException();
        Throwable actual = null;
        try {
            reflector.throwException(expected);
            Assert.fail("should have failed");
        } catch (Exception thrown) {
            actual = thrown;
        }
        assertThat(actual).isSameAs(expected);
    }

    // ////////////////////
    /**
     * Accessor interface for {@link SomeClass}'s internals.
     */
    @ForType(ReflectorTest.SomeClass.class)
    interface _SomeClass_ {
        @Static
        String someStaticMethod(String a, String b);

        @Static
        @Accessor("eStatic")
        void setEStatic(String value);

        @Static
        @Accessor("eStatic")
        String getEStatic();

        @Accessor("c")
        void setC(String value);

        @Accessor("c")
        String getC();

        @Accessor("mD")
        void setD(int value);

        @Accessor("mD")
        int getD();

        String someMethod(String a, String b);

        String anotherMethod(@WithType("java.lang.String")
        Object a, String b);

        default String defaultMethod(String which, String a, String b) {
            switch (which) {
                case "someMethod" :
                    return someMethod(a, b);
                case "anotherMethod" :
                    return anotherMethod(a, b);
                default :
                    throw new IllegalStateException(which);
            }
        }

        long returnLong();

        void throwException(Throwable t);
    }

    @SuppressWarnings("unused")
    static class SomeClass {
        private static String eStatic;

        private String c;

        private int mD;

        SomeClass(String c) {
            this.c = c;
        }

        private static String someStaticMethod(String a, String b) {
            return ((a + "-") + b) + " (someStaticMethod)";
        }

        private String someMethod(String a, String b) {
            return ((((a + "-") + b) + "-") + (c)) + " (someMethod)";
        }

        private String anotherMethod(String a, String b) {
            return ((((a + "-") + b) + "-") + (c)) + " (anotherMethod)";
        }

        private long returnLong() {
            return 1234L;
        }

        @SuppressWarnings("unused")
        private void throwException(Throwable t) throws Throwable {
            throw t;
        }
    }
}

