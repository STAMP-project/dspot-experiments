package org.robolectric.internal.bytecode;


import ShadowConstants.CLASS_HANDLER_DATA_FIELD_NAME;
import ShadowConstants.CONSTRUCTOR_METHOD_NAME;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.testing.AChild;
import org.robolectric.testing.AClassThatCallsAMethodReturningAForgettableClass;
import org.robolectric.testing.AClassThatExtendsAClassWithFinalEqualsHashCode;
import org.robolectric.testing.AClassThatRefersToAForgettableClass;
import org.robolectric.testing.AClassThatRefersToAForgettableClassInItsConstructor;
import org.robolectric.testing.AClassThatRefersToAForgettableClassInMethodCalls;
import org.robolectric.testing.AClassThatRefersToAForgettableClassInMethodCallsReturningPrimitive;
import org.robolectric.testing.AClassToForget;
import org.robolectric.testing.AClassToRemember;
import org.robolectric.testing.AClassWithEqualsHashCodeToString;
import org.robolectric.testing.AClassWithFunnyConstructors;
import org.robolectric.testing.AClassWithMethodReturningArray;
import org.robolectric.testing.AClassWithMethodReturningBoolean;
import org.robolectric.testing.AClassWithMethodReturningDouble;
import org.robolectric.testing.AClassWithMethodReturningInteger;
import org.robolectric.testing.AClassWithNativeMethod;
import org.robolectric.testing.AClassWithNativeMethodReturningPrimitive;
import org.robolectric.testing.AClassWithNoDefaultConstructor;
import org.robolectric.testing.AClassWithStaticMethod;
import org.robolectric.testing.AFinalClass;
import org.robolectric.testing.AnEnum;
import org.robolectric.testing.AnExampleClass;
import org.robolectric.testing.AnInstrumentedChild;
import org.robolectric.testing.AnUninstrumentedClass;
import org.robolectric.testing.AnUninstrumentedParent;
import org.robolectric.util.Util;

import static InvokeDynamic.ENABLED;


@RunWith(JUnit4.class)
public class SandboxClassLoaderTest {
    private ClassLoader classLoader;

    private List<String> transcript = new ArrayList<>();

    private SandboxClassLoaderTest.MyClassHandler classHandler = new SandboxClassLoaderTest.MyClassHandler(transcript);

    private ShadowImpl shadow;

    @Test
    public void shouldMakeClassesNonFinal() throws Exception {
        Class<?> clazz = loadClass(AFinalClass.class);
        Assert.assertEquals(0, ((clazz.getModifiers()) & (Modifier.FINAL)));
    }

    @Test
    public void forClassesWithNoDefaultConstructor_shouldCreateOneButItShouldNotCallShadow() throws Exception {
        Constructor<?> defaultCtor = loadClass(AClassWithNoDefaultConstructor.class).getConstructor();
        Assert.assertTrue(Modifier.isPublic(defaultCtor.getModifiers()));
        defaultCtor.setAccessible(true);
        Object instance = defaultCtor.newInstance();
        assertThat(((Object) (shadow.extract(instance)))).isNotNull();
        assertThat(transcript).isEmpty();
    }

    @Test
    public void shouldDelegateToHandlerForConstructors() throws Exception {
        Class<?> clazz = loadClass(AClassWithNoDefaultConstructor.class);
        Constructor<?> ctor = clazz.getDeclaredConstructor(String.class);
        Assert.assertTrue(Modifier.isPublic(ctor.getModifiers()));
        ctor.setAccessible(true);
        Object instance = ctor.newInstance("new one");
        assertThat(transcript).containsExactly("methodInvoked: AClassWithNoDefaultConstructor.__constructor__(java.lang.String new one)");
        Field nameField = clazz.getDeclaredField("name");
        nameField.setAccessible(true);
        Assert.assertNull(nameField.get(instance));
    }

    @Test
    public void shouldDelegateClassLoadForUnacquiredClasses() throws Exception {
        InstrumentationConfiguration config = Mockito.mock(InstrumentationConfiguration.class);
        Mockito.when(config.shouldAcquire(ArgumentMatchers.anyString())).thenReturn(false);
        Mockito.when(config.shouldInstrument(ArgumentMatchers.any(MutableClass.class))).thenReturn(false);
        ClassLoader classLoader = new SandboxClassLoader(config);
        Class<?> exampleClass = classLoader.loadClass(AnExampleClass.class.getName());
        Assert.assertSame(getClass().getClassLoader(), exampleClass.getClassLoader());
    }

