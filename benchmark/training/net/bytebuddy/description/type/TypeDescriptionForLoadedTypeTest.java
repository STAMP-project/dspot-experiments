package net.bytebuddy.description.type;


import net.bytebuddy.dynamic.loading.ByteArrayClassLoader;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.test.utility.JavaVersionRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import static net.bytebuddy.description.type.TypeDescription.ForLoadedType.getName;


public class TypeDescriptionForLoadedTypeTest extends AbstractTypeDescriptionTest {
    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Test
    @JavaVersionRule.Enforce(9)
    public void testTypeAnnotationOwnerType() throws Exception {
        super.testTypeAnnotationOwnerType();
    }

    @Test
    @JavaVersionRule.Enforce(9)
    public void testGenericNestedTypeAnnotationReceiverTypeOnConstructor() throws Exception {
        super.testGenericNestedTypeAnnotationReceiverTypeOnConstructor();
    }

    @Test
    @JavaVersionRule.Enforce(9)
    public void testTypeAnnotationNonGenericInnerType() throws Exception {
        super.testTypeAnnotationNonGenericInnerType();
    }

    @Test
    public void testNameEqualityNonAnonymous() throws Exception {
        MatcherAssert.assertThat(getName(Object.class), CoreMatchers.is(Object.class.getName()));
    }

    @Test
    public void testLazyResolution() throws Exception {
        ClassLoader classLoader = new ByteArrayClassLoader(ClassLoadingStrategy.BOOTSTRAP_LOADER, readToNames(TypeDescriptionForLoadedTypeTest.Foo.class));
        of(classLoader.loadClass(TypeDescriptionForLoadedTypeTest.Foo.class.getName()));
    }

    public static class Foo {
        public TypeDescriptionForLoadedTypeTest.Bar bar() {
            return new TypeDescriptionForLoadedTypeTest.Bar();
        }
    }

    /* empty */
    public static class Bar {}
}

