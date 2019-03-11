package net.bytebuddy.dynamic.scaffold;


import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;

import static net.bytebuddy.dynamic.scaffold.MethodGraph.Empty.INSTANCE;
import static net.bytebuddy.dynamic.scaffold.MethodGraph.Node.Sort.UNRESOLVED;


public class MethodGraphEmptyTest {
    @Test
    public void testNode() throws Exception {
        MatcherAssert.assertThat(INSTANCE.locate(Mockito.mock(MethodDescription.SignatureToken.class)).getSort(), CoreMatchers.is(UNRESOLVED));
    }

    @Test
    public void testListNode() throws Exception {
        MatcherAssert.assertThat(INSTANCE.listNodes().size(), CoreMatchers.is(0));
    }

    @Test
    public void testSuperGraph() throws Exception {
        MatcherAssert.assertThat(INSTANCE.getSuperClassGraph(), CoreMatchers.is(((MethodGraph) (INSTANCE))));
    }

    @Test
    public void testInterfaceGraph() throws Exception {
        MatcherAssert.assertThat(INSTANCE.getInterfaceGraph(Mockito.mock(TypeDescription.class)), CoreMatchers.is(((MethodGraph) (INSTANCE))));
    }
}

