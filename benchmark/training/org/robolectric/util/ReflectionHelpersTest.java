package org.robolectric.util;


import java.lang.reflect.Field;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.robolectric.util.ReflectionHelpers.ClassParameter;


@RunWith(JUnit4.class)
public class ReflectionHelpersTest {
    @Test
    public void getFieldReflectively_getsPrivateFields() {
        ReflectionHelpersTest.ExampleDescendant example = new ReflectionHelpersTest.ExampleDescendant();
        example.overridden = 5;
        assertThat(((int) (ReflectionHelpers.getField(example, "overridden")))).isEqualTo(5);
    }

    @Test
    public void getFieldReflectively_getsInheritedFields() {
        ReflectionHelpersTest.ExampleDescendant example = new ReflectionHelpersTest.ExampleDescendant();
        example.setNotOverridden(6);
        assertThat(((int) (ReflectionHelpers.getField(example, "notOverridden")))).isEqualTo(6);
    }

    @Test
    public void getFieldReflectively_givesHelpfulExceptions() {
        ReflectionHelpersTest.ExampleDescendant example = new ReflectionHelpersTest.ExampleDescendant();
        try {
            ReflectionHelpers.getField(example, "nonExistent");
            Assert.fail("Expected exception not thrown");
        } catch (RuntimeException e) {
            if (!(e.getMessage().contains("nonExistent"))) {
                throw new RuntimeException("Incorrect exception thrown", e);
            }
        }
    }

    @Test
    public void setFieldReflectively_setsPrivateFields() {
        ReflectionHelpersTest.ExampleDescendant example = new ReflectionHelpersTest.ExampleDescendant();
        example.overridden = 5;
        ReflectionHelpers.setField(example, "overridden", 10);
        assertThat(example.overridden).isEqualTo(10);
    }

    @Test
    public void setFieldReflectively_setsInheritedFields() {
        ReflectionHelpersTest.ExampleDescendant example = new ReflectionHelpersTest.ExampleDescendant();
        example.setNotOverridden(5);
        ReflectionHelpers.setField(example, "notOverridden", 10);
        assertThat(example.getNotOverridden()).isEqualTo(10);
    }

    @Test
    public void setFieldReflectively_givesHelpfulExceptions() {
        ReflectionHelpersTest.ExampleDescendant example = new ReflectionHelpersTest.ExampleDescendant();
        try {
            ReflectionHelpers.setField(example, "nonExistent", 6);
            Assert.fail("Expected exception not thrown");
        } catch (RuntimeException e) {
            if (!(e.getMessage().contains("nonExistent"))) {
                throw new RuntimeException("Incorrect exception thrown", e);
            }
        }
    }

    @Test
    public void getStaticFieldReflectively_withField_getsStaticField() throws Exception {
        Field field = ReflectionHelpersTest.ExampleDescendant.class.getDeclaredField("DESCENDANT");
        int result = ReflectionHelpers.getStaticField(field);
        assertThat(result).isEqualTo(6);
    }

    @Test
    public void getStaticFieldReflectively_withFieldName_getsStaticField() {
        assertThat(((int) (ReflectionHelpers.getStaticField(ReflectionHelpersTest.ExampleDescendant.class, "DESCENDANT")))).isEqualTo(6);
    }

    @Test
    public void setStaticFieldReflectively_withField_setsStaticFields() throws Exception {
        Field field = ReflectionHelpersTest.ExampleDescendant.class.getDeclaredField("DESCENDANT");
        int startingValue = ReflectionHelpers.getStaticField(field);
        ReflectionHelpers.setStaticField(field, 7);
        assertThat(startingValue).named("startingValue").isEqualTo(6);
        assertThat(ReflectionHelpersTest.ExampleDescendant.DESCENDANT).named("DESCENDENT").isEqualTo(7);
        // / Reset the value to avoid test pollution
        ReflectionHelpers.setStaticField(field, startingValue);
    }

    @Test
    public void setStaticFieldReflectively_withFieldName_setsStaticFields() {
        int startingValue = ReflectionHelpers.getStaticField(ReflectionHelpersTest.ExampleDescendant.class, "DESCENDANT");
        ReflectionHelpers.setStaticField(ReflectionHelpersTest.ExampleDescendant.class, "DESCENDANT", 7);
        assertThat(startingValue).named("startingValue").isEqualTo(6);
        assertThat(ReflectionHelpersTest.ExampleDescendant.DESCENDANT).named("DESCENDENT").isEqualTo(7);
        // Reset the value to avoid test pollution
        ReflectionHelpers.setStaticField(ReflectionHelpersTest.ExampleDescendant.class, "DESCENDANT", startingValue);
    }

