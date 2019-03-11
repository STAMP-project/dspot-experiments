package net.bytebuddy.dynamic.scaffold.subclass;


import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.PackageDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.AbstractDynamicTypeBuilderTest;
import net.bytebuddy.dynamic.TargetType;
import net.bytebuddy.dynamic.loading.ByteArrayClassLoader;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.StubMethod;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.implementation.bytecode.constant.TextConstant;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.scope.GenericType;
import net.bytebuddy.test.utility.InjectionStrategyResolver;
import net.bytebuddy.test.utility.JavaVersionRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.objectweb.asm.Opcodes;

import static net.bytebuddy.description.annotation.AnnotationDescription.Builder.ofType;
import static net.bytebuddy.description.type.TypeDescription.Generic.AnnotationReader.DISPATCHER;
import static net.bytebuddy.description.type.TypeDescription.Generic.Builder.rawType;
import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.CHILD_FIRST;
import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;
import static net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy.Default.DEFAULT_CONSTRUCTOR;
import static net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy.Default.NO_CONSTRUCTORS;


public class SubclassDynamicTypeBuilderTest extends AbstractDynamicTypeBuilderTest {
    private static final String TYPE_VARIABLE_NAME = "net.bytebuddy.test.precompiled.TypeAnnotation";

    private static final String VALUE = "value";

    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final int BAZ = 42;

    private static final String DEFAULT_METHOD_INTERFACE = "net.bytebuddy.test.precompiled.SingleDefaultMethodInterface";

    private static final String PARAMETER_NAME_CLASS = "net.bytebuddy.test.precompiled.ParameterNames";

    private static final Object STATIC_FIELD = null;