    @Test
    public void shouldPerformClassLoadForAcquiredClasses() throws Exception {
        ClassLoader classLoader = new SandboxClassLoader(configureBuilder().build());
        Class<?> exampleClass = classLoader.loadClass(AnUninstrumentedClass.class.getName());
        Assert.assertSame(classLoader, exampleClass.getClassLoader());
        try {
            exampleClass.getField(CLASS_HANDLER_DATA_FIELD_NAME);
            Assert.fail("class shouldn't be instrumented!");
        } catch (Exception e) {
            // expected
        }
    }

    @Test
    public void shouldPerformClassLoadAndInstrumentLoadForInstrumentedClasses() throws Exception {
        ClassLoader classLoader = new SandboxClassLoader(configureBuilder().build());
        Class<?> exampleClass = classLoader.loadClass(AnExampleClass.class.getName());
        Assert.assertSame(classLoader, exampleClass.getClassLoader());
        Field roboDataField = exampleClass.getField(CLASS_HANDLER_DATA_FIELD_NAME);
        Assert.assertNotNull(roboDataField);
        assertThat(Modifier.isPublic(roboDataField.getModifiers())).isTrue();
        // Java 9 doesn't allow updates to final fields from outside <init> or <clinit>:
        // https://bugs.openjdk.java.net/browse/JDK-8157181
        // Therefore, these fields need to be nonfinal / be made nonfinal.
        assertThat(Modifier.isFinal(roboDataField.getModifiers())).isFalse();
        assertThat(Modifier.isFinal(exampleClass.getField("STATIC_FINAL_FIELD").getModifiers())).isFalse();
        assertThat(Modifier.isFinal(exampleClass.getField("nonstaticFinalField").getModifiers())).isFalse();
    }

    @Test
    public void callingNormalMethodShouldInvokeClassHandler() throws Exception {
        Class<?> exampleClass = loadClass(AnExampleClass.class);
        Method normalMethod = exampleClass.getMethod("normalMethod", String.class, int.class);
        Object exampleInstance = exampleClass.getDeclaredConstructor().newInstance();
        Assert.assertEquals("response from methodInvoked: AnExampleClass.normalMethod(java.lang.String value1, int 123)", normalMethod.invoke(exampleInstance, "value1", 123));
        assertThat(transcript).containsExactly("methodInvoked: AnExampleClass.__constructor__()", "methodInvoked: AnExampleClass.normalMethod(java.lang.String value1, int 123)");
    }

    @Test
    public void shouldGenerateClassSpecificDirectAccessMethod() throws Exception {
        Class<?> exampleClass = loadClass(AnExampleClass.class);
        String methodName = shadow.directMethodName(exampleClass.getName(), "normalMethod");
        Method directMethod = exampleClass.getDeclaredMethod(methodName, String.class, int.class);
        directMethod.setAccessible(true);
        Object exampleInstance = exampleClass.getDeclaredConstructor().newInstance();
        Assert.assertEquals("normalMethod(value1, 123)", directMethod.invoke(exampleInstance, "value1", 123));
        assertThat(transcript).containsExactly("methodInvoked: AnExampleClass.__constructor__()");
    }

    @Test
    public void soMockitoDoesntExplodeDueToTooManyMethods_shouldGenerateClassSpecificDirectAccessMethodWhichIsPrivateAndFinal() throws Exception {
        Class<?> exampleClass = loadClass(AnExampleClass.class);
        String methodName = shadow.directMethodName(exampleClass.getName(), "normalMethod");
        Method directMethod = exampleClass.getDeclaredMethod(methodName, String.class, int.class);
        Assert.assertTrue(Modifier.isPrivate(directMethod.getModifiers()));
        Assert.assertTrue(Modifier.isFinal(directMethod.getModifiers()));
    }

