package net.bytebuddy.dynamic.scaffold;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.dynamic.scaffold.MethodGraph.Node.Sort.UNRESOLVED;
import static net.bytebuddy.dynamic.scaffold.MethodGraph.Node.Unresolved.INSTANCE;


public class MethodGraphNodeUnresolvedTest {
    @Test
    public void testSort() throws Exception {
        MatcherAssert.assertThat(INSTANCE.getSort(), CoreMatchers.is(UNRESOLVED));
    }

    @Test(expected = IllegalStateException.class)
    public void testBridgesThrowsException() throws Exception {
        INSTANCE.getMethodTypes();
    }

    @Test(expected = IllegalStateException.class)
    public void testRepresentativeThrowsException() throws Exception {
        INSTANCE.getRepresentative();
    }
}

