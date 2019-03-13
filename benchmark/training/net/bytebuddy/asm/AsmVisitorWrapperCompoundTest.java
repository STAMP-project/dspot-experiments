package net.bytebuddy.asm;


import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.ClassVisitor;


public class AsmVisitorWrapperCompoundTest {
    private static final int FOO = 1;

    private static final int BAR = 2;

    private static final int QUX = 3;

    private static final int BAZ = 4;

    private static final int FLAGS = 42;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription instrumentedType;

    @Mock
    private AsmVisitorWrapper wrapper;

    @Mock
    private AsmVisitorWrapper prepend;

    @Mock
    private AsmVisitorWrapper append;

    @Mock
    private ClassVisitor wrapperVisitor;

    @Mock
    private ClassVisitor prependVisitor;

    @Mock
    private ClassVisitor appendVisitor;

    @Mock
    private ClassVisitor resultVisitor;

    @Mock
    private Implementation.Context implementationContext;

    @Mock
    private TypePool typePool;

    @Mock
    private FieldList<FieldDescription.InDefinedShape> fields;

    @Mock
    private MethodList<?> methods;

    @Test
    public void testWrapperChain() throws Exception {
        AsmVisitorWrapper.Compound compound = new AsmVisitorWrapper.Compound(prepend, wrapper, append);
        MatcherAssert.assertThat(compound.wrap(instrumentedType, prependVisitor, implementationContext, typePool, fields, methods, AsmVisitorWrapperCompoundTest.FLAGS, ((AsmVisitorWrapperCompoundTest.FLAGS) * 2)), CoreMatchers.is(resultVisitor));
        Mockito.verify(prepend).wrap(instrumentedType, prependVisitor, implementationContext, typePool, fields, methods, AsmVisitorWrapperCompoundTest.FLAGS, ((AsmVisitorWrapperCompoundTest.FLAGS) * 2));
        Mockito.verifyNoMoreInteractions(prepend);
        Mockito.verify(wrapper).wrap(instrumentedType, wrapperVisitor, implementationContext, typePool, fields, methods, AsmVisitorWrapperCompoundTest.FLAGS, ((AsmVisitorWrapperCompoundTest.FLAGS) * 2));
        Mockito.verifyNoMoreInteractions(wrapper);
        Mockito.verify(append).wrap(instrumentedType, appendVisitor, implementationContext, typePool, fields, methods, AsmVisitorWrapperCompoundTest.FLAGS, ((AsmVisitorWrapperCompoundTest.FLAGS) * 2));
        Mockito.verifyNoMoreInteractions(append);
    }

    @Test
    public void testReaderFlags() throws Exception {
        AsmVisitorWrapper.Compound compound = new AsmVisitorWrapper.Compound(prepend, wrapper, append);
        MatcherAssert.assertThat(compound.mergeReader(AsmVisitorWrapperCompoundTest.FOO), CoreMatchers.is(AsmVisitorWrapperCompoundTest.BAZ));
        Mockito.verify(prepend).mergeReader(AsmVisitorWrapperCompoundTest.FOO);
        Mockito.verifyNoMoreInteractions(prepend);
        Mockito.verify(wrapper).mergeReader(AsmVisitorWrapperCompoundTest.BAR);
        Mockito.verifyNoMoreInteractions(wrapper);
        Mockito.verify(append).mergeReader(AsmVisitorWrapperCompoundTest.QUX);
        Mockito.verifyNoMoreInteractions(append);
    }

    @Test
    public void testWriterFlags() throws Exception {
        AsmVisitorWrapper.Compound compound = new AsmVisitorWrapper.Compound(prepend, wrapper, append);
        MatcherAssert.assertThat(compound.mergeWriter(AsmVisitorWrapperCompoundTest.FOO), CoreMatchers.is(AsmVisitorWrapperCompoundTest.BAZ));
        Mockito.verify(prepend).mergeWriter(AsmVisitorWrapperCompoundTest.FOO);
        Mockito.verifyNoMoreInteractions(prepend);
        Mockito.verify(wrapper).mergeWriter(AsmVisitorWrapperCompoundTest.BAR);
        Mockito.verifyNoMoreInteractions(wrapper);
        Mockito.verify(append).mergeWriter(AsmVisitorWrapperCompoundTest.QUX);
        Mockito.verifyNoMoreInteractions(append);
    }
}

