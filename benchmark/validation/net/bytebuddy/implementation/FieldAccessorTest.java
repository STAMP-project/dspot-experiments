package net.bytebuddy.implementation;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.CallTraceable;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


@RunWith(Parameterized.class)
public class FieldAccessorTest<T extends CallTraceable, S extends CallTraceable, U extends CallTraceable, V extends CallTraceable, X extends CallTraceable, Y extends CallTraceable> {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String GET = "get";

    private static final String SET = "set";

    private static final Object STATIC_FIELD = null;

    private static final String STRING_VALUE = "qux";

    private static final boolean BOOLEAN_VALUE = true;

    private static final byte BYTE_VALUE = 42;

    private static final short SHORT_VALUE = 42;

    private static final char CHAR_VALUE = '@';

    private static final int INT_VALUE = 42;

    private static final long LONG_VALUE = 42L;

    private static final float FLOAT_VALUE = 42.0F;

    private static final double DOUBLE_VALUE = 42.0;

    private static final String STRING_DEFAULT_VALUE = "baz";

    private static final boolean BOOLEAN_DEFAULT_VALUE = false;

    private static final byte BYTE_DEFAULT_VALUE = 0;

    private static final short SHORT_DEFAULT_VALUE = 0;

    private static final char CHAR_DEFAULT_VALUE = 0;

    private static final int INT_DEFAULT_VALUE = 0;

    private static final long LONG_DEFAULT_VALUE = 0L;

    private static final float FLOAT_DEFAULT_VALUE = 0.0F;

    private static final double DOUBLE_DEFAULT_VALUE = 0.0;

    private final Object value;

    private final Class<T> instanceGetter;

    private final Class<S> instanceSetter;

    private final Class<U> staticGetter;

    private final Class<V> staticSetter;

    private final Class<?> propertyType;

    private final Class<?> instanceSwap;

    private final Class<?> staticSwap;

    public FieldAccessorTest(Object value, Class<T> instanceGetter, Class<S> instanceSetter, Class<?> instanceSwap, Class<U> staticGetter, Class<V> staticSetter, Class<?> staticSwap, Class<?> propertyType) {
        this.value = value;
        this.instanceGetter = instanceGetter;
        this.instanceSetter = instanceSetter;
        this.instanceSwap = instanceSwap;
        this.staticGetter = staticGetter;
        this.staticSetter = staticSetter;
        this.staticSwap = staticSwap;
        this.propertyType = propertyType;
    }

    @Test
    public void testInstanceGetterBeanProperty() throws Exception {
        testGetter(instanceGetter, FieldAccessor.ofBeanProperty());
    }

    @Test
    public void testStaticGetterBeanProperty() throws Exception {
        testGetter(staticGetter, FieldAccessor.ofBeanProperty());
    }

    @Test
    public void testInstanceGetterExplicit() throws Exception {
        testGetter(instanceGetter, FieldAccessor.ofField(FieldAccessorTest.FOO));
    }

    @Test
    public void testStaticGetterExplicit() throws Exception {
        testGetter(staticGetter, FieldAccessor.ofField(FieldAccessorTest.FOO));
    }

    @Test
    public void testInstanceGetterField() throws Exception {
        testGetter(instanceGetter, FieldAccessor.of(instanceGetter.getDeclaredField(FieldAccessorTest.FOO)));
    }

    @Test
    public void testStaticGetterField() throws Exception {
        testGetter(staticGetter, FieldAccessor.of(staticGetter.getDeclaredField(FieldAccessorTest.FOO)));
    }

    @Test
    public void testInstanceSetterBeanProperty() throws Exception {
        testSetter(instanceSetter, FieldAccessor.ofBeanProperty());
    }

    @Test
    public void testStaticSetterBeanProperty() throws Exception {
        testSetter(staticSetter, FieldAccessor.ofBeanProperty());
    }

    @Test
    public void testInstanceSetterExplicit() throws Exception {
        testSetter(instanceSetter, FieldAccessor.ofField(FieldAccessorTest.FOO));
    }

    @Test
    public void testStaticSetterExplicit() throws Exception {
        testSetter(staticSetter, FieldAccessor.ofField(FieldAccessorTest.FOO));
    }

    @Test
    public void testStaticSetterField() throws Exception {
        testSetter(staticSetter, FieldAccessor.of(staticSetter.getDeclaredField(FieldAccessorTest.FOO)));
    }

