package net.bytebuddy.dynamic.scaffold.inline;


import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;


public class RebaseImplementationTargetFactoryTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodRebaseResolver methodRebaseResolver;

    @Mock
    private MethodGraph.Linked methodGraph;

    @Mock
    private ClassFileVersion classFileVersion;

    @Mock
    private TypeDescription instrumentedType;

    @Mock
    private TypeDescription.Generic superClass;

    private Implementation.Target.Factory factory;

    @Test
    public void testReturnsRebaseImplementationTarget() throws Exception {
        MatcherAssert.assertThat(((factory.make(instrumentedType, methodGraph, classFileVersion)) instanceof RebaseImplementationTarget), CoreMatchers.is(true));
    }
}

