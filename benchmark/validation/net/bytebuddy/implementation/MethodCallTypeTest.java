package net.bytebuddy.implementation;


import java.lang.reflect.Method;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.STATIC;


@RunWith(Parameterized.class)
public class MethodCallTypeTest {
    private static final String FOO = "foo";

    private static final String STRING_VALUE = "foo";

    private static final MethodCallTypeTest.Bar ENUM_VALUE = MethodCallTypeTest.Bar.INSTANCE;

    private static final Class<?> CLASS_VALUE = Object.class;

    private static final boolean BOOLEAN_VALUE = true;

    private static final byte BYTE_VALUE = 42;

    private static final short SHORT_VALUE = 42;

    private static final char CHAR_VALUE = '@';

    private static final int INT_VALUE = 42;

    private static final long LONG_VALUE = 42L;

    private static final float FLOAT_VALUE = 42.0F;

    private static final double DOUBLE_VALUE = 42.0;

    private static final Object NULL_CONSTANT = null;

    private static final Object REFERENCE_VALUE = new Object();

    private final Object value;

    private final boolean definesFieldReference;

    private final boolean definesFieldConstantPool;

    @Rule
    public TestRule methodRule = new MockitoRule(this);

    @Mock
    private Assigner nonAssigner;

    public MethodCallTypeTest(Object value, boolean definesFieldReference, boolean definesFieldConstantPool) {
        this.value = value;
        this.definesFieldReference = definesFieldReference;
        this.definesFieldConstantPool = definesFieldConstantPool;
    }

    @Test
    public void testFieldConstantPool() throws Exception {
        DynamicType.Loaded<MethodCallTypeTest.Foo> loaded = new ByteBuddy().subclass(MethodCallTypeTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodCallTypeTest.Foo.class)).intercept(MethodCall.invokeSuper().with(value)).make().load(MethodCallTypeTest.Foo.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethod(MethodCallTypeTest.FOO, Object.class), CoreMatchers.not(CoreMatchers.nullValue(Method.class)));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is((definesFieldConstantPool ? 1 : 0)));
        MethodCallTypeTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(MethodCallTypeTest.Foo.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(MethodCallTypeTest.Foo.class));
        MatcherAssert.assertThat(instance.foo(new Object()), CoreMatchers.is(value));
    }

    @Test
    public void testFieldReference() throws Exception {
        DynamicType.Loaded<MethodCallTypeTest.Foo> loaded = new ByteBuddy().subclass(MethodCallTypeTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodCallTypeTest.Foo.class)).intercept(MethodCall.invokeSuper().withReference(value)).make().load(MethodCallTypeTest.Foo.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethod(MethodCallTypeTest.FOO, Object.class), CoreMatchers.not(CoreMatchers.nullValue(Method.class)));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is((definesFieldReference ? 1 : 0)));
        MethodCallTypeTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(MethodCallTypeTest.Foo.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(MethodCallTypeTest.Foo.class));
        MatcherAssert.assertThat(instance.foo(new Object()), CoreMatchers.sameInstance(value));
    }

    @Test(expected = IllegalStateException.class)
    public void testNonAssignable() throws Exception {
        new ByteBuddy().subclass(MethodCallTypeTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodCallTypeTest.Foo.class)).intercept(MethodCall.invokeSuper().with(value).withAssigner(nonAssigner, STATIC)).make();
    }

    public enum Bar {

        INSTANCE;}

    public static class Foo {
        public Object foo(Object value) {
            return value;
        }
    }
}

