package net.bytebuddy.dynamic.loading;


import java.net.URL;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import net.bytebuddy.test.utility.IntegrationRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;


public class ByteArrayClassLoaderSingletonEnumerationTest {
    @Rule
    public MethodRule integrationRule = new IntegrationRule();

    @Test
    @IntegrationRule.Enforce
    public void testIteration() throws Exception {
        URL url = new URL("file://foo");
        Enumeration<URL> enumeration = new ByteArrayClassLoader.SingletonEnumeration(url);
        MatcherAssert.assertThat(enumeration.hasMoreElements(), CoreMatchers.is(true));
        MatcherAssert.assertThat(enumeration.nextElement(), CoreMatchers.is(url));
        MatcherAssert.assertThat(enumeration.hasMoreElements(), CoreMatchers.is(false));
    }

    @Test(expected = NoSuchElementException.class)
    public void testSecondElementThrowsException() throws Exception {
        Enumeration<URL> enumeration = new ByteArrayClassLoader.SingletonEnumeration(new URL("file://foo"));
        enumeration.nextElement();
        enumeration.nextElement();
    }
}

