package net.bytebuddy.implementation;


import java.lang.reflect.Method;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.implementation.bytecode.constant.TextConstant;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.matcher.ElementMatcher;
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
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.MethodVisitor;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;
import static net.bytebuddy.implementation.Implementation.SpecialMethodInvocation.Illegal.INSTANCE;


public class SuperMethodCallOtherTest {
    private static final String SINGLE_DEFAULT_METHOD = "net.bytebuddy.test.precompiled.SingleDefaultMethodInterface";

    private static final String SINGLE_DEFAULT_METHOD_CLASS = "net.bytebuddy.test.precompiled.SingleDefaultMethodClass";

    private static final String CONFLICTING_INTERFACE = "net.bytebuddy.test.precompiled.SingleDefaultMethodConflictingInterface";

    private static final String FOO = "foo";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Mock
    private InstrumentedType instrumentedType;

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private TypeDescription rawSuperClass;

    @Mock
    private TypeDescription returnType;

    @Mock
    private TypeDescription declaringType;

    @Mock
    private TypeDescription.Generic superClass;

    @Mock
    private TypeDescription.Generic genericReturnType;

    @Mock
    private Implementation.Target implementationTarget;

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    @Mock
    private MethodDescription methodDescription;

    @Mock
    private MethodDescription.SignatureToken token;

    @Mock
    private MethodList superClassMethods;

    @Test
    public void testPreparation() throws Exception {
        MatcherAssert.assertThat(SuperMethodCall.INSTANCE.prepare(instrumentedType), CoreMatchers.is(instrumentedType));
        Mockito.verifyZeroInteractions(instrumentedType);
    }

    @Test(expected = IllegalStateException.class)
    @SuppressWarnings("unchecked")
    public void testConstructor() throws Exception {
        Mockito.when(typeDescription.getSuperClass()).thenReturn(superClass);
        Mockito.when(methodDescription.isConstructor()).thenReturn(true);
        Mockito.when(rawSuperClass.getDeclaredMethods()).thenReturn(superClassMethods);
        Mockito.when(superClassMethods.filter(ArgumentMatchers.any(ElementMatcher.class))).thenReturn(superClassMethods);
        Mockito.when(implementationTarget.invokeDominant(token)).thenReturn(INSTANCE);
        SuperMethodCall.INSTANCE.appender(implementationTarget).apply(methodVisitor, implementationContext, methodDescription);
    }

    @Test(expected = IllegalStateException.class)
    @SuppressWarnings("unchecked")
    public void testStaticMethod() throws Exception {
        Mockito.when(typeDescription.getSuperClass()).thenReturn(superClass);
        Mockito.when(methodDescription.isStatic()).thenReturn(true);
        Mockito.when(methodDescription.getParameters()).thenReturn(((ParameterList) (new ParameterList.Empty<net.bytebuddy.description.method.ParameterDescription>())));
        Mockito.when(methodDescription.getReturnType()).thenReturn(genericReturnType);
        Mockito.when(returnType.getStackSize()).thenReturn(StackSize.SINGLE);
        Mockito.when(rawSuperClass.getDeclaredMethods()).thenReturn(superClassMethods);
        Mockito.when(superClassMethods.filter(ArgumentMatchers.any(ElementMatcher.class))).thenReturn(superClassMethods);
        Mockito.when(implementationTarget.invokeDominant(token)).thenReturn(INSTANCE);
        SuperMethodCall.INSTANCE.appender(implementationTarget).apply(methodVisitor, implementationContext, methodDescription);
    }

    @Test(expected = IllegalStateException.class)
    @SuppressWarnings("unchecked")
    public void testNoSuper() throws Exception {
        Mockito.when(typeDescription.getSuperClass()).thenReturn(superClass);
        Mockito.when(methodDescription.getParameters()).thenReturn(((ParameterList) (new ParameterList.Empty<net.bytebuddy.description.method.ParameterDescription>())));
        Mockito.when(methodDescription.getReturnType()).thenReturn(genericReturnType);
        Mockito.when(methodDescription.getDeclaringType()).thenReturn(declaringType);
        Mockito.when(declaringType.getStackSize()).thenReturn(StackSize.SINGLE);
        Mockito.when(returnType.getStackSize()).thenReturn(StackSize.SINGLE);
        Mockito.when(rawSuperClass.getDeclaredMethods()).thenReturn(superClassMethods);
        Mockito.when(superClassMethods.filter(ArgumentMatchers.any(ElementMatcher.class))).thenReturn(superClassMethods);
        Mockito.when(implementationTarget.invokeDominant(token)).thenReturn(INSTANCE);
        SuperMethodCall.INSTANCE.appender(implementationTarget).apply(methodVisitor, implementationContext, methodDescription);
    }

    @Test
    public void testAndThen() throws Exception {
        DynamicType.Loaded<SuperMethodCallOtherTest.Foo> loaded = new ByteBuddy().subclass(SuperMethodCallOtherTest.Foo.class).method(ElementMatchers.isDeclaredBy(SuperMethodCallOtherTest.Foo.class)).intercept(SuperMethodCall.INSTANCE.andThen(new Implementation.Simple(new TextConstant(SuperMethodCallOtherTest.FOO), MethodReturn.REFERENCE))).make().load(SuperMethodCallOtherTest.Foo.class.getClassLoader(), WRAPPER);
        SuperMethodCallOtherTest.Foo foo = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(foo.foo(), CoreMatchers.is(SuperMethodCallOtherTest.FOO));
        foo.assertOnlyCall(SuperMethodCallOtherTest.FOO);
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testUnambiguousDirectDefaultMethod() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Object.class).implement(Class.forName(SuperMethodCallOtherTest.SINGLE_DEFAULT_METHOD)).intercept(SuperMethodCall.INSTANCE).make().load(Class.forName(SuperMethodCallOtherTest.SINGLE_DEFAULT_METHOD).getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        Method method = loaded.getLoaded().getDeclaredMethod(SuperMethodCallOtherTest.FOO);
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(method.invoke(instance), CoreMatchers.is(((Object) (SuperMethodCallOtherTest.FOO))));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testInheritedDefaultMethod() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Class.forName(SuperMethodCallOtherTest.SINGLE_DEFAULT_METHOD_CLASS)).method(ElementMatchers.not(ElementMatchers.isDeclaredBy(Object.class))).intercept(SuperMethodCall.INSTANCE).make().load(Class.forName(SuperMethodCallOtherTest.SINGLE_DEFAULT_METHOD_CLASS).getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        Method method = loaded.getLoaded().getDeclaredMethod(SuperMethodCallOtherTest.FOO);
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(method.invoke(instance), CoreMatchers.is(((Object) (SuperMethodCallOtherTest.FOO))));
    }

    @Test(expected = IllegalStateException.class)
    @JavaVersionRule.Enforce(8)
    public void testAmbiguousDirectDefaultMethodThrowsException() throws Exception {
        new ByteBuddy().subclass(Object.class).implement(Class.forName(SuperMethodCallOtherTest.SINGLE_DEFAULT_METHOD), Class.forName(SuperMethodCallOtherTest.CONFLICTING_INTERFACE)).intercept(SuperMethodCall.INSTANCE).make().load(Class.forName(SuperMethodCallOtherTest.SINGLE_DEFAULT_METHOD).getClassLoader(), WRAPPER);
    }

    public static class Foo extends CallTraceable {
        public String foo() {
            register(SuperMethodCallOtherTest.FOO);
            return null;
        }
    }
}