    @Test
    public void callingStaticMethodShouldInvokeClassHandler() throws Exception {
        Class<?> exampleClass = loadClass(AClassWithStaticMethod.class);
        Method normalMethod = exampleClass.getMethod("staticMethod", String.class);
        Assert.assertEquals("response from methodInvoked: AClassWithStaticMethod.staticMethod(java.lang.String value1)", normalMethod.invoke(null, "value1"));
        assertThat(transcript).containsExactly("methodInvoked: AClassWithStaticMethod.staticMethod(java.lang.String value1)");
    }

    @Test
    public void callingStaticDirectAccessMethodShouldWork() throws Exception {
        Class<?> exampleClass = loadClass(AClassWithStaticMethod.class);
        String methodName = shadow.directMethodName(exampleClass.getName(), "staticMethod");
        Method directMethod = exampleClass.getDeclaredMethod(methodName, String.class);
        directMethod.setAccessible(true);
        Assert.assertEquals("staticMethod(value1)", directMethod.invoke(null, "value1"));
    }

    @Test
    public void callingNormalMethodReturningIntegerShouldInvokeClassHandler() throws Exception {
        Class<?> exampleClass = loadClass(AClassWithMethodReturningInteger.class);
        classHandler.valueToReturn = 456;
        Method normalMethod = exampleClass.getMethod("normalMethodReturningInteger", int.class);
        Object exampleInstance = exampleClass.getDeclaredConstructor().newInstance();
        Assert.assertEquals(456, normalMethod.invoke(exampleInstance, 123));
        assertThat(transcript).containsExactly("methodInvoked: AClassWithMethodReturningInteger.__constructor__()", "methodInvoked: AClassWithMethodReturningInteger.normalMethodReturningInteger(int 123)");
    }

    @Test
    public void callingMethodReturningDoubleShouldInvokeClassHandler() throws Exception {
        Class<?> exampleClass = loadClass(AClassWithMethodReturningDouble.class);
        classHandler.valueToReturn = 456;
        Method normalMethod = exampleClass.getMethod("normalMethodReturningDouble", double.class);
        Object exampleInstance = exampleClass.getDeclaredConstructor().newInstance();
        Assert.assertEquals(456.0, normalMethod.invoke(exampleInstance, 123.0));
        assertThat(transcript).containsExactly("methodInvoked: AClassWithMethodReturningDouble.__constructor__()", "methodInvoked: AClassWithMethodReturningDouble.normalMethodReturningDouble(double 123.0)");
    }

    @Test
    public void callingNativeMethodShouldInvokeClassHandler() throws Exception {
        Class<?> exampleClass = loadClass(AClassWithNativeMethod.class);
        Method normalMethod = exampleClass.getDeclaredMethod("nativeMethod", String.class, int.class);
        Object exampleInstance = exampleClass.getDeclaredConstructor().newInstance();
        Assert.assertEquals("response from methodInvoked: AClassWithNativeMethod.nativeMethod(java.lang.String value1, int 123)", normalMethod.invoke(exampleInstance, "value1", 123));
        assertThat(transcript).containsExactly("methodInvoked: AClassWithNativeMethod.__constructor__()", "methodInvoked: AClassWithNativeMethod.nativeMethod(java.lang.String value1, int 123)");
    }

    @Test
    public void directlyCallingNativeMethodShouldBeNoOp() throws Exception {
        Class<?> exampleClass = loadClass(AClassWithNativeMethod.class);
        Object exampleInstance = exampleClass.getDeclaredConstructor().newInstance();
        Method directMethod = findDirectMethod(exampleClass, "nativeMethod", String.class, int.class);
        assertThat(Modifier.isNative(directMethod.getModifiers())).isFalse();
        assertThat(directMethod.invoke(exampleInstance, "", 1)).isNull();
    }

    @Test
    public void directlyCallingNativeMethodReturningPrimitiveShouldBeNoOp() throws Exception {
        Class<?> exampleClass = loadClass(AClassWithNativeMethodReturningPrimitive.class);
        Object exampleInstance = exampleClass.getDeclaredConstructor().newInstance();
        Method directMethod = findDirectMethod(exampleClass, "nativeMethod");
        assertThat(Modifier.isNative(directMethod.getModifiers())).isFalse();
        assertThat(directMethod.invoke(exampleInstance)).isEqualTo(0);
    }

