package net.bytebuddy.asm;


import net.bytebuddy.ClassFileVersion;
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
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;


public class TypeConstantAdjustmentTest {
    private static final int FOOBAR = 42;

    private static final int IGNORED = -1;

    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String BAZ = "baz";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private ClassVisitor classVisitor;

    @Mock
    private MethodVisitor methodVisitor;

    @Test
    public void testWriterFlags() throws Exception {
        MatcherAssert.assertThat(TypeConstantAdjustment.INSTANCE.mergeWriter(TypeConstantAdjustmentTest.FOOBAR), CoreMatchers.is(TypeConstantAdjustmentTest.FOOBAR));
    }

    @Test
    public void testReaderFlags() throws Exception {
        MatcherAssert.assertThat(TypeConstantAdjustment.INSTANCE.mergeReader(TypeConstantAdjustmentTest.FOOBAR), CoreMatchers.is(TypeConstantAdjustmentTest.FOOBAR));
    }

    @Test
    public void testInstrumentationModernClassFile() throws Exception {
        ClassVisitor classVisitor = TypeConstantAdjustment.INSTANCE.wrap(Mockito.mock(TypeDescription.class), this.classVisitor, Mockito.mock(Implementation.Context.class), Mockito.mock(TypePool.class), new FieldList.Empty<net.bytebuddy.description.field.FieldDescription.InDefinedShape>(), new MethodList.Empty<net.bytebuddy.description.method.MethodDescription>(), TypeConstantAdjustmentTest.IGNORED, TypeConstantAdjustmentTest.IGNORED);
        classVisitor.visit(ClassFileVersion.JAVA_V5.getMinorMajorVersion(), TypeConstantAdjustmentTest.FOOBAR, TypeConstantAdjustmentTest.FOO, TypeConstantAdjustmentTest.BAR, TypeConstantAdjustmentTest.QUX, new String[]{ TypeConstantAdjustmentTest.BAZ });
        MatcherAssert.assertThat(classVisitor.visitMethod(TypeConstantAdjustmentTest.FOOBAR, TypeConstantAdjustmentTest.FOO, TypeConstantAdjustmentTest.BAR, TypeConstantAdjustmentTest.QUX, new String[]{ TypeConstantAdjustmentTest.BAZ }), CoreMatchers.is(methodVisitor));
        Mockito.verify(this.classVisitor).visit(ClassFileVersion.JAVA_V5.getMinorMajorVersion(), TypeConstantAdjustmentTest.FOOBAR, TypeConstantAdjustmentTest.FOO, TypeConstantAdjustmentTest.BAR, TypeConstantAdjustmentTest.QUX, new String[]{ TypeConstantAdjustmentTest.BAZ });
        Mockito.verify(this.classVisitor).visitMethod(TypeConstantAdjustmentTest.FOOBAR, TypeConstantAdjustmentTest.FOO, TypeConstantAdjustmentTest.BAR, TypeConstantAdjustmentTest.QUX, new String[]{ TypeConstantAdjustmentTest.BAZ });
        Mockito.verifyNoMoreInteractions(this.classVisitor);
        Mockito.verifyZeroInteractions(methodVisitor);
    }

    @Test
    public void testInstrumentationLegacyClassFileObjectType() throws Exception {
        ClassVisitor classVisitor = TypeConstantAdjustment.INSTANCE.wrap(Mockito.mock(TypeDescription.class), this.classVisitor, Mockito.mock(Implementation.Context.class), Mockito.mock(TypePool.class), new FieldList.Empty<net.bytebuddy.description.field.FieldDescription.InDefinedShape>(), new MethodList.Empty<net.bytebuddy.description.method.MethodDescription>(), TypeConstantAdjustmentTest.IGNORED, TypeConstantAdjustmentTest.IGNORED);
        classVisitor.visit(ClassFileVersion.JAVA_V4.getMinorMajorVersion(), TypeConstantAdjustmentTest.FOOBAR, TypeConstantAdjustmentTest.FOO, TypeConstantAdjustmentTest.BAR, TypeConstantAdjustmentTest.QUX, new String[]{ TypeConstantAdjustmentTest.BAZ });
        MethodVisitor methodVisitor = classVisitor.visitMethod(TypeConstantAdjustmentTest.FOOBAR, TypeConstantAdjustmentTest.FOO, TypeConstantAdjustmentTest.BAR, TypeConstantAdjustmentTest.QUX, new String[]{ TypeConstantAdjustmentTest.BAZ });
        MatcherAssert.assertThat(methodVisitor, CoreMatchers.not(this.methodVisitor));
        methodVisitor.visitLdcInsn(Type.getType(Object.class));
        Mockito.verify(this.classVisitor).visit(ClassFileVersion.JAVA_V4.getMinorMajorVersion(), TypeConstantAdjustmentTest.FOOBAR, TypeConstantAdjustmentTest.FOO, TypeConstantAdjustmentTest.BAR, TypeConstantAdjustmentTest.QUX, new String[]{ TypeConstantAdjustmentTest.BAZ });
        Mockito.verify(this.classVisitor).visitMethod(TypeConstantAdjustmentTest.FOOBAR, TypeConstantAdjustmentTest.FOO, TypeConstantAdjustmentTest.BAR, TypeConstantAdjustmentTest.QUX, new String[]{ TypeConstantAdjustmentTest.BAZ });
        Mockito.verifyNoMoreInteractions(this.classVisitor);
        Mockito.verify(this.methodVisitor).visitLdcInsn(Type.getType(Object.class).getClassName());
        Mockito.verify(this.methodVisitor).visitMethodInsn(Opcodes.INVOKESTATIC, Type.getType(Class.class).getInternalName(), "forName", Type.getType(Class.class.getDeclaredMethod("forName", String.class)).getDescriptor(), false);
        Mockito.verifyNoMoreInteractions(this.methodVisitor);
    }

