package net.bytebuddy.implementation;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.CallTraceable;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


@RunWith(Parameterized.class)
public class StubMethodTest {
    private static final String OBJECT_METHOD = "reference";

    private static final String BOOLEAN_METHOD = "aBoolean";

    private static final String BYTE_METHOD = "aByte";

    private static final String SHORT_METHOD = "aShort";

    private static final String CHAR_METHOD = "aChar";

    private static final String INT_METHOD = "aInt";

    private static final String LONG_METHOD = "aLong";

    private static final String FLOAT_METHOD = "aFloat";

    private static final String DOUBLE_METHOD = "aDouble";

    private static final String VOID_METHOD = "aVoid";

    private static final String PARAMETERS_METHOD = "parameters";

    private static final String STRING_VALUE = "foo";

    private static final boolean BOOLEAN_VALUE = true;

    private static final byte BYTE_VALUE = 42;

    private static final short SHORT_VALUE = 42;

    private static final char CHAR_VALUE = '@';

    private static final int INT_VALUE = 42;

    private static final long LONG_VALUE = 42L;

    private static final float FLOAT_VALUE = 42.0F;

    private static final double DOUBLE_VALUE = 42.0;

    private static final String STRING_DEFAULT_VALUE = null;

    private static final boolean BOOLEAN_DEFAULT_VALUE = false;

    private static final byte BYTE_DEFAULT_VALUE = 0;

    private static final short SHORT_DEFAULT_VALUE = 0;

    private static final char CHAR_DEFAULT_VALUE = 0;

    private static final int INT_DEFAULT_VALUE = 0;

    private static final long LONG_DEFAULT_VALUE = 0L;

    private static final float FLOAT_DEFAULT_VALUE = 0.0F;

    private static final double DOUBLE_DEFAULT_VALUE = 0.0;

    private final Matcher<?> matcher;

    private final String methodName;

    private final Class<?>[] methodParameterTypes;

    private final Object[] methodArguments;

    public StubMethodTest(Matcher<?> matcher, String methodName, Class<?>[] methodParameterTypes, Object[] methodArguments) {
        this.matcher = matcher;
        this.methodName = methodName;
        this.methodParameterTypes = methodParameterTypes;
        this.methodArguments = methodArguments;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInstrumentedMethod() throws Exception {
        DynamicType.Loaded<StubMethodTest.Foo> loaded = new ByteBuddy().subclass(StubMethodTest.Foo.class).method(ElementMatchers.isDeclaredBy(StubMethodTest.Foo.class)).intercept(StubMethod.INSTANCE).make().load(StubMethodTest.Foo.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(11));
        StubMethodTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(StubMethodTest.Foo.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(StubMethodTest.Foo.class));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethod(methodName, methodParameterTypes).invoke(instance, methodArguments), ((Matcher) (matcher)));
        instance.assertZeroCalls();
    }

    @SuppressWarnings("unused")
    public static class Foo extends CallTraceable {
        public Object reference() {
            register(StubMethodTest.OBJECT_METHOD);
            return StubMethodTest.STRING_VALUE;
        }

        public boolean aBoolean() {
            register(StubMethodTest.BOOLEAN_METHOD);
            return StubMethodTest.BOOLEAN_VALUE;
        }

        public byte aByte() {
            register(StubMethodTest.BYTE_METHOD);
            return StubMethodTest.BYTE_VALUE;
        }

        public short aShort() {
            register(StubMethodTest.SHORT_METHOD);
            return StubMethodTest.SHORT_VALUE;
        }

        public char aChar() {
            register(StubMethodTest.CHAR_METHOD);
            return StubMethodTest.CHAR_VALUE;
        }

        public int aInt() {
            register(StubMethodTest.INT_METHOD);
            return StubMethodTest.INT_VALUE;
        }

        public long aLong() {
            register(StubMethodTest.LONG_METHOD);
            return StubMethodTest.LONG_VALUE;
        }

        public float aFloat() {
            register(StubMethodTest.FLOAT_METHOD);
            return StubMethodTest.FLOAT_VALUE;
        }

        public double aDouble() {
            register(StubMethodTest.DOUBLE_METHOD);
            return StubMethodTest.DOUBLE_VALUE;
        }

        public void aVoid() {
            register(StubMethodTest.VOID_METHOD);
        }

        public void parameters(long l, float f, int i, double d, Object o) {
            register(StubMethodTest.PARAMETERS_METHOD, l, f, i, d, o);
        }
    }
}

