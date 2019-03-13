package net.bytebuddy.dynamic.scaffold.inline;


import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import junit.framework.TestCase;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.modifier.MethodManifestation;
import net.bytebuddy.description.modifier.TypeManifestation;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.AbstractDynamicTypeBuilderTest;
import net.bytebuddy.dynamic.loading.ByteArrayClassLoader;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.StubMethod;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.constant.TextConstant;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.test.scope.GenericType;
import net.bytebuddy.test.utility.JavaVersionRule;
import net.bytebuddy.utility.OpenedClassReader;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;

import static net.bytebuddy.description.annotation.AnnotationDescription.Builder.ofType;
import static net.bytebuddy.description.type.TypeDescription.Generic.AnnotationReader.DISPATCHER;
import static net.bytebuddy.description.type.TypeDescription.Generic.Builder.rawType;
import static net.bytebuddy.dynamic.Transformer.ForField.withModifiers;
import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.CHILD_FIRST;
import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;
import static net.bytebuddy.dynamic.loading.InjectionClassLoader.Strategy.INSTANCE;


public abstract class AbstractDynamicTypeBuilderForInliningTest extends AbstractDynamicTypeBuilderTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final int QUX = 42;

    private static final String PARAMETER_NAME_CLASS = "net.bytebuddy.test.precompiled.ParameterNames";

    private static final String SIMPLE_TYPE_ANNOTATED = "net.bytebuddy.test.precompiled.SimpleTypeAnnotatedType";

    private static final String TYPE_VARIABLE_NAME = "net.bytebuddy.test.precompiled.TypeAnnotation";

    private static final String VALUE = "value";

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    private TypePool typePool;

    @Test
    public void testTypeInitializerRetention() throws Exception {
        Class<?> type = create(AbstractDynamicTypeBuilderForInliningTest.Qux.class).invokable(ElementMatchers.isTypeInitializer()).intercept(MethodCall.invoke(AbstractDynamicTypeBuilderForInliningTest.Qux.class.getDeclaredMethod("invoke"))).make().load(new URLClassLoader(new URL[0], null), WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredConstructor().newInstance(), CoreMatchers.notNullValue(Object.class));
        MatcherAssert.assertThat(type.getDeclaredField(AbstractDynamicTypeBuilderForInliningTest.FOO).get(null), CoreMatchers.is(((Object) (AbstractDynamicTypeBuilderForInliningTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredField(AbstractDynamicTypeBuilderForInliningTest.BAR).get(null), CoreMatchers.is(((Object) (AbstractDynamicTypeBuilderForInliningTest.BAR))));
    }

    @Test
    public void testDefaultValue() throws Exception {
        Class<?> dynamicType = create(AbstractDynamicTypeBuilderForInliningTest.Baz.class).method(ElementMatchers.named(AbstractDynamicTypeBuilderForInliningTest.FOO)).defaultValue(AbstractDynamicTypeBuilderForInliningTest.FOO, String.class).make().load(new URLClassLoader(new URL[0], null), WRAPPER).getLoaded();
        MatcherAssert.assertThat(dynamicType.getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(dynamicType.getDeclaredMethod(AbstractDynamicTypeBuilderForInliningTest.FOO).getDefaultValue(), CoreMatchers.is(((Object) (AbstractDynamicTypeBuilderForInliningTest.FOO))));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testParameterMetaDataRetention() throws Exception {
        Class<?> dynamicType = create(Class.forName(AbstractDynamicTypeBuilderForInliningTest.PARAMETER_NAME_CLASS)).method(ElementMatchers.named(AbstractDynamicTypeBuilderForInliningTest.FOO)).intercept(StubMethod.INSTANCE).make().load(new URLClassLoader(new URL[0], null), WRAPPER).getLoaded();
        Class<?> executable = Class.forName("java.lang.reflect.Executable");
        Method getParameters = executable.getDeclaredMethod("getParameters");
        Class<?> parameter = Class.forName("java.lang.reflect.Parameter");
        Method getName = parameter.getDeclaredMethod("getName");
        Method getModifiers = parameter.getDeclaredMethod("getModifiers");
        Method first = dynamicType.getDeclaredMethod("foo", String.class, long.class, int.class);
        Object[] methodParameter = ((Object[]) (getParameters.invoke(first)));
        MatcherAssert.assertThat(getName.invoke(methodParameter[0]), CoreMatchers.is(((Object) ("first"))));
        MatcherAssert.assertThat(getName.invoke(methodParameter[1]), CoreMatchers.is(((Object) ("second"))));
        MatcherAssert.assertThat(getName.invoke(methodParameter[2]), CoreMatchers.is(((Object) ("third"))));
        MatcherAssert.assertThat(getModifiers.invoke(methodParameter[0]), CoreMatchers.is(((Object) (Opcodes.ACC_FINAL))));
        MatcherAssert.assertThat(getModifiers.invoke(methodParameter[1]), CoreMatchers.is(((Object) (0))));
        MatcherAssert.assertThat(getModifiers.invoke(methodParameter[2]), CoreMatchers.is(((Object) (0))));
    }

    @Test
    public void testGenericType() throws Exception {
        InjectionClassLoader classLoader = new ByteArrayClassLoader(ClassLoadingStrategy.BOOTSTRAP_LOADER, false, readToNames(GenericType.class));
        Class<?> dynamicType = create(GenericType.Inner.class).method(ElementMatchers.named(AbstractDynamicTypeBuilderForInliningTest.FOO)).intercept(StubMethod.INSTANCE).make().load(classLoader, INSTANCE).getLoaded();
        MatcherAssert.assertThat(dynamicType.getTypeParameters().length, CoreMatchers.is(2));
        MatcherAssert.assertThat(dynamicType.getTypeParameters()[0].getName(), CoreMatchers.is("T"));
        MatcherAssert.assertThat(dynamicType.getTypeParameters()[0].getBounds().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(dynamicType.getTypeParameters()[0].getBounds()[0], CoreMatchers.instanceOf(Class.class));
        MatcherAssert.assertThat(dynamicType.getTypeParameters()[0].getBounds()[0], CoreMatchers.is(((Type) (String.class))));
        MatcherAssert.assertThat(dynamicType.getTypeParameters()[1].getName(), CoreMatchers.is("S"));
        MatcherAssert.assertThat(dynamicType.getTypeParameters()[1].getBounds().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(dynamicType.getTypeParameters()[1].getBounds()[0], CoreMatchers.is(((Type) (dynamicType.getTypeParameters()[0]))));
        MatcherAssert.assertThat(dynamicType.getGenericSuperclass(), CoreMatchers.instanceOf(ParameterizedType.class));
        MatcherAssert.assertThat(((ParameterizedType) (dynamicType.getGenericSuperclass())).getActualTypeArguments().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(((ParameterizedType) (dynamicType.getGenericSuperclass())).getActualTypeArguments()[0], CoreMatchers.instanceOf(ParameterizedType.class));
        ParameterizedType superClass = ((ParameterizedType) (((ParameterizedType) (dynamicType.getGenericSuperclass())).getActualTypeArguments()[0]));
        MatcherAssert.assertThat(superClass.getActualTypeArguments().length, CoreMatchers.is(2));
        MatcherAssert.assertThat(superClass.getActualTypeArguments()[0], CoreMatchers.is(((Type) (dynamicType.getTypeParameters()[0]))));
        MatcherAssert.assertThat(superClass.getActualTypeArguments()[1], CoreMatchers.is(((Type) (dynamicType.getTypeParameters()[1]))));
        MatcherAssert.assertThat(superClass.getOwnerType(), CoreMatchers.instanceOf(ParameterizedType.class));
        MatcherAssert.assertThat(((ParameterizedType) (superClass.getOwnerType())).getRawType(), CoreMatchers.instanceOf(Class.class));
        MatcherAssert.assertThat(((Class<?>) (((ParameterizedType) (superClass.getOwnerType())).getRawType())).getName(), CoreMatchers.is(GenericType.class.getName()));
        MatcherAssert.assertThat(((ParameterizedType) (superClass.getOwnerType())).getActualTypeArguments().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(((ParameterizedType) (superClass.getOwnerType())).getActualTypeArguments()[0], CoreMatchers.is(((Type) (((Class<?>) (((ParameterizedType) (superClass.getOwnerType())).getRawType())).getTypeParameters()[0]))));
        MatcherAssert.assertThat(dynamicType.getGenericInterfaces().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(dynamicType.getGenericInterfaces()[0], CoreMatchers.instanceOf(ParameterizedType.class));
        MatcherAssert.assertThat(((ParameterizedType) (dynamicType.getGenericInterfaces()[0])).getActualTypeArguments()[0], CoreMatchers.instanceOf(ParameterizedType.class));
        MatcherAssert.assertThat(((ParameterizedType) (dynamicType.getGenericInterfaces()[0])).getRawType(), CoreMatchers.is(((Type) (Callable.class))));
        MatcherAssert.assertThat(((ParameterizedType) (dynamicType.getGenericInterfaces()[0])).getOwnerType(), CoreMatchers.nullValue(Type.class));
        MatcherAssert.assertThat(((ParameterizedType) (((ParameterizedType) (dynamicType.getGenericInterfaces()[0])).getActualTypeArguments()[0])).getActualTypeArguments().length, CoreMatchers.is(2));
        ParameterizedType interfaceType = ((ParameterizedType) (((ParameterizedType) (dynamicType.getGenericInterfaces()[0])).getActualTypeArguments()[0]));
        MatcherAssert.assertThat(interfaceType.getRawType(), CoreMatchers.is(((Type) (Map.class))));
        MatcherAssert.assertThat(interfaceType.getActualTypeArguments().length, CoreMatchers.is(2));
        MatcherAssert.assertThat(interfaceType.getActualTypeArguments()[0], CoreMatchers.instanceOf(WildcardType.class));
        MatcherAssert.assertThat(((WildcardType) (interfaceType.getActualTypeArguments()[0])).getUpperBounds().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(((WildcardType) (interfaceType.getActualTypeArguments()[0])).getUpperBounds()[0], CoreMatchers.is(((Type) (Object.class))));
        MatcherAssert.assertThat(((WildcardType) (interfaceType.getActualTypeArguments()[0])).getLowerBounds().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(((WildcardType) (interfaceType.getActualTypeArguments()[0])).getLowerBounds()[0], CoreMatchers.is(((Type) (String.class))));
        MatcherAssert.assertThat(interfaceType.getActualTypeArguments()[1], CoreMatchers.instanceOf(WildcardType.class));
        MatcherAssert.assertThat(((WildcardType) (interfaceType.getActualTypeArguments()[1])).getUpperBounds().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(((WildcardType) (interfaceType.getActualTypeArguments()[1])).getUpperBounds()[0], CoreMatchers.is(((Type) (String.class))));
        MatcherAssert.assertThat(((WildcardType) (interfaceType.getActualTypeArguments()[1])).getLowerBounds().length, CoreMatchers.is(0));
        Method foo = dynamicType.getDeclaredMethod(AbstractDynamicTypeBuilderForInliningTest.FOO, String.class);
        MatcherAssert.assertThat(foo.getGenericReturnType(), CoreMatchers.instanceOf(ParameterizedType.class));
        MatcherAssert.assertThat(((ParameterizedType) (foo.getGenericReturnType())).getActualTypeArguments().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(((ParameterizedType) (foo.getGenericReturnType())).getActualTypeArguments()[0], CoreMatchers.instanceOf(GenericArrayType.class));
        MatcherAssert.assertThat(((GenericArrayType) (((ParameterizedType) (foo.getGenericReturnType())).getActualTypeArguments()[0])).getGenericComponentType(), CoreMatchers.is(((Type) (dynamicType.getTypeParameters()[0]))));
        MatcherAssert.assertThat(foo.getTypeParameters().length, CoreMatchers.is(2));
        MatcherAssert.assertThat(foo.getTypeParameters()[0].getName(), CoreMatchers.is("V"));
        MatcherAssert.assertThat(foo.getTypeParameters()[0].getBounds().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(foo.getTypeParameters()[0].getBounds()[0], CoreMatchers.is(((Type) (dynamicType.getTypeParameters()[0]))));
        MatcherAssert.assertThat(foo.getTypeParameters()[1].getName(), CoreMatchers.is("W"));
        MatcherAssert.assertThat(foo.getTypeParameters()[1].getBounds().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(foo.getTypeParameters()[1].getBounds()[0], CoreMatchers.is(((Type) (Exception.class))));
        MatcherAssert.assertThat(foo.getGenericParameterTypes().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(foo.getGenericParameterTypes()[0], CoreMatchers.is(((Type) (foo.getTypeParameters()[0]))));
        MatcherAssert.assertThat(foo.getGenericExceptionTypes().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(foo.getGenericExceptionTypes()[0], CoreMatchers.is(((Type) (foo.getTypeParameters()[1]))));
        Method call = dynamicType.getDeclaredMethod("call");
        MatcherAssert.assertThat(call.getGenericReturnType(), CoreMatchers.is(((Type) (interfaceType))));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBridgeMethodCreation() throws Exception {
        Class<?> dynamicType = create(AbstractDynamicTypeBuilderTest.BridgeRetention.Inner.class).method(ElementMatchers.named(AbstractDynamicTypeBuilderForInliningTest.FOO)).intercept(new Implementation.Simple(new TextConstant(AbstractDynamicTypeBuilderForInliningTest.FOO), MethodReturn.REFERENCE)).make().load(getClass().getClassLoader(), CHILD_FIRST).getLoaded();
        TestCase.assertEquals(String.class, dynamicType.getDeclaredMethod(AbstractDynamicTypeBuilderForInliningTest.FOO).getReturnType());
        MatcherAssert.assertThat(dynamicType.getDeclaredMethod(AbstractDynamicTypeBuilderForInliningTest.FOO).getGenericReturnType(), CoreMatchers.is(((Type) (String.class))));
        AbstractDynamicTypeBuilderTest.BridgeRetention<String> bridgeRetention = ((AbstractDynamicTypeBuilderTest.BridgeRetention<String>) (dynamicType.getDeclaredConstructor().newInstance()));
        MatcherAssert.assertThat(bridgeRetention.foo(), CoreMatchers.is(AbstractDynamicTypeBuilderForInliningTest.FOO));
        bridgeRetention.assertZeroCalls();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBridgeMethodCreationForExistingBridgeMethod() throws Exception {
        Class<?> dynamicType = create(AbstractDynamicTypeBuilderTest.CallSuperMethod.Inner.class).method(ElementMatchers.named(AbstractDynamicTypeBuilderForInliningTest.FOO)).intercept(SuperMethodCall.INSTANCE).make().load(getClass().getClassLoader(), CHILD_FIRST).getLoaded();
        MatcherAssert.assertThat(dynamicType.getDeclaredMethods().length, CoreMatchers.is(2));
        TestCase.assertEquals(String.class, dynamicType.getDeclaredMethod(AbstractDynamicTypeBuilderForInliningTest.FOO, String.class).getReturnType());
        MatcherAssert.assertThat(dynamicType.getDeclaredMethod(AbstractDynamicTypeBuilderForInliningTest.FOO, String.class).getGenericReturnType(), CoreMatchers.is(((Type) (String.class))));
        MatcherAssert.assertThat(dynamicType.getDeclaredMethod(AbstractDynamicTypeBuilderForInliningTest.FOO, String.class).isBridge(), CoreMatchers.is(false));
        TestCase.assertEquals(Object.class, dynamicType.getDeclaredMethod(AbstractDynamicTypeBuilderForInliningTest.FOO, Object.class).getReturnType());
        MatcherAssert.assertThat(dynamicType.getDeclaredMethod(AbstractDynamicTypeBuilderForInliningTest.FOO, Object.class).getGenericReturnType(), CoreMatchers.is(((Type) (Object.class))));
        MatcherAssert.assertThat(dynamicType.getDeclaredMethod(AbstractDynamicTypeBuilderForInliningTest.FOO, Object.class).isBridge(), CoreMatchers.is(true));
        AbstractDynamicTypeBuilderTest.CallSuperMethod<String> callSuperMethod = ((AbstractDynamicTypeBuilderTest.CallSuperMethod<String>) (dynamicType.getDeclaredConstructor().newInstance()));
        MatcherAssert.assertThat(callSuperMethod.foo(AbstractDynamicTypeBuilderForInliningTest.FOO), CoreMatchers.is(AbstractDynamicTypeBuilderForInliningTest.FOO));
        callSuperMethod.assertOnlyCall(AbstractDynamicTypeBuilderForInliningTest.FOO);
    }

    @Test
    public void testBridgeMethodForAbstractMethod() throws Exception {
        Class<?> dynamicType = create(AbstractDynamicTypeBuilderForInliningTest.AbstractGenericType.Inner.class).modifiers(((Opcodes.ACC_ABSTRACT) | (Opcodes.ACC_PUBLIC))).method(ElementMatchers.named(AbstractDynamicTypeBuilderForInliningTest.FOO)).withoutCode().make().load(getClass().getClassLoader(), CHILD_FIRST).getLoaded();
        MatcherAssert.assertThat(dynamicType.getDeclaredMethods().length, CoreMatchers.is(2));
        TestCase.assertEquals(Void.class, dynamicType.getDeclaredMethod(AbstractDynamicTypeBuilderForInliningTest.FOO, Void.class).getReturnType());
        MatcherAssert.assertThat(dynamicType.getDeclaredMethod(AbstractDynamicTypeBuilderForInliningTest.FOO, Void.class).getGenericReturnType(), CoreMatchers.is(((Type) (Void.class))));
        MatcherAssert.assertThat(dynamicType.getDeclaredMethod(AbstractDynamicTypeBuilderForInliningTest.FOO, Void.class).isBridge(), CoreMatchers.is(false));
        MatcherAssert.assertThat(Modifier.isAbstract(dynamicType.getDeclaredMethod(AbstractDynamicTypeBuilderForInliningTest.FOO, Void.class).getModifiers()), CoreMatchers.is(true));
        TestCase.assertEquals(Object.class, dynamicType.getDeclaredMethod(AbstractDynamicTypeBuilderForInliningTest.FOO, Object.class).getReturnType());
        MatcherAssert.assertThat(dynamicType.getDeclaredMethod(AbstractDynamicTypeBuilderForInliningTest.FOO, Object.class).getGenericReturnType(), CoreMatchers.is(((Type) (Object.class))));
        MatcherAssert.assertThat(dynamicType.getDeclaredMethod(AbstractDynamicTypeBuilderForInliningTest.FOO, Object.class).isBridge(), CoreMatchers.is(true));
        MatcherAssert.assertThat(Modifier.isAbstract(dynamicType.getDeclaredMethod(AbstractDynamicTypeBuilderForInliningTest.FOO, Object.class).getModifiers()), CoreMatchers.is(false));
    }

    @Test
    public void testVisibilityBridge() throws Exception {
        InjectionClassLoader classLoader = new ByteArrayClassLoader(ClassLoadingStrategy.BOOTSTRAP_LOADER, false, net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.readToNames(AbstractDynamicTypeBuilderForInliningTest.VisibilityBridge.class, AbstractDynamicTypeBuilderForInliningTest.FooBar.class));
        Class<?> type = create(AbstractDynamicTypeBuilderForInliningTest.PackagePrivateVisibilityBridgeExtension.class).modifiers(Opcodes.ACC_PUBLIC).make().load(classLoader, INSTANCE).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredConstructors().length, CoreMatchers.is(1));
        Constructor<?> constructor = type.getDeclaredConstructor();
        constructor.setAccessible(true);
        MatcherAssert.assertThat(type.getDeclaredMethods().length, CoreMatchers.is(2));
        Method foo = type.getDeclaredMethod(AbstractDynamicTypeBuilderForInliningTest.FOO, String.class);
        foo.setAccessible(true);
        MatcherAssert.assertThat(foo.isBridge(), CoreMatchers.is(true));
        MatcherAssert.assertThat(foo.getDeclaredAnnotations().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(foo.getDeclaredAnnotations()[0].annotationType().getName(), CoreMatchers.is(AbstractDynamicTypeBuilderForInliningTest.FooBar.class.getName()));
        MatcherAssert.assertThat(foo.invoke(constructor.newInstance(), AbstractDynamicTypeBuilderForInliningTest.BAR), CoreMatchers.is(((Object) ((AbstractDynamicTypeBuilderForInliningTest.FOO) + (AbstractDynamicTypeBuilderForInliningTest.BAR)))));
        MatcherAssert.assertThat(foo.getParameterAnnotations()[0].length, CoreMatchers.is(1));
        MatcherAssert.assertThat(foo.getParameterAnnotations()[0][0].annotationType().getName(), CoreMatchers.is(AbstractDynamicTypeBuilderForInliningTest.FooBar.class.getName()));
        MatcherAssert.assertThat(foo.invoke(constructor.newInstance(), AbstractDynamicTypeBuilderForInliningTest.BAR), CoreMatchers.is(((Object) ((AbstractDynamicTypeBuilderForInliningTest.FOO) + (AbstractDynamicTypeBuilderForInliningTest.BAR)))));
        Method bar = type.getDeclaredMethod(AbstractDynamicTypeBuilderForInliningTest.BAR, List.class);
        bar.setAccessible(true);
        MatcherAssert.assertThat(bar.isBridge(), CoreMatchers.is(true));
        MatcherAssert.assertThat(bar.getDeclaredAnnotations().length, CoreMatchers.is(0));
        List<?> list = new ArrayList<Object>();
        MatcherAssert.assertThat(bar.invoke(constructor.newInstance(), list), CoreMatchers.sameInstance(((Object) (list))));
        MatcherAssert.assertThat(bar.getGenericReturnType(), CoreMatchers.instanceOf(Class.class));
        MatcherAssert.assertThat(bar.getGenericParameterTypes()[0], CoreMatchers.instanceOf(Class.class));
        MatcherAssert.assertThat(bar.getGenericExceptionTypes()[0], CoreMatchers.instanceOf(Class.class));
    }

    @Test
    public void testNoVisibilityBridgeForNonPublicType() throws Exception {
        InjectionClassLoader classLoader = new ByteArrayClassLoader(ClassLoadingStrategy.BOOTSTRAP_LOADER, false, net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.readToNames(AbstractDynamicTypeBuilderForInliningTest.PackagePrivateVisibilityBridgeExtension.class, AbstractDynamicTypeBuilderForInliningTest.VisibilityBridge.class, AbstractDynamicTypeBuilderForInliningTest.FooBar.class));
        Class<?> type = create(AbstractDynamicTypeBuilderForInliningTest.PackagePrivateVisibilityBridgeExtension.class).modifiers(0).make().load(classLoader, INSTANCE).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getDeclaredMethods().length, CoreMatchers.is(0));
    }

    @Test
    public void testNoVisibilityBridgeForInheritedType() throws Exception {
        InjectionClassLoader classLoader = new ByteArrayClassLoader(ClassLoadingStrategy.BOOTSTRAP_LOADER, false, net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.readToNames(AbstractDynamicTypeBuilderForInliningTest.PublicVisibilityBridgeExtension.class, AbstractDynamicTypeBuilderForInliningTest.VisibilityBridge.class, AbstractDynamicTypeBuilderForInliningTest.FooBar.class));
        Class<?> type = new ByteBuddy().subclass(AbstractDynamicTypeBuilderForInliningTest.PublicVisibilityBridgeExtension.class).modifiers(Opcodes.ACC_PUBLIC).make().load(classLoader, INSTANCE).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getDeclaredMethods().length, CoreMatchers.is(0));
    }

    @Test
    public void testNoVisibilityBridgeForAbstractMethod() throws Exception {
        InjectionClassLoader classLoader = new ByteArrayClassLoader(ClassLoadingStrategy.BOOTSTRAP_LOADER, false, net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.readToNames(AbstractDynamicTypeBuilderForInliningTest.PackagePrivateVisibilityBridgeExtensionAbstractMethod.class, AbstractDynamicTypeBuilderForInliningTest.VisibilityBridgeAbstractMethod.class));
        Class<?> type = create(AbstractDynamicTypeBuilderForInliningTest.PackagePrivateVisibilityBridgeExtensionAbstractMethod.class).modifiers(((Opcodes.ACC_PUBLIC) | (Opcodes.ACC_ABSTRACT))).make().load(classLoader, INSTANCE).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getDeclaredMethods().length, CoreMatchers.is(0));
    }

    @Test
    public void testMethodTransformationExistingMethod() throws Exception {
        Class<?> type = create(AbstractDynamicTypeBuilderForInliningTest.Transform.class).method(ElementMatchers.named(AbstractDynamicTypeBuilderForInliningTest.FOO)).intercept(new Implementation.Simple(new TextConstant(AbstractDynamicTypeBuilderForInliningTest.FOO), MethodReturn.REFERENCE)).transform(net.bytebuddy.dynamic.Transformer.ForMethod.withModifiers(MethodManifestation.FINAL)).make().load(new URLClassLoader(new URL[0], null), WRAPPER).getLoaded();
        Method foo = type.getDeclaredMethod(AbstractDynamicTypeBuilderForInliningTest.FOO);
        MatcherAssert.assertThat(foo.invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AbstractDynamicTypeBuilderForInliningTest.FOO))));
        MatcherAssert.assertThat(foo.getModifiers(), CoreMatchers.is(((Opcodes.ACC_FINAL) | (Opcodes.ACC_PUBLIC))));
    }

    @Test
    public void testFieldTransformationExistingField() throws Exception {
        Class<?> type = create(AbstractDynamicTypeBuilderForInliningTest.Transform.class).field(ElementMatchers.named(AbstractDynamicTypeBuilderForInliningTest.FOO)).transform(withModifiers(Visibility.PUBLIC)).make().load(new URLClassLoader(new URL[0], null), WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredField(AbstractDynamicTypeBuilderForInliningTest.FOO).getModifiers(), CoreMatchers.is(Opcodes.ACC_PUBLIC));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testReaderHint() throws Exception {
        AsmVisitorWrapper asmVisitorWrapper = Mockito.mock(AsmVisitorWrapper.class);
        Mockito.when(asmVisitorWrapper.wrap(ArgumentMatchers.any(TypeDescription.class), ArgumentMatchers.any(ClassVisitor.class), ArgumentMatchers.any(Implementation.Context.class), ArgumentMatchers.any(TypePool.class), ArgumentMatchers.any(FieldList.class), ArgumentMatchers.any(MethodList.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).then(new Answer<ClassVisitor>() {
            public ClassVisitor answer(InvocationOnMock invocationOnMock) throws Throwable {
                return new ClassVisitor(OpenedClassReader.ASM_API, ((ClassVisitor) (invocationOnMock.getArguments()[1]))) {
                    @Override
                    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                        return new LocalVariablesSorter(access, desc, super.visitMethod(access, name, desc, signature, exceptions));
                    }
                };
            }
        });
        Mockito.when(asmVisitorWrapper.mergeWriter(0)).thenReturn(ClassWriter.COMPUTE_MAXS);
        Mockito.when(asmVisitorWrapper.mergeReader(0)).thenReturn(ClassReader.EXPAND_FRAMES);
        Class<?> type = create(AbstractDynamicTypeBuilderForInliningTest.StackMapFrames.class).visit(asmVisitorWrapper).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AbstractDynamicTypeBuilderForInliningTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AbstractDynamicTypeBuilderForInliningTest.BAR))));
        Mockito.verify(asmVisitorWrapper).mergeWriter(0);
        Mockito.verify(asmVisitorWrapper).mergeReader(0);
        Mockito.verify(asmVisitorWrapper).wrap(ArgumentMatchers.any(TypeDescription.class), ArgumentMatchers.any(ClassVisitor.class), ArgumentMatchers.any(Implementation.Context.class), ArgumentMatchers.any(TypePool.class), ArgumentMatchers.any(FieldList.class), ArgumentMatchers.any(MethodList.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Mockito.verifyNoMoreInteractions(asmVisitorWrapper);
    }

    @Test(expected = IllegalStateException.class)
    public void testForbidTypeInitilizerInterception() throws Exception {
        createDisabledContext().initializer(new ByteCodeAppender.Simple()).make();
    }

    @Test
    public void testDisabledAnnotationRetention() throws Exception {
        Class<?> type = createDisabledRetention(AbstractDynamicTypeBuilderForInliningTest.Annotated.class).field(ElementMatchers.any()).annotateField(new Annotation[0]).method(ElementMatchers.any()).intercept(StubMethod.INSTANCE).make().load(new ByteArrayClassLoader(ClassLoadingStrategy.BOOTSTRAP_LOADER, readToNames(AbstractDynamicTypeBuilderForInliningTest.SampleAnnotation.class)), WRAPPER).getLoaded();
        @SuppressWarnings("unchecked")
        Class<? extends Annotation> sampleAnnotation = ((Class<? extends Annotation>) (type.getClassLoader().loadClass(AbstractDynamicTypeBuilderForInliningTest.SampleAnnotation.class.getName())));
        MatcherAssert.assertThat(type.isAnnotationPresent(sampleAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(type.getDeclaredField(AbstractDynamicTypeBuilderForInliningTest.FOO).isAnnotationPresent(sampleAnnotation), CoreMatchers.is(false));
        MatcherAssert.assertThat(type.getDeclaredMethod(AbstractDynamicTypeBuilderForInliningTest.FOO, Void.class).isAnnotationPresent(sampleAnnotation), CoreMatchers.is(false));
        MatcherAssert.assertThat(type.getDeclaredMethod(AbstractDynamicTypeBuilderForInliningTest.FOO, Void.class).getParameterAnnotations()[0].length, CoreMatchers.is(0));
    }

    @Test
    public void testEnabledAnnotationRetention() throws Exception {
        Class<?> type = create(AbstractDynamicTypeBuilderForInliningTest.Annotated.class).field(ElementMatchers.any()).annotateField(new Annotation[0]).method(ElementMatchers.any()).intercept(StubMethod.INSTANCE).make().load(new ByteArrayClassLoader(ClassLoadingStrategy.BOOTSTRAP_LOADER, readToNames(AbstractDynamicTypeBuilderForInliningTest.SampleAnnotation.class)), WRAPPER).getLoaded();
        @SuppressWarnings("unchecked")
        Class<? extends Annotation> sampleAnnotation = ((Class<? extends Annotation>) (type.getClassLoader().loadClass(AbstractDynamicTypeBuilderForInliningTest.SampleAnnotation.class.getName())));
        MatcherAssert.assertThat(type.isAnnotationPresent(sampleAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(type.getDeclaredField(AbstractDynamicTypeBuilderForInliningTest.FOO).isAnnotationPresent(sampleAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(type.getDeclaredMethod(AbstractDynamicTypeBuilderForInliningTest.FOO, Void.class).isAnnotationPresent(sampleAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(type.getDeclaredMethod(AbstractDynamicTypeBuilderForInliningTest.FOO, Void.class).getParameterAnnotations()[0].length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getDeclaredMethod(AbstractDynamicTypeBuilderForInliningTest.FOO, Void.class).getParameterAnnotations()[0][0].annotationType(), CoreMatchers.is(((Object) (sampleAnnotation))));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testAnnotationTypeOnInterfaceType() throws Exception {
        Class<? extends Annotation> typeAnnotationType = ((Class<? extends Annotation>) (Class.forName(AbstractDynamicTypeBuilderForInliningTest.TYPE_VARIABLE_NAME)));
        MethodDescription.InDefinedShape value = of(typeAnnotationType).getDeclaredMethods().filter(ElementMatchers.named(AbstractDynamicTypeBuilderForInliningTest.VALUE)).getOnly();
        Class<?> type = create(Class.forName(AbstractDynamicTypeBuilderForInliningTest.SIMPLE_TYPE_ANNOTATED)).merge(TypeManifestation.ABSTRACT).implement(rawType(Callable.class).build(ofType(typeAnnotationType).define(AbstractDynamicTypeBuilderForInliningTest.VALUE, ((AbstractDynamicTypeBuilderForInliningTest.QUX) * 3)).build())).make().load(typeAnnotationType.getClassLoader(), CHILD_FIRST).getLoaded();
        MatcherAssert.assertThat(type.getInterfaces().length, CoreMatchers.is(2));
        MatcherAssert.assertThat(DISPATCHER.resolveInterfaceType(type, 0).asList().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(DISPATCHER.resolveInterfaceType(type, 0).asList().ofType(typeAnnotationType).getValue(value).resolve(Integer.class), CoreMatchers.is(((AbstractDynamicTypeBuilderForInliningTest.QUX) * 2)));
        MatcherAssert.assertThat(DISPATCHER.resolveInterfaceType(type, 1).asList().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(DISPATCHER.resolveInterfaceType(type, 1).asList().ofType(typeAnnotationType).getValue(value).resolve(Integer.class), CoreMatchers.is(((AbstractDynamicTypeBuilderForInliningTest.QUX) * 3)));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testAnnotationTypeOnTypeVariableType() throws Exception {
        Class<? extends Annotation> typeAnnotationType = ((Class<? extends Annotation>) (Class.forName(AbstractDynamicTypeBuilderForInliningTest.TYPE_VARIABLE_NAME)));
        MethodDescription.InDefinedShape value = of(typeAnnotationType).getDeclaredMethods().filter(ElementMatchers.named(AbstractDynamicTypeBuilderForInliningTest.VALUE)).getOnly();
        Class<?> type = create(Class.forName(AbstractDynamicTypeBuilderForInliningTest.SIMPLE_TYPE_ANNOTATED)).merge(TypeManifestation.ABSTRACT).typeVariable(AbstractDynamicTypeBuilderForInliningTest.BAR, rawType(Callable.class).build(ofType(typeAnnotationType).define(AbstractDynamicTypeBuilderForInliningTest.VALUE, ((AbstractDynamicTypeBuilderForInliningTest.QUX) * 4)).build())).annotateTypeVariable(ofType(typeAnnotationType).define(AbstractDynamicTypeBuilderForInliningTest.VALUE, ((AbstractDynamicTypeBuilderForInliningTest.QUX) * 3)).build()).make().load(typeAnnotationType.getClassLoader(), CHILD_FIRST).getLoaded();
        MatcherAssert.assertThat(type.getTypeParameters().length, CoreMatchers.is(2));
        MatcherAssert.assertThat(DISPATCHER.resolveTypeVariable(type.getTypeParameters()[0]).asList().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(DISPATCHER.resolveTypeVariable(type.getTypeParameters()[0]).asList().ofType(typeAnnotationType).getValue(value).resolve(Integer.class), CoreMatchers.is(AbstractDynamicTypeBuilderForInliningTest.QUX));
        MatcherAssert.assertThat(DISPATCHER.resolveTypeVariable(type.getTypeParameters()[1]).asList().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(DISPATCHER.resolveTypeVariable(type.getTypeParameters()[1]).asList().ofType(typeAnnotationType).getValue(value).resolve(Integer.class), CoreMatchers.is(((AbstractDynamicTypeBuilderForInliningTest.QUX) * 3)));
        MatcherAssert.assertThat(DISPATCHER.resolveTypeVariable(type.getTypeParameters()[1]).ofTypeVariableBoundType(0).asList().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(DISPATCHER.resolveTypeVariable(type.getTypeParameters()[1]).ofTypeVariableBoundType(0).asList().ofType(typeAnnotationType).getValue(value).resolve(Integer.class), CoreMatchers.is(((AbstractDynamicTypeBuilderForInliningTest.QUX) * 4)));
    }

    public @interface Baz {
        String foo();
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface FooBar {}

    public static class Qux {
        public static final String foo;

        public static String bar;

        static {
            foo = AbstractDynamicTypeBuilderForInliningTest.FOO;
        }

        public static void invoke() {
            AbstractDynamicTypeBuilderForInliningTest.Qux.bar = AbstractDynamicTypeBuilderForInliningTest.BAR;
        }
    }

    @SuppressWarnings("unused")
    static class VisibilityBridge {
        @AbstractDynamicTypeBuilderForInliningTest.FooBar
        public String foo(@AbstractDynamicTypeBuilderForInliningTest.FooBar
        String value) {
            return (AbstractDynamicTypeBuilderForInliningTest.FOO) + value;
        }

        public <T extends Exception> List<String> bar(List<String> value) throws T {
            return value;
        }

        void qux() {
            /* empty */
        }

        protected void baz() {
            /* empty */
        }

        public final void foobar() {
            /* empty */
        }
    }

    /* empty */
    static class PackagePrivateVisibilityBridgeExtension extends AbstractDynamicTypeBuilderForInliningTest.VisibilityBridge {}

    /* empty */
    public static class PublicVisibilityBridgeExtension extends AbstractDynamicTypeBuilderForInliningTest.VisibilityBridge {}

    abstract static class VisibilityBridgeAbstractMethod {
        public abstract void foo();
    }

    /* empty */
    abstract static class PackagePrivateVisibilityBridgeExtensionAbstractMethod extends AbstractDynamicTypeBuilderForInliningTest.VisibilityBridgeAbstractMethod {}

    @SuppressWarnings("unused")
    public static class Transform {
        Void foo;

        public String foo() {
            return null;
        }
    }

    public abstract static class AbstractGenericType<T> {
        public abstract T foo(T t);

        /* empty */
        public abstract static class Inner extends AbstractDynamicTypeBuilderForInliningTest.AbstractGenericType<Void> {}
    }

    public static class StackMapFrames {
        public boolean foo;

        public String foo() {
            return foo ? AbstractDynamicTypeBuilderForInliningTest.FOO : AbstractDynamicTypeBuilderForInliningTest.BAR;
        }
    }

    @AbstractDynamicTypeBuilderForInliningTest.SampleAnnotation
    @SuppressWarnings("unused")
    public static class Annotated {
        @AbstractDynamicTypeBuilderForInliningTest.SampleAnnotation
        Void foo;

        @AbstractDynamicTypeBuilderForInliningTest.SampleAnnotation
        void foo(@AbstractDynamicTypeBuilderForInliningTest.SampleAnnotation
        Void v) {
            /* empty */
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface SampleAnnotation {}
}

