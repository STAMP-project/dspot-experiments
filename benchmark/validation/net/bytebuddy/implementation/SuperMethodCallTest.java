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
public class SuperMethodCallTest {
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

    private final Matcher<?> matcher;

    private final String methodName;

    private final Class<?>[] methodParameterTypes;

    private final Object[] methodArguments;

    public SuperMethodCallTest(Matcher<?> matcher, String methodName, Class<?>[] methodParameterTypes, Object[] methodArguments) {
        this.matcher = matcher;
        this.methodName = methodName;
        this.methodParameterTypes = methodParameterTypes;
        this.methodArguments = methodArguments;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInstrumentedMethod() throws Exception {
        DynamicType.Loaded<SuperMethodCallTest.Foo> loaded = new ByteBuddy().subclass(SuperMethodCallTest.Foo.class).method(ElementMatchers.isDeclaredBy(SuperMethodCallTest.Foo.class)).intercept(SuperMethodCall.INSTANCE).make().load(SuperMethodCallTest.Foo.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(11));
        SuperMethodCallTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(SuperMethodCallTest.Foo.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(SuperMethodCallTest.Foo.class));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethod(methodName, methodParameterTypes).invoke(instance, methodArguments), ((Matcher) (matcher)));
        instance.assertOnlyCall(methodName, methodArguments);
    }

    @SuppressWarnings("unused")
    public static class Foo extends CallTraceable {
        public Object reference() {
            register(SuperMethodCallTest.OBJECT_METHOD);
            return SuperMethodCallTest.STRING_VALUE;
        }

        public boolean aBoolean() {
            register(SuperMethodCallTest.BOOLEAN_METHOD);
            return SuperMethodCallTest.BOOLEAN_VALUE;
        }

        public byte aByte() {
            register(SuperMethodCallTest.BYTE_METHOD);
            return SuperMethodCallTest.BYTE_VALUE;
        }

        public short aShort() {
            register(SuperMethodCallTest.SHORT_METHOD);
            return SuperMethodCallTest.SHORT_VALUE;
        }

        public char aChar() {
            register(SuperMethodCallTest.CHAR_METHOD);
            return SuperMethodCallTest.CHAR_VALUE;
        }

        public int aInt() {
            register(SuperMethodCallTest.INT_METHOD);
            return SuperMethodCallTest.INT_VALUE;
        }

        public long aLong() {
            register(SuperMethodCallTest.LONG_METHOD);
            return SuperMethodCallTest.LONG_VALUE;
        }

        public float aFloat() {
            register(SuperMethodCallTest.FLOAT_METHOD);
            return SuperMethodCallTest.FLOAT_VALUE;
        }

        public double aDouble() {
            register(SuperMethodCallTest.DOUBLE_METHOD);
            return SuperMethodCallTest.DOUBLE_VALUE;
        }

        public void aVoid() {
            register(SuperMethodCallTest.VOID_METHOD);
        }

        public void parameters(long l, float f, int i, double d, Object o) {
            register(SuperMethodCallTest.PARAMETERS_METHOD, l, f, i, d, o);
        }
    }
}