    @Test
    public void shouldHandleMethodsReturningBoolean() throws Exception {
        Class<?> exampleClass = loadClass(AClassWithMethodReturningBoolean.class);
        classHandler.valueToReturn = true;
        Method directMethod = exampleClass.getMethod("normalMethodReturningBoolean", boolean.class, boolean[].class);
        directMethod.setAccessible(true);
        Object exampleInstance = exampleClass.getDeclaredConstructor().newInstance();
        Assert.assertEquals(true, directMethod.invoke(exampleInstance, true, new boolean[0]));
        assertThat(transcript).containsExactly("methodInvoked: AClassWithMethodReturningBoolean.__constructor__()", "methodInvoked: AClassWithMethodReturningBoolean.normalMethodReturningBoolean(boolean true, boolean[] {})");
    }

    @Test
    public void shouldHandleMethodsReturningArray() throws Exception {
        Class<?> exampleClass = loadClass(AClassWithMethodReturningArray.class);
        classHandler.valueToReturn = new String[]{ "miao, mieuw" };
        Method directMethod = exampleClass.getMethod("normalMethodReturningArray");
        directMethod.setAccessible(true);
        Object exampleInstance = exampleClass.getDeclaredConstructor().newInstance();
        assertThat(transcript).containsExactly("methodInvoked: AClassWithMethodReturningArray.__constructor__()");
        transcript.clear();
        Assert.assertArrayEquals(new String[]{ "miao, mieuw" }, ((String[]) (directMethod.invoke(exampleInstance))));
        assertThat(transcript).containsExactly("methodInvoked: AClassWithMethodReturningArray.normalMethodReturningArray()");
    }

    @Test
    public void shouldInvokeShadowForEachConstructorInInheritanceTree() throws Exception {
        loadClass(AChild.class).getDeclaredConstructor().newInstance();
        assertThat(transcript).containsExactly("methodInvoked: AGrandparent.__constructor__()", "methodInvoked: AParent.__constructor__()", "methodInvoked: AChild.__constructor__()");
    }

    @Test
    public void shouldRetainSuperCallInConstructor() throws Exception {
        Class<?> aClass = loadClass(AnInstrumentedChild.class);
        Object o = aClass.getDeclaredConstructor(String.class).newInstance("hortense");
        Assert.assertEquals("HORTENSE's child", aClass.getSuperclass().getDeclaredField("parentName").get(o));
        Assert.assertNull(aClass.getDeclaredField("childName").get(o));
    }

    @Test
    public void shouldCorrectlySplitStaticPrepFromConstructorChaining() throws Exception {
        Class<?> aClass = loadClass(AClassWithFunnyConstructors.class);
        Object o = aClass.getDeclaredConstructor(String.class).newInstance("hortense");
        assertThat(transcript).containsExactly((("methodInvoked: AClassWithFunnyConstructors.__constructor__(" + (AnUninstrumentedParent.class.getName())) + " UninstrumentedParent{parentName='hortense'}, java.lang.String foo)"), "methodInvoked: AClassWithFunnyConstructors.__constructor__(java.lang.String hortense)");
        // should not run constructor bodies...
        Assert.assertEquals(null, getDeclaredFieldValue(aClass, o, "name"));
        Assert.assertEquals(null, getDeclaredFieldValue(aClass, o, "uninstrumentedParent"));
    }

