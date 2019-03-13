package net.bytebuddy.utility;


import java.util.Collections;
import java.util.List;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.test.utility.JavaVersionRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import static net.bytebuddy.utility.JavaConstant.MethodType.ofConstant;
import static net.bytebuddy.utility.JavaConstant.MethodType.ofGetter;
import static net.bytebuddy.utility.JavaConstant.MethodType.ofLoaded;
import static net.bytebuddy.utility.JavaConstant.MethodType.ofSetter;


public class JavaConstantMethodTypeTest {
    private static final String BAR = "bar";

    private static final String QUX = "qux";

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodTypeOfLoadedType() throws Exception {
        JavaConstant.MethodType methodType = JavaConstant.MethodType.of(void.class, JavaConstantMethodTypeTest.Foo.class);
        MatcherAssert.assertThat(methodType.getReturnType(), CoreMatchers.is(TypeDescription.VOID));
        MatcherAssert.assertThat(methodType.getParameterTypes(), CoreMatchers.is(((List<TypeDescription>) (new TypeList.ForLoadedTypes(JavaConstantMethodTypeTest.Foo.class)))));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodTypeOfMethod() throws Exception {
        JavaConstant.MethodType methodType = JavaConstant.MethodType.of(JavaConstantMethodTypeTest.Foo.class.getDeclaredMethod(JavaConstantMethodTypeTest.BAR, Void.class));
        MatcherAssert.assertThat(methodType.getReturnType(), CoreMatchers.is(TypeDescription.VOID));
        MatcherAssert.assertThat(methodType.getParameterTypes(), CoreMatchers.is(((List<TypeDescription>) (new TypeList.ForLoadedTypes(Void.class)))));
        MatcherAssert.assertThat(methodType.getDescriptor(), CoreMatchers.is(new MethodDescription.ForLoadedMethod(JavaConstantMethodTypeTest.Foo.class.getDeclaredMethod(JavaConstantMethodTypeTest.BAR, Void.class)).getDescriptor()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodTypeOfStaticMethod() throws Exception {
        JavaConstant.MethodType methodType = JavaConstant.MethodType.of(JavaConstantMethodTypeTest.Foo.class.getDeclaredMethod(JavaConstantMethodTypeTest.QUX, Void.class));
        MatcherAssert.assertThat(methodType.getReturnType(), CoreMatchers.is(TypeDescription.VOID));
        MatcherAssert.assertThat(methodType.getParameterTypes(), CoreMatchers.is(((List<TypeDescription>) (new TypeList.ForLoadedTypes(Void.class)))));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodTypeOfConstructor() throws Exception {
        JavaConstant.MethodType methodType = JavaConstant.MethodType.of(JavaConstantMethodTypeTest.Foo.class.getDeclaredConstructor(Void.class));
        MatcherAssert.assertThat(methodType.getReturnType(), CoreMatchers.is(TypeDescription.VOID));
        MatcherAssert.assertThat(methodType.getParameterTypes(), CoreMatchers.is(((List<TypeDescription>) (new TypeList.ForLoadedTypes(Void.class)))));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodTypeOfGetter() throws Exception {
        JavaConstant.MethodType methodType = ofGetter(JavaConstantMethodTypeTest.Foo.class.getDeclaredField(JavaConstantMethodTypeTest.BAR));
        MatcherAssert.assertThat(methodType.getReturnType(), CoreMatchers.is(((TypeDescription) (of(Void.class)))));
        MatcherAssert.assertThat(methodType.getParameterTypes(), CoreMatchers.is(Collections.<TypeDescription>emptyList()));
    }

    @Test
    public void testMethodTypeOfStaticGetter() throws Exception {
        JavaConstant.MethodType methodType = ofGetter(JavaConstantMethodTypeTest.Foo.class.getDeclaredField(JavaConstantMethodTypeTest.QUX));
        MatcherAssert.assertThat(methodType.getReturnType(), CoreMatchers.is(((TypeDescription) (of(Void.class)))));
        MatcherAssert.assertThat(methodType.getParameterTypes(), CoreMatchers.is(Collections.<TypeDescription>emptyList()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodTypeOfSetter() throws Exception {
        JavaConstant.MethodType methodType = ofSetter(JavaConstantMethodTypeTest.Foo.class.getDeclaredField(JavaConstantMethodTypeTest.BAR));
        MatcherAssert.assertThat(methodType.getReturnType(), CoreMatchers.is(TypeDescription.VOID));
        MatcherAssert.assertThat(methodType.getParameterTypes(), CoreMatchers.is(((List<TypeDescription>) (new TypeList.ForLoadedTypes(Void.class)))));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodTypeOfStaticSetter() throws Exception {
        JavaConstant.MethodType methodType = ofSetter(JavaConstantMethodTypeTest.Foo.class.getDeclaredField(JavaConstantMethodTypeTest.QUX));
        MatcherAssert.assertThat(methodType.getReturnType(), CoreMatchers.is(TypeDescription.VOID));
        MatcherAssert.assertThat(methodType.getParameterTypes(), CoreMatchers.is(((List<TypeDescription>) (new TypeList.ForLoadedTypes(Void.class)))));
    }

    @Test
    public void testMethodTypeOfConstant() throws Exception {
        JavaConstant.MethodType methodType = ofConstant(new JavaConstantMethodTypeTest.Foo(null));
        MatcherAssert.assertThat(methodType.getReturnType(), CoreMatchers.is(((TypeDescription) (of(JavaConstantMethodTypeTest.Foo.class)))));
        MatcherAssert.assertThat(methodType.getParameterTypes(), CoreMatchers.is(Collections.<TypeDescription>emptyList()));
    }

    @Test
    @SuppressWarnings("unchecked")
    @JavaVersionRule.Enforce(7)
    public void testMethodTypeOfLoadedMethodType() throws Exception {
        Object loadedMethodType = JavaType.METHOD_TYPE.load().getDeclaredMethod("methodType", Class.class, Class[].class).invoke(null, void.class, new Class<?>[]{ Object.class });
        JavaConstant.MethodType methodType = ofLoaded(loadedMethodType);
        MatcherAssert.assertThat(methodType.getReturnType(), CoreMatchers.is(TypeDescription.VOID));
        MatcherAssert.assertThat(methodType.getParameterTypes(), CoreMatchers.is(((List<TypeDescription>) (new TypeList.ForLoadedTypes(Object.class)))));
    }

    @SuppressWarnings("unused")
    public static class Foo {
        static Void qux;

        Void bar;

        Foo(Void value) {
            /* empty */
        }

        static void qux(Void value) {
            /* empty */
        }

        void bar(Void value) {
            /* empty */
        }
    }
}

