package net.bytebuddy.dynamic.scaffold;


import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.dynamic.scaffold.MethodGraph.Empty.INSTANCE;


public class MethodGraphLinkedDelegationTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private MethodGraph methodGraph;

    @Mock
    private MethodGraph superGraph;

    @Mock
    private MethodGraph interfaceGraph;

    @Mock
    private MethodDescription.SignatureToken token;

    @Mock
    private MethodGraph.Node node;

    @Mock
    private MethodGraph.NodeList nodeList;

    private MethodGraph.Linked linkedMethodGraph;

    @Test
    public void testLocateNode() throws Exception {
        MatcherAssert.assertThat(linkedMethodGraph.locate(token), CoreMatchers.is(node));
    }

    @Test
    public void testNodeList() throws Exception {
        MatcherAssert.assertThat(linkedMethodGraph.listNodes(), CoreMatchers.is(nodeList));
    }

    @Test
    public void testSuperGraph() throws Exception {
        MatcherAssert.assertThat(linkedMethodGraph.getSuperClassGraph(), CoreMatchers.is(superGraph));
    }

    @Test
    public void testKnownInterfaceGraph() throws Exception {
        MatcherAssert.assertThat(linkedMethodGraph.getInterfaceGraph(typeDescription), CoreMatchers.is(interfaceGraph));
    }

    @Test
    public void testUnknownInterfaceGraph() throws Exception {
        MatcherAssert.assertThat(linkedMethodGraph.getInterfaceGraph(Mockito.mock(TypeDescription.class)), CoreMatchers.is(((MethodGraph) (INSTANCE))));
    }
}

