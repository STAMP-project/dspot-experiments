package org.junit.runners.model;


import java.lang.annotation.Annotation;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;


public class TestClassTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    public static class TwoConstructors {
        public TwoConstructors() {
        }

        public TwoConstructors(int x) {
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void complainIfMultipleConstructors() {
        new TestClass(TestClassTest.TwoConstructors.class);
    }

    public static class SuperclassWithField {
        @Rule
        public TestRule x;
    }

    public static class SubclassWithField extends TestClassTest.SuperclassWithField {
        @Rule
        public TestRule x;
    }

    @Test
    public void fieldsOnSubclassesDoNotShadowSuperclasses() {
        MatcherAssert.assertThat(new TestClass(TestClassTest.SubclassWithField.class).getAnnotatedFields(Rule.class).size(), is(2));
    }

    public static class OuterClass {
        public class NonStaticInnerClass {}
    }

    @Test
    public void identifyNonStaticInnerClass() {
        MatcherAssert.assertThat(new TestClass(TestClassTest.OuterClass.NonStaticInnerClass.class).isANonStaticInnerClass(), is(true));
    }

    public static class OuterClass2 {
        public static class StaticInnerClass {}
    }

    @Test
    public void dontMarkStaticInnerClassAsNonStatic() {
        MatcherAssert.assertThat(new TestClass(TestClassTest.OuterClass2.StaticInnerClass.class).isANonStaticInnerClass(), is(false));
    }

    public static class SimpleClass {}

    @Test
    public void dontMarkNonInnerClassAsInnerClass() {
        MatcherAssert.assertThat(new TestClass(TestClassTest.SimpleClass.class).isANonStaticInnerClass(), is(false));
    }

    public static class FieldAnnotated {
        @Rule
        public String fieldC = "andromeda";

        @Rule
        public boolean fieldA;

        @Rule
        public boolean fieldB;
    }

    @Test
    public void providesAnnotatedFieldsSortedByName() {
        TestClass tc = new TestClass(TestClassTest.FieldAnnotated.class);
        List<FrameworkField> annotatedFields = tc.getAnnotatedFields();
        MatcherAssert.assertThat("Wrong number of annotated fields.", annotatedFields.size(), is(3));
        MatcherAssert.assertThat("First annotated field is wrong.", annotatedFields.iterator().next().getName(), is("fieldA"));
    }

    @Test
    public void annotatedFieldValues() {
        TestClass tc = new TestClass(TestClassTest.FieldAnnotated.class);
        List<String> values = tc.getAnnotatedFieldValues(new TestClassTest.FieldAnnotated(), Rule.class, String.class);
        MatcherAssert.assertThat(values, hasItem("andromeda"));
        MatcherAssert.assertThat(values.size(), is(1));
    }

    public static class MethodsAnnotated {
        @Ignore
        @Test
        public int methodC() {
            return 0;
        }

        @Ignore
        @Test
        public String methodA() {
            return "jupiter";
        }

        @Ignore
        @Test
        public int methodB() {
            return 0;
        }

        public int methodWithoutAnnotation() {
            return 0;
        }
    }

    @Test
    public void providesAnnotatedMethodsSortedByName() {
        TestClass tc = new TestClass(TestClassTest.MethodsAnnotated.class);
        List<FrameworkMethod> annotatedMethods = tc.getAnnotatedMethods();
        List<String> methodNames = extractNames(annotatedMethods);
        MatcherAssert.assertThat(methodNames.indexOf("methodA"), lessThan(methodNames.indexOf("methodB")));
    }

    @Test
    public void getAnnotatedMethodsDoesNotReturnMethodWithoutAnnotation() {
        TestClass tc = new TestClass(TestClassTest.MethodsAnnotated.class);
        List<FrameworkMethod> annotatedMethods = tc.getAnnotatedMethods();
        List<String> methodNames = extractNames(annotatedMethods);
        MatcherAssert.assertThat(methodNames, not(hasItem("methodWithoutAnnotation")));
    }

    @Test
    public void annotatedMethodValues() {
        TestClass tc = new TestClass(TestClassTest.MethodsAnnotated.class);
        List<String> values = tc.getAnnotatedMethodValues(new TestClassTest.MethodsAnnotated(), Ignore.class, String.class);
        MatcherAssert.assertThat(values, hasItem("jupiter"));
        MatcherAssert.assertThat(values.size(), is(1));
    }

    @Test
    public void isEqualToTestClassThatWrapsSameJavaClass() {
        TestClass testClass = new TestClass(TestClassTest.DummyClass.class);
        TestClass testClassThatWrapsSameJavaClass = new TestClass(TestClassTest.DummyClass.class);
        Assert.assertTrue(testClass.equals(testClassThatWrapsSameJavaClass));
    }

    @Test
    public void isEqualToTestClassThatWrapsNoJavaClassToo() {
        TestClass testClass = new TestClass(null);
        TestClass testClassThatWrapsNoJavaClassToo = new TestClass(null);
        Assert.assertTrue(testClass.equals(testClassThatWrapsNoJavaClassToo));
    }

    @Test
    public void isNotEqualToTestClassThatWrapsADifferentJavaClass() {
        TestClass testClass = new TestClass(TestClassTest.DummyClass.class);
        TestClass testClassThatWrapsADifferentJavaClass = new TestClass(TestClassTest.AnotherDummyClass.class);
        Assert.assertFalse(testClass.equals(testClassThatWrapsADifferentJavaClass));
    }

    @Test
    public void isNotEqualToNull() {
        TestClass testClass = new TestClass(TestClassTest.DummyClass.class);
        Assert.assertFalse(testClass.equals(null));
    }

    private static class DummyClass {}

    private static class AnotherDummyClass {}

    @Test
    public void hasSameHashCodeAsTestClassThatWrapsSameJavaClass() {
        TestClass testClass = new TestClass(TestClassTest.DummyClass.class);
        TestClass testClassThatWrapsSameJavaClass = new TestClass(TestClassTest.DummyClass.class);
        Assert.assertEquals(testClass.hashCode(), testClassThatWrapsSameJavaClass.hashCode());
    }

    @Test
    public void hasHashCodeWithoutJavaClass() {
        TestClass testClass = new TestClass(null);
        testClass.hashCode();
        // everything is fine if no exception is thrown.
    }

    public static class PublicClass {}

    @Test
    public void identifiesPublicModifier() {
        TestClass tc = new TestClass(TestClassTest.PublicClass.class);
        Assert.assertEquals("Wrong flag 'public',", true, tc.isPublic());
    }

    static class NonPublicClass {}

    @Test
    public void identifiesNonPublicModifier() {
        TestClass tc = new TestClass(TestClassTest.NonPublicClass.class);
        Assert.assertEquals("Wrong flag 'public',", false, tc.isPublic());
    }

    @Ignore
    static class AnnotatedClass {}

    @Test
    public void presentAnnotationIsAvailable() {
        TestClass tc = new TestClass(TestClassTest.AnnotatedClass.class);
        Annotation annotation = tc.getAnnotation(Ignore.class);
        Assert.assertTrue(Ignore.class.isAssignableFrom(annotation.getClass()));
    }

    @Test
    public void missingAnnotationIsNotAvailable() {
        TestClass tc = new TestClass(TestClassTest.AnnotatedClass.class);
        Annotation annotation = tc.getAnnotation(RunWith.class);
        MatcherAssert.assertThat(annotation, is(nullValue()));
    }
}

