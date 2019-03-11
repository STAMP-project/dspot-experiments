package net.bytebuddy.description.type;


import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.description.type.TypeDescription.Generic.OBJECT;


public class TypeDescriptionForPackageDescriptionTest {
    private static final String FOO = "foo";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    private TypeDescription typeDescription;

    @Mock
    private PackageDescription packageDescription;

    @Test
    public void testName() throws Exception {
        Mockito.when(packageDescription.getName()).thenReturn(TypeDescriptionForPackageDescriptionTest.FOO);
        MatcherAssert.assertThat(typeDescription.getName(), CoreMatchers.is((((TypeDescriptionForPackageDescriptionTest.FOO) + ".") + (PackageDescription.PACKAGE_CLASS_NAME))));
    }

    @Test
    public void testModifiers() throws Exception {
        MatcherAssert.assertThat(typeDescription.getModifiers(), CoreMatchers.is(PackageDescription.PACKAGE_MODIFIERS));
    }

    @Test
    public void testInterfaces() throws Exception {
        MatcherAssert.assertThat(typeDescription.getInterfaces().size(), CoreMatchers.is(0));
    }

    @Test
    public void testAnnotations() throws Exception {
        AnnotationList annotationList = Mockito.mock(AnnotationList.class);
        Mockito.when(packageDescription.getDeclaredAnnotations()).thenReturn(annotationList);
        MatcherAssert.assertThat(typeDescription.getDeclaredAnnotations(), CoreMatchers.is(annotationList));
    }

    @Test
    public void testTypeVariables() throws Exception {
        MatcherAssert.assertThat(typeDescription.getTypeVariables().size(), CoreMatchers.is(0));
    }

    @Test
    public void testFields() throws Exception {
        MatcherAssert.assertThat(typeDescription.getDeclaredFields().size(), CoreMatchers.is(0));
    }

    @Test
    public void testMethods() throws Exception {
        MatcherAssert.assertThat(typeDescription.getDeclaredMethods().size(), CoreMatchers.is(0));
    }

    @Test
    public void testPackage() throws Exception {
        MatcherAssert.assertThat(typeDescription.getPackage(), CoreMatchers.is(packageDescription));
    }

    @Test
    public void testSuperClass() throws Exception {
        MatcherAssert.assertThat(typeDescription.getSuperClass(), CoreMatchers.is(OBJECT));
    }
}

