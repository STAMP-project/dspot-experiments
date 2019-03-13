package org.junit.tests.running.classes.parent;


import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.FrameworkMethod;


public class ParentRunnerClassLoaderTest {
    @Test
    public void testClassRuleAccessToClassInAnotherClassLoader() throws Exception {
        Class<?> testClassWithOwnClassLoader = wrapToClassLoader(TestWithClassRule.class);
        runTestWithParentRunner(testClassWithOwnClassLoader);
        Field fieldWithReference = testClassWithOwnClassLoader.getDeclaredField("applyTestClass");
        Class<?> usedClass = ((Class<?>) (fieldWithReference.get(null)));
        Assert.assertEquals(("JUnitRunner can be located in own classLoader, so, " + ("Class.forName org.junit.runner.Description.getTestClass can not see " + "in current classloader by execute Class.forName")), testClassWithOwnClassLoader, usedClass);
    }

    @Test
    public void testDescriptionContainCorrectTestClass() throws Exception {
        Class<?> testClassWithOwnClassLoader = wrapToClassLoader(TestWithClassRule.class);
        ParentRunner<?> runner = new BlockJUnit4ClassRunner(testClassWithOwnClassLoader);
        Description description = runner.getDescription();
        Assert.assertEquals(("ParentRunner accept already instantiate Class<?> with tests, if we lost it instance, and will " + ("use Class.forName we can not find test class again, because tests can be " + "located in different ClassLoader")), description.getTestClass(), testClassWithOwnClassLoader);
    }

    @Test
    public void testBackwardCompatibilityWithOverrideGetName() throws Exception {
        final Class<TestWithClassRule> originalTestClass = TestWithClassRule.class;
        final Class<?> waitClass = ParentRunnerClassLoaderTest.class;
        ParentRunner<FrameworkMethod> subParentRunner = new BlockJUnit4ClassRunner(originalTestClass) {
            @Override
            protected String getName() {
                return waitClass.getName();
            }
        };
        Description description = subParentRunner.getDescription();
        Class<?> result = description.getTestClass();
        Assert.assertEquals(("Subclass of ParentRunner can override getName method and specify another test class for run, " + "we should  maintain backwards compatibility with JUnit 4.12"), waitClass, result);
    }

    private static class VisibleClassLoader extends URLClassLoader {
        public VisibleClassLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }

        // just making public
        @Override
        public Class<?> findClass(String name) throws ClassNotFoundException {
            return super.findClass(name);
        }
    }
}

