package net.bytebuddy.utility;


import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.test.utility.JavaVersionRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.mockito.Mockito;

import static net.bytebuddy.utility.JavaConstant.MethodHandle.HandleType.GET_FIELD;
import static net.bytebuddy.utility.JavaConstant.MethodHandle.HandleType.GET_STATIC_FIELD;
import static net.bytebuddy.utility.JavaConstant.MethodHandle.HandleType.INVOKE_SPECIAL;
import static net.bytebuddy.utility.JavaConstant.MethodHandle.HandleType.INVOKE_SPECIAL_CONSTRUCTOR;
import static net.bytebuddy.utility.JavaConstant.MethodHandle.HandleType.INVOKE_STATIC;
import static net.bytebuddy.utility.JavaConstant.MethodHandle.HandleType.INVOKE_VIRTUAL;
import static net.bytebuddy.utility.JavaConstant.MethodHandle.HandleType.PUT_FIELD;
import static net.bytebuddy.utility.JavaConstant.MethodHandle.HandleType.PUT_STATIC_FIELD;
import static net.bytebuddy.utility.JavaConstant.MethodHandle.HandleType.of;
import static net.bytebuddy.utility.JavaConstant.MethodHandle.ofGetter;
import static net.bytebuddy.utility.JavaConstant.MethodHandle.ofLoaded;
import static net.bytebuddy.utility.JavaConstant.MethodHandle.ofSetter;
import static net.bytebuddy.utility.JavaConstant.MethodHandle.ofSpecial;


public class JavaConstantMethodHandleTest {
    private static final String BAR = "bar";

