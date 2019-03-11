package net.bytebuddy.matcher;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class ClassLoaderParentMatcherTest extends AbstractElementMatcherTest<ClassLoaderParentMatcher<?>> {
    private ClassLoader parent;

    private ClassLoader child;

    private ClassLoader noChild;

    @SuppressWarnings("unchecked")
    public ClassLoaderParentMatcherTest() {
        super(((Class<ClassLoaderParentMatcher<?>>) ((Object) (ClassLoaderParentMatcher.class))), "isParentOf");
    }

    @Test
    public void testMatch() throws Exception {
        MatcherAssert.assertThat(new ClassLoaderParentMatcher<ClassLoader>(child).matches(parent), CoreMatchers.is(true));
    }

    @Test
    public void testMatchBootstrap() throws Exception {
        MatcherAssert.assertThat(new ClassLoaderParentMatcher<ClassLoader>(child).matches(null), CoreMatchers.is(true));
    }

    @Test
    public void testNoMatch() throws Exception {
        MatcherAssert.assertThat(new ClassLoaderParentMatcher<ClassLoader>(noChild).matches(parent), CoreMatchers.is(false));
    }
}