    @Test
    public void callInstanceMethodReflectively_callsPrivateMethods() {
        ReflectionHelpersTest.ExampleDescendant example = new ReflectionHelpersTest.ExampleDescendant();
        assertThat(((int) (ReflectionHelpers.callInstanceMethod(example, "returnNumber")))).isEqualTo(1337);
    }

    @Test
    public void callInstanceMethodReflectively_whenMultipleSignaturesExistForAMethodName_callsMethodWithCorrectSignature() {
        ReflectionHelpersTest.ExampleDescendant example = new ReflectionHelpersTest.ExampleDescendant();
        int returnNumber = ReflectionHelpers.callInstanceMethod(example, "returnNumber", ClassParameter.from(int.class, 5));
        assertThat(returnNumber).isEqualTo(5);
    }

    @Test
    public void callInstanceMethodReflectively_callsInheritedMethods() {
        ReflectionHelpersTest.ExampleDescendant example = new ReflectionHelpersTest.ExampleDescendant();
        assertThat(((int) (ReflectionHelpers.callInstanceMethod(example, "returnNegativeNumber")))).isEqualTo((-46));
    }

    @Test
    public void callInstanceMethodReflectively_givesHelpfulExceptions() {
        ReflectionHelpersTest.ExampleDescendant example = new ReflectionHelpersTest.ExampleDescendant();
        try {
            ReflectionHelpers.callInstanceMethod(example, "nonExistent");
            Assert.fail("Expected exception not thrown");
        } catch (RuntimeException e) {
            if (!(e.getMessage().contains("nonExistent"))) {
                throw new RuntimeException("Incorrect exception thrown", e);
            }
        }
    }

    @Test
    public void callInstanceMethodReflectively_rethrowsUncheckedException() {
        ReflectionHelpersTest.ExampleDescendant example = new ReflectionHelpersTest.ExampleDescendant();
        try {
            ReflectionHelpers.callInstanceMethod(example, "throwUncheckedException");
            Assert.fail("Expected exception not thrown");
        } catch (ReflectionHelpersTest.TestRuntimeException e) {
        } catch (RuntimeException e) {
            throw new RuntimeException("Incorrect exception thrown", e);
        }
    }

    @Test
    public void callInstanceMethodReflectively_rethrowsError() {
        ReflectionHelpersTest.ExampleDescendant example = new ReflectionHelpersTest.ExampleDescendant();
        try {
            ReflectionHelpers.callInstanceMethod(example, "throwError");
            Assert.fail("Expected exception not thrown");
        } catch (RuntimeException e) {
            throw new RuntimeException("Incorrect exception thrown", e);
        } catch (ReflectionHelpersTest.TestError e) {
        }
    }

    @Test
    public void callInstanceMethodReflectively_wrapsCheckedException() {
        ReflectionHelpersTest.ExampleDescendant example = new ReflectionHelpersTest.ExampleDescendant();
        try {
            ReflectionHelpers.callInstanceMethod(example, "throwCheckedException");
            Assert.fail("Expected exception not thrown");
        } catch (RuntimeException e) {
            assertThat(e.getCause()).isInstanceOf(ReflectionHelpersTest.TestException.class);
        }
    }

    @Test
    public void callStaticMethodReflectively_callsPrivateStaticMethodsReflectively() {
        int constantNumber = ReflectionHelpers.callStaticMethod(ReflectionHelpersTest.ExampleDescendant.class, "getConstantNumber");
        assertThat(constantNumber).isEqualTo(1);
    }

    @Test
    public void callStaticMethodReflectively_rethrowsUncheckedException() {
        try {
            ReflectionHelpers.callStaticMethod(ReflectionHelpersTest.ExampleDescendant.class, "staticThrowUncheckedException");
            Assert.fail("Expected exception not thrown");
        } catch (ReflectionHelpersTest.TestRuntimeException e) {
        } catch (RuntimeException e) {
            throw new RuntimeException("Incorrect exception thrown", e);
        }
    }

    @Test
    public void callStaticMethodReflectively_rethrowsError() {
        try {
            ReflectionHelpers.callStaticMethod(ReflectionHelpersTest.ExampleDescendant.class, "staticThrowError");
            Assert.fail("Expected exception not thrown");
        } catch (RuntimeException e) {
            throw new RuntimeException("Incorrect exception thrown", e);
        } catch (ReflectionHelpersTest.TestError e) {
        }
    }

