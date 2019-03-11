package net.bytebuddy.utility;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.JavaVersionRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class JavaConstantDynamicTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Test
    @JavaVersionRule.Enforce(11)
    public void testDynamicConstantFactoryLookupOnly() throws Exception {
        Class<?> bootstrap = Class.forName("net.bytebuddy.test.precompiled.DynamicConstantBootstrap");
        Class<? extends JavaConstantDynamicTest.Foo> baz = new ByteBuddy().subclass(JavaConstantDynamicTest.Foo.class).method(ElementMatchers.isDeclaredBy(JavaConstantDynamicTest.Foo.class)).intercept(FixedValue.value(Dynamic.bootstrap(JavaConstantDynamicTest.FOO, bootstrap.getMethod("bootstrap", Class.forName("java.lang.invoke.MethodHandles$Lookup"), Object[].class)))).make().load(JavaConstantDynamicTest.Foo.class.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(baz.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(baz.getDeclaredMethods().length, CoreMatchers.is(1));
        JavaConstantDynamicTest.Foo foo = baz.getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.instanceOf(bootstrap));
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.sameInstance(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo)));
    }

    @Test
    @JavaVersionRule.Enforce(11)
    public void testDynamicConstantFactoryLookupAndStringOnly() throws Exception {
        Class<?> bootstrap = Class.forName("net.bytebuddy.test.precompiled.DynamicConstantBootstrap");
        Class<? extends JavaConstantDynamicTest.Foo> baz = new ByteBuddy().subclass(JavaConstantDynamicTest.Foo.class).method(ElementMatchers.isDeclaredBy(JavaConstantDynamicTest.Foo.class)).intercept(FixedValue.value(Dynamic.bootstrap(JavaConstantDynamicTest.FOO, bootstrap.getMethod("bootstrap", Class.forName("java.lang.invoke.MethodHandles$Lookup"), String.class, Object[].class)))).make().load(JavaConstantDynamicTest.Foo.class.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(baz.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(baz.getDeclaredMethods().length, CoreMatchers.is(1));
        JavaConstantDynamicTest.Foo foo = baz.getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.instanceOf(bootstrap));
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.sameInstance(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo)));
    }

    @Test
    @JavaVersionRule.Enforce(11)
    public void testDynamicConstantFactoryNoVarargs() throws Exception {
        Class<?> bootstrap = Class.forName("net.bytebuddy.test.precompiled.DynamicConstantBootstrap");
        Class<? extends JavaConstantDynamicTest.Foo> baz = new ByteBuddy().subclass(JavaConstantDynamicTest.Foo.class).method(ElementMatchers.isDeclaredBy(JavaConstantDynamicTest.Foo.class)).intercept(FixedValue.value(Dynamic.bootstrap(JavaConstantDynamicTest.FOO, bootstrap.getMethod("bootstrap", Class.forName("java.lang.invoke.MethodHandles$Lookup"), String.class, Class.class)))).make().load(JavaConstantDynamicTest.Foo.class.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(baz.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(baz.getDeclaredMethods().length, CoreMatchers.is(1));
        JavaConstantDynamicTest.Foo foo = baz.getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.instanceOf(bootstrap));
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.sameInstance(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo)));
    }

    @Test
    @JavaVersionRule.Enforce(11)
    public void testDynamicConstantFactoryVarargs() throws Exception {
        Class<?> bootstrap = Class.forName("net.bytebuddy.test.precompiled.DynamicConstantBootstrap");
        Class<? extends JavaConstantDynamicTest.Foo> baz = new ByteBuddy().subclass(JavaConstantDynamicTest.Foo.class).method(ElementMatchers.isDeclaredBy(JavaConstantDynamicTest.Foo.class)).intercept(FixedValue.value(Dynamic.bootstrap(JavaConstantDynamicTest.FOO, bootstrap.getMethod("bootstrap", Class.forName("java.lang.invoke.MethodHandles$Lookup"), String.class, Class.class, Object[].class)))).make().load(JavaConstantDynamicTest.Foo.class.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(baz.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(baz.getDeclaredMethods().length, CoreMatchers.is(1));
        JavaConstantDynamicTest.Foo foo = baz.getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.instanceOf(bootstrap));
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.sameInstance(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo)));
    }

    @Test
    @JavaVersionRule.Enforce(11)
    public void testDynamicConstantFactoryNested() throws Exception {
        Class<?> bootstrap = Class.forName("net.bytebuddy.test.precompiled.DynamicConstantBootstrap");
        Class<? extends JavaConstantDynamicTest.Foo> baz = new ByteBuddy().subclass(JavaConstantDynamicTest.Foo.class).method(ElementMatchers.isDeclaredBy(JavaConstantDynamicTest.Foo.class)).intercept(FixedValue.value(Dynamic.bootstrap(JavaConstantDynamicTest.FOO, bootstrap.getMethod("bootstrap", Class.forName("java.lang.invoke.MethodHandles$Lookup"), String.class, Class.class, bootstrap), Dynamic.bootstrap(JavaConstantDynamicTest.BAR, bootstrap.getMethod("bootstrap", Class.forName("java.lang.invoke.MethodHandles$Lookup"), Object[].class))))).make().load(JavaConstantDynamicTest.Foo.class.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(baz.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(baz.getDeclaredMethods().length, CoreMatchers.is(1));
        JavaConstantDynamicTest.Foo foo = baz.getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.instanceOf(bootstrap));
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.sameInstance(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo)));
    }

    @Test
    @JavaVersionRule.Enforce(11)
    public void testDynamicConstantFactoryWithArguments() throws Exception {
        Class<?> bootstrap = Class.forName("net.bytebuddy.test.precompiled.DynamicConstantBootstrap");
        Class<? extends JavaConstantDynamicTest.Foo> baz = new ByteBuddy().subclass(JavaConstantDynamicTest.Foo.class).method(ElementMatchers.isDeclaredBy(JavaConstantDynamicTest.Foo.class)).intercept(FixedValue.value(Dynamic.bootstrap(JavaConstantDynamicTest.FOO, bootstrap.getMethod("bootstrap", Class.forName("java.lang.invoke.MethodHandles$Lookup"), String.class, Class.class, int.class, long.class, float.class, double.class, String.class, Class.class, Class.forName("java.lang.invoke.MethodHandle"), Class.forName("java.lang.invoke.MethodType")), 42, 42L, 42.0F, 42.0, JavaConstantDynamicTest.FOO, Object.class, JavaConstantDynamicTest.methodHandle(), JavaConstantDynamicTest.methodType()))).make().load(JavaConstantDynamicTest.Foo.class.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(baz.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(baz.getDeclaredMethods().length, CoreMatchers.is(1));
        JavaConstantDynamicTest.Foo foo = baz.getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.instanceOf(bootstrap));
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.sameInstance(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo)));
    }

    @Test
    @JavaVersionRule.Enforce(11)
    public void testDynamicConstantConstructorLookupOnly() throws Exception {
        Class<?> bootstrap = Class.forName("net.bytebuddy.test.precompiled.DynamicConstantBootstrap");
        Class<? extends JavaConstantDynamicTest.Foo> baz = new ByteBuddy().subclass(JavaConstantDynamicTest.Foo.class).method(ElementMatchers.isDeclaredBy(JavaConstantDynamicTest.Foo.class)).intercept(FixedValue.value(Dynamic.bootstrap(JavaConstantDynamicTest.FOO, bootstrap.getConstructor(Class.forName("java.lang.invoke.MethodHandles$Lookup"), Object[].class)))).make().load(JavaConstantDynamicTest.Foo.class.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(baz.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(baz.getDeclaredMethods().length, CoreMatchers.is(1));
        JavaConstantDynamicTest.Foo foo = baz.getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.instanceOf(bootstrap));
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.sameInstance(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo)));
    }

    @Test
    @JavaVersionRule.Enforce(11)
    public void testDynamicConstantConstructorLookupAndStringOnly() throws Exception {
        Class<?> bootstrap = Class.forName("net.bytebuddy.test.precompiled.DynamicConstantBootstrap");
        Class<? extends JavaConstantDynamicTest.Foo> baz = new ByteBuddy().subclass(JavaConstantDynamicTest.Foo.class).method(ElementMatchers.isDeclaredBy(JavaConstantDynamicTest.Foo.class)).intercept(FixedValue.value(Dynamic.bootstrap(JavaConstantDynamicTest.FOO, bootstrap.getConstructor(Class.forName("java.lang.invoke.MethodHandles$Lookup"), String.class, Object[].class)))).make().load(JavaConstantDynamicTest.Foo.class.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(baz.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(baz.getDeclaredMethods().length, CoreMatchers.is(1));
        JavaConstantDynamicTest.Foo foo = baz.getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.instanceOf(bootstrap));
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.sameInstance(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo)));
    }

    @Test
    @JavaVersionRule.Enforce(11)
    public void testDynamicConstantConstructorNoVarargs() throws Exception {
        Class<?> bootstrap = Class.forName("net.bytebuddy.test.precompiled.DynamicConstantBootstrap");
        Class<? extends JavaConstantDynamicTest.Foo> baz = new ByteBuddy().subclass(JavaConstantDynamicTest.Foo.class).method(ElementMatchers.isDeclaredBy(JavaConstantDynamicTest.Foo.class)).intercept(FixedValue.value(Dynamic.bootstrap(JavaConstantDynamicTest.FOO, bootstrap.getConstructor(Class.forName("java.lang.invoke.MethodHandles$Lookup"), String.class, Class.class)))).make().load(JavaConstantDynamicTest.Foo.class.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(baz.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(baz.getDeclaredMethods().length, CoreMatchers.is(1));
        JavaConstantDynamicTest.Foo foo = baz.getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.instanceOf(bootstrap));
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.sameInstance(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo)));
    }

    @Test
    @JavaVersionRule.Enforce(11)
    public void testDynamicConstantConstructorVarargs() throws Exception {
        Class<?> bootstrap = Class.forName("net.bytebuddy.test.precompiled.DynamicConstantBootstrap");
        Class<? extends JavaConstantDynamicTest.Foo> baz = new ByteBuddy().subclass(JavaConstantDynamicTest.Foo.class).method(ElementMatchers.isDeclaredBy(JavaConstantDynamicTest.Foo.class)).intercept(FixedValue.value(Dynamic.bootstrap(JavaConstantDynamicTest.FOO, bootstrap.getConstructor(Class.forName("java.lang.invoke.MethodHandles$Lookup"), String.class, Class.class, Object[].class)))).make().load(JavaConstantDynamicTest.Foo.class.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(baz.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(baz.getDeclaredMethods().length, CoreMatchers.is(1));
        JavaConstantDynamicTest.Foo foo = baz.getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.instanceOf(bootstrap));
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.sameInstance(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo)));
    }

    @Test
    @JavaVersionRule.Enforce(11)
    public void testDynamicConstantConstructorNested() throws Exception {
        Class<?> bootstrap = Class.forName("net.bytebuddy.test.precompiled.DynamicConstantBootstrap");
        Class<? extends JavaConstantDynamicTest.Foo> baz = new ByteBuddy().subclass(JavaConstantDynamicTest.Foo.class).method(ElementMatchers.isDeclaredBy(JavaConstantDynamicTest.Foo.class)).intercept(FixedValue.value(Dynamic.bootstrap(JavaConstantDynamicTest.FOO, bootstrap.getConstructor(Class.forName("java.lang.invoke.MethodHandles$Lookup"), String.class, Class.class, bootstrap), Dynamic.bootstrap(JavaConstantDynamicTest.BAR, bootstrap.getConstructor(Class.forName("java.lang.invoke.MethodHandles$Lookup"), Object[].class))))).make().load(JavaConstantDynamicTest.Foo.class.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(baz.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(baz.getDeclaredMethods().length, CoreMatchers.is(1));
        JavaConstantDynamicTest.Foo foo = baz.getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.instanceOf(bootstrap));
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.sameInstance(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo)));
    }

    @Test
    @JavaVersionRule.Enforce(11)
    public void testDynamicConstantConstructorWithArguments() throws Exception {
        Class<?> bootstrap = Class.forName("net.bytebuddy.test.precompiled.DynamicConstantBootstrap");
        Class<? extends JavaConstantDynamicTest.Foo> baz = new ByteBuddy().subclass(JavaConstantDynamicTest.Foo.class).method(ElementMatchers.isDeclaredBy(JavaConstantDynamicTest.Foo.class)).intercept(FixedValue.value(Dynamic.bootstrap(JavaConstantDynamicTest.FOO, bootstrap.getConstructor(Class.forName("java.lang.invoke.MethodHandles$Lookup"), String.class, Class.class, int.class, long.class, float.class, double.class, String.class, Class.class, Class.forName("java.lang.invoke.MethodHandle"), Class.forName("java.lang.invoke.MethodType")), 42, 42L, 42.0F, 42.0, JavaConstantDynamicTest.FOO, Object.class, JavaConstantDynamicTest.methodHandle(), JavaConstantDynamicTest.methodType()))).make().load(JavaConstantDynamicTest.Foo.class.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(baz.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(baz.getDeclaredMethods().length, CoreMatchers.is(1));
        JavaConstantDynamicTest.Foo foo = baz.getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.instanceOf(bootstrap));
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.sameInstance(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo)));
    }

    @Test
    @JavaVersionRule.Enforce(11)
    public void testNullConstant() throws Exception {
        Class<? extends JavaConstantDynamicTest.Foo> baz = new ByteBuddy().subclass(JavaConstantDynamicTest.Foo.class).method(ElementMatchers.isDeclaredBy(JavaConstantDynamicTest.Foo.class)).intercept(FixedValue.value(Dynamic.ofNullConstant())).make().load(JavaConstantDynamicTest.Foo.class.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(baz.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(baz.getDeclaredMethods().length, CoreMatchers.is(1));
        JavaConstantDynamicTest.Foo foo = baz.getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.nullValue(Object.class));
    }

    @Test
    @JavaVersionRule.Enforce(11)
    public void testPrimitiveType() throws Exception {
        Class<? extends JavaConstantDynamicTest.Foo> baz = new ByteBuddy().subclass(JavaConstantDynamicTest.Foo.class).method(ElementMatchers.isDeclaredBy(JavaConstantDynamicTest.Foo.class)).intercept(FixedValue.value(Dynamic.ofPrimitiveType(void.class))).make().load(JavaConstantDynamicTest.Foo.class.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(baz.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(baz.getDeclaredMethods().length, CoreMatchers.is(1));
        JavaConstantDynamicTest.Foo foo = baz.getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.sameInstance(((Object) (void.class))));
    }

    @Test
    @JavaVersionRule.Enforce(11)
    public void testEnumeration() throws Exception {
        Class<? extends JavaConstantDynamicTest.Foo> baz = new ByteBuddy().subclass(JavaConstantDynamicTest.Foo.class).method(ElementMatchers.isDeclaredBy(JavaConstantDynamicTest.Foo.class)).intercept(FixedValue.value(Dynamic.ofEnumeration(JavaConstantDynamicTest.SampleEnum.INSTANCE))).make().load(JavaConstantDynamicTest.Foo.class.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(baz.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(baz.getDeclaredMethods().length, CoreMatchers.is(1));
        JavaConstantDynamicTest.Foo foo = baz.getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.sameInstance(((Object) (JavaConstantDynamicTest.SampleEnum.INSTANCE))));
    }

    @Test
    @JavaVersionRule.Enforce(11)
    public void testField() throws Exception {
        Class<? extends JavaConstantDynamicTest.Foo> baz = new ByteBuddy().subclass(JavaConstantDynamicTest.Foo.class).method(ElementMatchers.isDeclaredBy(JavaConstantDynamicTest.Foo.class)).intercept(FixedValue.value(Dynamic.ofField(JavaConstantDynamicTest.SampleClass.class.getDeclaredField("FOO")))).make().load(JavaConstantDynamicTest.Foo.class.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(baz.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(baz.getDeclaredMethods().length, CoreMatchers.is(1));
        JavaConstantDynamicTest.Foo foo = baz.getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.sameInstance(JavaConstantDynamicTest.SampleClass.FOO));
    }

    @Test
    @JavaVersionRule.Enforce(11)
    public void testFieldWithSelfDeclaredType() throws Exception {
        Class<? extends JavaConstantDynamicTest.Foo> baz = new ByteBuddy().subclass(JavaConstantDynamicTest.Foo.class).method(ElementMatchers.isDeclaredBy(JavaConstantDynamicTest.Foo.class)).intercept(FixedValue.value(Dynamic.ofField(JavaConstantDynamicTest.SampleClass.class.getDeclaredField("BAR")))).make().load(JavaConstantDynamicTest.Foo.class.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(baz.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(baz.getDeclaredMethods().length, CoreMatchers.is(1));
        JavaConstantDynamicTest.Foo foo = baz.getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.sameInstance(((Object) (JavaConstantDynamicTest.SampleClass.BAR))));
    }

    @Test
    @JavaVersionRule.Enforce(11)
    public void testConstructWithArguments() throws Exception {
        Class<?> bootstrap = Class.forName("net.bytebuddy.test.precompiled.DynamicConstantBootstrap");
        Class<? extends JavaConstantDynamicTest.Foo> baz = new ByteBuddy().subclass(JavaConstantDynamicTest.Foo.class).method(ElementMatchers.isDeclaredBy(JavaConstantDynamicTest.Foo.class)).intercept(FixedValue.value(Dynamic.ofInvocation(bootstrap.getConstructor(int.class, long.class, float.class, double.class, String.class, Class.class, Class.forName("java.lang.invoke.MethodHandle"), Class.forName("java.lang.invoke.MethodType")), 42, 42L, 42.0F, 42.0, JavaConstantDynamicTest.FOO, Object.class, JavaConstantDynamicTest.methodHandle(), JavaConstantDynamicTest.methodType()))).make().load(JavaConstantDynamicTest.Foo.class.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(baz.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(baz.getDeclaredMethods().length, CoreMatchers.is(1));
        JavaConstantDynamicTest.Foo foo = baz.getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.instanceOf(bootstrap));
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.sameInstance(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo)));
    }

    @Test
    @JavaVersionRule.Enforce(11)
    public void testInvoke() throws Exception {
        Class<? extends JavaConstantDynamicTest.Foo> baz = new ByteBuddy().subclass(JavaConstantDynamicTest.Foo.class).method(ElementMatchers.isDeclaredBy(JavaConstantDynamicTest.Foo.class)).intercept(FixedValue.value(Dynamic.ofInvocation(JavaConstantDynamicTest.SampleClass.class.getMethod("make")))).make().load(JavaConstantDynamicTest.Foo.class.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(baz.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(baz.getDeclaredMethods().length, CoreMatchers.is(1));
        JavaConstantDynamicTest.Foo foo = baz.getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.instanceOf(JavaConstantDynamicTest.SampleClass.class));
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.sameInstance(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo)));
    }

    @Test
    @JavaVersionRule.Enforce(11)
    public void testConstruct() throws Exception {
        Class<? extends JavaConstantDynamicTest.Foo> baz = new ByteBuddy().subclass(JavaConstantDynamicTest.Foo.class).method(ElementMatchers.isDeclaredBy(JavaConstantDynamicTest.Foo.class)).intercept(FixedValue.value(Dynamic.ofInvocation(JavaConstantDynamicTest.SampleClass.class.getConstructor()))).make().load(JavaConstantDynamicTest.Foo.class.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(baz.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(baz.getDeclaredMethods().length, CoreMatchers.is(1));
        JavaConstantDynamicTest.Foo foo = baz.getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.instanceOf(JavaConstantDynamicTest.SampleClass.class));
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.sameInstance(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo)));
    }

    @Test
    @JavaVersionRule.Enforce(11)
    public void testInvokeWithArguments() throws Exception {
        Class<?> bootstrap = Class.forName("net.bytebuddy.test.precompiled.DynamicConstantBootstrap");
        Class<? extends JavaConstantDynamicTest.Foo> baz = new ByteBuddy().subclass(JavaConstantDynamicTest.Foo.class).method(ElementMatchers.isDeclaredBy(JavaConstantDynamicTest.Foo.class)).intercept(FixedValue.value(Dynamic.ofInvocation(bootstrap.getMethod("make", int.class, long.class, float.class, double.class, String.class, Class.class, Class.forName("java.lang.invoke.MethodHandle"), Class.forName("java.lang.invoke.MethodType")), 42, 42L, 42.0F, 42.0, JavaConstantDynamicTest.FOO, Object.class, JavaConstantDynamicTest.methodHandle(), JavaConstantDynamicTest.methodType()))).make().load(JavaConstantDynamicTest.Foo.class.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(baz.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(baz.getDeclaredMethods().length, CoreMatchers.is(1));
        JavaConstantDynamicTest.Foo foo = baz.getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.instanceOf(bootstrap));
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.sameInstance(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo)));
    }

    @Test
    @JavaVersionRule.Enforce(11)
    public void testStaticFieldVarHandle() throws Exception {
        Class<? extends JavaConstantDynamicTest.Foo> baz = new ByteBuddy().subclass(JavaConstantDynamicTest.Foo.class).method(ElementMatchers.isDeclaredBy(JavaConstantDynamicTest.Foo.class)).intercept(FixedValue.value(Dynamic.ofVarHandle(JavaConstantDynamicTest.SampleClass.class.getDeclaredField("FOO")))).make().load(JavaConstantDynamicTest.Foo.class.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(baz.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(baz.getDeclaredMethods().length, CoreMatchers.is(1));
        JavaConstantDynamicTest.Foo foo = baz.getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.instanceOf(Class.forName("java.lang.invoke.VarHandle")));
    }

    @Test
    @JavaVersionRule.Enforce(11)
    public void testNonStaticFieldVarHandle() throws Exception {
        Class<? extends JavaConstantDynamicTest.Foo> baz = new ByteBuddy().subclass(JavaConstantDynamicTest.Foo.class).method(ElementMatchers.isDeclaredBy(JavaConstantDynamicTest.Foo.class)).intercept(FixedValue.value(Dynamic.ofVarHandle(JavaConstantDynamicTest.SampleClass.class.getDeclaredField("qux")))).make().load(JavaConstantDynamicTest.Foo.class.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(baz.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(baz.getDeclaredMethods().length, CoreMatchers.is(1));
        JavaConstantDynamicTest.Foo foo = baz.getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.instanceOf(Class.forName("java.lang.invoke.VarHandle")));
    }

    @Test
    @JavaVersionRule.Enforce(11)
    public void testArrayVarHandle() throws Exception {
        Class<? extends JavaConstantDynamicTest.Foo> baz = new ByteBuddy().subclass(JavaConstantDynamicTest.Foo.class).method(ElementMatchers.isDeclaredBy(JavaConstantDynamicTest.Foo.class)).intercept(FixedValue.value(Dynamic.ofArrayVarHandle(Object[].class))).make().load(JavaConstantDynamicTest.Foo.class.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(baz.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(baz.getDeclaredMethods().length, CoreMatchers.is(1));
        JavaConstantDynamicTest.Foo foo = baz.getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(baz.getDeclaredMethod(JavaConstantDynamicTest.FOO).invoke(foo), CoreMatchers.instanceOf(Class.forName("java.lang.invoke.VarHandle")));
    }

    @Test
    public void testTypeResolution() {
        MatcherAssert.assertThat(Dynamic.ofNullConstant(), CoreMatchers.equalTo(Dynamic.ofNullConstant()));
        MatcherAssert.assertThat(Dynamic.ofNullConstant(), CoreMatchers.not(CoreMatchers.equalTo(Dynamic.ofNullConstant().withType(TypeDescription.STRING))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalTypeResolutionForVoid() throws Exception {
        Dynamic.ofNullConstant().withType(void.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncompatibleTypeForMethod() throws Exception {
        Dynamic.ofInvocation(Object.class.getMethod("toString"), JavaConstantDynamicTest.FOO).withType(Integer.class);
    }

    @Test
    @JavaVersionRule.Enforce(11)
    public void testConstructorTypeResolutionCompatible() throws Exception {
        Class<?> bootstrap = Class.forName("net.bytebuddy.test.precompiled.DynamicConstantBootstrap");
        JavaConstant.Dynamic dynamic = Dynamic.bootstrap(JavaConstantDynamicTest.FOO, bootstrap.getConstructor(Class.forName("java.lang.invoke.MethodHandles$Lookup"), Object[].class));
        MatcherAssert.assertThat(dynamic.withType(bootstrap), CoreMatchers.equalTo(((JavaConstant) (dynamic))));
    }

    @Test(expected = IllegalArgumentException.class)
    @JavaVersionRule.Enforce(11)
    public void testConstructorTypeResolutionIncompatible() throws Exception {
        Class<?> bootstrap = Class.forName("net.bytebuddy.test.precompiled.DynamicConstantBootstrap");
        Dynamic.bootstrap(JavaConstantDynamicTest.FOO, bootstrap.getConstructor(Class.forName("java.lang.invoke.MethodHandles$Lookup"), Object[].class)).withType(String.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPrimitiveNonPrimitive() throws Exception {
        Dynamic.ofPrimitiveType(Object.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFieldNonStatic() throws Exception {
        Dynamic.ofField(JavaConstantDynamicTest.SampleClass.class.getField("qux"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFieldNonFinal() throws Exception {
        Dynamic.ofField(JavaConstantDynamicTest.SampleClass.class.getField("baz"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvokeVoid() throws Exception {
        Dynamic.ofInvocation(JavaConstantDynamicTest.SampleClass.class.getMethod("foo"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvokeNonStatic() throws Exception {
        Dynamic.ofInvocation(JavaConstantDynamicTest.SampleClass.class.getMethod("bar"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvokeWrongArguments() throws Exception {
        Dynamic.ofInvocation(JavaConstantDynamicTest.SampleClass.class.getConstructor(), "foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testArrayVarHandleNoArray() throws Exception {
        Dynamic.ofArrayVarHandle(Object.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBootstrapNonBootstrap() throws Exception {
        Dynamic.bootstrap(JavaConstantDynamicTest.FOO, JavaConstantDynamicTest.SampleClass.class.getMethod("foo"));
    }

    @Test(expected = IllegalArgumentException.class)
    @JavaVersionRule.Enforce(11)
    public void testEmptyName() throws Exception {
        Dynamic.bootstrap("", Class.forName("net.bytebuddy.test.precompiled.DynamicConstantBootstrap").getMethod("bootstrap", Class.forName("java.lang.invoke.MethodHandles$Lookup"), Object[].class));
    }

    @Test(expected = IllegalArgumentException.class)
    @JavaVersionRule.Enforce(11)
    public void testNameWithDot() throws Exception {
        Dynamic.bootstrap(".", Class.forName("net.bytebuddy.test.precompiled.DynamicConstantBootstrap").getMethod("bootstrap", Class.forName("java.lang.invoke.MethodHandles$Lookup"), Object[].class));
    }

    public static class Foo {
        public Object foo() {
            return null;
        }
    }

    public enum SampleEnum {

        INSTANCE;}

    public static class SampleClass {
        public static final Object FOO = new Object();

        public static final JavaConstantDynamicTest.SampleClass BAR = new JavaConstantDynamicTest.SampleClass();

        public final Object qux = new Object();

        public static Object baz = new Object();

        public static JavaConstantDynamicTest.SampleClass make() {
            return new JavaConstantDynamicTest.SampleClass();
        }

        public static void foo() {
            /* empty */
        }

        public Object bar() {
            return null;
        }
    }
}

