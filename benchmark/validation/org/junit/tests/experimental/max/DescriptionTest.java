package org.junit.tests.experimental.max;


import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;


public class DescriptionTest {
    @Test
    public void parseClass_whenCantParse() {
        Assert.assertNull(Description.TEST_MECHANISM.getTestClass());
    }

    @Test
    public void parseMethod_whenCantParse() {
        Assert.assertNull(Description.TEST_MECHANISM.getMethodName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createSuiteDescription_whenZeroLength() {
        Description.createSuiteDescription("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createSuiteDescription_whenNull() {
        Description.createSuiteDescription(((String) (null)));
    }

    @Test
    public void parseClassAndMethodNoAnnotations() throws Exception {
        Description description = Description.createTestDescription(Description.class, "aTestMethod");
        Assert.assertThat(description.getClassName(), CoreMatchers.equalTo("org.junit.runner.Description"));
        Assert.assertThat(description.getMethodName(), CoreMatchers.equalTo("aTestMethod"));
        Assert.assertThat(description.getAnnotations().size(), CoreMatchers.equalTo(0));
    }

    @Test
    public void parseClassAndMethodWithAnnotations() throws Exception {
        Annotation[] annotations = DescriptionTest.class.getMethod("parseClassAndMethodWithAnnotations").getDeclaredAnnotations();
        Description description = Description.createTestDescription(Description.class, "aTestMethod", annotations);
        Assert.assertThat(description.getClassName(), CoreMatchers.equalTo("org.junit.runner.Description"));
        Assert.assertThat(description.getMethodName(), CoreMatchers.equalTo("aTestMethod"));
        Assert.assertThat(description.getAnnotations().size(), CoreMatchers.equalTo(1));
    }

    @Test
    public void parseClassNameAndMethodUniqueId() throws Exception {
        Description description = Description.createTestDescription("not a class name", "aTestMethod", 123);
        Assert.assertThat(description.getClassName(), CoreMatchers.equalTo("not a class name"));
        Assert.assertThat(description.getMethodName(), CoreMatchers.equalTo("aTestMethod"));
        Assert.assertThat(description.getAnnotations().size(), CoreMatchers.equalTo(0));
    }

    @Test
    public void sameNamesButDifferentUniqueIdAreNotEqual() throws Exception {
        Assert.assertThat(Description.createTestDescription("not a class name", "aTestMethod", 1), CoreMatchers.not(CoreMatchers.equalTo(Description.createTestDescription("not a class name", "aTestMethod", 2))));
    }

    @Test
    public void usesPassedInClassObject() throws Exception {
        class URLClassLoader2 extends URLClassLoader {
            URLClassLoader2(URL... urls) {
                super(urls);
            }

            // just making public
            @Override
            public Class<?> findClass(String name) throws ClassNotFoundException {
                return super.findClass(name);
            }
        }
        URL classpath = DescriptionTest.Sweet.class.getProtectionDomain().getCodeSource().getLocation();
        URLClassLoader2 loader = new URLClassLoader2(classpath);
        Class<?> clazz = loader.findClass(DescriptionTest.Sweet.class.getName());
        Assert.assertEquals(loader, clazz.getClassLoader());
        Description d = Description.createSuiteDescription(clazz);
        Assert.assertEquals(clazz, d.getTestClass());
        Assert.assertNull(d.getMethodName());
        Assert.assertEquals(1, d.getAnnotations().size());
        Assert.assertEquals(Ignore.class, d.getAnnotations().iterator().next().annotationType());
        d = Description.createTestDescription(clazz, "tessed");
        Assert.assertEquals(clazz, d.getTestClass());
        Assert.assertEquals("tessed", d.getMethodName());
        Assert.assertEquals(0, d.getAnnotations().size());
        d = Description.createTestDescription(clazz, "tessed", clazz.getMethod("tessed").getAnnotations());
        Assert.assertEquals(clazz, d.getTestClass());
        Assert.assertEquals("tessed", d.getMethodName());
        Assert.assertEquals(1, d.getAnnotations().size());
        Assert.assertEquals(Test.class, d.getAnnotations().iterator().next().annotationType());
        d = d.childlessCopy();
        Assert.assertEquals(clazz, d.getTestClass());
        Assert.assertEquals("tessed", d.getMethodName());
        Assert.assertEquals(1, d.getAnnotations().size());
        Assert.assertEquals(Test.class, d.getAnnotations().iterator().next().annotationType());
    }

    @Ignore
    private static class Sweet {
        @Test
        public void tessed() {
        }
    }
}