    @Test
    public void shouldGenerateClassSpecificDirectAccessMethodForConstructorWhichDoesNotCallSuper() throws Exception {
        Class<?> aClass = loadClass(AClassWithFunnyConstructors.class);
        Object instance = aClass.getConstructor(String.class).newInstance("horace");
        assertThat(transcript).containsExactly((("methodInvoked: AClassWithFunnyConstructors.__constructor__(" + (AnUninstrumentedParent.class.getName())) + " UninstrumentedParent{parentName='horace'}, java.lang.String foo)"), "methodInvoked: AClassWithFunnyConstructors.__constructor__(java.lang.String horace)");
        transcript.clear();
        // each directly-accessible constructor body will need to be called explicitly, with the correct args...
        Class<?> uninstrumentedParentClass = loadClass(AnUninstrumentedParent.class);
        Method directMethod = findDirectMethod(aClass, "__constructor__", uninstrumentedParentClass, String.class);
        Object uninstrumentedParentIn = uninstrumentedParentClass.getDeclaredConstructor(String.class).newInstance("hortense");
        Assert.assertEquals(null, directMethod.invoke(instance, uninstrumentedParentIn, "foo"));
        assertThat(transcript).isEmpty();
        Assert.assertEquals(null, getDeclaredFieldValue(aClass, instance, "name"));
        Object uninstrumentedParentOut = getDeclaredFieldValue(aClass, instance, "uninstrumentedParent");
        Assert.assertEquals("hortense", getDeclaredFieldValue(uninstrumentedParentClass, uninstrumentedParentOut, "parentName"));
        Method directMethod2 = findDirectMethod(aClass, "__constructor__", String.class);
        Assert.assertEquals(null, directMethod2.invoke(instance, "hortense"));
        assertThat(transcript).isEmpty();
        Assert.assertEquals("hortense", getDeclaredFieldValue(aClass, instance, "name"));
    }

    @Test
    public void shouldNotInstrumentFinalEqualsHashcode() throws ClassNotFoundException {
        loadClass(AClassThatExtendsAClassWithFinalEqualsHashCode.class);
    }

    @Test
    public void shouldAlsoInstrumentEqualsAndHashCodeAndToStringWhenDeclared() throws Exception {
        Class<?> theClass = loadClass(AClassWithEqualsHashCodeToString.class);
        Object instance = theClass.getDeclaredConstructor().newInstance();
        assertThat(transcript).containsExactly("methodInvoked: AClassWithEqualsHashCodeToString.__constructor__()");
        transcript.clear();
        instance.toString();
        assertThat(transcript).containsExactly("methodInvoked: AClassWithEqualsHashCodeToString.toString()");
        transcript.clear();
        classHandler.valueToReturn = true;
        // noinspection ResultOfMethodCallIgnored,ObjectEqualsNull
        instance.equals(null);
        assertThat(transcript).containsExactly("methodInvoked: AClassWithEqualsHashCodeToString.equals(java.lang.Object null)");
        transcript.clear();
        classHandler.valueToReturn = 42;
        // noinspection ResultOfMethodCallIgnored
        instance.hashCode();
        assertThat(transcript).containsExactly("methodInvoked: AClassWithEqualsHashCodeToString.hashCode()");
    }

    @Test
    public void shouldRemapClasses() throws Exception {
        setClassLoader(new SandboxClassLoader(createRemappingConfig()));
        Class<?> theClass = loadClass(AClassThatRefersToAForgettableClass.class);
        Assert.assertEquals(loadClass(AClassToRemember.class), theClass.getField("someField").getType());
        Assert.assertEquals(Array.newInstance(loadClass(AClassToRemember.class), 0).getClass(), theClass.getField("someFields").getType());
    }

    @Test
    public void shouldFixTypesInFieldAccess() throws Exception {
        setClassLoader(new SandboxClassLoader(createRemappingConfig()));
        Class<?> theClass = loadClass(AClassThatRefersToAForgettableClassInItsConstructor.class);
        Object instance = theClass.getDeclaredConstructor().newInstance();
        Method method = theClass.getDeclaredMethod(shadow.directMethodName(theClass.getName(), CONSTRUCTOR_METHOD_NAME));
        method.setAccessible(true);
        method.invoke(instance);
    }

    @Test
    public void shouldFixTypesInMethodArgsAndReturn() throws Exception {
        setClassLoader(new SandboxClassLoader(createRemappingConfig()));
        Class<?> theClass = loadClass(AClassThatRefersToAForgettableClassInMethodCalls.class);
        Assert.assertNotNull(theClass.getDeclaredMethod("aMethod", int.class, loadClass(AClassToRemember.class), String.class));
    }

    @Test
    public void shouldInterceptFilteredMethodInvocations() throws Exception {
        setClassLoader(new SandboxClassLoader(configureBuilder().addInterceptedMethod(new MethodRef(AClassToForget.class, "forgettableMethod")).build()));
        Class<?> theClass = loadClass(AClassThatRefersToAForgettableClass.class);
        Object instance = theClass.getDeclaredConstructor().newInstance();
        Object output = theClass.getMethod("interactWithForgettableClass").invoke(shadow.directlyOn(instance, ((Class<Object>) (theClass))));
        Assert.assertEquals("null, get this!", output);
    }

