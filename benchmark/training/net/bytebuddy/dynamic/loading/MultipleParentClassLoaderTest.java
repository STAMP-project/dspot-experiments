package net.bytebuddy.dynamic.loading;


import java.net.URL;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.IntegrationRule;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;


public class MultipleParentClassLoaderTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String BAZ = "baz";

    private static final String SCHEME = "http://";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Rule
    public MethodRule integrationRule = new IntegrationRule();

    @Mock
    private ClassLoader first;

    @Mock
    private ClassLoader second;

    private URL fooUrl;

    private URL barFirstUrl;

    private URL barSecondUrl;

    private URL quxUrl;

    @Test
    public void testSingleParentReturnsOriginal() throws Exception {
        MatcherAssert.assertThat(new MultipleParentClassLoader.Builder().append(ClassLoader.getSystemClassLoader(), ClassLoader.getSystemClassLoader()).build(), CoreMatchers.is(ClassLoader.getSystemClassLoader()));
    }

    @Test
    public void testSingleParentReturnsOriginalChained() throws Exception {
        MatcherAssert.assertThat(new MultipleParentClassLoader.Builder().append(ClassLoader.getSystemClassLoader()).append(ClassLoader.getSystemClassLoader()).build(), CoreMatchers.is(ClassLoader.getSystemClassLoader()));
    }

    @Test
    public void testClassLoaderFilter() throws Exception {
        MatcherAssert.assertThat(new MultipleParentClassLoader.Builder().append(ClassLoader.getSystemClassLoader(), null).filter(ElementMatchers.not(ElementMatchers.isBootstrapClassLoader())).build(), CoreMatchers.is(ClassLoader.getSystemClassLoader()));
    }

    @Test
    public void testMostSpecificInHierarchyFirst() throws Exception {
        MatcherAssert.assertThat(new MultipleParentClassLoader.Builder().appendMostSpecific(ClassLoader.getSystemClassLoader(), ClassLoader.getSystemClassLoader().getParent()).build(), CoreMatchers.is(ClassLoader.getSystemClassLoader()));
    }

    @Test
    public void testMostSpecificInHierarchyFirstChained() throws Exception {
        MatcherAssert.assertThat(appendMostSpecific(ClassLoader.getSystemClassLoader().getParent()).build(), CoreMatchers.is(ClassLoader.getSystemClassLoader()));
    }

    @Test
    public void testMostSpecificInHierarchyLast() throws Exception {
        MatcherAssert.assertThat(new MultipleParentClassLoader.Builder().appendMostSpecific(ClassLoader.getSystemClassLoader().getParent(), ClassLoader.getSystemClassLoader()).build(), CoreMatchers.is(ClassLoader.getSystemClassLoader()));
    }

    @Test
    public void testMostSpecificInHierarchyFirstLast() throws Exception {
        MatcherAssert.assertThat(appendMostSpecific(ClassLoader.getSystemClassLoader()).build(), CoreMatchers.is(ClassLoader.getSystemClassLoader()));
    }

    @Test
    public void testMostSpecificInHierarchyUnique() throws Exception {
        MatcherAssert.assertThat(new MultipleParentClassLoader.Builder().appendMostSpecific(first, second).build(), CoreMatchers.instanceOf(MultipleParentClassLoader.class));
    }

    @Test
    public void testMostSpecificInHierarchyFirstUnique() throws Exception {
        MatcherAssert.assertThat(appendMostSpecific(second).build(), CoreMatchers.instanceOf(MultipleParentClassLoader.class));
    }

    @Test
    public void testMultipleParentClassLoading() throws Exception {
        ClassLoader classLoader = new MultipleParentClassLoader.Builder().append(first, second, null).build();
        MatcherAssert.assertThat(classLoader.loadClass(MultipleParentClassLoaderTest.FOO), CoreMatchers.<Class<?>>is(MultipleParentClassLoaderTest.Foo.class));
        MatcherAssert.assertThat(classLoader.loadClass(MultipleParentClassLoaderTest.BAR), CoreMatchers.<Class<?>>is(MultipleParentClassLoaderTest.BarFirst.class));
        MatcherAssert.assertThat(classLoader.loadClass(MultipleParentClassLoaderTest.QUX), CoreMatchers.<Class<?>>is(MultipleParentClassLoaderTest.Qux.class));
        Mockito.verify(first).loadClass(MultipleParentClassLoaderTest.FOO);
        Mockito.verify(first).loadClass(MultipleParentClassLoaderTest.BAR);
        Mockito.verify(first).loadClass(MultipleParentClassLoaderTest.QUX);
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verify(second).loadClass(MultipleParentClassLoaderTest.QUX);
        Mockito.verifyNoMoreInteractions(second);
    }

    @Test(expected = ClassNotFoundException.class)
    public void testMultipleParentClassLoadingNotFound() throws Exception {
        new MultipleParentClassLoader.Builder().append(first, second, null).build().loadClass(MultipleParentClassLoaderTest.BAZ);
    }

    @Test
    @IntegrationRule.Enforce
    public void testMultipleParentURL() throws Exception {
        ClassLoader classLoader = new MultipleParentClassLoader.Builder().append(first, second, null).build();
        MatcherAssert.assertThat(classLoader.getResource(MultipleParentClassLoaderTest.FOO), CoreMatchers.is(fooUrl));
        MatcherAssert.assertThat(classLoader.getResource(MultipleParentClassLoaderTest.BAR), CoreMatchers.is(barFirstUrl));
        MatcherAssert.assertThat(classLoader.getResource(MultipleParentClassLoaderTest.QUX), CoreMatchers.is(quxUrl));
        Mockito.verify(first).getResource(MultipleParentClassLoaderTest.FOO);
        Mockito.verify(first).getResource(MultipleParentClassLoaderTest.BAR);
        Mockito.verify(first).getResource(MultipleParentClassLoaderTest.QUX);
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verify(second).getResource(MultipleParentClassLoaderTest.QUX);
        Mockito.verifyNoMoreInteractions(second);
    }

    @Test
    public void testMultipleParentURLNotFound() throws Exception {
        MatcherAssert.assertThat(new MultipleParentClassLoader.Builder().append(first, second, null).build().getResource(MultipleParentClassLoaderTest.BAZ), CoreMatchers.nullValue(URL.class));
    }

    @Test
    @IntegrationRule.Enforce
    public void testMultipleParentEnumerationURL() throws Exception {
        ClassLoader classLoader = new MultipleParentClassLoader.Builder().append(first, second, null).build();
        Enumeration<URL> foo = classLoader.getResources(MultipleParentClassLoaderTest.FOO);
        MatcherAssert.assertThat(foo.hasMoreElements(), CoreMatchers.is(true));
        MatcherAssert.assertThat(foo.nextElement(), CoreMatchers.is(fooUrl));
        MatcherAssert.assertThat(foo.hasMoreElements(), CoreMatchers.is(false));
        Enumeration<URL> bar = classLoader.getResources(MultipleParentClassLoaderTest.BAR);
        MatcherAssert.assertThat(bar.hasMoreElements(), CoreMatchers.is(true));
        MatcherAssert.assertThat(bar.nextElement(), CoreMatchers.is(barFirstUrl));
        MatcherAssert.assertThat(bar.hasMoreElements(), CoreMatchers.is(true));
        MatcherAssert.assertThat(bar.nextElement(), CoreMatchers.is(barSecondUrl));
        MatcherAssert.assertThat(bar.hasMoreElements(), CoreMatchers.is(false));
        Enumeration<URL> qux = classLoader.getResources(MultipleParentClassLoaderTest.QUX);
        MatcherAssert.assertThat(qux.hasMoreElements(), CoreMatchers.is(true));
        MatcherAssert.assertThat(qux.nextElement(), CoreMatchers.is(quxUrl));
        MatcherAssert.assertThat(qux.hasMoreElements(), CoreMatchers.is(false));
    }

    @Test(expected = NoSuchElementException.class)
    public void testMultipleParentEnumerationNotFound() throws Exception {
        ClassLoader classLoader = new MultipleParentClassLoader.Builder().append(first, second, null).build();
        Enumeration<URL> enumeration = classLoader.getResources(MultipleParentClassLoaderTest.BAZ);
        MatcherAssert.assertThat(enumeration.hasMoreElements(), CoreMatchers.is(false));
        enumeration.nextElement();
    }

    @Test
    public void testMultipleParentClassLoaderExplicitParentOnly() throws Exception {
        MatcherAssert.assertThat(new MultipleParentClassLoader.Builder().build(first), CoreMatchers.is(first));
    }

    @Test
    public void testMultipleParentClassLoaderExplicitParentPreIncluded() throws Exception {
        MatcherAssert.assertThat(new MultipleParentClassLoader.Builder().append(first).build(first), CoreMatchers.is(first));
    }

    @Test
    public void testMultipleParentClassLoaderExplicitParentPreIncludedWithOther() throws Exception {
        ClassLoader classLoader = new MultipleParentClassLoader.Builder().append(first, second).build(first);
        MatcherAssert.assertThat(classLoader, CoreMatchers.not(first));
        MatcherAssert.assertThat(classLoader, CoreMatchers.not(second));
        MatcherAssert.assertThat(classLoader.getParent(), CoreMatchers.is(first));
    }

    @Test
    public void testMultipleParentClassLoaderExplicitParentNotPreIncludedWithOther() throws Exception {
        ClassLoader classLoader = new MultipleParentClassLoader.Builder().append(second).build(first);
        MatcherAssert.assertThat(classLoader, CoreMatchers.not(second));
        MatcherAssert.assertThat(classLoader.getParent(), CoreMatchers.is(first));
    }

    /* empty */
    public static class Foo {}

    /* empty */
    public static class BarFirst {}

    /* empty */
    public static class BarSecond {}

    /* empty */
    public static class Qux {}

    private static class SingleElementEnumeration implements Enumeration<URL> {
        private URL element;

        public SingleElementEnumeration(URL element) {
            this.element = element;
        }

        public boolean hasMoreElements() {
            return (element) != null;
        }

        public URL nextElement() {
            if (!(hasMoreElements())) {
                throw new AssertionError();
            }
            try {
                return element;
            } finally {
                element = null;
            }
        }
    }
}

