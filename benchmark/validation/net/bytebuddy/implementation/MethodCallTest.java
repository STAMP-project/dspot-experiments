package net.bytebuddy.implementation;


import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.modifier.Ownership;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.FieldLocator;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.constant.TextConstant;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.CallTraceable;
import net.bytebuddy.test.utility.JavaVersionRule;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;
import static net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy.Default.NO_CONSTRUCTORS;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.DYNAMIC;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.STATIC;
import static net.bytebuddy.utility.JavaConstant.MethodType.ofLoaded;


public class MethodCallTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String INVOKE_FOO = "invokeFoo";

    private static final String SINGLE_DEFAULT_METHOD = "net.bytebuddy.test.precompiled.SingleDefaultMethodInterface";

    @Rule
    public TestRule methodRule = new MockitoRule(this);

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Mock
    private Assigner nonAssigner;

    @Test
    public void testStaticMethodInvocationWithoutArguments() throws Exception {
        DynamicType.Loaded<MethodCallTest.SimpleMethod> loaded = new ByteBuddy().subclass(MethodCallTest.SimpleMethod.class).method(ElementMatchers.named(MethodCallTest.FOO)).intercept(MethodCall.invoke(MethodCallTest.SimpleMethod.class.getDeclaredMethod(MethodCallTest.BAR))).make().load(MethodCallTest.SimpleMethod.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MethodCallTest.SimpleMethod instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is(MethodCallTest.BAR));
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(MethodCallTest.SimpleMethod.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(MethodCallTest.SimpleMethod.class));
    }

    @Test
    public void testExternalStaticMethodInvocationWithoutArguments() throws Exception {
        DynamicType.Loaded<MethodCallTest.SimpleMethod> loaded = new ByteBuddy().subclass(MethodCallTest.SimpleMethod.class).method(ElementMatchers.named(MethodCallTest.FOO)).intercept(MethodCall.invoke(MethodCallTest.StaticExternalMethod.class.getDeclaredMethod(MethodCallTest.BAR))).make().load(MethodCallTest.SimpleMethod.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MethodCallTest.SimpleMethod instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is(MethodCallTest.BAR));
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(MethodCallTest.SimpleMethod.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(MethodCallTest.SimpleMethod.class));
    }

    @Test
    public void testInstanceMethodInvocationWithoutArguments() throws Exception {
        DynamicType.Loaded<MethodCallTest.InstanceMethod> loaded = new ByteBuddy().subclass(MethodCallTest.InstanceMethod.class).method(ElementMatchers.named(MethodCallTest.FOO)).intercept(MethodCall.invoke(MethodCallTest.InstanceMethod.class.getDeclaredMethod(MethodCallTest.BAR))).make().load(MethodCallTest.SimpleMethod.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MethodCallTest.InstanceMethod instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is(MethodCallTest.BAR));
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(MethodCallTest.InstanceMethod.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(MethodCallTest.InstanceMethod.class));
    }

    @Test
    public void testInstanceMethodInvocationWithoutArgumentsByMatcher() throws Exception {
        DynamicType.Loaded<MethodCallTest.InstanceMethod> loaded = new ByteBuddy().subclass(MethodCallTest.InstanceMethod.class).method(ElementMatchers.named(MethodCallTest.FOO)).intercept(MethodCall.invoke(ElementMatchers.named(MethodCallTest.BAR))).make().load(MethodCallTest.SimpleMethod.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MethodCallTest.InstanceMethod instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is(MethodCallTest.BAR));
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(MethodCallTest.InstanceMethod.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(MethodCallTest.InstanceMethod.class));
    }

    @Test
    public void testMethodInvocationUsingStackManipulation() throws Exception {
        DynamicType.Loaded<MethodCallTest.SimpleMethod> loaded = new ByteBuddy().subclass(MethodCallTest.SimpleMethod.class).method(ElementMatchers.named(MethodCallTest.FOO)).intercept(MethodCall.invoke(String.class.getMethod("toUpperCase")).on(new TextConstant(MethodCallTest.FOO), String.class)).make().load(MethodCallTest.SimpleMethod.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MethodCallTest.SimpleMethod instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is(MethodCallTest.FOO.toUpperCase()));
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(MethodCallTest.SimpleMethod.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(MethodCallTest.SimpleMethod.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testMatchedCallAmbiguous() throws Exception {
        new ByteBuddy().subclass(MethodCallTest.InstanceMethod.class).method(ElementMatchers.named(MethodCallTest.FOO)).intercept(MethodCall.invoke(ElementMatchers.any())).make();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOnArgumentInvocationNegativeArgument() throws Exception {
        MethodCall.invoke(Object.class.getDeclaredMethod("toString")).onArgument((-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOnArgumentInvocationNonExisting() throws Exception {
        new ByteBuddy().subclass(MethodCallTest.ArgumentCall.class).method(ElementMatchers.isDeclaredBy(MethodCallTest.ArgumentCall.class)).intercept(MethodCall.invoke(Object.class.getDeclaredMethod("toString")).onArgument(10)).make();
    }

    @Test
    public void testInvokeOnArgument() throws Exception {
        DynamicType.Loaded<MethodCallTest.ArgumentCall> loaded = new ByteBuddy().subclass(MethodCallTest.ArgumentCall.class).method(ElementMatchers.isDeclaredBy(MethodCallTest.ArgumentCall.class)).intercept(MethodCall.invoke(MethodCallTest.ArgumentCall.Target.class.getDeclaredMethod("foo")).onArgument(0)).make().load(MethodCallTest.ArgumentCall.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MethodCallTest.ArgumentCall instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.foo(new MethodCallTest.ArgumentCall.Target(MethodCallTest.BAR)), CoreMatchers.is(MethodCallTest.BAR));
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(MethodCallTest.InstanceMethod.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(MethodCallTest.ArgumentCall.class));
    }

    @Test
    public void testInvokeOnArgumentUsingMatcher() throws Exception {
        DynamicType.Loaded<MethodCallTest.ArgumentCall> loaded = new ByteBuddy().subclass(MethodCallTest.ArgumentCall.class).method(ElementMatchers.named("foo")).intercept(MethodCall.invoke(ElementMatchers.named("toUpperCase").and(ElementMatchers.takesArguments(0))).onMethodCall(MethodCall.invoke(ElementMatchers.named("foo")).onArgument(0))).make().load(MethodCallTest.ArgumentCall.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MethodCallTest.ArgumentCall instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.foo(new MethodCallTest.ArgumentCall.Target("foo")), CoreMatchers.is("FOO"));
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(MethodCallTest.InstanceMethod.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(MethodCallTest.ArgumentCall.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testInvokeOnArgumentNonAssignable() throws Exception {
        new ByteBuddy().subclass(MethodCallTest.ArgumentCallDynamic.class).method(ElementMatchers.isDeclaredBy(MethodCallTest.ArgumentCallDynamic.class)).intercept(MethodCall.invoke(MethodCallTest.ArgumentCallDynamic.Target.class.getDeclaredMethod("foo")).onArgument(0)).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testInvokeOnArgumentNonVirtual() throws Exception {
        new ByteBuddy().subclass(MethodCallTest.ArgumentCallDynamic.class).method(ElementMatchers.isDeclaredBy(MethodCallTest.ArgumentCallDynamic.class)).intercept(MethodCall.invoke(MethodCallTest.NonVirtual.class.getDeclaredMethod("foo")).onArgument(0)).make();
    }

    @Test
    public void testInvokeOnArgumentDynamic() throws Exception {
        DynamicType.Loaded<MethodCallTest.ArgumentCallDynamic> loaded = new ByteBuddy().subclass(MethodCallTest.ArgumentCallDynamic.class).method(ElementMatchers.isDeclaredBy(MethodCallTest.ArgumentCallDynamic.class)).intercept(MethodCall.invoke(MethodCallTest.ArgumentCallDynamic.Target.class.getDeclaredMethod("foo")).onArgument(0).withAssigner(Assigner.DEFAULT, DYNAMIC)).make().load(MethodCallTest.ArgumentCallDynamic.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MethodCallTest.ArgumentCallDynamic instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.foo(new MethodCallTest.ArgumentCallDynamic.Target(MethodCallTest.BAR)), CoreMatchers.is(MethodCallTest.BAR));
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(MethodCallTest.InstanceMethod.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(MethodCallTest.ArgumentCallDynamic.class));
    }

    @Test
    public void testInvokeOnMethodCall() throws Exception {
        DynamicType.Loaded<MethodCallTest.MethodCallChaining> loaded = new ByteBuddy().subclass(MethodCallTest.MethodCallChaining.class).method(ElementMatchers.named("foobar")).intercept(onMethodCall(MethodCall.invoke(ElementMatchers.named("bar")))).make().load(MethodCallTest.MethodCallChaining.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MethodCallTest.MethodCallChaining instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.foobar(), CoreMatchers.is("BAR"));
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(MethodCallTest.InstanceMethod.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(MethodCallTest.MethodCallChaining.class));
    }

    @Test
    public void testInvokeOnMethodCallUsingMatcher() throws Exception {
        DynamicType.Loaded<MethodCallTest.MethodCallChaining> loaded = new ByteBuddy().subclass(MethodCallTest.MethodCallChaining.class).method(ElementMatchers.named("foobar")).intercept(onMethodCall(MethodCall.invoke(ElementMatchers.named("bar")))).make().load(MethodCallTest.MethodCallChaining.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MethodCallTest.MethodCallChaining instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.foobar(), CoreMatchers.is("BAR"));
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(MethodCallTest.InstanceMethod.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(MethodCallTest.MethodCallChaining.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testInvokeOnIncompatibleMethodCall() throws Exception {
        new ByteBuddy().subclass(MethodCallTest.MethodCallChaining.class).method(ElementMatchers.named("foobar")).intercept(onMethodCall(MethodCall.invoke(ElementMatchers.named("bar")))).make();
    }

    @Test
    public void testStaticOnStaticFieldFromAnotherClass() throws Exception {
        DynamicType.Loaded<Object> loaded = new ByteBuddy().subclass(Object.class).invokable(ElementMatchers.isTypeInitializer()).intercept(MethodCall.invoke(ElementMatchers.named("println").and(ElementMatchers.takesArguments(Object.class))).onField("out", new FieldLocator.ForExactType.Factory(of(System.class))).with("")).make().load(Object.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(Object.class));
    }

    @Test
    public void testStaticFieldExplicit() throws Exception {
        DynamicType.Loaded<Object> loaded = new ByteBuddy().subclass(Object.class).invokable(ElementMatchers.isTypeInitializer()).intercept(MethodCall.invoke(ElementMatchers.named("println").and(ElementMatchers.takesArguments(Object.class))).onField(System.class.getField("out")).with("")).make().load(Object.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(Object.class));
    }

    @Test
    public void testOnStaticFieldFromAnotherClass() throws Exception {
        DynamicType.Loaded<Object> loaded = new ByteBuddy().subclass(Object.class).defineMethod("foo", void.class).intercept(MethodCall.invoke(ElementMatchers.named("println").and(ElementMatchers.takesArguments(Object.class))).onField("out", new FieldLocator.ForExactType.Factory(of(System.class))).with("fooCall")).make().load(Object.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(Object.class));
    }

    @Test
    public void testOnFieldUsingMatcher() throws Exception {
        DynamicType.Loaded<MethodCallTest.MethodCallOnField> loaded = new ByteBuddy().subclass(MethodCallTest.MethodCallOnField.class).method(ElementMatchers.named("foo")).intercept(MethodCall.invoke(ElementMatchers.named("trim")).onField("foo")).make().load(MethodCallTest.MethodCallOnField.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethod(MethodCallTest.FOO), CoreMatchers.not(CoreMatchers.nullValue(Method.class)));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MethodCallTest.MethodCallOnField instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        instance.foo = " abc ";
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(MethodCallTest.MethodCallOnField.class));
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is("abc"));
    }

    @Test
    public void testSuperConstructorInvocationWithoutArguments() throws Exception {
        DynamicType.Loaded<Object> loaded = new ByteBuddy().subclass(Object.class).constructor(ElementMatchers.any()).intercept(MethodCall.invoke(Object.class.getDeclaredConstructor()).onSuper()).make().load(Object.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(Object.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(Object.class));
    }

    @Test
    public void testSuperConstructorInvocationUsingMatcher() throws Exception {
        MatcherAssert.assertThat(new ByteBuddy().subclass(Object.class, NO_CONSTRUCTORS).defineConstructor(Visibility.PUBLIC).intercept(MethodCall.invoke(ElementMatchers.isConstructor()).onSuper()).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded().getConstructor().newInstance(), CoreMatchers.notNullValue(Object.class));
    }

    @Test
    public void testObjectConstruction() throws Exception {
        DynamicType.Loaded<MethodCallTest.SelfReference> loaded = new ByteBuddy().subclass(MethodCallTest.SelfReference.class).method(ElementMatchers.isDeclaredBy(MethodCallTest.SelfReference.class)).intercept(MethodCall.construct(MethodCallTest.SelfReference.class.getDeclaredConstructor())).make().load(MethodCallTest.SelfReference.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MethodCallTest.SelfReference instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MethodCallTest.SelfReference created = instance.foo();
        MatcherAssert.assertThat(created.getClass(), CoreMatchers.<Class<?>>is(MethodCallTest.SelfReference.class));
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(MethodCallTest.SelfReference.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(MethodCallTest.SelfReference.class));
        MatcherAssert.assertThat(created, CoreMatchers.not(instance));
    }

    @Test
    public void testSelfInvocation() throws Exception {
        MethodCallTest.SuperMethodInvocation delegate = Mockito.mock(MethodCallTest.SuperMethodInvocation.class);
        Mockito.when(delegate.foo()).thenReturn(MethodCallTest.FOO);
        DynamicType.Loaded<MethodCallTest.SuperMethodInvocation> loaded = new ByteBuddy().subclass(MethodCallTest.SuperMethodInvocation.class).method(ElementMatchers.takesArguments(0).and(ElementMatchers.named(MethodCallTest.FOO))).intercept(MethodCall.invokeSelf().on(delegate)).make().load(MethodCallTest.SuperMethodInvocation.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethod(MethodCallTest.FOO), CoreMatchers.not(CoreMatchers.nullValue(Method.class)));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(1));
        MethodCallTest.SuperMethodInvocation instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(MethodCallTest.SuperMethodInvocation.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(MethodCallTest.SuperMethodInvocation.class));
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is(MethodCallTest.FOO));
        Mockito.verify(delegate).foo();
        Mockito.verifyNoMoreInteractions(delegate);
    }

    @Test
    public void testSuperInvocation() throws Exception {
        DynamicType.Loaded<MethodCallTest.SuperMethodInvocation> loaded = new ByteBuddy().subclass(MethodCallTest.SuperMethodInvocation.class).method(ElementMatchers.takesArguments(0).and(ElementMatchers.named(MethodCallTest.FOO))).intercept(MethodCall.invokeSuper()).make().load(MethodCallTest.SuperMethodInvocation.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethod(MethodCallTest.FOO), CoreMatchers.not(CoreMatchers.nullValue(Method.class)));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MethodCallTest.SuperMethodInvocation instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(MethodCallTest.SuperMethodInvocation.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(MethodCallTest.SuperMethodInvocation.class));
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is(MethodCallTest.FOO));
    }

    @Test
    public void testWithExplicitArgumentConstantPool() throws Exception {
        DynamicType.Loaded<MethodCallTest.MethodCallWithExplicitArgument> loaded = new ByteBuddy().subclass(MethodCallTest.MethodCallWithExplicitArgument.class).method(ElementMatchers.isDeclaredBy(MethodCallTest.MethodCallWithExplicitArgument.class)).intercept(MethodCall.invokeSuper().with(MethodCallTest.FOO)).make().load(MethodCallTest.MethodCallWithExplicitArgument.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethod(MethodCallTest.FOO, String.class), CoreMatchers.not(CoreMatchers.nullValue(Method.class)));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MethodCallTest.MethodCallWithExplicitArgument instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(MethodCallTest.MethodCallWithExplicitArgument.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(MethodCallTest.MethodCallWithExplicitArgument.class));
        MatcherAssert.assertThat(instance.foo(MethodCallTest.BAR), CoreMatchers.is(MethodCallTest.FOO));
    }

    @Test(expected = IllegalStateException.class)
    public void testWithExplicitArgumentConstantPoolNonAssignable() throws Exception {
        new ByteBuddy().subclass(MethodCallTest.MethodCallWithExplicitArgument.class).method(ElementMatchers.isDeclaredBy(MethodCallTest.MethodCallWithExplicitArgument.class)).intercept(MethodCall.invokeSuper().with(MethodCallTest.FOO).withAssigner(nonAssigner, STATIC)).make();
    }

    @Test
    public void testWithExplicitArgumentStackManipulation() throws Exception {
        DynamicType.Loaded<MethodCallTest.MethodCallWithExplicitArgument> loaded = new ByteBuddy().subclass(MethodCallTest.MethodCallWithExplicitArgument.class).method(ElementMatchers.isDeclaredBy(MethodCallTest.MethodCallWithExplicitArgument.class)).intercept(MethodCall.invokeSuper().with(new TextConstant(MethodCallTest.FOO), String.class)).make().load(MethodCallTest.MethodCallWithExplicitArgument.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethod(MethodCallTest.FOO, String.class), CoreMatchers.not(CoreMatchers.nullValue(Method.class)));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MethodCallTest.MethodCallWithExplicitArgument instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(MethodCallTest.MethodCallWithExplicitArgument.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(MethodCallTest.MethodCallWithExplicitArgument.class));
        MatcherAssert.assertThat(instance.foo(MethodCallTest.BAR), CoreMatchers.is(MethodCallTest.FOO));
    }

    @Test
    public void testWithExplicitArgumentField() throws Exception {
        DynamicType.Loaded<MethodCallTest.MethodCallWithExplicitArgument> loaded = new ByteBuddy().subclass(MethodCallTest.MethodCallWithExplicitArgument.class).method(ElementMatchers.isDeclaredBy(MethodCallTest.MethodCallWithExplicitArgument.class)).intercept(MethodCall.invokeSuper().withReference(MethodCallTest.FOO)).make().load(MethodCallTest.MethodCallWithExplicitArgument.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethod(MethodCallTest.FOO, String.class), CoreMatchers.not(CoreMatchers.nullValue(Method.class)));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(1));
        MethodCallTest.MethodCallWithExplicitArgument instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(MethodCallTest.MethodCallWithExplicitArgument.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(MethodCallTest.MethodCallWithExplicitArgument.class));
        MatcherAssert.assertThat(instance.foo(MethodCallTest.BAR), CoreMatchers.is(MethodCallTest.FOO));
    }

    @Test(expected = IllegalStateException.class)
    public void testWithExplicitArgumentFieldNonAssignable() throws Exception {
        new ByteBuddy().subclass(MethodCallTest.MethodCallWithExplicitArgument.class).method(ElementMatchers.isDeclaredBy(MethodCallTest.MethodCallWithExplicitArgument.class)).intercept(MethodCall.invokeSuper().withReference(MethodCallTest.FOO).withAssigner(nonAssigner, STATIC)).make();
    }

    @Test
    public void testWithArgument() throws Exception {
        DynamicType.Loaded<MethodCallTest.MethodCallWithExplicitArgument> loaded = new ByteBuddy().subclass(MethodCallTest.MethodCallWithExplicitArgument.class).method(ElementMatchers.isDeclaredBy(MethodCallTest.MethodCallWithExplicitArgument.class)).intercept(MethodCall.invokeSuper().withArgument(0)).make().load(MethodCallTest.MethodCallWithExplicitArgument.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethod(MethodCallTest.FOO, String.class), CoreMatchers.not(CoreMatchers.nullValue(Method.class)));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MethodCallTest.MethodCallWithExplicitArgument instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(MethodCallTest.MethodCallWithExplicitArgument.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(MethodCallTest.MethodCallWithExplicitArgument.class));
        MatcherAssert.assertThat(instance.foo(MethodCallTest.BAR), CoreMatchers.is(MethodCallTest.BAR));
    }

    @Test
    public void testWithAllArguments() throws Exception {
        DynamicType.Loaded<MethodCallTest.MethodCallWithExplicitArgument> loaded = new ByteBuddy().subclass(MethodCallTest.MethodCallWithExplicitArgument.class).method(ElementMatchers.isDeclaredBy(MethodCallTest.MethodCallWithExplicitArgument.class)).intercept(MethodCall.invokeSuper().withAllArguments()).make().load(MethodCallTest.MethodCallWithExplicitArgument.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethod(MethodCallTest.FOO, String.class), CoreMatchers.not(CoreMatchers.nullValue(Method.class)));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MethodCallTest.MethodCallWithExplicitArgument instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(MethodCallTest.MethodCallWithExplicitArgument.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(MethodCallTest.MethodCallWithExplicitArgument.class));
        MatcherAssert.assertThat(instance.foo(MethodCallTest.BAR), CoreMatchers.is(MethodCallTest.BAR));
    }

    @Test
    public void testWithAllArgumentsTwoArguments() throws Exception {
        DynamicType.Loaded<MethodCallTest.MethodCallWithTwoExplicitArguments> loaded = new ByteBuddy().subclass(MethodCallTest.MethodCallWithTwoExplicitArguments.class).method(ElementMatchers.isDeclaredBy(MethodCallTest.MethodCallWithTwoExplicitArguments.class)).intercept(MethodCall.invokeSuper().withAllArguments()).make().load(MethodCallTest.MethodCallWithTwoExplicitArguments.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethod(MethodCallTest.FOO, String.class, String.class), CoreMatchers.not(CoreMatchers.nullValue(Method.class)));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MethodCallTest.MethodCallWithTwoExplicitArguments instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(MethodCallTest.MethodCallWithTwoExplicitArguments.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(MethodCallTest.MethodCallWithTwoExplicitArguments.class));
        MatcherAssert.assertThat(instance.foo(MethodCallTest.FOO, MethodCallTest.BAR), CoreMatchers.is(((MethodCallTest.FOO) + (MethodCallTest.BAR))));
    }

    @Test
    public void testWithArgumentsAsArray() throws Exception {
        DynamicType.Loaded<MethodCallTest.ArrayConsuming> loaded = new ByteBuddy().subclass(MethodCallTest.ArrayConsuming.class).method(ElementMatchers.named(MethodCallTest.FOO)).intercept(MethodCall.invoke(MethodCallTest.ArrayConsuming.class.getDeclaredMethod(MethodCallTest.BAR, String[].class)).withArgumentArray()).make().load(MethodCallTest.ArrayConsuming.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethod(MethodCallTest.FOO, String.class, String.class), CoreMatchers.not(CoreMatchers.nullValue(Method.class)));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MethodCallTest.ArrayConsuming instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(MethodCallTest.ArrayConsuming.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(MethodCallTest.ArrayConsuming.class));
        MatcherAssert.assertThat(instance.foo(MethodCallTest.FOO, MethodCallTest.BAR), CoreMatchers.is(((MethodCallTest.FOO) + (MethodCallTest.BAR))));
    }

    @Test
    public void testWithArgumentsFromArray() throws Exception {
        DynamicType.Loaded<MethodCallTest.MethodCallWithExplicitArgument> loaded = new ByteBuddy().subclass(MethodCallTest.MethodCallWithExplicitArgument.class).implement(MethodCallTest.MethodCallDelegator.class).intercept(MethodCall.invoke(MethodCallTest.MethodCallWithExplicitArgument.class.getDeclaredMethod("foo", String.class)).withArgumentArrayElements(0, 1)).make().load(MethodCallTest.MethodCallDelegator.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethod(MethodCallTest.INVOKE_FOO, String[].class), CoreMatchers.not(CoreMatchers.nullValue(Method.class)));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MethodCallTest.MethodCallDelegator instance = ((MethodCallTest.MethodCallDelegator) (loaded.getLoaded().getDeclaredConstructor().newInstance()));
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(MethodCallTest.MethodCallWithExplicitArgument.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(MethodCallTest.MethodCallDelegator.class));
        MatcherAssert.assertThat(instance.invokeFoo(MethodCallTest.BAR), CoreMatchers.is(MethodCallTest.BAR));
    }

    @Test
    public void testWithArgumentsFromArrayComplete() throws Exception {
        DynamicType.Loaded<MethodCallTest.MethodCallWithExplicitArgument> loaded = new ByteBuddy().subclass(MethodCallTest.MethodCallWithExplicitArgument.class).implement(MethodCallTest.MethodCallDelegator.class).intercept(MethodCall.invoke(MethodCallTest.MethodCallWithExplicitArgument.class.getDeclaredMethod("foo", String.class)).withArgumentArrayElements(0)).make().load(MethodCallTest.MethodCallDelegator.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethod(MethodCallTest.INVOKE_FOO, String[].class), CoreMatchers.not(CoreMatchers.nullValue(Method.class)));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MethodCallTest.MethodCallDelegator instance = ((MethodCallTest.MethodCallDelegator) (loaded.getLoaded().getDeclaredConstructor().newInstance()));
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(MethodCallTest.MethodCallWithExplicitArgument.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(MethodCallTest.MethodCallDelegator.class));
        MatcherAssert.assertThat(instance.invokeFoo(MethodCallTest.BAR), CoreMatchers.is(MethodCallTest.BAR));
    }

    @Test
    public void testWithArgumentsFromArrayExplicitSize() throws Exception {
        DynamicType.Loaded<MethodCallTest.MethodCallWithExplicitArgument> loaded = new ByteBuddy().subclass(MethodCallTest.MethodCallWithExplicitArgument.class).implement(MethodCallTest.MethodCallDelegator.class).intercept(MethodCall.invoke(MethodCallTest.MethodCallWithExplicitArgument.class.getDeclaredMethod("foo", String.class)).withArgumentArrayElements(0, 1, 1)).make().load(MethodCallTest.MethodCallDelegator.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethod(MethodCallTest.INVOKE_FOO, String[].class), CoreMatchers.not(CoreMatchers.nullValue(Method.class)));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MethodCallTest.MethodCallDelegator instance = ((MethodCallTest.MethodCallDelegator) (loaded.getLoaded().getDeclaredConstructor().newInstance()));
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(MethodCallTest.MethodCallWithExplicitArgument.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(MethodCallTest.MethodCallDelegator.class));
        MatcherAssert.assertThat(instance.invokeFoo(MethodCallTest.FOO, MethodCallTest.BAR, MethodCallTest.FOO), CoreMatchers.is(MethodCallTest.BAR));
    }

    @Test(expected = IllegalStateException.class)
    public void testWithArgumentsFromArrayDoesNotExist() throws Exception {
        new ByteBuddy().subclass(MethodCallTest.MethodCallWithExplicitArgument.class).implement(MethodCallTest.MethodCallDelegator.class).intercept(MethodCall.invoke(MethodCallTest.MethodCallWithExplicitArgument.class.getDeclaredMethod("foo", String.class)).withArgumentArrayElements(1, 1)).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testWithArgumentsFromArrayDoesNotExistCompleteArray() throws Exception {
        new ByteBuddy().subclass(MethodCallTest.MethodCallWithExplicitArgument.class).implement(MethodCallTest.MethodCallDelegator.class).intercept(MethodCall.invoke(MethodCallTest.MethodCallWithExplicitArgument.class.getDeclaredMethod("foo", String.class)).withArgumentArrayElements(1)).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testWithArgumentsFromArrayIllegalType() throws Exception {
        new ByteBuddy().subclass(MethodCallTest.MethodCallWithExplicitArgument.class).implement(MethodCallTest.IllegalMethodCallDelegator.class).intercept(MethodCall.invoke(MethodCallTest.MethodCallWithExplicitArgument.class.getDeclaredMethod("foo", String.class)).withArgumentArrayElements(0, 1)).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testWithArgumentsFromArrayIllegalTypeCompleteArray() throws Exception {
        new ByteBuddy().subclass(MethodCallTest.MethodCallWithExplicitArgument.class).implement(MethodCallTest.IllegalMethodCallDelegator.class).intercept(MethodCall.invoke(MethodCallTest.MethodCallWithExplicitArgument.class.getDeclaredMethod("foo", String.class)).withArgumentArrayElements(0)).make();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeIndex() throws Exception {
        MethodCall.invoke(MethodCallTest.MethodCallWithExplicitArgument.class.getDeclaredMethod("foo", String.class)).withArgumentArrayElements((-1), 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeIndexComplete() throws Exception {
        MethodCall.invoke(MethodCallTest.MethodCallWithExplicitArgument.class.getDeclaredMethod("foo", String.class)).withArgumentArrayElements((-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeStartIndex() throws Exception {
        MethodCall.invoke(MethodCallTest.MethodCallWithExplicitArgument.class.getDeclaredMethod("foo", String.class)).withArgumentArrayElements(0, (-1), 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeSize() throws Exception {
        MethodCall.invoke(MethodCallTest.MethodCallWithExplicitArgument.class.getDeclaredMethod("foo", String.class)).withArgumentArrayElements(0, 1, (-1));
    }

    @Test
    public void testSameSize() throws Exception {
        MethodCall methodCall = MethodCall.invoke(MethodCallTest.MethodCallWithExplicitArgument.class.getDeclaredMethod("foo", String.class));
        MatcherAssert.assertThat(methodCall.withArgumentArrayElements(0, 0), CoreMatchers.sameInstance(methodCall));
    }

    @Test(expected = IllegalStateException.class)
    public void testWithTooBigParameter() throws Exception {
        new ByteBuddy().subclass(MethodCallTest.MethodCallWithExplicitArgument.class).method(ElementMatchers.isDeclaredBy(MethodCallTest.MethodCallWithExplicitArgument.class)).intercept(MethodCall.invokeSuper().withArgument(1)).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testWithParameterNonAssignable() throws Exception {
        new ByteBuddy().subclass(MethodCallTest.MethodCallWithExplicitArgument.class).method(ElementMatchers.isDeclaredBy(MethodCallTest.MethodCallWithExplicitArgument.class)).intercept(MethodCall.invokeSuper().withArgument(0).withAssigner(nonAssigner, STATIC)).make();
    }

    @Test
    public void testWithField() throws Exception {
        DynamicType.Loaded<MethodCallTest.MethodCallWithField> loaded = new ByteBuddy().subclass(MethodCallTest.MethodCallWithField.class).method(ElementMatchers.isDeclaredBy(MethodCallTest.MethodCallWithField.class)).intercept(MethodCall.invokeSuper().withField(MethodCallTest.FOO)).make().load(MethodCallTest.MethodCallWithField.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethod(MethodCallTest.FOO, String.class), CoreMatchers.not(CoreMatchers.nullValue(Method.class)));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MethodCallTest.MethodCallWithField instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        instance.foo = MethodCallTest.FOO;
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(MethodCallTest.MethodCallWithField.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(MethodCallTest.MethodCallWithField.class));
        MatcherAssert.assertThat(instance.foo(MethodCallTest.BAR), CoreMatchers.is(MethodCallTest.FOO));
    }

    @Test(expected = IllegalStateException.class)
    public void testWithFieldNotExist() throws Exception {
        new ByteBuddy().subclass(MethodCallTest.MethodCallWithField.class).method(ElementMatchers.isDeclaredBy(MethodCallTest.MethodCallWithField.class)).intercept(MethodCall.invokeSuper().withField(MethodCallTest.BAR)).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testWithFieldNonAssignable() throws Exception {
        new ByteBuddy().subclass(MethodCallTest.MethodCallWithField.class).method(ElementMatchers.isDeclaredBy(MethodCallTest.MethodCallWithField.class)).intercept(MethodCall.invokeSuper().withField(MethodCallTest.FOO).withAssigner(nonAssigner, STATIC)).make();
    }

    @Test
    public void testWithFieldHierarchyVisibility() throws Exception {
        DynamicType.Loaded<MethodCallTest.InvisibleMethodCallWithField> loaded = new ByteBuddy().subclass(MethodCallTest.InvisibleMethodCallWithField.class).method(ElementMatchers.isDeclaredBy(MethodCallTest.InvisibleMethodCallWithField.class)).intercept(MethodCall.invokeSuper().withField(MethodCallTest.FOO)).make().load(MethodCallTest.InvisibleMethodCallWithField.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethod(MethodCallTest.FOO, String.class), CoreMatchers.not(CoreMatchers.nullValue(Method.class)));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MethodCallTest.InvisibleMethodCallWithField instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        ((MethodCallTest.InvisibleBase) (instance)).foo = MethodCallTest.FOO;
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(MethodCallTest.InvisibleMethodCallWithField.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(MethodCallTest.InvisibleMethodCallWithField.class));
        MatcherAssert.assertThat(instance.foo(MethodCallTest.BAR), CoreMatchers.is(MethodCallTest.FOO));
    }

    @Test
    public void testWithThis() throws Exception {
        DynamicType.Loaded<MethodCallTest.MethodCallWithThis> loaded = new ByteBuddy().subclass(MethodCallTest.MethodCallWithThis.class).method(ElementMatchers.isDeclaredBy(MethodCallTest.MethodCallWithThis.class)).intercept(MethodCall.invokeSuper().withThis()).make().load(MethodCallTest.MethodCallWithThis.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethod(MethodCallTest.FOO, MethodCallTest.MethodCallWithThis.class), CoreMatchers.not(CoreMatchers.nullValue(Method.class)));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MethodCallTest.MethodCallWithThis instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(MethodCallTest.MethodCallWithThis.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(MethodCallTest.MethodCallWithThis.class));
        MatcherAssert.assertThat(instance.foo(null), CoreMatchers.is(instance));
    }

    @Test
    public void testWithOwnType() throws Exception {
        DynamicType.Loaded<MethodCallTest.MethodCallWithOwnType> loaded = new ByteBuddy().subclass(MethodCallTest.MethodCallWithOwnType.class).method(ElementMatchers.isDeclaredBy(MethodCallTest.MethodCallWithOwnType.class)).intercept(MethodCall.invokeSuper().withOwnType()).make().load(MethodCallTest.MethodCallWithOwnType.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethod(MethodCallTest.FOO, Class.class), CoreMatchers.not(CoreMatchers.nullValue(Method.class)));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MethodCallTest.MethodCallWithOwnType instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(MethodCallTest.MethodCallWithThis.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(MethodCallTest.MethodCallWithOwnType.class));
        MatcherAssert.assertThat(instance.foo(null), CoreMatchers.<Class<?>>is(loaded.getLoaded()));
    }

    @Test
    public void testInvokeWithMethodCall() throws Exception {
        DynamicType.Loaded<MethodCallTest.MethodCallChaining> loaded = new ByteBuddy().subclass(MethodCallTest.MethodCallChaining.class).method(ElementMatchers.named("foobar")).intercept(withMethodCall(MethodCall.invoke(ElementMatchers.named("bar")))).make().load(MethodCallTest.MethodCallChaining.class.getClassLoader(), WRAPPER);
        MethodCallTest.MethodCallChaining instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethod("foobar"), CoreMatchers.not(CoreMatchers.nullValue(Method.class)));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(instance.foobar(), CoreMatchers.is("foobar"));
    }

    @Test(expected = IllegalStateException.class)
    public void testStaticInvokeWithMethodCall() throws Exception {
        new ByteBuddy().subclass(MethodCallTest.MethodCallChaining.class).defineMethod("staticFoobar", String.class, Ownership.STATIC).intercept(withMethodCall(MethodCall.invoke(ElementMatchers.named("bar")))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testInvokeWithIncompatibleMethodCall() throws Exception {
        new ByteBuddy().subclass(MethodCallTest.MethodCallChaining.class).method(ElementMatchers.named("foobar")).intercept(withMethodCall(MethodCall.invoke(ElementMatchers.named("someInt")))).make();
    }

    @Test
    public void testImplementationAppendingMethod() throws Exception {
        DynamicType.Loaded<MethodCallTest.MethodCallAppending> loaded = new ByteBuddy().subclass(MethodCallTest.MethodCallAppending.class).method(ElementMatchers.isDeclaredBy(MethodCallTest.MethodCallAppending.class)).intercept(MethodCall.invokeSuper().andThen(new Implementation.Simple(new TextConstant(MethodCallTest.FOO), MethodReturn.REFERENCE))).make().load(MethodCallTest.MethodCallAppending.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethod(MethodCallTest.FOO), CoreMatchers.not(CoreMatchers.nullValue(Method.class)));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MethodCallTest.MethodCallAppending instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(MethodCallTest.MethodCallAppending.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(MethodCallTest.MethodCallAppending.class));
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is(((Object) (MethodCallTest.FOO))));
        instance.assertOnlyCall(MethodCallTest.FOO);
    }

    @Test
    public void testImplementationAppendingConstructor() throws Exception {
        DynamicType.Loaded<MethodCallTest.MethodCallAppending> loaded = new ByteBuddy().subclass(MethodCallTest.MethodCallAppending.class).method(ElementMatchers.isDeclaredBy(MethodCallTest.MethodCallAppending.class)).intercept(MethodCall.construct(Object.class.getDeclaredConstructor()).andThen(new Implementation.Simple(new TextConstant(MethodCallTest.FOO), MethodReturn.REFERENCE))).make().load(MethodCallTest.MethodCallAppending.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethod(MethodCallTest.FOO), CoreMatchers.not(CoreMatchers.nullValue(Method.class)));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MethodCallTest.MethodCallAppending instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(MethodCallTest.MethodCallAppending.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(MethodCallTest.MethodCallAppending.class));
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is(((Object) (MethodCallTest.FOO))));
        instance.assertZeroCalls();
    }

    @Test
    public void testWithExplicitTarget() throws Exception {
        Object target = new Object();
        DynamicType.Loaded<MethodCallTest.ExplicitTarget> loaded = new ByteBuddy().subclass(MethodCallTest.ExplicitTarget.class).method(ElementMatchers.isDeclaredBy(MethodCallTest.ExplicitTarget.class)).intercept(MethodCall.invoke(Object.class.getDeclaredMethod("toString")).on(target)).make().load(MethodCallTest.ExplicitTarget.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethod(MethodCallTest.FOO), CoreMatchers.not(CoreMatchers.nullValue(Method.class)));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(1));
        MethodCallTest.ExplicitTarget instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(MethodCallTest.ExplicitTarget.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(MethodCallTest.ExplicitTarget.class));
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is(target.toString()));
    }

    @Test
    public void testWithFieldTarget() throws Exception {
        Object target = new Object();
        DynamicType.Loaded<MethodCallTest.ExplicitTarget> loaded = new ByteBuddy().subclass(MethodCallTest.ExplicitTarget.class).defineField(MethodCallTest.FOO, Object.class, Visibility.PUBLIC).method(ElementMatchers.isDeclaredBy(MethodCallTest.ExplicitTarget.class)).intercept(MethodCall.invoke(Object.class.getDeclaredMethod("toString")).onField(MethodCallTest.FOO)).make().load(MethodCallTest.ExplicitTarget.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethod(MethodCallTest.FOO), CoreMatchers.not(CoreMatchers.nullValue(Method.class)));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(1));
        MethodCallTest.ExplicitTarget instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        loaded.getLoaded().getDeclaredField(MethodCallTest.FOO).set(instance, target);
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(MethodCallTest.ExplicitTarget.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(MethodCallTest.ExplicitTarget.class));
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is(target.toString()));
    }

    @Test
    public void testUnloadedType() throws Exception {
        DynamicType.Loaded<MethodCallTest.SimpleMethod> loaded = new ByteBuddy().subclass(MethodCallTest.SimpleMethod.class).method(ElementMatchers.named(MethodCallTest.FOO)).intercept(MethodCall.invoke(MethodCallTest.Foo.class.getDeclaredMethod(MethodCallTest.BAR, Object.class, Object.class)).with(TypeDescription.OBJECT, TypeDescription.STRING)).make().load(MethodCallTest.SimpleMethod.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MethodCallTest.SimpleMethod instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is((("" + (Object.class)) + (String.class))));
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(MethodCallTest.SimpleMethod.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(MethodCallTest.SimpleMethod.class));
    }

    @Test
    @JavaVersionRule.Enforce(value = 7, hotSpot = 7)
    public void testJava7Types() throws Exception {
        DynamicType.Loaded<MethodCallTest.SimpleMethod> loaded = new ByteBuddy().subclass(MethodCallTest.SimpleMethod.class).method(ElementMatchers.named(MethodCallTest.FOO)).intercept(MethodCall.invoke(MethodCallTest.Foo.class.getDeclaredMethod(MethodCallTest.BAR, Object.class, Object.class)).with(MethodCallTest.makeMethodHandle(), MethodCallTest.makeMethodType(void.class))).make().load(MethodCallTest.SimpleMethod.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MethodCallTest.SimpleMethod instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is((("" + (MethodCallTest.makeMethodHandle())) + (MethodCallTest.makeMethodType(void.class)))));
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(MethodCallTest.SimpleMethod.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(MethodCallTest.SimpleMethod.class));
    }

    @Test
    @JavaVersionRule.Enforce(value = 7, hotSpot = 7)
    public void testJava7TypesExplicit() throws Exception {
        DynamicType.Loaded<MethodCallTest.SimpleMethod> loaded = new ByteBuddy().subclass(MethodCallTest.SimpleMethod.class).method(ElementMatchers.named(MethodCallTest.FOO)).intercept(MethodCall.invoke(MethodCallTest.Foo.class.getDeclaredMethod(MethodCallTest.BAR, Object.class, Object.class)).with(net.bytebuddy.utility.JavaConstant.MethodHandle.ofLoaded(MethodCallTest.makeMethodHandle()), ofLoaded(MethodCallTest.makeMethodType(void.class)))).make().load(MethodCallTest.SimpleMethod.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MethodCallTest.SimpleMethod instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is((("" + (MethodCallTest.makeMethodHandle())) + (MethodCallTest.makeMethodType(void.class)))));
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(MethodCallTest.SimpleMethod.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(MethodCallTest.SimpleMethod.class));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testDefaultMethod() throws Exception {
        DynamicType.Loaded<Object> loaded = new ByteBuddy().subclass(Object.class).implement(Class.forName(MethodCallTest.SINGLE_DEFAULT_METHOD)).method(ElementMatchers.not(ElementMatchers.isDeclaredBy(Object.class))).intercept(MethodCall.invoke(Class.forName(MethodCallTest.SINGLE_DEFAULT_METHOD).getDeclaredMethod(MethodCallTest.FOO)).onDefault()).make().load(Class.forName(MethodCallTest.SINGLE_DEFAULT_METHOD).getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        Method method = loaded.getLoaded().getDeclaredMethod(MethodCallTest.FOO);
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(method.invoke(instance), CoreMatchers.is(((Object) (MethodCallTest.FOO))));
    }

    @Test
    public void testCallable() throws Exception {
        MethodCallTest.Traceable traceable = new MethodCallTest.Traceable();
        DynamicType.Loaded<MethodCallTest.SimpleStringMethod> loaded = new ByteBuddy().subclass(MethodCallTest.SimpleStringMethod.class).method(ElementMatchers.isDeclaredBy(MethodCallTest.SimpleStringMethod.class)).intercept(MethodCall.call(traceable)).make().load(MethodCallTest.SimpleStringMethod.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredConstructor().newInstance().foo(), CoreMatchers.is(MethodCallTest.FOO));
        traceable.assertOnlyCall(MethodCallTest.FOO);
    }

    @Test
    public void testRunnable() throws Exception {
        MethodCallTest.Traceable traceable = new MethodCallTest.Traceable();
        DynamicType.Loaded<MethodCallTest.SimpleStringMethod> loaded = new ByteBuddy().subclass(MethodCallTest.SimpleStringMethod.class).method(ElementMatchers.isDeclaredBy(MethodCallTest.SimpleStringMethod.class)).intercept(MethodCall.run(traceable)).make().load(MethodCallTest.SimpleStringMethod.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredConstructor().newInstance().foo(), CoreMatchers.nullValue(String.class));
        traceable.assertOnlyCall(MethodCallTest.FOO);
    }

    @Test
    public void testFieldSetting() throws Exception {
        DynamicType.Loaded<MethodCallTest.FieldSetting> loaded = new ByteBuddy().subclass(MethodCallTest.FieldSetting.class).method(ElementMatchers.named(MethodCallTest.FOO)).intercept(MethodCall.invoke(MethodCallTest.FieldSetting.class.getMethod(MethodCallTest.BAR)).setsField(MethodCallTest.FieldSetting.class.getField(MethodCallTest.FOO))).make().load(MethodCallTest.FieldSetting.class.getClassLoader(), WRAPPER);
        MethodCallTest.FieldSetting instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        instance.foo();
        MatcherAssert.assertThat(instance.foo, CoreMatchers.is(((Object) (instance.bar()))));
    }

    @Test
    public void testFieldSettingWithMatcher() throws Exception {
        DynamicType.Loaded<MethodCallTest.FieldSetting> loaded = new ByteBuddy().subclass(MethodCallTest.FieldSetting.class).method(ElementMatchers.named(MethodCallTest.FOO)).intercept(setsField(ElementMatchers.is(MethodCallTest.FieldSetting.class.getField(MethodCallTest.FOO)))).make().load(MethodCallTest.FieldSetting.class.getClassLoader(), WRAPPER);
        MethodCallTest.FieldSetting instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        instance.foo();
        MatcherAssert.assertThat(instance.foo, CoreMatchers.is(((Object) (instance.bar()))));
    }

    @Test(expected = IllegalStateException.class)
    public void testFieldSettingLast() throws Exception {
        new ByteBuddy().subclass(MethodCallTest.FieldSetting.class).method(ElementMatchers.named(MethodCallTest.QUX)).intercept(MethodCall.invoke(MethodCallTest.FieldSetting.class.getMethod(MethodCallTest.BAR)).setsField(MethodCallTest.FieldSetting.class.getField(MethodCallTest.FOO))).make();
    }

    @Test
    public void testFieldSettingLastChained() throws Exception {
        DynamicType.Loaded<MethodCallTest.FieldSetting> loaded = new ByteBuddy().subclass(MethodCallTest.FieldSetting.class).method(ElementMatchers.named(MethodCallTest.QUX)).intercept(setsField(ElementMatchers.is(MethodCallTest.FieldSetting.class.getField(MethodCallTest.FOO))).andThen(StubMethod.INSTANCE)).make().load(MethodCallTest.FieldSetting.class.getClassLoader(), WRAPPER);
        MethodCallTest.FieldSetting instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.qux(), CoreMatchers.nullValue(Object.class));
        MatcherAssert.assertThat(instance.foo, CoreMatchers.is(((Object) (instance.bar()))));
    }

    @Test
    public void testConstructorIsAccessibleFromDifferentPackage() throws Exception {
        MatcherAssert.assertThat(new ByteBuddy().subclass(MethodCallTest.ProtectedConstructor.class, NO_CONSTRUCTORS).name("foo.Bar").defineConstructor(Visibility.PUBLIC).intercept(MethodCall.invoke(MethodCallTest.ProtectedConstructor.class.getDeclaredConstructor()).onSuper()).make().load(MethodCallTest.Foo.class.getClassLoader(), WRAPPER).getLoaded().newInstance(), CoreMatchers.instanceOf(MethodCallTest.ProtectedConstructor.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testDefaultMethodNotCompatible() throws Exception {
        new ByteBuddy().subclass(Object.class).method(ElementMatchers.isDeclaredBy(Object.class)).intercept(MethodCall.invoke(String.class.getDeclaredMethod("toString")).onDefault()).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodTypeIncompatible() throws Exception {
        new ByteBuddy().subclass(MethodCallTest.InstanceMethod.class).method(ElementMatchers.isDeclaredBy(MethodCallTest.InstanceMethod.class)).intercept(MethodCall.invoke(String.class.getDeclaredMethod("toLowerCase"))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testArgumentIncompatibleTooFew() throws Exception {
        new ByteBuddy().subclass(MethodCallTest.InstanceMethod.class).method(ElementMatchers.named(MethodCallTest.FOO)).intercept(MethodCall.invoke(MethodCallTest.StaticIncompatibleExternalMethod.class.getDeclaredMethod("bar", String.class))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testArgumentIncompatibleTooMany() throws Exception {
        new ByteBuddy().subclass(MethodCallTest.InstanceMethod.class).method(ElementMatchers.named(MethodCallTest.FOO)).intercept(MethodCall.invoke(MethodCallTest.StaticIncompatibleExternalMethod.class.getDeclaredMethod("bar", String.class)).with(MethodCallTest.FOO, MethodCallTest.BAR)).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testArgumentIncompatibleNotAssignable() throws Exception {
        new ByteBuddy().subclass(MethodCallTest.InstanceMethod.class).method(ElementMatchers.named(MethodCallTest.FOO)).intercept(MethodCall.invoke(MethodCallTest.StaticIncompatibleExternalMethod.class.getDeclaredMethod("bar", String.class)).with(new Object())).make();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructNonConstructorThrowsException() throws Exception {
        MethodCall.construct(Mockito.mock(MethodDescription.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalIndex() throws Exception {
        MethodCall.invokeSuper().withArgument((-1));
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodCallNonVirtual() throws Exception {
        new ByteBuddy().subclass(MethodCallTest.InstanceMethod.class).method(ElementMatchers.named(MethodCallTest.FOO)).intercept(MethodCall.invoke(MethodCallTest.StaticIncompatibleExternalMethod.class.getDeclaredMethod("bar", String.class)).on(new MethodCallTest.StaticIncompatibleExternalMethod()).with(MethodCallTest.FOO)).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodCallIncompatibleInstance() throws Exception {
        new ByteBuddy().subclass(MethodCallTest.InstanceMethod.class).method(ElementMatchers.named(MethodCallTest.FOO)).intercept(MethodCall.invoke(MethodCallTest.StaticIncompatibleExternalMethod.class.getDeclaredMethod("bar", String.class)).on(new Object()).with(MethodCallTest.FOO)).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodCallNonVisibleType() throws Exception {
        new ByteBuddy().subclass(Object.class).method(ElementMatchers.isDeclaredBy(Object.class)).intercept(MethodCall.invoke(MethodCallTest.PackagePrivateType.class.getDeclaredMethod("foo")).on(new MethodCallTest.PackagePrivateType())).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodCallStaticTargetNonVisibleType() throws Exception {
        new ByteBuddy().subclass(Object.class).method(ElementMatchers.isDeclaredBy(Object.class)).intercept(MethodCall.invoke(MethodCallTest.PackagePrivateType.class.getDeclaredMethod("bar"))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodCallSuperCallNonInvokable() throws Exception {
        new ByteBuddy().subclass(Object.class).method(ElementMatchers.isDeclaredBy(Object.class)).intercept(MethodCall.invoke(MethodCallTest.Bar.class.getDeclaredMethod("bar")).onSuper()).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodCallDefaultCallNonInvokable() throws Exception {
        new ByteBuddy().subclass(Object.class).method(ElementMatchers.isDeclaredBy(Object.class)).intercept(MethodCall.invoke(MethodCallTest.Bar.class.getDeclaredMethod("bar")).onDefault()).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodCallFieldDoesNotExist() throws Exception {
        new ByteBuddy().subclass(MethodCallTest.ExplicitTarget.class).method(ElementMatchers.isDeclaredBy(MethodCallTest.ExplicitTarget.class)).intercept(MethodCall.invoke(Object.class.getDeclaredMethod("toString")).onField(MethodCallTest.FOO)).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodCallIsNotAccessible() throws Exception {
        new ByteBuddy().subclass(Object.class).defineField(MethodCallTest.FOO, Object.class).method(ElementMatchers.isDeclaredBy(Object.class)).intercept(MethodCall.invokeSelf().onField(MethodCallTest.FOO).withAllArguments()).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testInstanceCallFromStaticMethod() throws Exception {
        new ByteBuddy().subclass(Object.class).defineMethod(MethodCallTest.FOO, void.class).intercept(StubMethod.INSTANCE).defineMethod(MethodCallTest.BAR, void.class, Ownership.STATIC).intercept(MethodCall.invoke(ElementMatchers.named(MethodCallTest.FOO))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testCallConstructorFromMethod() throws Exception {
        new ByteBuddy().subclass(Object.class).defineMethod(MethodCallTest.FOO, void.class).intercept(MethodCall.invoke(Object.class.getConstructor())).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testConstructorCallFromNonRelatedConstructor() throws Exception {
        new ByteBuddy().subclass(Number.class).constructor(ElementMatchers.any()).intercept(MethodCall.invoke(Object.class.getConstructor())).make();
    }

    public static class SimpleMethod {
        public String foo() {
            return null;
        }

        public String bar() {
            return MethodCallTest.BAR;
        }
    }

    public static class StaticExternalMethod {
        public static String bar() {
            return MethodCallTest.BAR;
        }
    }

    public static class InstanceMethod {
        public String foo() {
            return null;
        }

        public String bar() {
            return MethodCallTest.BAR;
        }
    }

    public abstract static class ArgumentCall {
        public abstract String foo(MethodCallTest.ArgumentCall.Target target);

        public static class Target {
            private final String value;

            public Target(String value) {
                this.value = value;
            }

            public String foo() {
                return value;
            }
        }
    }

    public abstract static class ArgumentCallDynamic {
        public abstract String foo(Object target);

        public static class Target {
            private final String value;

            public Target(String value) {
                this.value = value;
            }

            public String foo() {
                return value;
            }
        }
    }

    public static class SelfReference {
        public MethodCallTest.SelfReference foo() {
            return null;
        }
    }

    public static class SuperMethodInvocation {
        public String foo() {
            return MethodCallTest.FOO;
        }
    }

    public static class MethodCallWithExplicitArgument {
        public String foo(String value) {
            return value;
        }
    }

    public static class MethodCallWithTwoExplicitArguments {
        public String foo(String first, String second) {
            return first + second;
        }
    }

    public interface MethodCallDelegator {
        String invokeFoo(String... argument);
    }

    public interface IllegalMethodCallDelegator {
        String invokeFoo(String argument);
    }

    @SuppressWarnings("unused")
    public static class MethodCallWithField {
        public String foo;

        public String foo(String value) {
            return value;
        }
    }

    public static class MethodCallOnField {
        public String foo;

        public String foo() {
            return foo;
        }
    }

    @SuppressWarnings("unused")
    public static class InvisibleMethodCallWithField extends MethodCallTest.InvisibleBase {
        private String foo;

        public String foo(String value) {
            return value;
        }
    }

    public static class InvisibleBase {
        public String foo;
    }

    public static class MethodCallWithThis {
        public MethodCallTest.MethodCallWithThis foo(MethodCallTest.MethodCallWithThis value) {
            return value;
        }
    }

    public static class MethodCallWithOwnType {
        public Class<?> foo(Class<?> value) {
            return value;
        }
    }

    public abstract static class MethodCallChaining {
        public String foo(String bar) {
            return "foo" + bar;
        }

        public String bar() {
            return "bar";
        }

        public abstract String foobar();

        public int someInt() {
            return -889275714;
        }
    }

    public static class MethodCallAppending extends CallTraceable {
        public Object foo() {
            register(MethodCallTest.FOO);
            return null;
        }
    }

    public static class ExplicitTarget {
        public String foo() {
            return null;
        }
    }

    @SuppressWarnings("unused")
    public static class StaticIncompatibleExternalMethod {
        public static String bar(String value) {
            return null;
        }
    }

    public static class Foo {
        public static String bar(Object arg0, Object arg1) {
            return ("" + arg0) + arg1;
        }
    }

    static class PackagePrivateType {
        public String foo() {
            return MethodCallTest.FOO;
        }

        public static String bar() {
            return MethodCallTest.BAR;
        }
    }

    public static class Bar {
        public void bar() {
            /* empty */
        }
    }

    public static class SimpleStringMethod {
        public String foo() {
            return null;
        }
    }

    public static class Traceable extends CallTraceable implements Runnable , Callable<String> {
        public String call() throws Exception {
            register(MethodCallTest.FOO);
            return MethodCallTest.FOO;
        }

        public void run() {
            register(MethodCallTest.FOO);
        }
    }

    public static class NonVirtual {
        public static void foo() {
            /* empty */
        }
    }

    public abstract static class ArrayConsuming {
        public abstract String foo(String arg1, String arg2);

        public String bar(String[] arg) {
            return (arg[0]) + (arg[1]);
        }
    }

    public static class FieldSetting {
        public String foo;

        public void foo() {
            throw new AssertionError();
        }

        public String bar() {
            return MethodCallTest.FOO;
        }

        public String qux() {
            throw new AssertionError();
        }
    }

    public static class ProtectedConstructor {
        protected ProtectedConstructor() {
            /* empty */
        }
    }
}

