package net.bytebuddy.implementation.attribute;


import net.bytebuddy.test.utility.MockitoRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.TypePath;

import static net.bytebuddy.implementation.attribute.AnnotationAppender.Target.OnMethodParameter.<init>;


public class AnnotationAppenderTargetTest {
    private static final String FOO = "foo";

    private static final int BAR = 42;

    private static final String TYPE_PATH = "*";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private FieldVisitor fieldVisitor;

    @Mock
    private ClassVisitor classVisitor;

    @Test
    public void testOnField() throws Exception {
        new AnnotationAppender.Target.OnField(fieldVisitor).visit(AnnotationAppenderTargetTest.FOO, true);
        Mockito.verify(fieldVisitor).visitAnnotation(AnnotationAppenderTargetTest.FOO, true);
        Mockito.verifyNoMoreInteractions(fieldVisitor);
    }

    @Test
    public void testOnType() throws Exception {
        new AnnotationAppender.Target.OnType(classVisitor).visit(AnnotationAppenderTargetTest.FOO, true);
        Mockito.verify(classVisitor).visitAnnotation(AnnotationAppenderTargetTest.FOO, true);
        Mockito.verifyNoMoreInteractions(classVisitor);
    }

    @Test
    public void testOnMethod() throws Exception {
        new AnnotationAppender.Target.OnMethod(methodVisitor).visit(AnnotationAppenderTargetTest.FOO, true);
        Mockito.verify(methodVisitor).visitAnnotation(AnnotationAppenderTargetTest.FOO, true);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }

    @Test
    public void testOnMethodParameter() throws Exception {
        new AnnotationAppender.Target.OnMethodParameter(methodVisitor, 0).visit(AnnotationAppenderTargetTest.FOO, true);
        Mockito.verify(methodVisitor).visitParameterAnnotation(0, AnnotationAppenderTargetTest.FOO, true);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }

    @Test
    public void testTypeAnnotationOnField() throws Exception {
        new AnnotationAppender.Target.OnField(fieldVisitor).visit(AnnotationAppenderTargetTest.FOO, true, AnnotationAppenderTargetTest.BAR, AnnotationAppenderTargetTest.TYPE_PATH);
        Mockito.verify(fieldVisitor).visitTypeAnnotation(ArgumentMatchers.eq(AnnotationAppenderTargetTest.BAR), ArgumentMatchers.any(TypePath.class), ArgumentMatchers.eq(AnnotationAppenderTargetTest.FOO), ArgumentMatchers.eq(true));
        Mockito.verifyNoMoreInteractions(fieldVisitor);
    }

    @Test
    public void testTypeAnnotationOnType() throws Exception {
        new AnnotationAppender.Target.OnType(classVisitor).visit(AnnotationAppenderTargetTest.FOO, true, AnnotationAppenderTargetTest.BAR, AnnotationAppenderTargetTest.TYPE_PATH);
        Mockito.verify(classVisitor).visitTypeAnnotation(ArgumentMatchers.eq(AnnotationAppenderTargetTest.BAR), ArgumentMatchers.any(TypePath.class), ArgumentMatchers.eq(AnnotationAppenderTargetTest.FOO), ArgumentMatchers.eq(true));
        Mockito.verifyNoMoreInteractions(classVisitor);
    }

    @Test
    public void testTypeAnnotationOnMethod() throws Exception {
        new AnnotationAppender.Target.OnMethod(methodVisitor).visit(AnnotationAppenderTargetTest.FOO, true, AnnotationAppenderTargetTest.BAR, AnnotationAppenderTargetTest.TYPE_PATH);
        Mockito.verify(methodVisitor).visitTypeAnnotation(ArgumentMatchers.eq(AnnotationAppenderTargetTest.BAR), ArgumentMatchers.any(TypePath.class), ArgumentMatchers.eq(AnnotationAppenderTargetTest.FOO), ArgumentMatchers.eq(true));
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }

    @Test
    public void testTypeAnnotationOnMethodParameter() throws Exception {
        new AnnotationAppender.Target.OnMethodParameter(methodVisitor, 0).visit(AnnotationAppenderTargetTest.FOO, true, AnnotationAppenderTargetTest.BAR, AnnotationAppenderTargetTest.TYPE_PATH);
        Mockito.verify(methodVisitor).visitTypeAnnotation(ArgumentMatchers.eq(AnnotationAppenderTargetTest.BAR), ArgumentMatchers.any(TypePath.class), ArgumentMatchers.eq(AnnotationAppenderTargetTest.FOO), ArgumentMatchers.eq(true));
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }
}

