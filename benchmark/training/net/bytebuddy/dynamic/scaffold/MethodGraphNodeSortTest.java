package net.bytebuddy.dynamic.scaffold;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.dynamic.scaffold.MethodGraph.Node.Sort.AMBIGUOUS;
import static net.bytebuddy.dynamic.scaffold.MethodGraph.Node.Sort.RESOLVED;
import static net.bytebuddy.dynamic.scaffold.MethodGraph.Node.Sort.UNRESOLVED;
import static net.bytebuddy.dynamic.scaffold.MethodGraph.Node.Sort.VISIBLE;


public class MethodGraphNodeSortTest {
    @Test
    public void testSortVisible() throws Exception {
        MatcherAssert.assertThat(VISIBLE.isResolved(), CoreMatchers.is(true));
        MatcherAssert.assertThat(VISIBLE.isUnique(), CoreMatchers.is(true));
        MatcherAssert.assertThat(VISIBLE.isMadeVisible(), CoreMatchers.is(true));
    }

    @Test
    public void testSortResolved() throws Exception {
        MatcherAssert.assertThat(RESOLVED.isResolved(), CoreMatchers.is(true));
        MatcherAssert.assertThat(RESOLVED.isUnique(), CoreMatchers.is(true));
        MatcherAssert.assertThat(RESOLVED.isMadeVisible(), CoreMatchers.is(false));
    }

    @Test
    public void testAmbiguous() throws Exception {
        MatcherAssert.assertThat(AMBIGUOUS.isResolved(), CoreMatchers.is(true));
        MatcherAssert.assertThat(AMBIGUOUS.isUnique(), CoreMatchers.is(false));
        MatcherAssert.assertThat(AMBIGUOUS.isMadeVisible(), CoreMatchers.is(false));
    }

    @Test
    public void testUnresolved() throws Exception {
        MatcherAssert.assertThat(UNRESOLVED.isResolved(), CoreMatchers.is(false));
        MatcherAssert.assertThat(UNRESOLVED.isUnique(), CoreMatchers.is(false));
        MatcherAssert.assertThat(UNRESOLVED.isMadeVisible(), CoreMatchers.is(false));
    }
}