    @Test
    public void shouldInterceptFilteredStaticMethodInvocations() throws Exception {
        setClassLoader(new SandboxClassLoader(configureBuilder().addInterceptedMethod(new MethodRef(AClassToForget.class, "forgettableStaticMethod")).build()));
        Class<?> theClass = loadClass(AClassThatRefersToAForgettableClass.class);
        Object instance = theClass.getDeclaredConstructor().newInstance();
        Object output = theClass.getMethod("interactWithForgettableStaticMethod").invoke(shadow.directlyOn(instance, ((Class<Object>) (theClass))));
        Assert.assertEquals("yess? forget this: null", output);
    }

    @Test
    public void byte_shouldBeHandledAsReturnValueFromInterceptHandler() throws Exception {
        if (ENABLED)
            return;

        classHandler.valueToReturnFromIntercept = ((byte) (10));
        assertThat(invokeInterceptedMethodOnAClassToForget("byteMethod")).isEqualTo(((byte) (10)));
    }

    @Test
    public void byteArray_shouldBeHandledAsReturnValueFromInterceptHandler() throws Exception {
        if (ENABLED)
            return;

        classHandler.valueToReturnFromIntercept = new byte[]{ 10, 12, 14 };
        assertThat(invokeInterceptedMethodOnAClassToForget("byteArrayMethod")).isEqualTo(new byte[]{ 10, 12, 14 });
    }

    @Test
    public void int_shouldBeHandledAsReturnValueFromInterceptHandler() throws Exception {
        if (ENABLED)
            return;

        classHandler.valueToReturnFromIntercept = 20;
        assertThat(invokeInterceptedMethodOnAClassToForget("intMethod")).isEqualTo(20);
    }

    @Test
    public void intArray_shouldBeHandledAsReturnValueFromInterceptHandler() throws Exception {
        if (ENABLED)
            return;

        classHandler.valueToReturnFromIntercept = new int[]{ 20, 22, 24 };
        assertThat(invokeInterceptedMethodOnAClassToForget("intArrayMethod")).isEqualTo(new int[]{ 20, 22, 24 });
    }

    @Test
    public void long_shouldBeHandledAsReturnValueFromInterceptHandler() throws Exception {
        if (ENABLED)
            return;

        classHandler.valueToReturnFromIntercept = 30L;
        assertThat(invokeInterceptedMethodOnAClassToForget("longMethod")).isEqualTo(30L);
    }

    @Test
    public void longArray_shouldBeHandledAsReturnValueFromInterceptHandler() throws Exception {
        if (ENABLED)
            return;

        classHandler.valueToReturnFromIntercept = new long[]{ 30L, 32L, 34L };
        assertThat(invokeInterceptedMethodOnAClassToForget("longArrayMethod")).isEqualTo(new long[]{ 30L, 32L, 34L });
    }

    @Test
    public void float_shouldBeHandledAsReturnValueFromInterceptHandler() throws Exception {
        if (ENABLED)
            return;

        classHandler.valueToReturnFromIntercept = 40.0F;
        assertThat(invokeInterceptedMethodOnAClassToForget("floatMethod")).isEqualTo(40.0F);
    }

    @Test
    public void floatArray_shouldBeHandledAsReturnValueFromInterceptHandler() throws Exception {
        if (ENABLED)
            return;

        classHandler.valueToReturnFromIntercept = new float[]{ 50.0F, 52.0F, 54.0F };
        assertThat(invokeInterceptedMethodOnAClassToForget("floatArrayMethod")).isEqualTo(new float[]{ 50.0F, 52.0F, 54.0F });
    }

    @Test
    public void double_shouldBeHandledAsReturnValueFromInterceptHandler() throws Exception {
        if (ENABLED)
            return;

        classHandler.valueToReturnFromIntercept = 80.0;
        assertThat(invokeInterceptedMethodOnAClassToForget("doubleMethod")).isEqualTo(80.0);
    }

