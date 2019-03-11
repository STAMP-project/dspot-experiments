package net.bytebuddy.asm;


import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
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
import org.objectweb.asm.FieldVisitor;


public class AsmVisitorWrapperForDeclaredFieldsTest {
    private static final int MODIFIERS = 42;

    private static final int IRRELEVANT = -1;

    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String BAZ = "baz";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private ElementMatcher<? super FieldDescription.InDefinedShape> matcher;

    @Mock
    private AsmVisitorWrapper.ForDeclaredFields.FieldVisitorWrapper fieldVisitorWrapper;

    @Mock
    private TypeDescription instrumentedType;

    @Mock
    private FieldDescription.InDefinedShape foo;

    @Mock
    private FieldDescription.InDefinedShape bar;

    @Mock
    private ClassVisitor classVisitor;

    @Mock
    private TypePool typePool;

    @Mock
    private Implementation.Context implementationContext;

    @Mock
    private FieldVisitor fieldVisitor;

    @Mock
    private FieldVisitor wrappedVisitor;

    @Test
    public void testMatched() throws Exception {
        MatcherAssert.assertThat(new AsmVisitorWrapper.ForDeclaredFields().field(matcher, fieldVisitorWrapper).wrap(instrumentedType, classVisitor, implementationContext, typePool, new FieldList.Explicit<FieldDescription.InDefinedShape>(foo, bar), new MethodList.Empty<net.bytebuddy.description.method.MethodDescription>(), AsmVisitorWrapperForDeclaredFieldsTest.IRRELEVANT, AsmVisitorWrapperForDeclaredFieldsTest.IRRELEVANT).visitField(AsmVisitorWrapperForDeclaredFieldsTest.MODIFIERS, AsmVisitorWrapperForDeclaredFieldsTest.FOO, AsmVisitorWrapperForDeclaredFieldsTest.QUX, AsmVisitorWrapperForDeclaredFieldsTest.BAZ, ((AsmVisitorWrapperForDeclaredFieldsTest.QUX) + (AsmVisitorWrapperForDeclaredFieldsTest.BAZ))), CoreMatchers.is(wrappedVisitor));
        Mockito.verify(matcher).matches(foo);
        Mockito.verifyNoMoreInteractions(matcher);
        Mockito.verify(fieldVisitorWrapper).wrap(instrumentedType, foo, fieldVisitor);
        Mockito.verifyNoMoreInteractions(fieldVisitorWrapper);
    }

    @Test
    public void testNotMatched() throws Exception {
        MatcherAssert.assertThat(new AsmVisitorWrapper.ForDeclaredFields().field(matcher, fieldVisitorWrapper).wrap(instrumentedType, classVisitor, implementationContext, typePool, new FieldList.Explicit<FieldDescription.InDefinedShape>(foo, bar), new MethodList.Empty<net.bytebuddy.description.method.MethodDescription>(), AsmVisitorWrapperForDeclaredFieldsTest.IRRELEVANT, AsmVisitorWrapperForDeclaredFieldsTest.IRRELEVANT).visitField(AsmVisitorWrapperForDeclaredFieldsTest.MODIFIERS, AsmVisitorWrapperForDeclaredFieldsTest.BAR, AsmVisitorWrapperForDeclaredFieldsTest.QUX, AsmVisitorWrapperForDeclaredFieldsTest.BAZ, ((AsmVisitorWrapperForDeclaredFieldsTest.QUX) + (AsmVisitorWrapperForDeclaredFieldsTest.BAZ))), CoreMatchers.is(fieldVisitor));
        Mockito.verify(matcher).matches(bar);
        Mockito.verifyNoMoreInteractions(matcher);
        Mockito.verifyZeroInteractions(fieldVisitorWrapper);
    }

    @Test
    public void testUnknown() throws Exception {
        MatcherAssert.assertThat(new AsmVisitorWrapper.ForDeclaredFields().field(matcher, fieldVisitorWrapper).wrap(instrumentedType, classVisitor, implementationContext, typePool, new FieldList.Explicit<FieldDescription.InDefinedShape>(foo, bar), new MethodList.Empty<net.bytebuddy.description.method.MethodDescription>(), AsmVisitorWrapperForDeclaredFieldsTest.IRRELEVANT, AsmVisitorWrapperForDeclaredFieldsTest.IRRELEVANT).visitField(AsmVisitorWrapperForDeclaredFieldsTest.MODIFIERS, ((AsmVisitorWrapperForDeclaredFieldsTest.FOO) + (AsmVisitorWrapperForDeclaredFieldsTest.BAR)), AsmVisitorWrapperForDeclaredFieldsTest.QUX, AsmVisitorWrapperForDeclaredFieldsTest.BAZ, ((AsmVisitorWrapperForDeclaredFieldsTest.QUX) + (AsmVisitorWrapperForDeclaredFieldsTest.BAZ))), CoreMatchers.is(fieldVisitor));
        Mockito.verifyZeroInteractions(matcher);
        Mockito.verifyZeroInteractions(fieldVisitorWrapper);
    }
}

