package net.bytebuddy.dynamic.scaffold;


import java.util.Collections;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.attribute.AnnotationValueFilter;
import net.bytebuddy.implementation.attribute.MethodAttributeAppender;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.constant.DefaultValue;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static net.bytebuddy.description.annotation.AnnotationValue.ForConstant.of;
import static net.bytebuddy.description.type.TypeDescription.Generic.OBJECT;
import static net.bytebuddy.dynamic.scaffold.TypeWriter.MethodPool.Record.Sort.DEFINED;
import static net.bytebuddy.dynamic.scaffold.TypeWriter.MethodPool.Record.Sort.IMPLEMENTED;
import static net.bytebuddy.dynamic.scaffold.TypeWriter.MethodPool.Record.Sort.SKIPPED;


public class TypeWriterMethodPoolRecordTest {
    private static final int MODIFIERS = 42;

    private static final int ONE = 1;

    private static final int TWO = 2;

    private static final int MULTIPLIER = 4;

    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String BAZ = "baz";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodAttributeAppender methodAttributeAppender;

    @Mock
    private ClassVisitor classVisitor;

    @Mock
    private Implementation.Context implementationContext;

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private AnnotationVisitor annotationVisitor;

    @Mock
    private MethodDescription methodDescription;

    @Mock
    private MethodDescription bridgeTarget;

    @Mock
    private TypeDescription superClass;

    @Mock
    private ByteCodeAppender byteCodeAppender;

    @Mock
    private ByteCodeAppender otherAppender;

    @Mock
    private TypeList.Generic exceptionTypes;

    @Mock
    private TypeList rawExceptionTypes;

    @Mock
    private ParameterDescription parameterDescription;

    @Mock
    private TypeWriter.MethodPool.Record delegate;

    @Mock
    private TypeDescription instrumentedType;

    @Mock
    private MethodDescription.TypeToken typeToken;

    @Mock
    private AnnotationValueFilter annotationValueFilter;

    @Mock
    private AnnotationValueFilter.Factory annotationValueFilterFactory;