    @Test
    public void testInstrumentationLegacyClassFileArrayType() throws Exception {
        ClassVisitor classVisitor = TypeConstantAdjustment.INSTANCE.wrap(Mockito.mock(TypeDescription.class), this.classVisitor, Mockito.mock(Implementation.Context.class), Mockito.mock(TypePool.class), new FieldList.Empty<net.bytebuddy.description.field.FieldDescription.InDefinedShape>(), new MethodList.Empty<net.bytebuddy.description.method.MethodDescription>(), TypeConstantAdjustmentTest.IGNORED, TypeConstantAdjustmentTest.IGNORED);
        classVisitor.visit(ClassFileVersion.JAVA_V4.getMinorMajorVersion(), TypeConstantAdjustmentTest.FOOBAR, TypeConstantAdjustmentTest.FOO, TypeConstantAdjustmentTest.BAR, TypeConstantAdjustmentTest.QUX, new String[]{ TypeConstantAdjustmentTest.BAZ });
        MethodVisitor methodVisitor = classVisitor.visitMethod(TypeConstantAdjustmentTest.FOOBAR, TypeConstantAdjustmentTest.FOO, TypeConstantAdjustmentTest.BAR, TypeConstantAdjustmentTest.QUX, new String[]{ TypeConstantAdjustmentTest.BAZ });
        MatcherAssert.assertThat(methodVisitor, CoreMatchers.not(this.methodVisitor));
        methodVisitor.visitLdcInsn(Type.getType(Object[].class));
        Mockito.verify(this.classVisitor).visit(ClassFileVersion.JAVA_V4.getMinorMajorVersion(), TypeConstantAdjustmentTest.FOOBAR, TypeConstantAdjustmentTest.FOO, TypeConstantAdjustmentTest.BAR, TypeConstantAdjustmentTest.QUX, new String[]{ TypeConstantAdjustmentTest.BAZ });
        Mockito.verify(this.classVisitor).visitMethod(TypeConstantAdjustmentTest.FOOBAR, TypeConstantAdjustmentTest.FOO, TypeConstantAdjustmentTest.BAR, TypeConstantAdjustmentTest.QUX, new String[]{ TypeConstantAdjustmentTest.BAZ });
        Mockito.verifyNoMoreInteractions(this.classVisitor);
        Mockito.verify(this.methodVisitor).visitLdcInsn(Type.getType(Object[].class).getInternalName().replace('/', '.'));
        Mockito.verify(this.methodVisitor).visitMethodInsn(Opcodes.INVOKESTATIC, Type.getType(Class.class).getInternalName(), "forName", Type.getType(Class.class.getDeclaredMethod("forName", String.class)).getDescriptor(), false);
        Mockito.verifyNoMoreInteractions(this.methodVisitor);
    }

    @Test
    public void testInstrumentationLegacyClassOtherType() throws Exception {
        ClassVisitor classVisitor = TypeConstantAdjustment.INSTANCE.wrap(Mockito.mock(TypeDescription.class), this.classVisitor, Mockito.mock(Implementation.Context.class), Mockito.mock(TypePool.class), new FieldList.Empty<net.bytebuddy.description.field.FieldDescription.InDefinedShape>(), new MethodList.Empty<net.bytebuddy.description.method.MethodDescription>(), TypeConstantAdjustmentTest.IGNORED, TypeConstantAdjustmentTest.IGNORED);
        classVisitor.visit(ClassFileVersion.JAVA_V4.getMinorMajorVersion(), TypeConstantAdjustmentTest.FOOBAR, TypeConstantAdjustmentTest.FOO, TypeConstantAdjustmentTest.BAR, TypeConstantAdjustmentTest.QUX, new String[]{ TypeConstantAdjustmentTest.BAZ });
        MethodVisitor methodVisitor = classVisitor.visitMethod(TypeConstantAdjustmentTest.FOOBAR, TypeConstantAdjustmentTest.FOO, TypeConstantAdjustmentTest.BAR, TypeConstantAdjustmentTest.QUX, new String[]{ TypeConstantAdjustmentTest.BAZ });
        MatcherAssert.assertThat(methodVisitor, CoreMatchers.not(this.methodVisitor));
        methodVisitor.visitLdcInsn(TypeConstantAdjustmentTest.FOO);
        Mockito.verify(this.classVisitor).visit(ClassFileVersion.JAVA_V4.getMinorMajorVersion(), TypeConstantAdjustmentTest.FOOBAR, TypeConstantAdjustmentTest.FOO, TypeConstantAdjustmentTest.BAR, TypeConstantAdjustmentTest.QUX, new String[]{ TypeConstantAdjustmentTest.BAZ });
        Mockito.verify(this.classVisitor).visitMethod(TypeConstantAdjustmentTest.FOOBAR, TypeConstantAdjustmentTest.FOO, TypeConstantAdjustmentTest.BAR, TypeConstantAdjustmentTest.QUX, new String[]{ TypeConstantAdjustmentTest.BAZ });
        Mockito.verifyNoMoreInteractions(this.classVisitor);
        Mockito.verify(this.methodVisitor).visitLdcInsn(TypeConstantAdjustmentTest.FOO);
        Mockito.verifyNoMoreInteractions(this.methodVisitor);
    }
}

