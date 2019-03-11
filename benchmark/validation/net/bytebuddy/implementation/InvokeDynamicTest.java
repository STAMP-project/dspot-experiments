package net.bytebuddy.implementation;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.enumeration.EnumerationDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.JavaVersionRule;
import net.bytebuddy.utility.JavaType;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.DYNAMIC;
import static net.bytebuddy.utility.JavaConstant.MethodHandle.ofLoaded;


public class InvokeDynamicTest {
    public static final String INSTANCE = "INSTANCE";

    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String BAZ = "baz";

    private static final boolean BOOLEAN = true;

    private static final byte BYTE = 42;

    private static final short SHORT = 42;

    private static final char CHARACTER = 42;

    private static final int INTEGER = 42;

    private static final long LONG = 42L;

    private static final float FLOAT = 42.0F;

    private static final double DOUBLE = 42.0;

    private static final Class<?> CLASS = Object.class;

    private static final String BOOTSTRAP_CLASS = "net.bytebuddy.test.precompiled.DynamicInvokeBootstrap";

    private static final String SAMPLE_ENUM = (InvokeDynamicTest.BOOTSTRAP_CLASS) + "$SampleEnum";

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Test
    @JavaVersionRule.Enforce(7)
    public void testBootstrapMethod() throws Exception {
        for (Method method : Class.forName(InvokeDynamicTest.BOOTSTRAP_CLASS).getDeclaredMethods()) {
            if (!(method.getName().equals("bootstrap"))) {
                continue;
            }
            DynamicType.Loaded<InvokeDynamicTest.Simple> dynamicType = new ByteBuddy().subclass(InvokeDynamicTest.Simple.class).method(ElementMatchers.isDeclaredBy(InvokeDynamicTest.Simple.class)).intercept(InvokeDynamic.bootstrap(method).withoutArguments()).make().load(getClass().getClassLoader(), WRAPPER);
            MatcherAssert.assertThat(dynamicType.getLoaded().getDeclaredConstructor().newInstance().foo(), CoreMatchers.is(InvokeDynamicTest.FOO));
        }
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testBootstrapConstructor() throws Exception {
        for (Constructor<?> constructor : Class.forName(InvokeDynamicTest.BOOTSTRAP_CLASS).getDeclaredConstructors()) {
            DynamicType.Loaded<InvokeDynamicTest.Simple> dynamicType = new ByteBuddy().subclass(InvokeDynamicTest.Simple.class).method(ElementMatchers.isDeclaredBy(InvokeDynamicTest.Simple.class)).intercept(InvokeDynamic.bootstrap(constructor).withoutArguments()).make().load(getClass().getClassLoader(), WRAPPER);
            MatcherAssert.assertThat(dynamicType.getLoaded().getDeclaredConstructor().newInstance().foo(), CoreMatchers.is(InvokeDynamicTest.FOO));
        }
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testBootstrapWithArrayArgumentsWithoutArguments() throws Exception {
        Class<?> type = Class.forName(InvokeDynamicTest.BOOTSTRAP_CLASS);
        Field field = type.getField("arguments");
        field.set(null, null);
        TypeDescription typeDescription = of(type);
        DynamicType.Loaded<InvokeDynamicTest.Simple> dynamicType = new ByteBuddy().subclass(InvokeDynamicTest.Simple.class).method(ElementMatchers.isDeclaredBy(InvokeDynamicTest.Simple.class)).intercept(InvokeDynamic.bootstrap(typeDescription.getDeclaredMethods().filter(ElementMatchers.named("bootstrapArrayArguments")).getOnly()).withoutArguments()).make().load(getClass().getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(dynamicType.getLoaded().getDeclaredConstructor().newInstance().foo(), CoreMatchers.is(InvokeDynamicTest.FOO));
        Object[] arguments = ((Object[]) (field.get(null)));
        MatcherAssert.assertThat(arguments.length, CoreMatchers.is(0));
    }

    @Test
    @JavaVersionRule.Enforce(value = 7, hotSpot = 7)
    public void testBootstrapWithArrayArgumentsWithArguments() throws Exception {
        Class<?> type = Class.forName(InvokeDynamicTest.BOOTSTRAP_CLASS);
        Field field = type.getField("arguments");
        field.set(null, null);
        TypeDescription typeDescription = of(type);
        DynamicType.Loaded<InvokeDynamicTest.Simple> dynamicType = new ByteBuddy().subclass(InvokeDynamicTest.Simple.class).method(ElementMatchers.isDeclaredBy(InvokeDynamicTest.Simple.class)).intercept(InvokeDynamic.bootstrap(typeDescription.getDeclaredMethods().filter(ElementMatchers.named("bootstrapArrayArguments")).getOnly(), InvokeDynamicTest.INTEGER, InvokeDynamicTest.LONG, InvokeDynamicTest.FLOAT, InvokeDynamicTest.DOUBLE, InvokeDynamicTest.FOO, InvokeDynamicTest.CLASS, InvokeDynamicTest.makeMethodType(InvokeDynamicTest.CLASS), InvokeDynamicTest.makeMethodHandle()).withoutArguments()).make().load(getClass().getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(dynamicType.getLoaded().getDeclaredConstructor().newInstance().foo(), CoreMatchers.is(InvokeDynamicTest.FOO));
        Object[] arguments = ((Object[]) (field.get(null)));
        MatcherAssert.assertThat(arguments.length, CoreMatchers.is(8));
        MatcherAssert.assertThat(arguments[0], CoreMatchers.is(((Object) (InvokeDynamicTest.INTEGER))));
        MatcherAssert.assertThat(arguments[1], CoreMatchers.is(((Object) (InvokeDynamicTest.LONG))));
        MatcherAssert.assertThat(arguments[2], CoreMatchers.is(((Object) (InvokeDynamicTest.FLOAT))));
        MatcherAssert.assertThat(arguments[3], CoreMatchers.is(((Object) (InvokeDynamicTest.DOUBLE))));
        MatcherAssert.assertThat(arguments[4], CoreMatchers.is(((Object) (InvokeDynamicTest.FOO))));
        MatcherAssert.assertThat(arguments[5], CoreMatchers.is(((Object) (InvokeDynamicTest.CLASS))));
        MatcherAssert.assertThat(arguments[6], CoreMatchers.is(InvokeDynamicTest.makeMethodType(InvokeDynamicTest.CLASS)));
        MatcherAssert.assertThat(ofLoaded(arguments[7]), CoreMatchers.is(ofLoaded(InvokeDynamicTest.makeMethodHandle())));
    }

    @Test
    @JavaVersionRule.Enforce(value = 7, hotSpot = 7)
    public void testBootstrapWithExplicitArgumentsWithArguments() throws Exception {
        Class<?> type = Class.forName(InvokeDynamicTest.BOOTSTRAP_CLASS);
        Field field = type.getField("arguments");
        field.set(null, null);
        TypeDescription typeDescription = of(type);
        DynamicType.Loaded<InvokeDynamicTest.Simple> dynamicType = new ByteBuddy().subclass(InvokeDynamicTest.Simple.class).method(ElementMatchers.isDeclaredBy(InvokeDynamicTest.Simple.class)).intercept(InvokeDynamic.bootstrap(typeDescription.getDeclaredMethods().filter(ElementMatchers.named("bootstrapExplicitArguments")).getOnly(), InvokeDynamicTest.INTEGER, InvokeDynamicTest.LONG, InvokeDynamicTest.FLOAT, InvokeDynamicTest.DOUBLE, InvokeDynamicTest.FOO, InvokeDynamicTest.CLASS, InvokeDynamicTest.makeMethodType(InvokeDynamicTest.CLASS), InvokeDynamicTest.makeMethodHandle()).withoutArguments()).make().load(getClass().getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(dynamicType.getLoaded().getDeclaredConstructor().newInstance().foo(), CoreMatchers.is(InvokeDynamicTest.FOO));
        Object[] arguments = ((Object[]) (field.get(null)));
        MatcherAssert.assertThat(arguments.length, CoreMatchers.is(8));
        MatcherAssert.assertThat(arguments[0], CoreMatchers.is(((Object) (InvokeDynamicTest.INTEGER))));
        MatcherAssert.assertThat(arguments[1], CoreMatchers.is(((Object) (InvokeDynamicTest.LONG))));
        MatcherAssert.assertThat(arguments[2], CoreMatchers.is(((Object) (InvokeDynamicTest.FLOAT))));
        MatcherAssert.assertThat(arguments[3], CoreMatchers.is(((Object) (InvokeDynamicTest.DOUBLE))));
        MatcherAssert.assertThat(arguments[4], CoreMatchers.is(((Object) (InvokeDynamicTest.FOO))));
        MatcherAssert.assertThat(arguments[5], CoreMatchers.is(((Object) (InvokeDynamicTest.CLASS))));
        MatcherAssert.assertThat(arguments[6], CoreMatchers.is(InvokeDynamicTest.makeMethodType(InvokeDynamicTest.CLASS)));
        MatcherAssert.assertThat(ofLoaded(arguments[7]), CoreMatchers.is(ofLoaded(InvokeDynamicTest.makeMethodHandle())));
    }

    @Test(expected = IllegalArgumentException.class)
    @JavaVersionRule.Enforce(7)
    public void testBootstrapWithExplicitArgumentsWithoutArgumentsThrowsException() throws Exception {
        TypeDescription typeDescription = of(Class.forName(InvokeDynamicTest.BOOTSTRAP_CLASS));
        InvokeDynamic.bootstrap(typeDescription.getDeclaredMethods().filter(ElementMatchers.named("bootstrapExplicitArguments")).getOnly()).withoutArguments();
    }

    @Test
    @JavaVersionRule.Enforce(value = 7, hotSpot = 7)
    public void testBootstrapOfMethodsWithParametersPrimitive() throws Exception {
        TypeDescription typeDescription = of(Class.forName(InvokeDynamicTest.BOOTSTRAP_CLASS));
        Object value = new Object();
        DynamicType.Loaded<InvokeDynamicTest.Simple> dynamicType = new ByteBuddy().subclass(InvokeDynamicTest.Simple.class).method(ElementMatchers.isDeclaredBy(InvokeDynamicTest.Simple.class)).intercept(InvokeDynamic.bootstrap(typeDescription.getDeclaredMethods().filter(ElementMatchers.named("bootstrapSimple")).getOnly()).invoke(InvokeDynamicTest.FOO, String.class).withBooleanValue(InvokeDynamicTest.BOOLEAN).withByteValue(InvokeDynamicTest.BYTE).withShortValue(InvokeDynamicTest.SHORT).withCharacterValue(InvokeDynamicTest.CHARACTER).withIntegerValue(InvokeDynamicTest.INTEGER).withLongValue(InvokeDynamicTest.LONG).withFloatValue(InvokeDynamicTest.FLOAT).withDoubleValue(InvokeDynamicTest.DOUBLE).withType(TypeDescription.ForLoadedType.of(InvokeDynamicTest.CLASS)).withEnumeration(new EnumerationDescription.ForLoadedEnumeration(makeEnum())).withInstance(net.bytebuddy.utility.JavaConstant.MethodType.ofLoaded(InvokeDynamicTest.makeMethodType(InvokeDynamicTest.CLASS)), ofLoaded(InvokeDynamicTest.makeMethodHandle())).withValue(InvokeDynamicTest.FOO, InvokeDynamicTest.CLASS, makeEnum(), InvokeDynamicTest.makeMethodType(InvokeDynamicTest.CLASS), InvokeDynamicTest.makeMethodHandle(), value)).make().load(getClass().getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(dynamicType.getLoaded().getDeclaredConstructor().newInstance().foo(), CoreMatchers.is((((((((((((((((((("" + (InvokeDynamicTest.BOOLEAN)) + (InvokeDynamicTest.BYTE)) + (InvokeDynamicTest.SHORT)) + (InvokeDynamicTest.CHARACTER)) + (InvokeDynamicTest.INTEGER)) + (InvokeDynamicTest.LONG)) + (InvokeDynamicTest.FLOAT)) + (InvokeDynamicTest.DOUBLE)) + (InvokeDynamicTest.CLASS)) + (makeEnum())) + (InvokeDynamicTest.makeMethodType(InvokeDynamicTest.CLASS))) + (InvokeDynamicTest.makeMethodHandle())) + (InvokeDynamicTest.FOO)) + (InvokeDynamicTest.CLASS)) + (makeEnum())) + (InvokeDynamicTest.makeMethodType(InvokeDynamicTest.CLASS))) + (InvokeDynamicTest.makeMethodHandle())) + value)));
    }

    @Test
    @JavaVersionRule.Enforce(value = 7, hotSpot = 7)
    public void testBootstrapOfMethodsWithParametersWrapperConstantPool() throws Exception {
        TypeDescription typeDescription = of(Class.forName(InvokeDynamicTest.BOOTSTRAP_CLASS));
        Object value = new Object();
        DynamicType.Loaded<InvokeDynamicTest.Simple> dynamicType = new ByteBuddy().subclass(InvokeDynamicTest.Simple.class).method(ElementMatchers.isDeclaredBy(InvokeDynamicTest.Simple.class)).intercept(InvokeDynamic.bootstrap(typeDescription.getDeclaredMethods().filter(ElementMatchers.named("bootstrapSimple")).getOnly()).invoke(InvokeDynamicTest.BAR, String.class).withValue(InvokeDynamicTest.BOOLEAN, InvokeDynamicTest.BYTE, InvokeDynamicTest.SHORT, InvokeDynamicTest.CHARACTER, InvokeDynamicTest.INTEGER, InvokeDynamicTest.LONG, InvokeDynamicTest.FLOAT, InvokeDynamicTest.DOUBLE, InvokeDynamicTest.FOO, InvokeDynamicTest.CLASS, makeEnum(), InvokeDynamicTest.makeMethodType(InvokeDynamicTest.CLASS), InvokeDynamicTest.makeMethodHandle(), value)).make().load(getClass().getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(dynamicType.getLoaded().getDeclaredFields().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(dynamicType.getLoaded().getDeclaredConstructor().newInstance().foo(), CoreMatchers.is((((((((((((((("" + (InvokeDynamicTest.BOOLEAN)) + (InvokeDynamicTest.BYTE)) + (InvokeDynamicTest.SHORT)) + (InvokeDynamicTest.CHARACTER)) + (InvokeDynamicTest.INTEGER)) + (InvokeDynamicTest.LONG)) + (InvokeDynamicTest.FLOAT)) + (InvokeDynamicTest.DOUBLE)) + (InvokeDynamicTest.FOO)) + (InvokeDynamicTest.CLASS)) + (makeEnum())) + (InvokeDynamicTest.makeMethodType(InvokeDynamicTest.CLASS))) + (InvokeDynamicTest.makeMethodHandle())) + value)));
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testBootstrapOfMethodsWithParametersWrapperReference() throws Exception {
        TypeDescription typeDescription = of(Class.forName(InvokeDynamicTest.BOOTSTRAP_CLASS));
        Object value = new Object();
        DynamicType.Loaded<InvokeDynamicTest.Simple> dynamicType = new ByteBuddy().subclass(InvokeDynamicTest.Simple.class).method(ElementMatchers.isDeclaredBy(InvokeDynamicTest.Simple.class)).intercept(// avoid direct method handle
        InvokeDynamic.bootstrap(typeDescription.getDeclaredMethods().filter(ElementMatchers.named("bootstrapSimple")).getOnly()).invoke(InvokeDynamicTest.BAR, String.class).withReference(InvokeDynamicTest.BOOLEAN, InvokeDynamicTest.BYTE, InvokeDynamicTest.SHORT, InvokeDynamicTest.CHARACTER, InvokeDynamicTest.INTEGER, InvokeDynamicTest.LONG, InvokeDynamicTest.FLOAT, InvokeDynamicTest.DOUBLE, InvokeDynamicTest.FOO, InvokeDynamicTest.CLASS, makeEnum(), InvokeDynamicTest.makeMethodType(InvokeDynamicTest.CLASS)).withReference(InvokeDynamicTest.makeMethodHandle()).as(JavaType.METHOD_HANDLE.load()).withReference(value)).make().load(getClass().getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(dynamicType.getLoaded().getDeclaredFields().length, CoreMatchers.is(14));
        MatcherAssert.assertThat(dynamicType.getLoaded().getDeclaredConstructor().newInstance().foo(), CoreMatchers.is((((((((((((((("" + (InvokeDynamicTest.BOOLEAN)) + (InvokeDynamicTest.BYTE)) + (InvokeDynamicTest.SHORT)) + (InvokeDynamicTest.CHARACTER)) + (InvokeDynamicTest.INTEGER)) + (InvokeDynamicTest.LONG)) + (InvokeDynamicTest.FLOAT)) + (InvokeDynamicTest.DOUBLE)) + (InvokeDynamicTest.FOO)) + (InvokeDynamicTest.CLASS)) + (makeEnum())) + (InvokeDynamicTest.makeMethodType(InvokeDynamicTest.CLASS))) + (InvokeDynamicTest.makeMethodHandle())) + value)));
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testBootstrapWithFieldCreation() throws Exception {
        TypeDescription typeDescription = of(Class.forName(InvokeDynamicTest.BOOTSTRAP_CLASS));
        DynamicType.Loaded<InvokeDynamicTest.Simple> dynamicType = new ByteBuddy().subclass(InvokeDynamicTest.Simple.class).defineField(InvokeDynamicTest.FOO, String.class).method(ElementMatchers.isDeclaredBy(InvokeDynamicTest.Simple.class)).intercept(InvokeDynamic.bootstrap(typeDescription.getDeclaredMethods().filter(ElementMatchers.named("bootstrapSimple")).getOnly()).invoke(InvokeDynamicTest.QUX, String.class).withField(InvokeDynamicTest.FOO)).make().load(getClass().getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(dynamicType.getLoaded().getDeclaredFields().length, CoreMatchers.is(1));
        InvokeDynamicTest.Simple instance = dynamicType.getLoaded().getDeclaredConstructor().newInstance();
        Field field = dynamicType.getLoaded().getDeclaredField(InvokeDynamicTest.FOO);
        field.setAccessible(true);
        field.set(instance, InvokeDynamicTest.FOO);
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is(InvokeDynamicTest.FOO));
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testBootstrapWithFieldExplicitType() throws Exception {
        TypeDescription typeDescription = of(Class.forName(InvokeDynamicTest.BOOTSTRAP_CLASS));
        DynamicType.Loaded<InvokeDynamicTest.Simple> dynamicType = new ByteBuddy().subclass(InvokeDynamicTest.Simple.class).defineField(InvokeDynamicTest.FOO, Object.class).method(ElementMatchers.isDeclaredBy(InvokeDynamicTest.Simple.class)).intercept(InvokeDynamic.bootstrap(typeDescription.getDeclaredMethods().filter(ElementMatchers.named("bootstrapSimple")).getOnly()).invoke(InvokeDynamicTest.QUX, String.class).withField(InvokeDynamicTest.FOO).as(String.class).withAssigner(Assigner.DEFAULT, DYNAMIC)).make().load(getClass().getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(dynamicType.getLoaded().getDeclaredFields().length, CoreMatchers.is(1));
        InvokeDynamicTest.Simple instance = dynamicType.getLoaded().getDeclaredConstructor().newInstance();
        Field field = dynamicType.getLoaded().getDeclaredField(InvokeDynamicTest.FOO);
        field.setAccessible(true);
        field.set(instance, InvokeDynamicTest.FOO);
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is(InvokeDynamicTest.FOO));
    }

    @Test(expected = IllegalStateException.class)
    @JavaVersionRule.Enforce(7)
    public void testBootstrapFieldNotExistent() throws Exception {
        TypeDescription typeDescription = of(Class.forName(InvokeDynamicTest.BOOTSTRAP_CLASS));
        new ByteBuddy().subclass(InvokeDynamicTest.Simple.class).method(ElementMatchers.isDeclaredBy(InvokeDynamicTest.Simple.class)).intercept(InvokeDynamic.bootstrap(typeDescription.getDeclaredMethods().filter(ElementMatchers.named("bootstrapSimple")).getOnly()).invoke(InvokeDynamicTest.QUX, String.class).withField(InvokeDynamicTest.FOO).withAssigner(Assigner.DEFAULT, DYNAMIC)).make();
    }

    @Test(expected = IllegalStateException.class)
    @JavaVersionRule.Enforce(7)
    public void testBootstrapFieldNotAssignable() throws Exception {
        TypeDescription typeDescription = of(Class.forName(InvokeDynamicTest.BOOTSTRAP_CLASS));
        new ByteBuddy().subclass(InvokeDynamicTest.Simple.class).defineField(InvokeDynamicTest.FOO, Object.class).method(ElementMatchers.isDeclaredBy(InvokeDynamicTest.Simple.class)).intercept(InvokeDynamic.bootstrap(typeDescription.getDeclaredMethods().filter(ElementMatchers.named("bootstrapSimple")).getOnly()).invoke(InvokeDynamicTest.QUX, String.class).withField(InvokeDynamicTest.FOO).as(String.class)).make();
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testBootstrapWithFieldUse() throws Exception {
        TypeDescription typeDescription = of(Class.forName(InvokeDynamicTest.BOOTSTRAP_CLASS));
        DynamicType.Loaded<InvokeDynamicTest.SimpleWithField> dynamicType = new ByteBuddy().subclass(InvokeDynamicTest.SimpleWithField.class).method(ElementMatchers.isDeclaredBy(InvokeDynamicTest.SimpleWithField.class)).intercept(InvokeDynamic.bootstrap(typeDescription.getDeclaredMethods().filter(ElementMatchers.named("bootstrapSimple")).getOnly()).invoke(InvokeDynamicTest.QUX, String.class).withField(InvokeDynamicTest.FOO)).make().load(getClass().getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(dynamicType.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        InvokeDynamicTest.SimpleWithField instance = dynamicType.getLoaded().getDeclaredConstructor().newInstance();
        Field field = InvokeDynamicTest.SimpleWithField.class.getDeclaredField(InvokeDynamicTest.FOO);
        field.setAccessible(true);
        field.set(instance, InvokeDynamicTest.FOO);
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is(InvokeDynamicTest.FOO));
    }

    @Test(expected = IllegalStateException.class)
    @JavaVersionRule.Enforce(7)
    public void testBootstrapWithFieldUseInvisible() throws Exception {
        TypeDescription typeDescription = of(Class.forName(InvokeDynamicTest.BOOTSTRAP_CLASS));
        new ByteBuddy().subclass(InvokeDynamicTest.SimpleWithFieldInvisible.class).method(ElementMatchers.isDeclaredBy(InvokeDynamicTest.SimpleWithFieldInvisible.class)).intercept(InvokeDynamic.bootstrap(typeDescription.getDeclaredMethods().filter(ElementMatchers.named("bootstrapSimple")).getOnly()).invoke(InvokeDynamicTest.QUX, String.class).withField(InvokeDynamicTest.FOO)).make();
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testBootstrapWithNullValue() throws Exception {
        TypeDescription typeDescription = of(Class.forName(InvokeDynamicTest.BOOTSTRAP_CLASS));
        DynamicType.Loaded<InvokeDynamicTest.Simple> dynamicType = new ByteBuddy().subclass(InvokeDynamicTest.Simple.class).method(ElementMatchers.isDeclaredBy(InvokeDynamicTest.Simple.class)).intercept(InvokeDynamic.bootstrap(typeDescription.getDeclaredMethods().filter(ElementMatchers.named("bootstrapSimple")).getOnly()).invoke(InvokeDynamicTest.QUX, String.class).withNullValue(String.class)).make().load(getClass().getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(dynamicType.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(dynamicType.getLoaded().getDeclaredConstructor().newInstance().foo(), CoreMatchers.nullValue(String.class));
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testBootstrapWithThisValue() throws Exception {
        TypeDescription typeDescription = of(Class.forName(InvokeDynamicTest.BOOTSTRAP_CLASS));
        DynamicType.Loaded<InvokeDynamicTest.Simple> dynamicType = new ByteBuddy().subclass(InvokeDynamicTest.Simple.class).method(ElementMatchers.isDeclaredBy(InvokeDynamicTest.Simple.class)).intercept(InvokeDynamic.bootstrap(typeDescription.getDeclaredMethods().filter(ElementMatchers.named("bootstrapSimple")).getOnly()).invoke(InvokeDynamicTest.BAZ, String.class).withThis(Object.class)).make().load(getClass().getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(dynamicType.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        InvokeDynamicTest.Simple simple = dynamicType.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(simple.foo(), CoreMatchers.is(simple.toString()));
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testBootstrapWithArgument() throws Exception {
        TypeDescription typeDescription = of(Class.forName(InvokeDynamicTest.BOOTSTRAP_CLASS));
        DynamicType.Loaded<InvokeDynamicTest.SimpleWithArgument> dynamicType = new ByteBuddy().subclass(InvokeDynamicTest.SimpleWithArgument.class).method(ElementMatchers.isDeclaredBy(InvokeDynamicTest.SimpleWithArgument.class)).intercept(InvokeDynamic.bootstrap(typeDescription.getDeclaredMethods().filter(ElementMatchers.named("bootstrapSimple")).getOnly()).invoke(InvokeDynamicTest.QUX, String.class).withArgument(0)).make().load(getClass().getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(dynamicType.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(dynamicType.getLoaded().getDeclaredConstructor().newInstance().foo(InvokeDynamicTest.FOO), CoreMatchers.is(InvokeDynamicTest.FOO));
    }

    @Test(expected = IllegalArgumentException.class)
    @JavaVersionRule.Enforce(7)
    public void testNegativeArgumentThrowsException() throws Exception {
        Class<?> type = Class.forName(InvokeDynamicTest.BOOTSTRAP_CLASS);
        TypeDescription typeDescription = of(type);
        InvokeDynamic.bootstrap(typeDescription.getDeclaredMethods().filter(ElementMatchers.named("bootstrapSimple")).getOnly()).invoke(InvokeDynamicTest.QUX, String.class).withArgument((-1));
    }

    @Test(expected = IllegalStateException.class)
    @JavaVersionRule.Enforce(7)
    public void testNonExistentArgumentThrowsException() throws Exception {
        TypeDescription typeDescription = of(Class.forName(InvokeDynamicTest.BOOTSTRAP_CLASS));
        new ByteBuddy().subclass(InvokeDynamicTest.SimpleWithArgument.class).method(ElementMatchers.isDeclaredBy(InvokeDynamicTest.SimpleWithArgument.class)).intercept(InvokeDynamic.bootstrap(typeDescription.getDeclaredMethods().filter(ElementMatchers.named("bootstrapSimple")).getOnly()).invoke(InvokeDynamicTest.QUX, String.class).withArgument(1)).make();
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testChainedInvocation() throws Exception {
        TypeDescription typeDescription = of(Class.forName(InvokeDynamicTest.BOOTSTRAP_CLASS));
        DynamicType.Loaded<InvokeDynamicTest.SimpleWithArgument> dynamicType = new ByteBuddy().subclass(InvokeDynamicTest.SimpleWithArgument.class).method(ElementMatchers.isDeclaredBy(InvokeDynamicTest.SimpleWithArgument.class)).intercept(InvokeDynamic.bootstrap(typeDescription.getDeclaredMethods().filter(ElementMatchers.named("bootstrapSimple")).getOnly()).invoke(InvokeDynamicTest.QUX, String.class).withArgument(0).andThen(FixedValue.value(InvokeDynamicTest.BAZ))).make().load(getClass().getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(dynamicType.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(dynamicType.getLoaded().getDeclaredConstructor().newInstance().foo(InvokeDynamicTest.FOO), CoreMatchers.is(InvokeDynamicTest.BAZ));
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testBootstrapWithImplicitArgument() throws Exception {
        TypeDescription typeDescription = of(Class.forName(InvokeDynamicTest.BOOTSTRAP_CLASS));
        DynamicType.Loaded<InvokeDynamicTest.SimpleWithArgument> dynamicType = new ByteBuddy().subclass(InvokeDynamicTest.SimpleWithArgument.class).method(ElementMatchers.isDeclaredBy(InvokeDynamicTest.SimpleWithArgument.class)).intercept(InvokeDynamic.bootstrap(typeDescription.getDeclaredMethods().filter(ElementMatchers.named("bootstrapSimple")).getOnly()).invoke(InvokeDynamicTest.QUX, String.class).withMethodArguments()).make().load(getClass().getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(dynamicType.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(dynamicType.getLoaded().getDeclaredConstructor().newInstance().foo(InvokeDynamicTest.FOO), CoreMatchers.is(InvokeDynamicTest.FOO));
    }

    @Test(expected = IllegalArgumentException.class)
    @JavaVersionRule.Enforce(7)
    public void testArgumentCannotAssignIllegalInstanceType() throws Exception {
        Class<?> type = Class.forName(InvokeDynamicTest.BOOTSTRAP_CLASS);
        TypeDescription typeDescription = of(type);
        InvokeDynamic.bootstrap(typeDescription.getDeclaredMethods().filter(ElementMatchers.named("bootstrapSimple")).getOnly()).invoke(InvokeDynamicTest.QUX, String.class).withReference(new Object()).as(String.class);
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testLambdaMetaFactory() throws Exception {
        DynamicType.Loaded<InvokeDynamicTest.FunctionalFactory> dynamicType = new ByteBuddy().subclass(InvokeDynamicTest.FunctionalFactory.class).method(ElementMatchers.isDeclaredBy(InvokeDynamicTest.FunctionalFactory.class)).intercept(lambda(InvokeDynamicTest.class.getMethod("value"), Callable.class)).make().load(getClass().getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(dynamicType.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(dynamicType.getLoaded().getDeclaredConstructor().newInstance().make().call(), CoreMatchers.is(InvokeDynamicTest.FOO));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testLambdaMetaFactoryWithArgument() throws Exception {
        DynamicType.Loaded<InvokeDynamicTest.FunctionalFactoryWithValue> dynamicType = new ByteBuddy().subclass(InvokeDynamicTest.FunctionalFactoryWithValue.class).method(ElementMatchers.isDeclaredBy(InvokeDynamicTest.FunctionalFactoryWithValue.class)).intercept(lambda(InvokeDynamicTest.class.getMethod("value", String.class), Callable.class).withArgument(0)).make().load(getClass().getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(dynamicType.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(dynamicType.getLoaded().getDeclaredConstructor().newInstance().make(InvokeDynamicTest.BAR).call(), CoreMatchers.is(((InvokeDynamicTest.FOO) + (InvokeDynamicTest.BAR))));
    }

    @Test(expected = LinkageError.class)
    @JavaVersionRule.Enforce(8)
    public void testLambdaMetaFactoryIllegalBinding() throws Exception {
        DynamicType.Loaded<InvokeDynamicTest.FunctionalFactory> dynamicType = new ByteBuddy().subclass(InvokeDynamicTest.FunctionalFactory.class).method(ElementMatchers.isDeclaredBy(InvokeDynamicTest.FunctionalFactory.class)).intercept(lambda(InvokeDynamicTest.class.getMethod("value"), Callable.class).withValue(InvokeDynamicTest.FOO)).make().load(getClass().getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(dynamicType.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(dynamicType.getLoaded().getDeclaredConstructor().newInstance().make().call(), CoreMatchers.is(InvokeDynamicTest.FOO));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLambdaMetaFactoryNoInterface() throws Exception {
        lambda(InvokeDynamicTest.class.getMethod("value"), Object.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLambdaMetaFactoryNoFunctionalInterface() throws Exception {
        lambda(InvokeDynamicTest.class.getMethod("value"), ExecutorService.class);
    }

    public static class Simple {
        public String foo() {
            return null;
        }
    }

    public static class SimpleWithField {
        public String foo;

        public String foo() {
            return null;
        }
    }

    public static class SimpleWithFieldInvisible {
        private String foo;

        public String foo() {
            return null;
        }
    }

    public static class SimpleWithArgument {
        public String foo(String arg) {
            return null;
        }
    }

    public static class SimpleWithMethod {
        public Callable<String> foo(String arg) {
            return null;
        }

        private static String bar(String arg) {
            return arg;
        }
    }

    public interface FunctionalFactory {
        Callable<String> make();
    }

    public interface FunctionalFactoryWithValue {
        Callable<String> make(String argument);
    }
}

