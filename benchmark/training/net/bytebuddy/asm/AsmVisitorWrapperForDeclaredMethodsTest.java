package net.bytebuddy.asm;


import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.matcher.ElementMatcher;
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
import org.objectweb.asm.MethodVisitor;


public class AsmVisitorWrapperForDeclaredMethodsTest {
    private static final int MODIFIERS = 42;

    private static final int FLAGS = 42;

    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String BAZ = "baz";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private ElementMatcher<? super MethodDescription> matcher;

    @Mock
    private AsmVisitorWrapper.ForDeclaredMethods.MethodVisitorWrapper methodVisitorWrapper;

    @Mock
    private TypeDescription instrumentedType;

    @Mock
    private MethodDescription.InDefinedShape foo;

    @Mock
    private MethodDescription.InDefinedShape bar;

    @Mock
    private ClassVisitor classVisitor;

    @Mock
    private Implementation.Context implementationContext;

    @Mock
    private TypePool typePool;

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private MethodVisitor wrappedVisitor;

    @Test
    public void testMatchedInvokable() throws Exception {
        MatcherAssert.assertThat(invokable(matcher, methodVisitorWrapper).wrap(instrumentedType, classVisitor, implementationContext, typePool, new FieldList.Empty<net.bytebuddy.description.field.FieldDescription.InDefinedShape>(), new MethodList.Explicit<MethodDescription>(foo, bar), AsmVisitorWrapperForDeclaredMethodsTest.FLAGS, ((AsmVisitorWrapperForDeclaredMethodsTest.FLAGS) * 2)).visitMethod(AsmVisitorWrapperForDeclaredMethodsTest.MODIFIERS, AsmVisitorWrapperForDeclaredMethodsTest.FOO, AsmVisitorWrapperForDeclaredMethodsTest.QUX, AsmVisitorWrapperForDeclaredMethodsTest.BAZ, new String[]{ (AsmVisitorWrapperForDeclaredMethodsTest.QUX) + (AsmVisitorWrapperForDeclaredMethodsTest.BAZ) }), CoreMatchers.is(wrappedVisitor));
        Mockito.verify(matcher).matches(foo);
        Mockito.verifyNoMoreInteractions(matcher);
        Mockito.verify(methodVisitorWrapper).wrap(instrumentedType, foo, methodVisitor, implementationContext, typePool, AsmVisitorWrapperForDeclaredMethodsTest.FLAGS, ((AsmVisitorWrapperForDeclaredMethodsTest.FLAGS) * 2));
        Mockito.verifyNoMoreInteractions(methodVisitorWrapper);
        Mockito.verifyZeroInteractions(typePool);
    }

    @Test
    public void testNonMatchedInvokable() throws Exception {
        MatcherAssert.assertThat(invokable(matcher, methodVisitorWrapper).wrap(instrumentedType, classVisitor, implementationContext, typePool, new FieldList.Empty<net.bytebuddy.description.field.FieldDescription.InDefinedShape>(), new MethodList.Explicit<MethodDescription>(foo, bar), AsmVisitorWrapperForDeclaredMethodsTest.FLAGS, ((AsmVisitorWrapperForDeclaredMethodsTest.FLAGS) * 2)).visitMethod(AsmVisitorWrapperForDeclaredMethodsTest.MODIFIERS, AsmVisitorWrapperForDeclaredMethodsTest.BAR, AsmVisitorWrapperForDeclaredMethodsTest.BAZ, AsmVisitorWrapperForDeclaredMethodsTest.BAZ, new String[]{ (AsmVisitorWrapperForDeclaredMethodsTest.QUX) + (AsmVisitorWrapperForDeclaredMethodsTest.BAZ) }), CoreMatchers.is(methodVisitor));
        Mockito.verify(matcher).matches(bar);
        Mockito.verifyNoMoreInteractions(matcher);
        Mockito.verifyZeroInteractions(methodVisitorWrapper);
        Mockito.verifyZeroInteractions(typePool);
    }

