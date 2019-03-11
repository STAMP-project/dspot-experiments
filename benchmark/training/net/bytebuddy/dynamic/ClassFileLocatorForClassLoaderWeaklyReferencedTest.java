package net.bytebuddy.dynamic;


import java.io.ByteArrayInputStream;
import java.io.Closeable;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.WeaklyReferenced.of;


public class ClassFileLocatorForClassLoaderWeaklyReferencedTest {
    private static final String FOOBAR = "foo/bar";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private ClassFileLocatorForClassLoaderWeaklyReferencedTest.ClosableClassLoader classLoader;

    @Test
    public void testCreation() throws Exception {
        MatcherAssert.assertThat(of(classLoader), FieldByFieldComparison.hasPrototype(((ClassFileLocator) (new ClassFileLocator.ForClassLoader.WeaklyReferenced(classLoader)))));
        MatcherAssert.assertThat(of(null), FieldByFieldComparison.hasPrototype(ofBootLoader()));
        MatcherAssert.assertThat(of(ClassLoader.getSystemClassLoader()), FieldByFieldComparison.hasPrototype(ofSystemLoader()));
        MatcherAssert.assertThat(of(ClassLoader.getSystemClassLoader().getParent()), FieldByFieldComparison.hasPrototype(ofPlatformLoader()));
    }

    @Test
    public void testLocatable() throws Exception {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[]{ 1, 2, 3 });
        Mockito.when(classLoader.getResourceAsStream(((ClassFileLocatorForClassLoaderWeaklyReferencedTest.FOOBAR) + ".class"))).thenReturn(inputStream);
        ClassFileLocator.Resolution resolution = new ClassFileLocator.ForClassLoader.WeaklyReferenced(classLoader).locate(ClassFileLocatorForClassLoaderWeaklyReferencedTest.FOOBAR);
        MatcherAssert.assertThat(resolution.isResolved(), CoreMatchers.is(true));
        MatcherAssert.assertThat(resolution.resolve(), CoreMatchers.is(new byte[]{ 1, 2, 3 }));
        Mockito.verify(classLoader).getResourceAsStream(((ClassFileLocatorForClassLoaderWeaklyReferencedTest.FOOBAR) + ".class"));
        Mockito.verifyNoMoreInteractions(classLoader);
    }

    @Test(expected = IllegalStateException.class)
    public void testNonLocatable() throws Exception {
        ClassFileLocator.Resolution resolution = new ClassFileLocator.ForClassLoader.WeaklyReferenced(classLoader).locate(ClassFileLocatorForClassLoaderWeaklyReferencedTest.FOOBAR);
        MatcherAssert.assertThat(resolution.isResolved(), CoreMatchers.is(false));
        Mockito.verify(classLoader).getResourceAsStream(((ClassFileLocatorForClassLoaderWeaklyReferencedTest.FOOBAR) + ".class"));
        Mockito.verifyNoMoreInteractions(classLoader);
        resolution.resolve();
        Assert.fail();
    }

    @Test
    public void testClose() throws Exception {
        of(classLoader).close();
        Mockito.verifyZeroInteractions(classLoader);
    }

    /* empty */
    private abstract static class ClosableClassLoader extends ClassLoader implements Closeable {}
}