    @Test
    public void doubleArray_shouldBeHandledAsReturnValueFromInterceptHandler() throws Exception {
        if (ENABLED)
            return;

        classHandler.valueToReturnFromIntercept = new double[]{ 90.0, 92.0, 94.0 };
        assertThat(invokeInterceptedMethodOnAClassToForget("doubleArrayMethod")).isEqualTo(new double[]{ 90.0, 92.0, 94.0 });
    }

    @Test
    public void short_shouldBeHandledAsReturnValueFromInterceptHandler() throws Exception {
        if (ENABLED)
            return;

        classHandler.valueToReturnFromIntercept = ((short) (60));
        assertThat(invokeInterceptedMethodOnAClassToForget("shortMethod")).isEqualTo(((short) (60)));
    }

    @Test
    public void shortArray_shouldBeHandledAsReturnValueFromInterceptHandler() throws Exception {
        if (ENABLED)
            return;

        classHandler.valueToReturnFromIntercept = new short[]{ 70, 72, 74 };
        assertThat(invokeInterceptedMethodOnAClassToForget("shortArrayMethod")).isEqualTo(new short[]{ 70, 72, 74 });
    }

    @Test
    public void void_shouldBeHandledAsReturnValueFromInterceptHandler() throws Exception {
        if (ENABLED)
            return;

        classHandler.valueToReturnFromIntercept = null;
        assertThat(invokeInterceptedMethodOnAClassToForget("voidReturningMethod")).isNull();
    }

    @Test
    public void shouldPassArgumentsFromInterceptedMethods() throws Exception {
        if (ENABLED)
            return;

        classHandler.valueToReturnFromIntercept = 10L;
        setClassLoader(new SandboxClassLoader(configureBuilder().addInterceptedMethod(new MethodRef(AClassToForget.class, "*")).build()));
        Class<?> theClass = loadClass(AClassThatRefersToAForgettableClassInMethodCallsReturningPrimitive.class);
        Object instance = theClass.getDeclaredConstructor().newInstance();
        shadow.directlyOn(instance, ((Class<Object>) (theClass)), "longMethod");
        assertThat(transcript).containsExactly("methodInvoked: AClassThatRefersToAForgettableClassInMethodCallsReturningPrimitive.__constructor__()", "intercept: org/robolectric/testing/AClassToForget/longReturningMethod(Ljava/lang/String;IJ)J with params (str str, 123 123, 456 456)");
    }

    @Test
    public void shouldRemapClassesWhileInterceptingMethods() throws Exception {
        InstrumentationConfiguration config = configureBuilder().addClassNameTranslation(AClassToForget.class.getName(), AClassToRemember.class.getName()).addInterceptedMethod(new MethodRef(AClassThatCallsAMethodReturningAForgettableClass.class, "getAForgettableClass")).build();
        setClassLoader(new SandboxClassLoader(config));
        Class<?> theClass = loadClass(AClassThatCallsAMethodReturningAForgettableClass.class);
        theClass.getMethod("callSomeMethod").invoke(shadow.directlyOn(theClass.getDeclaredConstructor().newInstance(), ((Class<Object>) (theClass))));
    }

    @Test
    public void shouldWorkWithEnums() throws Exception {
        loadClass(AnEnum.class);
    }

    @Test
    public void shouldReverseAnArray() throws Exception {
        Assert.assertArrayEquals(new Integer[]{ 5, 4, 3, 2, 1 }, Util.reverse(new Integer[]{ 1, 2, 3, 4, 5 }));
        Assert.assertArrayEquals(new Integer[]{ 4, 3, 2, 1 }, Util.reverse(new Integer[]{ 1, 2, 3, 4 }));
        Assert.assertArrayEquals(new Integer[]{ 1 }, Util.reverse(new Integer[]{ 1 }));
        Assert.assertArrayEquals(new Integer[]{  }, Util.reverse(new Integer[]{  }));
    }

    public static class MyClassHandler implements ClassHandler {
        private static final Object GENERATE_YOUR_OWN_VALUE = new Object();

        private List<String> transcript;

        private Object valueToReturn = SandboxClassLoaderTest.MyClassHandler.GENERATE_YOUR_OWN_VALUE;

