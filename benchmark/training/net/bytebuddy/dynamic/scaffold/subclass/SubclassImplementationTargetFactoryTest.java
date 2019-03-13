package net.bytebuddy.dynamic.scaffold.subclass;


import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;

import static net.bytebuddy.dynamic.scaffold.subclass.SubclassImplementationTarget.Factory.LEVEL_TYPE;
import static net.bytebuddy.dynamic.scaffold.subclass.SubclassImplementationTarget.Factory.SUPER_CLASS;


public class SubclassImplementationTargetFactoryTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodGraph.Linked methodGraph;

    @Mock
    private TypeDescription instrumentedType;

    @Mock
    private TypeDescription rawSuperClass;

    @Mock
    private ClassFileVersion classFileVersion;

    @Mock
    private TypeDescription.Generic superClass;

    @Test
    public void testReturnsSubclassImplementationTarget() throws Exception {
        MatcherAssert.assertThat(SUPER_CLASS.make(instrumentedType, methodGraph, classFileVersion), CoreMatchers.instanceOf(SubclassImplementationTarget.class));
        MatcherAssert.assertThat(LEVEL_TYPE.make(instrumentedType, methodGraph, classFileVersion), CoreMatchers.instanceOf(SubclassImplementationTarget.class));
    }

    @Test
    public void testOriginTypeSuperClass() throws Exception {
        MatcherAssert.assertThat(SUPER_CLASS.make(instrumentedType, methodGraph, classFileVersion).getOriginType(), CoreMatchers.is(((TypeDefinition) (superClass))));
    }

    @Test
    public void testOriginTypeLevelType() throws Exception {
        MatcherAssert.assertThat(LEVEL_TYPE.make(instrumentedType, methodGraph, classFileVersion).getOriginType(), CoreMatchers.is(((TypeDefinition) (instrumentedType))));
    }
}

