package net.bytebuddy.asm;


import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.pool.TypePool;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;
import org.objectweb.asm.ClassVisitor;

import static net.bytebuddy.asm.AsmVisitorWrapper.NoOp.INSTANCE;


public class AsmVisitorWrapperNoOpTest {
    private static final int FOO = 42;

    private static final int IGNORED = -1;

    @Test
    public void testWrapperChain() throws Exception {
        ClassVisitor classVisitor = Mockito.mock(ClassVisitor.class);
        MatcherAssert.assertThat(INSTANCE.wrap(Mockito.mock(TypeDescription.class), classVisitor, Mockito.mock(Implementation.Context.class), Mockito.mock(TypePool.class), new FieldList.Empty<net.bytebuddy.description.field.FieldDescription.InDefinedShape>(), new MethodList.Empty<net.bytebuddy.description.method.MethodDescription>(), AsmVisitorWrapperNoOpTest.IGNORED, AsmVisitorWrapperNoOpTest.IGNORED), CoreMatchers.is(classVisitor));
        Mockito.verifyZeroInteractions(classVisitor);
    }

    @Test
    public void testReaderFlags() throws Exception {
        MatcherAssert.assertThat(INSTANCE.mergeReader(AsmVisitorWrapperNoOpTest.FOO), CoreMatchers.is(AsmVisitorWrapperNoOpTest.FOO));
    }

    @Test
    public void testWriterFlags() throws Exception {
        MatcherAssert.assertThat(INSTANCE.mergeWriter(AsmVisitorWrapperNoOpTest.FOO), CoreMatchers.is(AsmVisitorWrapperNoOpTest.FOO));
    }
}