    private static final String QUX = "qux";

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodHandleOfMethod() throws Exception {
        JavaConstant.MethodHandle methodHandle = JavaConstant.MethodHandle.of(JavaConstantMethodHandleTest.Foo.class.getDeclaredMethod(JavaConstantMethodHandleTest.BAR, Void.class));
        MatcherAssert.assertThat(methodHandle.getHandleType(), CoreMatchers.is(INVOKE_VIRTUAL));
        MatcherAssert.assertThat(methodHandle.getName(), CoreMatchers.is(JavaConstantMethodHandleTest.BAR));
        MatcherAssert.assertThat(methodHandle.getOwnerType(), CoreMatchers.is(((TypeDescription) (TypeDescription.ForLoadedType.of(JavaConstantMethodHandleTest.Foo.class)))));
        MatcherAssert.assertThat(methodHandle.getReturnType(), CoreMatchers.is(TypeDescription.VOID));
        MatcherAssert.assertThat(methodHandle.getParameterTypes(), CoreMatchers.is(((List<TypeDescription>) (new TypeList.ForLoadedTypes(Void.class)))));
        MatcherAssert.assertThat(methodHandle.getDescriptor(), CoreMatchers.is(new MethodDescription.ForLoadedMethod(JavaConstantMethodHandleTest.Foo.class.getDeclaredMethod(JavaConstantMethodHandleTest.BAR, Void.class)).getDescriptor()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodHandleOfMethodSpecialInvocation() throws Exception {
        JavaConstant.MethodHandle methodHandle = JavaConstant.MethodHandle.ofSpecial(JavaConstantMethodHandleTest.Foo.class.getDeclaredMethod(JavaConstantMethodHandleTest.BAR, Void.class), JavaConstantMethodHandleTest.Foo.class);
        MatcherAssert.assertThat(methodHandle.getHandleType(), CoreMatchers.is(INVOKE_SPECIAL));
        MatcherAssert.assertThat(methodHandle.getName(), CoreMatchers.is(JavaConstantMethodHandleTest.BAR));
        MatcherAssert.assertThat(methodHandle.getOwnerType(), CoreMatchers.is(((TypeDescription) (TypeDescription.ForLoadedType.of(JavaConstantMethodHandleTest.Foo.class)))));
        MatcherAssert.assertThat(methodHandle.getReturnType(), CoreMatchers.is(TypeDescription.VOID));
        MatcherAssert.assertThat(methodHandle.getParameterTypes(), CoreMatchers.is(((List<TypeDescription>) (new TypeList.ForLoadedTypes(Void.class)))));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodHandleOfStaticMethod() throws Exception {
        JavaConstant.MethodHandle methodHandle = JavaConstant.MethodHandle.of(JavaConstantMethodHandleTest.Foo.class.getDeclaredMethod(JavaConstantMethodHandleTest.QUX, Void.class));
        MatcherAssert.assertThat(methodHandle.getHandleType(), CoreMatchers.is(INVOKE_STATIC));
        MatcherAssert.assertThat(methodHandle.getName(), CoreMatchers.is(JavaConstantMethodHandleTest.QUX));
        MatcherAssert.assertThat(methodHandle.getOwnerType(), CoreMatchers.is(((TypeDescription) (TypeDescription.ForLoadedType.of(JavaConstantMethodHandleTest.Foo.class)))));
        MatcherAssert.assertThat(methodHandle.getReturnType(), CoreMatchers.is(TypeDescription.VOID));
        MatcherAssert.assertThat(methodHandle.getParameterTypes(), CoreMatchers.is(((List<TypeDescription>) (new TypeList.ForLoadedTypes(Void.class)))));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodHandleOfConstructor() throws Exception {
        JavaConstant.MethodHandle methodHandle = JavaConstant.MethodHandle.of(JavaConstantMethodHandleTest.Foo.class.getDeclaredConstructor(Void.class));
        MatcherAssert.assertThat(methodHandle.getHandleType(), CoreMatchers.is(INVOKE_SPECIAL_CONSTRUCTOR));
        MatcherAssert.assertThat(methodHandle.getName(), CoreMatchers.is(MethodDescription.CONSTRUCTOR_INTERNAL_NAME));
        MatcherAssert.assertThat(methodHandle.getOwnerType(), CoreMatchers.is(((TypeDescription) (TypeDescription.ForLoadedType.of(JavaConstantMethodHandleTest.Foo.class)))));
        MatcherAssert.assertThat(methodHandle.getReturnType(), CoreMatchers.is(TypeDescription.VOID));
        MatcherAssert.assertThat(methodHandle.getParameterTypes(), CoreMatchers.is(((List<TypeDescription>) (new TypeList.ForLoadedTypes(Void.class)))));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodHandleOfConstructorSpecialInvocation() throws Exception {
        JavaConstant.MethodHandle methodHandle = JavaConstant.MethodHandle.of(new MethodDescription.ForLoadedConstructor(JavaConstantMethodHandleTest.Foo.class.getDeclaredConstructor(Void.class)));
        MatcherAssert.assertThat(methodHandle.getHandleType(), CoreMatchers.is(INVOKE_SPECIAL_CONSTRUCTOR));
        MatcherAssert.assertThat(methodHandle.getName(), CoreMatchers.is(MethodDescription.CONSTRUCTOR_INTERNAL_NAME));
        MatcherAssert.assertThat(methodHandle.getOwnerType(), CoreMatchers.is(((TypeDescription) (TypeDescription.ForLoadedType.of(JavaConstantMethodHandleTest.Foo.class)))));
        MatcherAssert.assertThat(methodHandle.getReturnType(), CoreMatchers.is(TypeDescription.VOID));
        MatcherAssert.assertThat(methodHandle.getParameterTypes(), CoreMatchers.is(((List<TypeDescription>) (new TypeList.ForLoadedTypes(Void.class)))));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodHandleOfGetter() throws Exception {
        JavaConstant.MethodHandle methodHandle = ofGetter(JavaConstantMethodHandleTest.Foo.class.getDeclaredField(JavaConstantMethodHandleTest.BAR));
        MatcherAssert.assertThat(methodHandle.getHandleType(), CoreMatchers.is(GET_FIELD));
        MatcherAssert.assertThat(methodHandle.getName(), CoreMatchers.is(JavaConstantMethodHandleTest.BAR));
        MatcherAssert.assertThat(methodHandle.getOwnerType(), CoreMatchers.is(((TypeDescription) (TypeDescription.ForLoadedType.of(JavaConstantMethodHandleTest.Foo.class)))));
        MatcherAssert.assertThat(methodHandle.getReturnType(), CoreMatchers.is(((TypeDefinition) (TypeDescription.ForLoadedType.of(Void.class)))));
        MatcherAssert.assertThat(methodHandle.getParameterTypes(), CoreMatchers.is(Collections.<TypeDescription>emptyList()));
    }

    @Test
    public void testMethodHandleOfStaticGetter() throws Exception {
        JavaConstant.MethodHandle methodHandle = ofGetter(JavaConstantMethodHandleTest.Foo.class.getDeclaredField(JavaConstantMethodHandleTest.QUX));
        MatcherAssert.assertThat(methodHandle.getHandleType(), CoreMatchers.is(GET_STATIC_FIELD));
        MatcherAssert.assertThat(methodHandle.getName(), CoreMatchers.is(JavaConstantMethodHandleTest.QUX));
        MatcherAssert.assertThat(methodHandle.getOwnerType(), CoreMatchers.is(((TypeDescription) (TypeDescription.ForLoadedType.of(JavaConstantMethodHandleTest.Foo.class)))));
        MatcherAssert.assertThat(methodHandle.getReturnType(), CoreMatchers.is(((TypeDefinition) (TypeDescription.ForLoadedType.of(Void.class)))));
        MatcherAssert.assertThat(methodHandle.getParameterTypes(), CoreMatchers.is(Collections.<TypeDescription>emptyList()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodHandleOfSetter() throws Exception {
        JavaConstant.MethodHandle methodHandle = ofSetter(JavaConstantMethodHandleTest.Foo.class.getDeclaredField(JavaConstantMethodHandleTest.BAR));
        MatcherAssert.assertThat(methodHandle.getHandleType(), CoreMatchers.is(PUT_FIELD));
        MatcherAssert.assertThat(methodHandle.getName(), CoreMatchers.is(JavaConstantMethodHandleTest.BAR));
        MatcherAssert.assertThat(methodHandle.getOwnerType(), CoreMatchers.is(((TypeDescription) (TypeDescription.ForLoadedType.of(JavaConstantMethodHandleTest.Foo.class)))));
        MatcherAssert.assertThat(methodHandle.getReturnType(), CoreMatchers.is(TypeDescription.VOID));
        MatcherAssert.assertThat(methodHandle.getParameterTypes(), CoreMatchers.is(((List<TypeDescription>) (new TypeList.ForLoadedTypes(Void.class)))));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodHandleOfStaticSetter() throws Exception {
        JavaConstant.MethodHandle methodHandle = ofSetter(JavaConstantMethodHandleTest.Foo.class.getDeclaredField(JavaConstantMethodHandleTest.QUX));
        MatcherAssert.assertThat(methodHandle.getHandleType(), CoreMatchers.is(PUT_STATIC_FIELD));
        MatcherAssert.assertThat(methodHandle.getName(), CoreMatchers.is(JavaConstantMethodHandleTest.QUX));
        MatcherAssert.assertThat(methodHandle.getOwnerType(), CoreMatchers.is(((TypeDescription) (TypeDescription.ForLoadedType.of(JavaConstantMethodHandleTest.Foo.class)))));
        MatcherAssert.assertThat(methodHandle.getReturnType(), CoreMatchers.is(TypeDescription.VOID));
        MatcherAssert.assertThat(methodHandle.getParameterTypes(), CoreMatchers.is(((List<TypeDescription>) (new TypeList.ForLoadedTypes(Void.class)))));
    }

    @Test
    @SuppressWarnings("unchecked")
    @JavaVersionRule.Enforce(value = 7, hotSpot = 7)
    public void testMethodHandleOfLoadedMethodHandle() throws Exception {
        Method publicLookup = Class.forName("java.lang.invoke.MethodHandles").getDeclaredMethod("publicLookup");
        Object lookup = publicLookup.invoke(null);
        Method unreflected = Class.forName("java.lang.invoke.MethodHandles$Lookup").getDeclaredMethod("unreflect", Method.class);
        Object methodHandleLoaded = unreflected.invoke(lookup, JavaConstantMethodHandleTest.Foo.class.getDeclaredMethod(JavaConstantMethodHandleTest.BAR, Void.class));
        JavaConstant.MethodHandle methodHandle = JavaConstant.MethodHandle.ofLoaded(methodHandleLoaded);
        MatcherAssert.assertThat(methodHandle.getHandleType(), CoreMatchers.is(INVOKE_VIRTUAL));
        MatcherAssert.assertThat(methodHandle.getName(), CoreMatchers.is(JavaConstantMethodHandleTest.BAR));
        MatcherAssert.assertThat(methodHandle.getOwnerType(), CoreMatchers.is(((TypeDescription) (TypeDescription.ForLoadedType.of(JavaConstantMethodHandleTest.Foo.class)))));
        MatcherAssert.assertThat(methodHandle.getReturnType(), CoreMatchers.is(TypeDescription.VOID));
        MatcherAssert.assertThat(methodHandle.getParameterTypes(), CoreMatchers.is(((List<TypeDescription>) (new TypeList.ForLoadedTypes(Void.class)))));
    }

    @Test(expected = IllegalArgumentException.class)
    @JavaVersionRule.Enforce(value = 7, hotSpot = 7)
    public void testMethodHandleLoadedIllegal() throws Exception {
        JavaConstant.MethodHandle.ofLoaded(new Object());
    }

    @Test(expected = IllegalArgumentException.class)
    @JavaVersionRule.Enforce(value = 7, hotSpot = 7)
    public void testMethodHandleLoadedLookupIllegal() throws Exception {
        Method publicLookup = Class.forName("java.lang.invoke.MethodHandles").getDeclaredMethod("publicLookup");
        Object lookup = publicLookup.invoke(null);
        Method unreflect = Class.forName("java.lang.invoke.MethodHandles$Lookup").getDeclaredMethod("unreflect", Method.class);
        Object methodHandleLoaded = unreflect.invoke(lookup, JavaConstantMethodHandleTest.Foo.class.getDeclaredMethod(JavaConstantMethodHandleTest.BAR, Void.class));
        ofLoaded(methodHandleLoaded, new Object());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalParameterThrowsException() throws Exception {
        of((-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStaticMethodNotSpecial() throws Exception {
        MethodDescription.InDefinedShape methodDescription = Mockito.mock(MethodDescription.InDefinedShape.class);
        TypeDescription typeDescription = Mockito.mock(TypeDescription.class);
        Mockito.when(methodDescription.isStatic()).thenReturn(true);
        Mockito.when(methodDescription.isSpecializableFor(typeDescription)).thenReturn(true);
        ofSpecial(methodDescription, typeDescription);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAbstractMethodNotSpecial() throws Exception {
        MethodDescription.InDefinedShape methodDescription = Mockito.mock(MethodDescription.InDefinedShape.class);
        TypeDescription typeDescription = Mockito.mock(TypeDescription.class);
        Mockito.when(methodDescription.isAbstract()).thenReturn(true);
        Mockito.when(methodDescription.isSpecializableFor(typeDescription)).thenReturn(true);
        ofSpecial(methodDescription, typeDescription);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMethodNotSpecializable() throws Exception {
        MethodDescription.InDefinedShape methodDescription = Mockito.mock(MethodDescription.InDefinedShape.class);
        TypeDescription typeDescription = Mockito.mock(TypeDescription.class);
        Mockito.when(methodDescription.isSpecializableFor(typeDescription)).thenReturn(false);
        ofSpecial(methodDescription, typeDescription);
    }

    @SuppressWarnings("unused")
    public static class Foo {
        public static Void qux;

        public Void bar;

        public Foo(Void value) {
            /* empty */
        }

        public static void qux(Void value) {
            /* empty */
        }

        public void bar(Void value) {
            /* empty */
        }
    }
}

