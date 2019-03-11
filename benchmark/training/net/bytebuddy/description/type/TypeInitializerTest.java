package net.bytebuddy.description.type;


import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.dynamic.scaffold.TypeInitializer;
import net.bytebuddy.dynamic.scaffold.TypeWriter;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.MethodVisitor;

import static net.bytebuddy.dynamic.scaffold.TypeInitializer.None.INSTANCE;


public class TypeInitializerTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeWriter.MethodPool.Record record;

    @Mock
    private TypeWriter.MethodPool.Record expanded;

    @Mock
    private ByteCodeAppender byteCodeAppender;

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    @Mock
    private MethodDescription methodDescription;

    @Test
    public void testNoneExpansion() throws Exception {
        MatcherAssert.assertThat(INSTANCE.expandWith(byteCodeAppender), FieldByFieldComparison.hasPrototype(((TypeInitializer) (new TypeInitializer.Simple(byteCodeAppender)))));
    }

    @Test
    public void testNoneDefined() throws Exception {
        MatcherAssert.assertThat(INSTANCE.isDefined(), CoreMatchers.is(false));
    }

    @Test
    public void testNoneThrowsExceptionOnApplication() throws Exception {
        ByteCodeAppender.Size size = INSTANCE.apply(methodVisitor, implementationContext, methodDescription);
        MatcherAssert.assertThat(size.getOperandStackSize(), CoreMatchers.is(0));
        MatcherAssert.assertThat(size.getLocalVariableSize(), CoreMatchers.is(0));
        Mockito.verifyZeroInteractions(methodDescription);
    }

    @Test
    public void testNoneWrap() throws Exception {
        MatcherAssert.assertThat(INSTANCE.wrap(record), CoreMatchers.is(record));
    }

    @Test
    public void testSimpleExpansion() throws Exception {
        MatcherAssert.assertThat(new TypeInitializer.Simple(byteCodeAppender).expandWith(byteCodeAppender), FieldByFieldComparison.hasPrototype(((TypeInitializer) (new TypeInitializer.Simple(new ByteCodeAppender.Compound(byteCodeAppender, byteCodeAppender))))));
    }

    @Test
    public void testSimpleApplication() throws Exception {
        TypeInitializer typeInitializer = new TypeInitializer.Simple(byteCodeAppender);
        MatcherAssert.assertThat(typeInitializer.isDefined(), CoreMatchers.is(true));
        typeInitializer.apply(methodVisitor, implementationContext, methodDescription);
        Mockito.verify(byteCodeAppender).apply(methodVisitor, implementationContext, methodDescription);
        Mockito.verifyZeroInteractions(byteCodeAppender);
        Mockito.verifyZeroInteractions(implementationContext);
    }

    @Test
    public void testSimpleWrap() throws Exception {
        Mockito.when(record.prepend(byteCodeAppender)).thenReturn(expanded);
        MatcherAssert.assertThat(new TypeInitializer.Simple(byteCodeAppender).wrap(record), CoreMatchers.is(expanded));
    }
}

