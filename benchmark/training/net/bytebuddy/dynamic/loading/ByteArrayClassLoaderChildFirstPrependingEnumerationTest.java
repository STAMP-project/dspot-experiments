package net.bytebuddy.dynamic.loading;


import java.net.URL;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Vector;
import net.bytebuddy.test.utility.IntegrationRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;


public class ByteArrayClassLoaderChildFirstPrependingEnumerationTest {
    @Rule
    public MethodRule integrationRule = new IntegrationRule();

    private URL first;

    private URL second;

    private URL third;

    @Test
    @IntegrationRule.Enforce
    public void testPrepending() throws Exception {
        Vector<URL> vector = new Vector<URL>();
        vector.add(second);
        vector.add(third);
        vector.add(first);
        Enumeration<URL> enumeration = new ByteArrayClassLoader.ChildFirst.PrependingEnumeration(first, vector.elements());
        MatcherAssert.assertThat(enumeration.hasMoreElements(), CoreMatchers.is(true));
        MatcherAssert.assertThat(enumeration.nextElement(), CoreMatchers.is(first));
        MatcherAssert.assertThat(enumeration.hasMoreElements(), CoreMatchers.is(true));
        MatcherAssert.assertThat(enumeration.nextElement(), CoreMatchers.is(second));
        MatcherAssert.assertThat(enumeration.hasMoreElements(), CoreMatchers.is(true));
        MatcherAssert.assertThat(enumeration.nextElement(), CoreMatchers.is(third));
        MatcherAssert.assertThat(enumeration.hasMoreElements(), CoreMatchers.is(false));
    }

    @Test(expected = NoSuchElementException.class)
    public void testNextElementThrowsException() throws Exception {
        Vector<URL> vector = new Vector<URL>();
        vector.add(second);
        vector.add(third);
        vector.add(first);
        Enumeration<URL> enumeration = new ByteArrayClassLoader.ChildFirst.PrependingEnumeration(first, vector.elements());
        enumeration.nextElement();
        enumeration.nextElement();
        enumeration.nextElement();
        enumeration.nextElement();
    }
}