    @Test
    public void callStaticMethodReflectively_wrapsCheckedException() {
        try {
            ReflectionHelpers.callStaticMethod(ReflectionHelpersTest.ExampleDescendant.class, "staticThrowCheckedException");
            Assert.fail("Expected exception not thrown");
        } catch (RuntimeException e) {
            assertThat(e.getCause()).isInstanceOf(ReflectionHelpersTest.TestException.class);
        }
    }

    @Test
    public void callConstructorReflectively_callsPrivateConstructors() {
        ReflectionHelpersTest.ExampleClass e = ReflectionHelpers.callConstructor(ReflectionHelpersTest.ExampleClass.class);
        assertThat(e).isNotNull();
    }

    @Test
    public void callConstructorReflectively_rethrowsUncheckedException() {
        try {
            ReflectionHelpers.callConstructor(ReflectionHelpersTest.ThrowsUncheckedException.class);
            Assert.fail("Expected exception not thrown");
        } catch (ReflectionHelpersTest.TestRuntimeException e) {
        } catch (RuntimeException e) {
            throw new RuntimeException("Incorrect exception thrown", e);
        }
    }

    @Test
    public void callConstructorReflectively_rethrowsError() {
        try {
            ReflectionHelpers.callConstructor(ReflectionHelpersTest.ThrowsError.class);
            Assert.fail("Expected exception not thrown");
        } catch (RuntimeException e) {
            throw new RuntimeException("Incorrect exception thrown", e);
        } catch (ReflectionHelpersTest.TestError e) {
        }
    }

    @Test
    public void callConstructorReflectively_wrapsCheckedException() {
        try {
            ReflectionHelpers.callConstructor(ReflectionHelpersTest.ThrowsCheckedException.class);
            Assert.fail("Expected exception not thrown");
        } catch (RuntimeException e) {
            assertThat(e.getCause()).isInstanceOf(ReflectionHelpersTest.TestException.class);
        }
    }

    @Test
    public void callConstructorReflectively_whenMultipleSignaturesExistForTheConstructor_callsConstructorWithCorrectSignature() {
        ReflectionHelpersTest.ExampleClass ec = ReflectionHelpers.callConstructor(ReflectionHelpersTest.ExampleClass.class, ClassParameter.from(int.class, 16));
        assertThat(ec.index).named("index").isEqualTo(16);
        assertThat(ec.name).named("name").isNull();
    }

    @SuppressWarnings("serial")
    private static class TestError extends Error {}

    @SuppressWarnings("serial")
    private static class TestException extends Exception {}

    @SuppressWarnings("serial")
    private static class TestRuntimeException extends RuntimeException {}

    @SuppressWarnings("unused")
    private static class ExampleBase {
        private int notOverridden;

        protected int overridden;

        private static final int BASE = 8;

        public int getNotOverridden() {
            return notOverridden;
        }

        public void setNotOverridden(int notOverridden) {
            this.notOverridden = notOverridden;
        }

        private int returnNegativeNumber() {
            return -46;
        }
    }

    @SuppressWarnings("unused")
    private static class ExampleDescendant extends ReflectionHelpersTest.ExampleBase {
        public static int DESCENDANT = 6;

        @SuppressWarnings("HidingField")
        protected int overridden;

        private int returnNumber() {
            return 1337;
        }

        private int returnNumber(int n) {
            return n;
        }

        private static int getConstantNumber() {
            return 1;
        }

        private void throwUncheckedException() {
            throw new ReflectionHelpersTest.TestRuntimeException();
        }

        private void throwCheckedException() throws Exception {
            throw new ReflectionHelpersTest.TestException();
        }

        private void throwError() {
            throw new ReflectionHelpersTest.TestError();
        }

        private static void staticThrowUncheckedException() {
            throw new ReflectionHelpersTest.TestRuntimeException();
        }

        private static void staticThrowCheckedException() throws Exception {
            throw new ReflectionHelpersTest.TestException();
        }

        private static void staticThrowError() {
            throw new ReflectionHelpersTest.TestError();
        }
    }

    private static class ThrowsError {
        @SuppressWarnings("unused")
        public ThrowsError() {
            throw new ReflectionHelpersTest.TestError();
        }
    }

    private static class ThrowsCheckedException {
        @SuppressWarnings("unused")
        public ThrowsCheckedException() throws Exception {
            throw new ReflectionHelpersTest.TestException();
        }
    }

    private static class ThrowsUncheckedException {
        @SuppressWarnings("unused")
        public ThrowsUncheckedException() {
            throw new ReflectionHelpersTest.TestRuntimeException();
        }
    }

    private static class ExampleClass {
        public String name;

        public int index;

        private ExampleClass() {
        }

        private ExampleClass(String name) {
            this.name = name;
        }

        private ExampleClass(int index) {
            this.index = index;
        }
    }
}