        private Object valueToReturnFromIntercept = null;

        public MyClassHandler(List<String> transcript) {
            this.transcript = transcript;
        }

        @Override
        public void classInitializing(Class clazz) {
        }

        @Override
        public Object initializing(Object instance) {
            return "a shadow!";
        }

        public Object methodInvoked(Class clazz, String methodName, Object instance, String[] paramTypes, Object[] params) {
            StringBuilder buf = new StringBuilder();
            buf.append("methodInvoked: ").append(clazz.getSimpleName()).append(".").append(methodName).append("(");
            for (int i = 0; i < (paramTypes.length); i++) {
                if (i > 0)
                    buf.append(", ");

                Object param = params[i];
                Object display = (param == null) ? "null" : param.getClass().isArray() ? "{}" : param;
                buf.append(paramTypes[i]).append(" ").append(display);
            }
            buf.append(")");
            transcript.add(buf.toString());
            if ((valueToReturn) != (SandboxClassLoaderTest.MyClassHandler.GENERATE_YOUR_OWN_VALUE))
                return valueToReturn;

            return "response from " + (buf.toString());
        }

        @Override
        public Plan methodInvoked(String signature, boolean isStatic, Class<?> theClass) {
            final InvocationProfile invocationProfile = new InvocationProfile(signature, isStatic, getClass().getClassLoader());
            return new Plan() {
                @Override
                public Object run(Object instance, Object[] params) throws Exception {
                    try {
                        return methodInvoked(invocationProfile.clazz, invocationProfile.methodName, instance, invocationProfile.paramTypes, params);
                    } catch (Throwable throwable) {
                        throw new RuntimeException(throwable);
                    }
                }

                @Override
                public String describe() {
                    return invocationProfile.methodName;
                }
            };
        }

        @Override
        public MethodHandle getShadowCreator(Class<?> theClass) {
            return MethodHandles.dropArguments(MethodHandles.constant(String.class, "a shadow!"), 0, theClass);
        }

        @SuppressWarnings({ "UnusedDeclaration", "unused" })
        private Object invoke(InvocationProfile invocationProfile, Object instance, Object[] params) {
            return methodInvoked(invocationProfile.clazz, invocationProfile.methodName, instance, invocationProfile.paramTypes, params);
        }

        @Override
        public MethodHandle findShadowMethodHandle(Class<?> theClass, String name, MethodType type, boolean isStatic) throws IllegalAccessException {
            String signature = getSignature(theClass, name, type, isStatic);
            InvocationProfile invocationProfile = new InvocationProfile(signature, isStatic, getClass().getClassLoader());
            try {
                MethodHandle mh = MethodHandles.lookup().findVirtual(getClass(), "invoke", MethodType.methodType(Object.class, InvocationProfile.class, Object.class, Object[].class));
                mh = MethodHandles.insertArguments(mh, 0, this, invocationProfile);
                if (isStatic) {
                    return mh.bindTo(null).asCollector(Object[].class, type.parameterCount());
                } else {
                    return mh.asCollector(Object[].class, ((type.parameterCount()) - 1));
                }
            } catch (NoSuchMethodException e) {
                throw new AssertionError(e);
            }
        }

        public String getSignature(Class<?> caller, String name, MethodType type, boolean isStatic) {
            String className = caller.getName().replace('.', '/');
            // Remove implicit first argument
            if (!isStatic)
                type = type.dropParameterTypes(0, 1);

            return ((className + "/") + name) + (type.toMethodDescriptorString());
        }

        @Override
        public Object intercept(String signature, Object instance, Object[] params, Class theClass) throws Throwable {
            StringBuilder buf = new StringBuilder();
            buf.append("intercept: ").append(signature).append(" with params (");
            for (int i = 0; i < (params.length); i++) {
                if (i > 0)
                    buf.append(", ");

                Object param = params[i];
                Object display = (param == null) ? "null" : param.getClass().isArray() ? "{}" : param;
                buf.append(params[i]).append(" ").append(display);
            }
            buf.append(")");
            transcript.add(buf.toString());
            return valueToReturnFromIntercept;
        }

        @Override
        public <T extends Throwable> T stripStackTrace(T throwable) {
            return throwable;
        }
    }
}