    @Test
    public void testSkippedMethod() throws Exception {
        TypeWriter.MethodPool.Record record = new TypeWriter.MethodPool.Record.ForNonImplementedMethod(methodDescription);
        MatcherAssert.assertThat(record.getSort(), CoreMatchers.is(SKIPPED));
        MatcherAssert.assertThat(record.getMethod(), CoreMatchers.is(methodDescription));
        record.apply(classVisitor, implementationContext, annotationValueFilterFactory);
        Mockito.verifyZeroInteractions(classVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
        Mockito.verifyZeroInteractions(methodAttributeAppender);
    }

    @Test
    public void testSkippedMethodCannotBePrepended() throws Exception {
        Mockito.when(methodDescription.getReturnType()).thenReturn(OBJECT);
        MatcherAssert.assertThat(new TypeWriter.MethodPool.Record.ForNonImplementedMethod(methodDescription).prepend(byteCodeAppender), FieldByFieldComparison.hasPrototype(((TypeWriter.MethodPool.Record) (new TypeWriter.MethodPool.Record.ForDefinedMethod.WithBody(methodDescription, new ByteCodeAppender.Compound(byteCodeAppender, new ByteCodeAppender.Simple(DefaultValue.REFERENCE, MethodReturn.REFERENCE)))))));
    }

    @Test(expected = IllegalStateException.class)
    public void testSkippedMethodCannotApplyBody() throws Exception {
        new TypeWriter.MethodPool.Record.ForNonImplementedMethod(methodDescription).applyBody(methodVisitor, implementationContext, annotationValueFilterFactory);
    }

    @Test(expected = IllegalStateException.class)
    public void testSkippedMethodCannotApplyHead() throws Exception {
        new TypeWriter.MethodPool.Record.ForNonImplementedMethod(methodDescription).applyHead(methodVisitor);
    }

    @Test
    public void testDefinedMethod() throws Exception {
        TypeWriter.MethodPool.Record record = new TypeWriter.MethodPool.Record.ForDefinedMethod.WithoutBody(methodDescription, methodAttributeAppender, Visibility.PUBLIC);
        MatcherAssert.assertThat(record.getSort(), CoreMatchers.is(DEFINED));
        record.apply(classVisitor, implementationContext, annotationValueFilterFactory);
        Mockito.verify(classVisitor).visitMethod(TypeWriterMethodPoolRecordTest.MODIFIERS, TypeWriterMethodPoolRecordTest.FOO, TypeWriterMethodPoolRecordTest.BAR, TypeWriterMethodPoolRecordTest.QUX, new String[]{ TypeWriterMethodPoolRecordTest.BAZ });
        Mockito.verifyNoMoreInteractions(classVisitor);
        Mockito.verify(methodVisitor).visitEnd();
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
        Mockito.verify(methodAttributeAppender).apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verifyNoMoreInteractions(methodAttributeAppender);
    }

    @Test
    public void testDefinedMethodHeadOnly() throws Exception {
        Mockito.when(parameterDescription.hasModifiers()).thenReturn(true);
        Mockito.when(parameterDescription.isNamed()).thenReturn(true);
        TypeWriter.MethodPool.Record record = new TypeWriter.MethodPool.Record.ForDefinedMethod.WithoutBody(methodDescription, methodAttributeAppender, Visibility.PUBLIC);
        record.applyHead(methodVisitor);
        Mockito.verifyZeroInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
        Mockito.verifyZeroInteractions(methodAttributeAppender);
    }

    @Test
    public void testDefinedMethodBodyOnly() throws Exception {
        Mockito.when(parameterDescription.hasModifiers()).thenReturn(true);
        Mockito.when(parameterDescription.isNamed()).thenReturn(true);
        TypeWriter.MethodPool.Record record = new TypeWriter.MethodPool.Record.ForDefinedMethod.WithoutBody(methodDescription, methodAttributeAppender, Visibility.PUBLIC);
        record.applyBody(methodVisitor, implementationContext, annotationValueFilterFactory);
        Mockito.verifyZeroInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
        Mockito.verify(methodAttributeAppender).apply(methodVisitor, methodDescription, annotationValueFilter);
    }

    @Test
    public void testDefinedMethodWithParameters() throws Exception {
        Mockito.when(parameterDescription.hasModifiers()).thenReturn(true);
        Mockito.when(parameterDescription.isNamed()).thenReturn(true);
        TypeWriter.MethodPool.Record record = new TypeWriter.MethodPool.Record.ForDefinedMethod.WithoutBody(methodDescription, methodAttributeAppender, Visibility.PUBLIC);
        MatcherAssert.assertThat(record.getSort(), CoreMatchers.is(DEFINED));
        record.apply(classVisitor, implementationContext, annotationValueFilterFactory);
        Mockito.verify(classVisitor).visitMethod(TypeWriterMethodPoolRecordTest.MODIFIERS, TypeWriterMethodPoolRecordTest.FOO, TypeWriterMethodPoolRecordTest.BAR, TypeWriterMethodPoolRecordTest.QUX, new String[]{ TypeWriterMethodPoolRecordTest.BAZ });
        Mockito.verifyNoMoreInteractions(classVisitor);
        Mockito.verify(methodVisitor).visitParameter(TypeWriterMethodPoolRecordTest.FOO, TypeWriterMethodPoolRecordTest.MODIFIERS);
        Mockito.verify(methodVisitor).visitEnd();
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
        Mockito.verify(methodAttributeAppender).apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verifyNoMoreInteractions(methodAttributeAppender);
    }

    @Test
    public void testDefinedMethodApplyAttributes() throws Exception {
        new TypeWriter.MethodPool.Record.ForDefinedMethod.WithoutBody(methodDescription, methodAttributeAppender, Visibility.PUBLIC).applyAttributes(methodVisitor, annotationValueFilterFactory);
        Mockito.verifyZeroInteractions(methodVisitor);
        Mockito.verify(methodAttributeAppender).apply(methodVisitor, methodDescription, annotationValueFilter);
    }

    @Test(expected = IllegalStateException.class)
    public void testDefinedMethodApplyCode() throws Exception {
        new TypeWriter.MethodPool.Record.ForDefinedMethod.WithoutBody(methodDescription, methodAttributeAppender, Visibility.PUBLIC).applyCode(methodVisitor, implementationContext);
    }

    @Test(expected = IllegalStateException.class)
    public void testDefinedMethodPrepended() throws Exception {
        new TypeWriter.MethodPool.Record.ForDefinedMethod.WithoutBody(methodDescription, methodAttributeAppender, Visibility.PUBLIC).prepend(otherAppender);
    }

    @Test
    public void testDefaultValueMethod() throws Exception {
        Mockito.when(methodDescription.getReturnType()).thenReturn(TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(String.class));
        Mockito.when(methodDescription.isDefaultValue(of(TypeWriterMethodPoolRecordTest.FOO))).thenReturn(true);
        TypeWriter.MethodPool.Record record = new TypeWriter.MethodPool.Record.ForDefinedMethod.WithAnnotationDefaultValue(methodDescription, of(TypeWriterMethodPoolRecordTest.FOO), methodAttributeAppender);
        MatcherAssert.assertThat(record.getSort(), CoreMatchers.is(DEFINED));
        record.apply(classVisitor, implementationContext, annotationValueFilterFactory);
        Mockito.verify(classVisitor).visitMethod(TypeWriterMethodPoolRecordTest.MODIFIERS, TypeWriterMethodPoolRecordTest.FOO, TypeWriterMethodPoolRecordTest.BAR, TypeWriterMethodPoolRecordTest.QUX, new String[]{ TypeWriterMethodPoolRecordTest.BAZ });
        Mockito.verifyNoMoreInteractions(classVisitor);
        Mockito.verify(methodVisitor).visitAnnotationDefault();
        Mockito.verify(methodVisitor).visitEnd();
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verify(annotationVisitor).visit(null, TypeWriterMethodPoolRecordTest.FOO);
        Mockito.verify(annotationVisitor).visitEnd();
        Mockito.verifyNoMoreInteractions(annotationVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
        Mockito.verify(methodAttributeAppender).apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verifyNoMoreInteractions(methodAttributeAppender);
    }

    @Test
    public void testDefaultValueMethodHeadOnly() throws Exception {
        Mockito.when(methodDescription.getReturnType()).thenReturn(TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(String.class));
        Mockito.when(parameterDescription.hasModifiers()).thenReturn(true);
        Mockito.when(parameterDescription.isNamed()).thenReturn(true);
        Mockito.when(methodDescription.isDefaultValue(of(TypeWriterMethodPoolRecordTest.FOO))).thenReturn(true);
        TypeWriter.MethodPool.Record record = new TypeWriter.MethodPool.Record.ForDefinedMethod.WithAnnotationDefaultValue(methodDescription, of(TypeWriterMethodPoolRecordTest.FOO), methodAttributeAppender);
        record.applyHead(methodVisitor);
        Mockito.verify(methodVisitor).visitAnnotationDefault();
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verify(annotationVisitor).visit(null, TypeWriterMethodPoolRecordTest.FOO);
        Mockito.verify(annotationVisitor).visitEnd();
        Mockito.verifyNoMoreInteractions(annotationVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
        Mockito.verifyZeroInteractions(methodAttributeAppender);
    }

    @Test
    public void testDefaultValueMethodBodyOnly() throws Exception {
        Mockito.when(parameterDescription.hasModifiers()).thenReturn(true);
        Mockito.when(parameterDescription.isNamed()).thenReturn(true);
        TypeWriter.MethodPool.Record record = new TypeWriter.MethodPool.Record.ForDefinedMethod.WithAnnotationDefaultValue(methodDescription, of(TypeWriterMethodPoolRecordTest.FOO), methodAttributeAppender);
        record.applyBody(methodVisitor, implementationContext, annotationValueFilterFactory);
        Mockito.verifyZeroInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
        Mockito.verify(methodAttributeAppender).apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verifyNoMoreInteractions(methodAttributeAppender);
    }

    @Test
    public void testDefaultValueMethodWithParameters() throws Exception {
        Mockito.when(parameterDescription.hasModifiers()).thenReturn(true);
        Mockito.when(parameterDescription.isNamed()).thenReturn(true);
        Mockito.when(methodDescription.getReturnType()).thenReturn(TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(String.class));
        Mockito.when(methodDescription.isDefaultValue(of(TypeWriterMethodPoolRecordTest.FOO))).thenReturn(true);
        TypeWriter.MethodPool.Record record = new TypeWriter.MethodPool.Record.ForDefinedMethod.WithAnnotationDefaultValue(methodDescription, of(TypeWriterMethodPoolRecordTest.FOO), methodAttributeAppender);
        MatcherAssert.assertThat(record.getSort(), CoreMatchers.is(DEFINED));
        record.apply(classVisitor, implementationContext, annotationValueFilterFactory);
        Mockito.verify(classVisitor).visitMethod(TypeWriterMethodPoolRecordTest.MODIFIERS, TypeWriterMethodPoolRecordTest.FOO, TypeWriterMethodPoolRecordTest.BAR, TypeWriterMethodPoolRecordTest.QUX, new String[]{ TypeWriterMethodPoolRecordTest.BAZ });
        Mockito.verifyNoMoreInteractions(classVisitor);
        Mockito.verify(methodVisitor).visitParameter(TypeWriterMethodPoolRecordTest.FOO, TypeWriterMethodPoolRecordTest.MODIFIERS);
        Mockito.verify(methodVisitor).visitAnnotationDefault();
        Mockito.verify(methodVisitor).visitEnd();
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verify(annotationVisitor).visit(null, TypeWriterMethodPoolRecordTest.FOO);
        Mockito.verify(annotationVisitor).visitEnd();
        Mockito.verifyNoMoreInteractions(annotationVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
        Mockito.verify(methodAttributeAppender).apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verifyNoMoreInteractions(methodAttributeAppender);
    }

    @Test(expected = IllegalStateException.class)
    public void testDefaultValueMethodApplyCode() throws Exception {
        new TypeWriter.MethodPool.Record.ForDefinedMethod.WithAnnotationDefaultValue(methodDescription, of(TypeWriterMethodPoolRecordTest.FOO), methodAttributeAppender).applyCode(methodVisitor, implementationContext);
    }

    @Test(expected = IllegalStateException.class)
    public void testDefaultValueMethodApplyAttributes() throws Exception {
        new TypeWriter.MethodPool.Record.ForDefinedMethod.WithAnnotationDefaultValue(methodDescription, of(TypeWriterMethodPoolRecordTest.FOO), methodAttributeAppender).applyAttributes(methodVisitor, annotationValueFilterFactory);
    }

    @Test(expected = IllegalStateException.class)
    public void testDefaultValueMethodPrepended() throws Exception {
        new TypeWriter.MethodPool.Record.ForDefinedMethod.WithAnnotationDefaultValue(methodDescription, of(TypeWriterMethodPoolRecordTest.FOO), methodAttributeAppender).prepend(otherAppender);
    }

    @Test(expected = IllegalStateException.class)
    public void testNoDefaultValue() throws Exception {
        Mockito.when(methodDescription.isDefaultValue(of(TypeWriterMethodPoolRecordTest.FOO))).thenReturn(false);
        new TypeWriter.MethodPool.Record.ForDefinedMethod.WithAnnotationDefaultValue(methodDescription, of(TypeWriterMethodPoolRecordTest.FOO), methodAttributeAppender).apply(classVisitor, implementationContext, annotationValueFilterFactory);
    }

    @Test
    public void testImplementedMethod() throws Exception {
        TypeWriter.MethodPool.Record record = new TypeWriter.MethodPool.Record.ForDefinedMethod.WithBody(methodDescription, byteCodeAppender, methodAttributeAppender, Visibility.PUBLIC);
        MatcherAssert.assertThat(record.getSort(), CoreMatchers.is(IMPLEMENTED));
        record.apply(classVisitor, implementationContext, annotationValueFilterFactory);
        Mockito.verify(classVisitor).visitMethod(TypeWriterMethodPoolRecordTest.MODIFIERS, TypeWriterMethodPoolRecordTest.FOO, TypeWriterMethodPoolRecordTest.BAR, TypeWriterMethodPoolRecordTest.QUX, new String[]{ TypeWriterMethodPoolRecordTest.BAZ });
        Mockito.verifyNoMoreInteractions(classVisitor);
        Mockito.verify(methodVisitor).visitCode();
        Mockito.verify(methodVisitor).visitMaxs(TypeWriterMethodPoolRecordTest.ONE, TypeWriterMethodPoolRecordTest.TWO);
        Mockito.verify(methodVisitor).visitEnd();
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
        Mockito.verify(methodAttributeAppender).apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verifyNoMoreInteractions(methodAttributeAppender);
        Mockito.verify(byteCodeAppender).apply(methodVisitor, implementationContext, methodDescription);
        Mockito.verifyNoMoreInteractions(byteCodeAppender);
    }

    @Test
    public void testImplementedMethodHeadOnly() throws Exception {
        Mockito.when(parameterDescription.hasModifiers()).thenReturn(true);
        Mockito.when(parameterDescription.isNamed()).thenReturn(true);
        TypeWriter.MethodPool.Record record = new TypeWriter.MethodPool.Record.ForDefinedMethod.WithBody(methodDescription, byteCodeAppender, methodAttributeAppender, Visibility.PUBLIC);
        record.applyHead(methodVisitor);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
        Mockito.verifyZeroInteractions(methodAttributeAppender);
        Mockito.verifyZeroInteractions(byteCodeAppender);
    }

    @Test
    public void testImplementedMethodBodyOnly() throws Exception {
        Mockito.when(parameterDescription.hasModifiers()).thenReturn(true);
        Mockito.when(parameterDescription.isNamed()).thenReturn(true);
        TypeWriter.MethodPool.Record record = new TypeWriter.MethodPool.Record.ForDefinedMethod.WithBody(methodDescription, byteCodeAppender, methodAttributeAppender, Visibility.PUBLIC);
        record.applyBody(methodVisitor, implementationContext, annotationValueFilterFactory);
        Mockito.verify(methodVisitor).visitCode();
        Mockito.verify(methodVisitor).visitMaxs(TypeWriterMethodPoolRecordTest.ONE, TypeWriterMethodPoolRecordTest.TWO);
        Mockito.verifyZeroInteractions(implementationContext);
        Mockito.verify(methodAttributeAppender).apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verifyNoMoreInteractions(methodAttributeAppender);
        Mockito.verify(byteCodeAppender).apply(methodVisitor, implementationContext, methodDescription);
        Mockito.verifyNoMoreInteractions(byteCodeAppender);
    }

    @Test
    public void testImplementedMethodWithParameters() throws Exception {
        Mockito.when(parameterDescription.hasModifiers()).thenReturn(true);
        Mockito.when(parameterDescription.isNamed()).thenReturn(true);
        TypeWriter.MethodPool.Record record = new TypeWriter.MethodPool.Record.ForDefinedMethod.WithBody(methodDescription, byteCodeAppender, methodAttributeAppender, Visibility.PUBLIC);
        MatcherAssert.assertThat(record.getSort(), CoreMatchers.is(IMPLEMENTED));
        record.apply(classVisitor, implementationContext, annotationValueFilterFactory);
        Mockito.verify(classVisitor).visitMethod(TypeWriterMethodPoolRecordTest.MODIFIERS, TypeWriterMethodPoolRecordTest.FOO, TypeWriterMethodPoolRecordTest.BAR, TypeWriterMethodPoolRecordTest.QUX, new String[]{ TypeWriterMethodPoolRecordTest.BAZ });
        Mockito.verifyNoMoreInteractions(classVisitor);
        Mockito.verify(methodVisitor).visitParameter(TypeWriterMethodPoolRecordTest.FOO, TypeWriterMethodPoolRecordTest.MODIFIERS);
        Mockito.verify(methodVisitor).visitCode();
        Mockito.verify(methodVisitor).visitMaxs(TypeWriterMethodPoolRecordTest.ONE, TypeWriterMethodPoolRecordTest.TWO);
        Mockito.verify(methodVisitor).visitEnd();
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
        Mockito.verify(methodAttributeAppender).apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verifyNoMoreInteractions(methodAttributeAppender);
        Mockito.verify(byteCodeAppender).apply(methodVisitor, implementationContext, methodDescription);
        Mockito.verifyNoMoreInteractions(byteCodeAppender);
    }

    @Test
    public void testImplementedMethodPrepended() throws Exception {
        TypeWriter.MethodPool.Record record = new TypeWriter.MethodPool.Record.ForDefinedMethod.WithBody(methodDescription, byteCodeAppender, methodAttributeAppender, Visibility.PUBLIC).prepend(otherAppender);
        MatcherAssert.assertThat(record.getSort(), CoreMatchers.is(IMPLEMENTED));
        record.apply(classVisitor, implementationContext, annotationValueFilterFactory);
        Mockito.verify(classVisitor).visitMethod(TypeWriterMethodPoolRecordTest.MODIFIERS, TypeWriterMethodPoolRecordTest.FOO, TypeWriterMethodPoolRecordTest.BAR, TypeWriterMethodPoolRecordTest.QUX, new String[]{ TypeWriterMethodPoolRecordTest.BAZ });
        Mockito.verifyNoMoreInteractions(classVisitor);
        Mockito.verify(methodVisitor).visitCode();
        Mockito.verify(methodVisitor).visitMaxs(((TypeWriterMethodPoolRecordTest.ONE) * (TypeWriterMethodPoolRecordTest.MULTIPLIER)), ((TypeWriterMethodPoolRecordTest.TWO) * (TypeWriterMethodPoolRecordTest.MULTIPLIER)));
        Mockito.verify(methodVisitor).visitEnd();
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
        Mockito.verify(methodAttributeAppender).apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verifyNoMoreInteractions(methodAttributeAppender);
        Mockito.verify(byteCodeAppender).apply(methodVisitor, implementationContext, methodDescription);
        Mockito.verifyNoMoreInteractions(byteCodeAppender);
        Mockito.verify(otherAppender).apply(methodVisitor, implementationContext, methodDescription);
        Mockito.verifyNoMoreInteractions(otherAppender);
    }

    @Test
    public void testVisibilityBridgeProperties() throws Exception {
        TypeWriter.MethodPool.Record record = new TypeWriter.MethodPool.Record.ForDefinedMethod.OfVisibilityBridge(methodDescription, bridgeTarget, superClass, methodAttributeAppender);
        MatcherAssert.assertThat(record.getSort(), CoreMatchers.is(IMPLEMENTED));
    }

    @Test
    public void testVisibilityBridgePrepending() throws Exception {
        MatcherAssert.assertThat(new TypeWriter.MethodPool.Record.ForDefinedMethod.OfVisibilityBridge(methodDescription, bridgeTarget, superClass, methodAttributeAppender).prepend(byteCodeAppender), CoreMatchers.instanceOf(TypeWriter.MethodPool.Record.ForDefinedMethod.WithBody.class));
    }

    @Test
    public void testAccessorBridgeProperties() throws Exception {
        Mockito.when(delegate.getSort()).thenReturn(IMPLEMENTED);
        TypeWriter.MethodPool.Record record = new TypeWriter.MethodPool.Record.AccessBridgeWrapper(delegate, instrumentedType, bridgeTarget, Collections.singleton(typeToken), methodAttributeAppender);
        MatcherAssert.assertThat(record.getSort(), CoreMatchers.is(IMPLEMENTED));
    }

    @Test
    public void testAccessorBridgeBodyApplication() throws Exception {
        new TypeWriter.MethodPool.Record.AccessBridgeWrapper(delegate, instrumentedType, bridgeTarget, Collections.singleton(typeToken), methodAttributeAppender).applyBody(methodVisitor, implementationContext, annotationValueFilterFactory);
        Mockito.verify(delegate).applyBody(methodVisitor, implementationContext, annotationValueFilterFactory);
        Mockito.verifyNoMoreInteractions(delegate);
        Mockito.verifyZeroInteractions(methodVisitor);
    }

    @Test
    public void testAccessorBridgeHeadApplication() throws Exception {
        new TypeWriter.MethodPool.Record.AccessBridgeWrapper(delegate, instrumentedType, bridgeTarget, Collections.singleton(typeToken), methodAttributeAppender).applyHead(methodVisitor);
        Mockito.verify(delegate).applyHead(methodVisitor);
        Mockito.verifyNoMoreInteractions(delegate);
        Mockito.verifyZeroInteractions(methodVisitor);
    }

    @Test
    public void testAccessorBridgeAttributeApplication() throws Exception {
        new TypeWriter.MethodPool.Record.AccessBridgeWrapper(delegate, instrumentedType, bridgeTarget, Collections.singleton(typeToken), methodAttributeAppender).applyAttributes(methodVisitor, annotationValueFilterFactory);
        Mockito.verify(delegate).applyAttributes(methodVisitor, annotationValueFilterFactory);
        Mockito.verifyNoMoreInteractions(delegate);
        Mockito.verifyZeroInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(annotationValueFilterFactory);
    }

    @Test
    public void testAccessorBridgeCodeApplication() throws Exception {
        new TypeWriter.MethodPool.Record.AccessBridgeWrapper(delegate, instrumentedType, bridgeTarget, Collections.singleton(typeToken), methodAttributeAppender).applyCode(methodVisitor, implementationContext);
        Mockito.verify(delegate).applyCode(methodVisitor, implementationContext);
        Mockito.verifyNoMoreInteractions(delegate);
        Mockito.verifyZeroInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
    }

    @Test
    public void testAccessorBridgePrepending() throws Exception {
        MatcherAssert.assertThat(new TypeWriter.MethodPool.Record.AccessBridgeWrapper(delegate, instrumentedType, bridgeTarget, Collections.singleton(typeToken), methodAttributeAppender).prepend(byteCodeAppender), CoreMatchers.instanceOf(TypeWriter.MethodPool.Record.AccessBridgeWrapper.class));
    }

    @Test
    public void testAccessorBridgePrependingTakesDelegateVisibility() throws Exception {
        Visibility visibility = Visibility.PUBLIC;
        Mockito.when(delegate.getVisibility()).thenReturn(visibility);
        MatcherAssert.assertThat(new TypeWriter.MethodPool.Record.AccessBridgeWrapper(delegate, instrumentedType, bridgeTarget, Collections.singleton(typeToken), methodAttributeAppender).getVisibility(), CoreMatchers.is(visibility));
    }
}