    private static final String INTERFACE_STATIC_FIELD_NAME = "FOO";

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Test
    public void testSimpleSubclass() throws Exception {
        Class<?> type = new ByteBuddy().subclass(Object.class).make().load(getClass().getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethods().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(type.getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getDeclaredConstructor(), CoreMatchers.notNullValue(Constructor.class));
        MatcherAssert.assertThat(Object.class.isAssignableFrom(type), CoreMatchers.is(true));
        MatcherAssert.assertThat(type, CoreMatchers.not(CoreMatchers.<Class<?>>is(Object.class)));
        MatcherAssert.assertThat(type.getDeclaredConstructor().newInstance(), CoreMatchers.notNullValue(Object.class));
        MatcherAssert.assertThat(type.isInterface(), CoreMatchers.is(false));
        MatcherAssert.assertThat(type.isAnnotation(), CoreMatchers.is(false));
    }

    @Test
    public void testSimpleSubclassWithoutConstructor() throws Exception {
        Class<?> type = new ByteBuddy().subclass(Object.class, NO_CONSTRUCTORS).make().load(getClass().getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethods().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(type.getDeclaredConstructors().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(Object.class.isAssignableFrom(type), CoreMatchers.is(true));
        MatcherAssert.assertThat(type, CoreMatchers.not(CoreMatchers.<Class<?>>is(Object.class)));
        MatcherAssert.assertThat(type.isInterface(), CoreMatchers.is(false));
        MatcherAssert.assertThat(type.isAnnotation(), CoreMatchers.is(false));
    }

    @Test
    public void testSimpleSubclassWithDefaultConstructor() throws Exception {
        Class<? extends SubclassDynamicTypeBuilderTest.DefaultConstructor> type = new ByteBuddy().subclass(SubclassDynamicTypeBuilderTest.DefaultConstructor.class, DEFAULT_CONSTRUCTOR).make().load(getClass().getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethods().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(type.getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getDeclaredConstructor(), CoreMatchers.notNullValue(Constructor.class));
        MatcherAssert.assertThat(SubclassDynamicTypeBuilderTest.DefaultConstructor.class.isAssignableFrom(type), CoreMatchers.is(true));
        MatcherAssert.assertThat(type, CoreMatchers.not(CoreMatchers.<Class<?>>is(SubclassDynamicTypeBuilderTest.DefaultConstructor.class)));
        MatcherAssert.assertThat(type.getDeclaredConstructor().newInstance(), CoreMatchers.notNullValue(SubclassDynamicTypeBuilderTest.DefaultConstructor.class));
        MatcherAssert.assertThat(type.isInterface(), CoreMatchers.is(false));
        MatcherAssert.assertThat(type.isAnnotation(), CoreMatchers.is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonExtendableIsIllegal() throws Exception {
        new ByteBuddy().subclass(String.class);
    }

    @Test
    public void testInterfaceDefinition() throws Exception {
        Class<? extends SubclassDynamicTypeBuilderTest.SimpleInterface> type = new ByteBuddy().makeInterface(SubclassDynamicTypeBuilderTest.SimpleInterface.class).defineMethod(SubclassDynamicTypeBuilderTest.FOO, void.class, Visibility.PUBLIC).withParameters(Void.class).withoutCode().make().load(getClass().getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getDeclaredMethod(SubclassDynamicTypeBuilderTest.FOO, Void.class), CoreMatchers.notNullValue(Method.class));
        MatcherAssert.assertThat(type.getDeclaredConstructors().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(SubclassDynamicTypeBuilderTest.SimpleInterface.class.isAssignableFrom(type), CoreMatchers.is(true));
        MatcherAssert.assertThat(type, CoreMatchers.not(CoreMatchers.<Class<?>>is(SubclassDynamicTypeBuilderTest.SimpleInterface.class)));
        MatcherAssert.assertThat(type.isInterface(), CoreMatchers.is(true));
        MatcherAssert.assertThat(type.isAnnotation(), CoreMatchers.is(false));
    }

    @Test
    public void testAnnotationDefinition() throws Exception {
        Class<? extends Annotation> type = new ByteBuddy().makeAnnotation().defineMethod(SubclassDynamicTypeBuilderTest.FOO, int.class, Visibility.PUBLIC).withoutCode().defineMethod(SubclassDynamicTypeBuilderTest.BAR, String.class, Visibility.PUBLIC).defaultValue(SubclassDynamicTypeBuilderTest.FOO, String.class).defineMethod(SubclassDynamicTypeBuilderTest.QUX, SubclassDynamicTypeBuilderTest.SimpleEnum.class, Visibility.PUBLIC).defaultValue(SubclassDynamicTypeBuilderTest.SimpleEnum.FIRST, SubclassDynamicTypeBuilderTest.SimpleEnum.class).make().load(getClass().getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethods().length, CoreMatchers.is(3));
        MatcherAssert.assertThat(type.getDeclaredMethod(SubclassDynamicTypeBuilderTest.FOO), CoreMatchers.notNullValue(Method.class));
        MatcherAssert.assertThat(type.getDeclaredMethod(SubclassDynamicTypeBuilderTest.BAR).getDefaultValue(), CoreMatchers.is(((Object) (SubclassDynamicTypeBuilderTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredMethod(SubclassDynamicTypeBuilderTest.QUX).getDefaultValue(), CoreMatchers.is(((Object) (SubclassDynamicTypeBuilderTest.SimpleEnum.FIRST))));
        MatcherAssert.assertThat(type.getDeclaredConstructors().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(Annotation.class.isAssignableFrom(type), CoreMatchers.is(true));
        MatcherAssert.assertThat(type, CoreMatchers.not(CoreMatchers.<Class<?>>is(Annotation.class)));
        MatcherAssert.assertThat(type.isInterface(), CoreMatchers.is(true));
        MatcherAssert.assertThat(type.isAnnotation(), CoreMatchers.is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEnumerationDefinition() throws Exception {
        Class<? extends Enum<?>> type = new ByteBuddy().makeEnumeration(SubclassDynamicTypeBuilderTest.FOO, SubclassDynamicTypeBuilderTest.BAR).make().load(getClass().getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethods().length, CoreMatchers.is(2));
        MatcherAssert.assertThat(type.getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getDeclaredFields().length, CoreMatchers.is(3));
        MatcherAssert.assertThat(Enum.class.isAssignableFrom(type), CoreMatchers.is(true));
        MatcherAssert.assertThat(type, CoreMatchers.not(CoreMatchers.<Class<?>>is(Enum.class)));
        MatcherAssert.assertThat(type.isInterface(), CoreMatchers.is(false));
        MatcherAssert.assertThat(type.isAnnotation(), CoreMatchers.is(false));
        MatcherAssert.assertThat(type.isEnum(), CoreMatchers.is(true));
        Enum foo = Enum.valueOf(((Class) (type)), SubclassDynamicTypeBuilderTest.FOO);
        MatcherAssert.assertThat(foo.name(), CoreMatchers.is(SubclassDynamicTypeBuilderTest.FOO));
        MatcherAssert.assertThat(foo.ordinal(), CoreMatchers.is(0));
        Enum bar = Enum.valueOf(((Class) (type)), SubclassDynamicTypeBuilderTest.BAR);
        MatcherAssert.assertThat(bar.name(), CoreMatchers.is(SubclassDynamicTypeBuilderTest.BAR));
        MatcherAssert.assertThat(bar.ordinal(), CoreMatchers.is(1));
    }

    @Test
    public void testPackageDefinition() throws Exception {
        Class<?> packageType = new ByteBuddy().makePackage(SubclassDynamicTypeBuilderTest.FOO).annotateType(ofType(SubclassDynamicTypeBuilderTest.Foo.class).build()).make().load(getClass().getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(packageType.getSimpleName(), CoreMatchers.is(PackageDescription.PACKAGE_CLASS_NAME));
        MatcherAssert.assertThat(packageType.getName(), CoreMatchers.is((((SubclassDynamicTypeBuilderTest.FOO) + ".") + (PackageDescription.PACKAGE_CLASS_NAME))));
        MatcherAssert.assertThat(packageType.getModifiers(), CoreMatchers.is(PackageDescription.PACKAGE_MODIFIERS));
        MatcherAssert.assertThat(packageType.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(packageType.getDeclaredMethods().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(packageType.getDeclaredAnnotations().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(packageType.getAnnotation(SubclassDynamicTypeBuilderTest.Foo.class), CoreMatchers.notNullValue(SubclassDynamicTypeBuilderTest.Foo.class));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testDefaultMethodNonOverridden() throws Exception {
        Class<?> interfaceType = Class.forName(SubclassDynamicTypeBuilderTest.DEFAULT_METHOD_INTERFACE);
        Object interfaceMarker = interfaceType.getDeclaredField(SubclassDynamicTypeBuilderTest.INTERFACE_STATIC_FIELD_NAME).get(SubclassDynamicTypeBuilderTest.STATIC_FIELD);
        Method interfaceMethod = interfaceType.getDeclaredMethod(SubclassDynamicTypeBuilderTest.FOO);
        Class<?> dynamicType = new ByteBuddy().subclass(interfaceType).make().load(getClass().getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(dynamicType.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(dynamicType.getDeclaredMethods().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(interfaceMethod.invoke(dynamicType.getDeclaredConstructor().newInstance()), CoreMatchers.is(interfaceMarker));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testDefaultMethodOverridden() throws Exception {
        Class<?> interfaceType = Class.forName(SubclassDynamicTypeBuilderTest.DEFAULT_METHOD_INTERFACE);
        Method interfaceMethod = interfaceType.getDeclaredMethod(SubclassDynamicTypeBuilderTest.FOO);
        Class<?> dynamicType = new ByteBuddy().subclass(interfaceType).method(ElementMatchers.isDeclaredBy(interfaceType)).intercept(FixedValue.value(SubclassDynamicTypeBuilderTest.BAR)).make().load(getClass().getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(dynamicType.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(dynamicType.getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(interfaceMethod.invoke(dynamicType.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (SubclassDynamicTypeBuilderTest.BAR))));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testParameterMetaDataSubclassForLoaded() throws Exception {
        Class<?> dynamicType = new ByteBuddy().subclass(Class.forName(SubclassDynamicTypeBuilderTest.PARAMETER_NAME_CLASS)).method(ElementMatchers.named(SubclassDynamicTypeBuilderTest.FOO)).intercept(StubMethod.INSTANCE).make().load(getClass().getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(dynamicType.getDeclaredMethods().length, CoreMatchers.is(1));
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
    @JavaVersionRule.Enforce(8)
    public void testDefaultInterfaceSubInterface() throws Exception {
        Class<?> interfaceType = Class.forName(SubclassDynamicTypeBuilderTest.DEFAULT_METHOD_INTERFACE);
        Class<?> dynamicInterfaceType = new ByteBuddy().subclass(interfaceType).modifiers((((Opcodes.ACC_PUBLIC) | (Opcodes.ACC_INTERFACE)) | (Opcodes.ACC_ABSTRACT))).method(ElementMatchers.named(SubclassDynamicTypeBuilderTest.FOO)).intercept(MethodDelegation.to(AbstractDynamicTypeBuilderTest.InterfaceOverrideInterceptor.class)).make().load(getClass().getClassLoader(), WRAPPER).getLoaded();
        Class<?> dynamicClassType = new ByteBuddy().subclass(dynamicInterfaceType).make().load(dynamicInterfaceType.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(dynamicClassType.getMethod(SubclassDynamicTypeBuilderTest.FOO).invoke(dynamicClassType.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) ((SubclassDynamicTypeBuilderTest.FOO) + (SubclassDynamicTypeBuilderTest.BAR)))));
        MatcherAssert.assertThat(dynamicInterfaceType.getDeclaredMethods().length, CoreMatchers.is(2));
        MatcherAssert.assertThat(dynamicClassType.getDeclaredMethods().length, CoreMatchers.is(0));
    }

    @Test
    public void testDoesNotOverrideMethodWithPackagePrivateReturnType() throws Exception {
        Class<?> type = new ByteBuddy().subclass(SubclassDynamicTypeBuilderTest.PackagePrivateReturnType.class).name(("net.bytebuddy.test.generated." + (SubclassDynamicTypeBuilderTest.FOO))).method(ElementMatchers.isDeclaredBy(SubclassDynamicTypeBuilderTest.PackagePrivateReturnType.class)).intercept(StubMethod.INSTANCE).make().load(new ByteArrayClassLoader(ClassLoadingStrategy.BOOTSTRAP_LOADER, net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.readToNames(SubclassDynamicTypeBuilderTest.PackagePrivateReturnType.class, SubclassDynamicTypeBuilderTest.PackagePrivateReturnType.Argument.class)), WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethods().length, CoreMatchers.is(0));
    }

    @Test
    public void testDoesNotOverrideMethodWithPackagePrivateArgumentType() throws Exception {
        Class<?> type = new ByteBuddy().subclass(SubclassDynamicTypeBuilderTest.PackagePrivateArgumentType.class).name(("net.bytebuddy.test.generated." + (SubclassDynamicTypeBuilderTest.FOO))).method(ElementMatchers.isDeclaredBy(SubclassDynamicTypeBuilderTest.PackagePrivateArgumentType.class)).intercept(StubMethod.INSTANCE).make().load(new ByteArrayClassLoader(ClassLoadingStrategy.BOOTSTRAP_LOADER, net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.readToNames(SubclassDynamicTypeBuilderTest.PackagePrivateArgumentType.class, SubclassDynamicTypeBuilderTest.PackagePrivateArgumentType.Argument.class)), WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethods().length, CoreMatchers.is(0));
    }

    @Test
    public void testDoesNotOverridePrivateMethod() throws Exception {
        Class<?> type = new ByteBuddy().subclass(SubclassDynamicTypeBuilderTest.PrivateMethod.class).method(ElementMatchers.isDeclaredBy(SubclassDynamicTypeBuilderTest.PrivateMethod.class)).intercept(StubMethod.INSTANCE).make().load(new ByteArrayClassLoader(ClassLoadingStrategy.BOOTSTRAP_LOADER, readToNames(SubclassDynamicTypeBuilderTest.PrivateMethod.class)), WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethods().length, CoreMatchers.is(0));
    }

    @Test
    public void testGenericTypeRawExtension() throws Exception {
        Class<?> dynamicType = new ByteBuddy().subclass(GenericType.Inner.class).method(ElementMatchers.named(SubclassDynamicTypeBuilderTest.FOO).or(ElementMatchers.named("call"))).intercept(StubMethod.INSTANCE).make().load(getClass().getClassLoader(), InjectionStrategyResolver.resolve(GenericType.Inner.class)).getLoaded();
        MatcherAssert.assertThat(dynamicType.getTypeParameters().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(dynamicType.getGenericSuperclass(), CoreMatchers.instanceOf(Class.class));
        MatcherAssert.assertThat(dynamicType.getGenericSuperclass(), CoreMatchers.is(((Type) (GenericType.Inner.class))));
        MatcherAssert.assertThat(dynamicType.getGenericInterfaces().length, CoreMatchers.is(0));
        Method foo = dynamicType.getDeclaredMethod(SubclassDynamicTypeBuilderTest.FOO, String.class);
        MatcherAssert.assertThat(foo.getTypeParameters().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(foo.getGenericReturnType(), CoreMatchers.is(((Object) (List.class))));
        Method call = dynamicType.getDeclaredMethod("call");
        MatcherAssert.assertThat(call.getGenericReturnType(), CoreMatchers.is(((Object) (Map.class))));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBridgeMethodCreation() throws Exception {
        Class<?> dynamicType = new ByteBuddy().subclass(AbstractDynamicTypeBuilderTest.BridgeRetention.Inner.class).method(ElementMatchers.named(SubclassDynamicTypeBuilderTest.FOO)).intercept(new Implementation.Simple(new TextConstant(SubclassDynamicTypeBuilderTest.FOO), MethodReturn.REFERENCE)).make().load(getClass().getClassLoader(), CHILD_FIRST).getLoaded();
        TestCase.assertEquals(String.class, dynamicType.getDeclaredMethod(SubclassDynamicTypeBuilderTest.FOO).getReturnType());
        MatcherAssert.assertThat(dynamicType.getDeclaredMethod(SubclassDynamicTypeBuilderTest.FOO).getGenericReturnType(), CoreMatchers.is(((Type) (String.class))));
        AbstractDynamicTypeBuilderTest.BridgeRetention<String> bridgeRetention = ((AbstractDynamicTypeBuilderTest.BridgeRetention<String>) (dynamicType.getDeclaredConstructor().newInstance()));
        MatcherAssert.assertThat(bridgeRetention.foo(), CoreMatchers.is(SubclassDynamicTypeBuilderTest.FOO));
        bridgeRetention.assertZeroCalls();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBridgeMethodCreationForExistingBridgeMethod() throws Exception {
        Class<?> dynamicType = new ByteBuddy().subclass(AbstractDynamicTypeBuilderTest.CallSuperMethod.Inner.class).method(ElementMatchers.named(SubclassDynamicTypeBuilderTest.FOO)).intercept(SuperMethodCall.INSTANCE).make().load(getClass().getClassLoader(), CHILD_FIRST).getLoaded();
        MatcherAssert.assertThat(dynamicType.getDeclaredMethods().length, CoreMatchers.is(2));
        TestCase.assertEquals(String.class, dynamicType.getDeclaredMethod(SubclassDynamicTypeBuilderTest.FOO, String.class).getReturnType());
        MatcherAssert.assertThat(dynamicType.getDeclaredMethod(SubclassDynamicTypeBuilderTest.FOO, String.class).getGenericReturnType(), CoreMatchers.is(((Type) (String.class))));
        MatcherAssert.assertThat(dynamicType.getDeclaredMethod(SubclassDynamicTypeBuilderTest.FOO, String.class).isBridge(), CoreMatchers.is(false));
        TestCase.assertEquals(Object.class, dynamicType.getDeclaredMethod(SubclassDynamicTypeBuilderTest.FOO, Object.class).getReturnType());
        MatcherAssert.assertThat(dynamicType.getDeclaredMethod(SubclassDynamicTypeBuilderTest.FOO, Object.class).getGenericReturnType(), CoreMatchers.is(((Type) (Object.class))));
        MatcherAssert.assertThat(dynamicType.getDeclaredMethod(SubclassDynamicTypeBuilderTest.FOO, Object.class).isBridge(), CoreMatchers.is(true));
        AbstractDynamicTypeBuilderTest.CallSuperMethod<String> callSuperMethod = ((AbstractDynamicTypeBuilderTest.CallSuperMethod<String>) (dynamicType.getDeclaredConstructor().newInstance()));
        MatcherAssert.assertThat(callSuperMethod.foo(SubclassDynamicTypeBuilderTest.FOO), CoreMatchers.is(SubclassDynamicTypeBuilderTest.FOO));
        callSuperMethod.assertOnlyCall(SubclassDynamicTypeBuilderTest.FOO);
    }

    @Test
    public void testBridgeMethodForAbstractMethod() throws Exception {
        Class<?> dynamicType = new ByteBuddy().subclass(SubclassDynamicTypeBuilderTest.AbstractGenericType.Inner.class).modifiers(((Opcodes.ACC_ABSTRACT) | (Opcodes.ACC_PUBLIC))).method(ElementMatchers.named(SubclassDynamicTypeBuilderTest.FOO)).withoutCode().make().load(getClass().getClassLoader(), CHILD_FIRST).getLoaded();
        MatcherAssert.assertThat(dynamicType.getDeclaredMethods().length, CoreMatchers.is(2));
        TestCase.assertEquals(Void.class, dynamicType.getDeclaredMethod(SubclassDynamicTypeBuilderTest.FOO, Void.class).getReturnType());
        MatcherAssert.assertThat(dynamicType.getDeclaredMethod(SubclassDynamicTypeBuilderTest.FOO, Void.class).getGenericReturnType(), CoreMatchers.is(((Type) (Void.class))));
        MatcherAssert.assertThat(dynamicType.getDeclaredMethod(SubclassDynamicTypeBuilderTest.FOO, Void.class).isBridge(), CoreMatchers.is(false));
        MatcherAssert.assertThat(Modifier.isAbstract(dynamicType.getDeclaredMethod(SubclassDynamicTypeBuilderTest.FOO, Void.class).getModifiers()), CoreMatchers.is(true));
        TestCase.assertEquals(Object.class, dynamicType.getDeclaredMethod(SubclassDynamicTypeBuilderTest.FOO, Object.class).getReturnType());
        MatcherAssert.assertThat(dynamicType.getDeclaredMethod(SubclassDynamicTypeBuilderTest.FOO, Object.class).getGenericReturnType(), CoreMatchers.is(((Type) (Object.class))));
        MatcherAssert.assertThat(dynamicType.getDeclaredMethod(SubclassDynamicTypeBuilderTest.FOO, Object.class).isBridge(), CoreMatchers.is(true));
        MatcherAssert.assertThat(Modifier.isAbstract(dynamicType.getDeclaredMethod(SubclassDynamicTypeBuilderTest.FOO, Object.class).getModifiers()), CoreMatchers.is(false));
    }

    @Test
    public void testVisibilityBridge() throws Exception {
        Class<?> type = new ByteBuddy().subclass(SubclassDynamicTypeBuilderTest.VisibilityBridge.class).modifiers(Visibility.PUBLIC).make().load(getClass().getClassLoader(), InjectionStrategyResolver.resolve(SubclassDynamicTypeBuilderTest.VisibilityBridge.class)).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getDeclaredMethods().length, CoreMatchers.is(2));
        Method foo = type.getDeclaredMethod(SubclassDynamicTypeBuilderTest.FOO, String.class);
        MatcherAssert.assertThat(foo.isBridge(), CoreMatchers.is(true));
        MatcherAssert.assertThat(foo.getDeclaredAnnotations().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(foo.getAnnotation(SubclassDynamicTypeBuilderTest.Foo.class), CoreMatchers.notNullValue(SubclassDynamicTypeBuilderTest.Foo.class));
        MatcherAssert.assertThat(foo.invoke(type.getDeclaredConstructor().newInstance(), SubclassDynamicTypeBuilderTest.BAR), CoreMatchers.is(((Object) ((SubclassDynamicTypeBuilderTest.FOO) + (SubclassDynamicTypeBuilderTest.BAR)))));
        Method bar = type.getDeclaredMethod(SubclassDynamicTypeBuilderTest.BAR, List.class);
        MatcherAssert.assertThat(bar.isBridge(), CoreMatchers.is(true));
        MatcherAssert.assertThat(bar.getDeclaredAnnotations().length, CoreMatchers.is(0));
        List<?> list = new ArrayList<Object>();
        MatcherAssert.assertThat(bar.invoke(type.getDeclaredConstructor().newInstance(), list), CoreMatchers.sameInstance(((Object) (list))));
        MatcherAssert.assertThat(bar.getGenericReturnType(), CoreMatchers.instanceOf(Class.class));
        MatcherAssert.assertThat(bar.getGenericParameterTypes()[0], CoreMatchers.instanceOf(Class.class));
        MatcherAssert.assertThat(bar.getGenericExceptionTypes()[0], CoreMatchers.instanceOf(Class.class));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testVisibilityBridgeForDefaultMethod() throws Exception {
        Class<?> defaultInterface = new ByteBuddy().makeInterface().merge(Visibility.PACKAGE_PRIVATE).defineMethod(SubclassDynamicTypeBuilderTest.FOO, String.class, Visibility.PUBLIC).intercept(FixedValue.value(SubclassDynamicTypeBuilderTest.BAR)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, opened()).getLoaded();
        Class<?> type = new ByteBuddy().subclass(defaultInterface).modifiers(Visibility.PUBLIC).make().load(defaultInterface.getClassLoader()).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getDeclaredMethods().length, CoreMatchers.is(1));
        Method foo = type.getDeclaredMethod(SubclassDynamicTypeBuilderTest.FOO);
        MatcherAssert.assertThat(foo.isBridge(), CoreMatchers.is(true));
        MatcherAssert.assertThat(foo.invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (SubclassDynamicTypeBuilderTest.BAR))));
    }

    @Test
    public void testNoVisibilityBridgeForNonPublicType() throws Exception {
        Class<?> type = new ByteBuddy().subclass(SubclassDynamicTypeBuilderTest.VisibilityBridge.class).modifiers(0).make().load(getClass().getClassLoader(), InjectionStrategyResolver.resolve(SubclassDynamicTypeBuilderTest.VisibilityBridge.class)).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getDeclaredMethods().length, CoreMatchers.is(0));
    }

    @Test
    public void testNoVisibilityBridgeForInheritedType() throws Exception {
        Class<?> type = new ByteBuddy().subclass(SubclassDynamicTypeBuilderTest.VisibilityBridgeExtension.class).modifiers(Opcodes.ACC_PUBLIC).make().load(getClass().getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getDeclaredMethods().length, CoreMatchers.is(0));
    }

    @Test
    public void testNoVisibilityBridgeForAbstractMethod() throws Exception {
        Class<?> type = new ByteBuddy().subclass(SubclassDynamicTypeBuilderTest.VisibilityBridgeAbstractMethod.class).modifiers(((Opcodes.ACC_PUBLIC) | (Opcodes.ACC_ABSTRACT))).make().load(getClass().getClassLoader(), InjectionStrategyResolver.resolve(SubclassDynamicTypeBuilderTest.VisibilityBridgeAbstractMethod.class)).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getDeclaredMethods().length, CoreMatchers.is(0));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testAnnotationTypeOnSuperClass() throws Exception {
        Class<? extends Annotation> typeAnnotationType = ((Class<? extends Annotation>) (Class.forName(SubclassDynamicTypeBuilderTest.TYPE_VARIABLE_NAME)));
        MethodDescription.InDefinedShape value = of(typeAnnotationType).getDeclaredMethods().filter(ElementMatchers.named(SubclassDynamicTypeBuilderTest.VALUE)).getOnly();
        Class<?> type = new ByteBuddy().subclass(rawType(Object.class).build(ofType(typeAnnotationType).define(SubclassDynamicTypeBuilderTest.VALUE, SubclassDynamicTypeBuilderTest.BAZ).build())).make().load(typeAnnotationType.getClassLoader(), CHILD_FIRST).getLoaded();
        MatcherAssert.assertThat(type.getSuperclass(), CoreMatchers.is(((Object) (Object.class))));
        MatcherAssert.assertThat(DISPATCHER.resolveSuperClassType(type).asList().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(DISPATCHER.resolveSuperClassType(type).asList().ofType(typeAnnotationType).getValue(value).resolve(Integer.class), CoreMatchers.is(SubclassDynamicTypeBuilderTest.BAZ));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testReceiverTypeDefinition() throws Exception {
        Class<? extends Annotation> typeAnnotationType = ((Class<? extends Annotation>) (Class.forName(SubclassDynamicTypeBuilderTest.TYPE_VARIABLE_NAME)));
        MethodDescription.InDefinedShape value = of(typeAnnotationType).getDeclaredMethods().filter(ElementMatchers.named(SubclassDynamicTypeBuilderTest.VALUE)).getOnly();
        Method method = createPlain().defineMethod(SubclassDynamicTypeBuilderTest.FOO, void.class).intercept(StubMethod.INSTANCE).receiverType(rawType(TargetType.class).annotate(ofType(typeAnnotationType).define(SubclassDynamicTypeBuilderTest.VALUE, SubclassDynamicTypeBuilderTest.BAZ).build()).build()).make().load(typeAnnotationType.getClassLoader(), WRAPPER).getLoaded().getDeclaredMethod(SubclassDynamicTypeBuilderTest.FOO);
        MatcherAssert.assertThat(DISPATCHER.resolveReceiverType(method).getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(DISPATCHER.resolveReceiverType(method).getDeclaredAnnotations().ofType(typeAnnotationType).getValue(value).resolve(Integer.class), CoreMatchers.is(SubclassDynamicTypeBuilderTest.BAZ));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testReceiverTypeInterception() throws Exception {
        Class<? extends Annotation> typeAnnotationType = ((Class<? extends Annotation>) (Class.forName(SubclassDynamicTypeBuilderTest.TYPE_VARIABLE_NAME)));
        MethodDescription.InDefinedShape value = of(typeAnnotationType).getDeclaredMethods().filter(ElementMatchers.named(SubclassDynamicTypeBuilderTest.VALUE)).getOnly();
        Method method = createPlain().method(ElementMatchers.named("toString")).intercept(StubMethod.INSTANCE).receiverType(rawType(TargetType.class).annotate(ofType(typeAnnotationType).define(SubclassDynamicTypeBuilderTest.VALUE, SubclassDynamicTypeBuilderTest.BAZ).build()).build()).make().load(typeAnnotationType.getClassLoader(), WRAPPER).getLoaded().getDeclaredMethod("toString");
        MatcherAssert.assertThat(DISPATCHER.resolveReceiverType(method).getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(DISPATCHER.resolveReceiverType(method).getDeclaredAnnotations().ofType(typeAnnotationType).getValue(value).resolve(Integer.class), CoreMatchers.is(SubclassDynamicTypeBuilderTest.BAZ));
    }

    @SuppressWarnings("unused")
    public enum SimpleEnum {

        FIRST,
        SECOND;}

    public interface SimpleInterface {
        void bar(Void arg);
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Foo {}

    @SuppressWarnings("unused")
    public static class DefaultConstructor {
        public DefaultConstructor() {
            /* empty */
        }

        public DefaultConstructor(Void arg) {
            /* empty */
        }
    }

    public static class PackagePrivateReturnType {
        public SubclassDynamicTypeBuilderTest.PackagePrivateReturnType.Argument foo() {
            return null;
        }

        /* empty */
        static class Argument {}
    }

    @SuppressWarnings("unused")
    public static class PackagePrivateArgumentType {
        public void foo(SubclassDynamicTypeBuilderTest.PackagePrivateArgumentType.Argument argument) {
            /* empty */
        }

        /* empty */
        static class Argument {}
    }

    @SuppressWarnings("unused")
    public static class PrivateMethod {
        private void foo() {
            /* empty */
        }
    }

    @SuppressWarnings("unused")
    static class VisibilityBridge {
        @SubclassDynamicTypeBuilderTest.Foo
        public String foo(@SubclassDynamicTypeBuilderTest.Foo
        String value) {
            return (SubclassDynamicTypeBuilderTest.FOO) + value;
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

    abstract static class VisibilityBridgeAbstractMethod {
        public abstract void foo();
    }

    /* empty */
    public static class VisibilityBridgeExtension extends SubclassDynamicTypeBuilderTest.VisibilityBridge {}

    public abstract static class AbstractGenericType<T> {
        public abstract T foo(T t);

        /* empty */
        public abstract static class Inner extends SubclassDynamicTypeBuilderTest.AbstractGenericType<Void> {}
    }
}