    @Test
    public void testUnknownInvokable() throws Exception {
        MatcherAssert.assertThat(invokable(matcher, methodVisitorWrapper).wrap(instrumentedType, classVisitor, implementationContext, typePool, new FieldList.Empty<net.bytebuddy.description.field.FieldDescription.InDefinedShape>(), new MethodList.Explicit<MethodDescription>(foo, bar), AsmVisitorWrapperForDeclaredMethodsTest.FLAGS, ((AsmVisitorWrapperForDeclaredMethodsTest.FLAGS) * 2)).visitMethod(AsmVisitorWrapperForDeclaredMethodsTest.MODIFIERS, ((AsmVisitorWrapperForDeclaredMethodsTest.FOO) + (AsmVisitorWrapperForDeclaredMethodsTest.BAR)), AsmVisitorWrapperForDeclaredMethodsTest.QUX, AsmVisitorWrapperForDeclaredMethodsTest.BAZ, new String[]{ (AsmVisitorWrapperForDeclaredMethodsTest.QUX) + (AsmVisitorWrapperForDeclaredMethodsTest.BAZ) }), CoreMatchers.is(methodVisitor));
        Mockito.verifyZeroInteractions(matcher);
        Mockito.verifyZeroInteractions(methodVisitorWrapper);
        Mockito.verifyZeroInteractions(typePool);
    }

    @Test
    public void testNonMatchedMethod() throws Exception {
        MatcherAssert.assertThat(new AsmVisitorWrapper.ForDeclaredMethods().method(matcher, methodVisitorWrapper).wrap(instrumentedType, classVisitor, implementationContext, typePool, new FieldList.Empty<net.bytebuddy.description.field.FieldDescription.InDefinedShape>(), new MethodList.Explicit<MethodDescription>(foo, bar), AsmVisitorWrapperForDeclaredMethodsTest.FLAGS, ((AsmVisitorWrapperForDeclaredMethodsTest.FLAGS) * 2)).visitMethod(AsmVisitorWrapperForDeclaredMethodsTest.MODIFIERS, AsmVisitorWrapperForDeclaredMethodsTest.FOO, AsmVisitorWrapperForDeclaredMethodsTest.QUX, AsmVisitorWrapperForDeclaredMethodsTest.BAZ, new String[]{ (AsmVisitorWrapperForDeclaredMethodsTest.QUX) + (AsmVisitorWrapperForDeclaredMethodsTest.BAZ) }), CoreMatchers.is(methodVisitor));
        Mockito.verifyZeroInteractions(matcher);
    }

    @Test
    public void testNonMatchedConstructor() throws Exception {
        MatcherAssert.assertThat(constructor(matcher, methodVisitorWrapper).wrap(instrumentedType, classVisitor, implementationContext, typePool, new FieldList.Empty<net.bytebuddy.description.field.FieldDescription.InDefinedShape>(), new MethodList.Explicit<MethodDescription>(foo, bar), AsmVisitorWrapperForDeclaredMethodsTest.FLAGS, ((AsmVisitorWrapperForDeclaredMethodsTest.FLAGS) * 2)).visitMethod(AsmVisitorWrapperForDeclaredMethodsTest.MODIFIERS, AsmVisitorWrapperForDeclaredMethodsTest.FOO, AsmVisitorWrapperForDeclaredMethodsTest.QUX, AsmVisitorWrapperForDeclaredMethodsTest.BAZ, new String[]{ (AsmVisitorWrapperForDeclaredMethodsTest.QUX) + (AsmVisitorWrapperForDeclaredMethodsTest.BAZ) }), CoreMatchers.is(methodVisitor));
        Mockito.verifyZeroInteractions(matcher);
    }

    @Test
    public void testWriterFlags() throws Exception {
        MatcherAssert.assertThat(new AsmVisitorWrapper.ForDeclaredMethods().writerFlags(AsmVisitorWrapperForDeclaredMethodsTest.FLAGS).mergeWriter(0), CoreMatchers.is(AsmVisitorWrapperForDeclaredMethodsTest.FLAGS));
    }

    @Test
    public void testReaderFlags() throws Exception {
        MatcherAssert.assertThat(new AsmVisitorWrapper.ForDeclaredMethods().readerFlags(AsmVisitorWrapperForDeclaredMethodsTest.FLAGS).mergeReader(0), CoreMatchers.is(AsmVisitorWrapperForDeclaredMethodsTest.FLAGS));
    }
}