    @Test
    public void testInstanceSetterField() throws Exception {
        testSetter(instanceSetter, FieldAccessor.of(instanceSetter.getDeclaredField(FieldAccessorTest.FOO)));
    }

    @Test
    public void testInstanceSwap() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(instanceSwap).method(ElementMatchers.isDeclaredBy(instanceSwap)).intercept(setsFieldValueOf(FieldAccessorTest.FOO)).make().load(instanceSwap.getClassLoader(), WRAPPER);
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(loaded.getLoaded().getMethod(FieldAccessorTest.FOO).invoke(instance), CoreMatchers.nullValue(Object.class));
        MatcherAssert.assertThat(loaded.getLoaded().getField(FieldAccessorTest.FOO).get(instance), CoreMatchers.is(value));
        MatcherAssert.assertThat(loaded.getLoaded().getField(FieldAccessorTest.BAR).get(instance), CoreMatchers.is(value));
    }

    @Test
    public void testStaticSwap() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(staticSwap).method(ElementMatchers.isDeclaredBy(staticSwap)).intercept(setsFieldValueOf(FieldAccessorTest.FOO)).make().load(staticSwap.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoaded().getMethod(FieldAccessorTest.FOO).invoke(loaded.getLoaded().getConstructor().newInstance()), CoreMatchers.nullValue(Object.class));
        MatcherAssert.assertThat(loaded.getLoaded().getField(FieldAccessorTest.FOO).get(null), CoreMatchers.is(value));
        MatcherAssert.assertThat(loaded.getLoaded().getField(FieldAccessorTest.BAR).get(null), CoreMatchers.is(value));
    }

    @Test
    public void testInstanceValue() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(instanceSwap).method(ElementMatchers.isDeclaredBy(instanceSwap)).intercept(setsValue(value)).make().load(instanceSwap.getClassLoader(), WRAPPER);
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(loaded.getLoaded().getMethod(FieldAccessorTest.FOO).invoke(instance), CoreMatchers.nullValue(Object.class));
        MatcherAssert.assertThat(loaded.getLoaded().getField(FieldAccessorTest.BAR).get(instance), CoreMatchers.is(value));
    }

    @Test
    public void testStaticValue() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(staticSwap).method(ElementMatchers.isDeclaredBy(staticSwap)).intercept(setsValue(value)).make().load(staticSwap.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoaded().getMethod(FieldAccessorTest.FOO).invoke(loaded.getLoaded().getConstructor().newInstance()), CoreMatchers.nullValue(Object.class));
        MatcherAssert.assertThat(loaded.getLoaded().getField(FieldAccessorTest.BAR).get(null), CoreMatchers.is(value));
    }

    @Test
    public void testInstanceReference() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(instanceSwap).method(ElementMatchers.isDeclaredBy(instanceSwap)).intercept(setsReference(value)).make().load(instanceSwap.getClassLoader(), WRAPPER);
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(loaded.getLoaded().getMethod(FieldAccessorTest.FOO).invoke(instance), CoreMatchers.nullValue(Object.class));
        MatcherAssert.assertThat(loaded.getLoaded().getField(FieldAccessorTest.BAR).get(instance), CoreMatchers.is(value));
    }

    @Test
    public void testStaticReference() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(staticSwap).method(ElementMatchers.isDeclaredBy(staticSwap)).intercept(setsReference(value)).make().load(staticSwap.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoaded().getMethod(FieldAccessorTest.FOO).invoke(loaded.getLoaded().getConstructor().newInstance()), CoreMatchers.nullValue(Object.class));
        MatcherAssert.assertThat(loaded.getLoaded().getField(FieldAccessorTest.BAR).get(null), CoreMatchers.is(value));
    }

    @Test
    public void testInstanceDefault() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(instanceSwap).method(ElementMatchers.isDeclaredBy(instanceSwap)).intercept(setsDefaultValue()).make().load(instanceSwap.getClassLoader(), WRAPPER);
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(loaded.getLoaded().getMethod(FieldAccessorTest.FOO).invoke(instance), CoreMatchers.nullValue(Object.class));
        MatcherAssert.assertThat(loaded.getLoaded().getField(FieldAccessorTest.FOO).get(instance), CoreMatchers.not(value));
    }

    @Test
    public void testStaticDefault() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(staticSwap).method(ElementMatchers.isDeclaredBy(staticSwap)).intercept(setsDefaultValue()).make().load(staticSwap.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoaded().getMethod(FieldAccessorTest.FOO).invoke(loaded.getLoaded().getConstructor().newInstance()), CoreMatchers.nullValue(Object.class));
        MatcherAssert.assertThat(loaded.getLoaded().getField(FieldAccessorTest.FOO).get(null), CoreMatchers.not(value));
    }

    public static class BooleanInstanceGetter extends CallTraceable {
        protected boolean foo = FieldAccessorTest.BOOLEAN_VALUE;

        public boolean getFoo() {
            register(FieldAccessorTest.FOO);
            return FieldAccessorTest.BOOLEAN_DEFAULT_VALUE;
        }
    }

    public static class BooleanInstanceSetter extends CallTraceable {
        protected boolean foo = FieldAccessorTest.BOOLEAN_DEFAULT_VALUE;

        public void setFoo(boolean foo) {
            register(FieldAccessorTest.FOO, foo);
        }
    }

    public static class BooleanClassGetter extends CallTraceable {
        protected static boolean foo = FieldAccessorTest.BOOLEAN_VALUE;

        public boolean getFoo() {
            register(FieldAccessorTest.FOO);
            return FieldAccessorTest.BOOLEAN_DEFAULT_VALUE;
        }
    }

    public static class BooleanClassSetter extends CallTraceable {
        protected static boolean foo = FieldAccessorTest.BOOLEAN_DEFAULT_VALUE;

        public void setFoo(boolean foo) {
            register(FieldAccessorTest.FOO, foo);
        }
    }

    public static class ByteInstanceGetter extends CallTraceable {
        protected byte foo = FieldAccessorTest.BYTE_VALUE;

        public byte getFoo() {
            register(FieldAccessorTest.FOO);
            return FieldAccessorTest.BYTE_DEFAULT_VALUE;
        }
    }

    public static class BooleanInstanceSwap {
        public boolean foo = FieldAccessorTest.BOOLEAN_VALUE;

        public boolean bar;

        public void foo() {
            /* empty */
        }
    }

    public static class BooleanClassSwap {
        public static boolean foo = FieldAccessorTest.BOOLEAN_VALUE;

        public static boolean bar;

        public void foo() {
            /* empty */
        }
    }

    public static class ByteInstanceSetter extends CallTraceable {
        protected byte foo = FieldAccessorTest.BYTE_DEFAULT_VALUE;

        public void setFoo(byte foo) {
            register(FieldAccessorTest.FOO, foo);
        }
    }

    public static class ByteClassGetter extends CallTraceable {
        protected static byte foo = FieldAccessorTest.BYTE_VALUE;

        public byte getFoo() {
            register(FieldAccessorTest.FOO);
            return FieldAccessorTest.BYTE_DEFAULT_VALUE;
        }
    }

    public static class ByteClassSetter extends CallTraceable {
        protected static byte foo = FieldAccessorTest.BYTE_DEFAULT_VALUE;

        public void setFoo(byte foo) {
            register(FieldAccessorTest.FOO, foo);
        }
    }

    public static class ByteInstanceSwap {
        public byte foo = FieldAccessorTest.BYTE_VALUE;

        public byte bar;

        public void foo() {
            /* empty */
        }
    }

    public static class ByteClassSwap {
        public static byte foo = FieldAccessorTest.BYTE_VALUE;

        public static byte bar;

        public void foo() {
            /* empty */
        }
    }

    public static class ShortInstanceGetter extends CallTraceable {
        protected short foo = FieldAccessorTest.SHORT_VALUE;

        public short getFoo() {
            register(FieldAccessorTest.FOO);
            return FieldAccessorTest.SHORT_DEFAULT_VALUE;
        }
    }

    public static class ShortInstanceSetter extends CallTraceable {
        protected short foo = FieldAccessorTest.SHORT_DEFAULT_VALUE;

        public void setFoo(short foo) {
            register(FieldAccessorTest.FOO, foo);
        }
    }

    public static class ShortClassGetter extends CallTraceable {
        protected static short foo = FieldAccessorTest.SHORT_VALUE;

        public short getFoo() {
            register(FieldAccessorTest.FOO);
            return FieldAccessorTest.SHORT_DEFAULT_VALUE;
        }
    }

    public static class ShortClassSetter extends CallTraceable {
        protected static short foo = FieldAccessorTest.SHORT_DEFAULT_VALUE;

        public void setFoo(short foo) {
            register(FieldAccessorTest.FOO, foo);
        }
    }

    public static class ShortInstanceSwap {
        public short foo = FieldAccessorTest.SHORT_VALUE;

        public short bar;

        public void foo() {
            /* empty */
        }
    }

    public static class ShortClassSwap {
        public static short foo = FieldAccessorTest.INT_VALUE;

        public static short bar;

        public void foo() {
            /* empty */
        }
    }

    public static class IntegerInstanceGetter extends CallTraceable {
        protected int foo = FieldAccessorTest.INT_VALUE;

        public int getFoo() {
            register(FieldAccessorTest.FOO);
            return FieldAccessorTest.INT_DEFAULT_VALUE;
        }
    }

    public static class IntegerInstanceSetter extends CallTraceable {
        protected int foo = FieldAccessorTest.INT_DEFAULT_VALUE;

        public void setFoo(int foo) {
            register(FieldAccessorTest.FOO, foo);
        }
    }

    public static class IntegerClassGetter extends CallTraceable {
        protected static int foo = FieldAccessorTest.INT_VALUE;

        public int getFoo() {
            register(FieldAccessorTest.FOO);
            return FieldAccessorTest.INT_DEFAULT_VALUE;
        }
    }

    public static class IntegerClassSetter extends CallTraceable {
        protected static int foo = FieldAccessorTest.INT_DEFAULT_VALUE;

        public void setFoo(int foo) {
            register(FieldAccessorTest.FOO, foo);
        }
    }

    public static class IntegerInstanceSwap {
        public int foo = FieldAccessorTest.INT_VALUE;

        public int bar;

        public void foo() {
            /* empty */
        }
    }

    public static class IntegerClassSwap {
        public static int foo = FieldAccessorTest.INT_VALUE;

        public static int bar;

        public void foo() {
            /* empty */
        }
    }

    public static class CharacterInstanceGetter extends CallTraceable {
        protected char foo = FieldAccessorTest.CHAR_VALUE;

        public char getFoo() {
            register(FieldAccessorTest.FOO);
            return FieldAccessorTest.CHAR_DEFAULT_VALUE;
        }
    }

    public static class CharacterInstanceSetter extends CallTraceable {
        protected char foo = FieldAccessorTest.CHAR_DEFAULT_VALUE;

        public void setFoo(char foo) {
            register(FieldAccessorTest.FOO, foo);
        }
    }

    public static class CharacterClassGetter extends CallTraceable {
        protected static char foo = FieldAccessorTest.CHAR_VALUE;

        public char getFoo() {
            register(FieldAccessorTest.FOO);
            return FieldAccessorTest.CHAR_DEFAULT_VALUE;
        }
    }

    public static class CharacterClassSetter extends CallTraceable {
        protected static char foo = FieldAccessorTest.CHAR_DEFAULT_VALUE;

        public void setFoo(char foo) {
            register(FieldAccessorTest.FOO, foo);
        }
    }

    public static class CharacterInstanceSwap {
        public char foo = FieldAccessorTest.CHAR_VALUE;

        public char bar;

        public void foo() {
            /* empty */
        }
    }

    public static class CharacterClassSwap {
        public static char foo = FieldAccessorTest.CHAR_VALUE;

        public static char bar;

        public void foo() {
            /* empty */
        }
    }

    public static class LongInstanceGetter extends CallTraceable {
        protected long foo = FieldAccessorTest.LONG_VALUE;

        public long getFoo() {
            register(FieldAccessorTest.FOO);
            return FieldAccessorTest.LONG_DEFAULT_VALUE;
        }
    }

    public static class LongInstanceSetter extends CallTraceable {
        protected long foo = FieldAccessorTest.LONG_DEFAULT_VALUE;

        public void setFoo(long foo) {
            register(FieldAccessorTest.FOO, foo);
        }
    }

    public static class LongClassGetter extends CallTraceable {
        protected static long foo = FieldAccessorTest.LONG_VALUE;

        public long getFoo() {
            register(FieldAccessorTest.FOO);
            return FieldAccessorTest.LONG_DEFAULT_VALUE;
        }
    }

    public static class LongClassSetter extends CallTraceable {
        protected static long foo = FieldAccessorTest.LONG_DEFAULT_VALUE;

        public void setFoo(long foo) {
            register(FieldAccessorTest.FOO, foo);
        }
    }

    public static class LongInstanceSwap {
        public long foo = FieldAccessorTest.LONG_VALUE;

        public long bar;

        public void foo() {
            /* empty */
        }
    }

    public static class LongClassSwap {
        public static long foo = FieldAccessorTest.LONG_VALUE;

        public static long bar;

        public void foo() {
            /* empty */
        }
    }

    public static class FloatInstanceGetter extends CallTraceable {
        protected float foo = FieldAccessorTest.FLOAT_VALUE;

        public float getFoo() {
            register(FieldAccessorTest.FOO);
            return FieldAccessorTest.FLOAT_DEFAULT_VALUE;
        }
    }

    public static class FloatInstanceSetter extends CallTraceable {
        protected float foo = FieldAccessorTest.FLOAT_DEFAULT_VALUE;

        public void setFoo(float foo) {
            register(FieldAccessorTest.FOO, foo);
        }
    }

    public static class FloatClassGetter extends CallTraceable {
        protected static float foo = FieldAccessorTest.FLOAT_VALUE;

        public float getFoo() {
            register(FieldAccessorTest.FOO);
            return FieldAccessorTest.FLOAT_DEFAULT_VALUE;
        }
    }

    public static class FloatClassSetter extends CallTraceable {
        protected static float foo = FieldAccessorTest.FLOAT_DEFAULT_VALUE;

        public void setFoo(float foo) {
            register(FieldAccessorTest.FOO, foo);
        }
    }

    public static class FloatInstanceSwap {
        public float foo = FieldAccessorTest.FLOAT_VALUE;

        public float bar;

        public void foo() {
            /* empty */
        }
    }

    public static class FloatClassSwap {
        public static float foo = FieldAccessorTest.FLOAT_VALUE;

        public static float bar;

        public void foo() {
            /* empty */
        }
    }

    public static class DoubleInstanceGetter extends CallTraceable {
        protected double foo = FieldAccessorTest.DOUBLE_VALUE;

        public double getFoo() {
            register(FieldAccessorTest.FOO);
            return FieldAccessorTest.DOUBLE_DEFAULT_VALUE;
        }
    }

    public static class DoubleInstanceSetter extends CallTraceable {
        protected double foo = FieldAccessorTest.DOUBLE_DEFAULT_VALUE;

        public void setFoo(double foo) {
            register(FieldAccessorTest.FOO, foo);
        }
    }

    public static class DoubleClassGetter extends CallTraceable {
        protected static double foo = FieldAccessorTest.DOUBLE_VALUE;

        public double getFoo() {
            register(FieldAccessorTest.FOO);
            return FieldAccessorTest.DOUBLE_DEFAULT_VALUE;
        }
    }

    public static class DoubleClassSetter extends CallTraceable {
        protected static double foo = FieldAccessorTest.DOUBLE_DEFAULT_VALUE;

        public void setFoo(double foo) {
            register(FieldAccessorTest.FOO, foo);
        }
    }

    public static class DoubleInstanceSwap {
        public double foo = FieldAccessorTest.DOUBLE_VALUE;

        public double bar;

        public void foo() {
            /* empty */
        }
    }

    public static class DoubleClassSwap {
        public static double foo = FieldAccessorTest.DOUBLE_VALUE;

        public static double bar;

        public void foo() {
            /* empty */
        }
    }

    public static class ObjectInstanceGetter extends CallTraceable {
        protected Object foo = FieldAccessorTest.STRING_VALUE;

        public Object getFoo() {
            register(FieldAccessorTest.FOO);
            return FieldAccessorTest.STRING_DEFAULT_VALUE;
        }
    }

    public static class ObjectInstanceSetter extends CallTraceable {
        protected Object foo = FieldAccessorTest.STRING_DEFAULT_VALUE;

        public void setFoo(Object foo) {
            register(FieldAccessorTest.FOO, foo);
        }
    }

    public static class ObjectClassGetter extends CallTraceable {
        protected static Object foo = FieldAccessorTest.STRING_VALUE;

        public Object getFoo() {
            register(FieldAccessorTest.FOO);
            return FieldAccessorTest.STRING_DEFAULT_VALUE;
        }
    }

    public static class ObjectClassSetter extends CallTraceable {
        protected static Object foo = FieldAccessorTest.STRING_DEFAULT_VALUE;

        public void setFoo(Object foo) {
            register(FieldAccessorTest.FOO, foo);
        }
    }

    public static class ObjectInstanceSwap {
        public Object foo = FieldAccessorTest.STRING_VALUE;

        public Object bar;

        public void foo() {
            /* empty */
        }
    }

    public static class ObjectClassSwap {
        public static String foo = FieldAccessorTest.STRING_VALUE;

        public static String bar;

        public void foo() {
            /* empty */
        }
    }
}

