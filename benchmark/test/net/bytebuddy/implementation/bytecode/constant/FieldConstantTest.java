package net.bytebuddy.implementation.bytecode.constant;


import java.lang.reflect.Field;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;


public class FieldConstantTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String BAZ = "baz";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private FieldDescription.InDefinedShape fieldDescription;

    @Mock
    private FieldDescription.InDefinedShape cacheField;

    @Mock
    private TypeDescription declaringType;

    @Mock
    private TypeDescription cacheDeclaringType;

    @Mock
    private TypeDescription cacheFieldType;

    @Mock
    private TypeDescription instrumentedType;

    @Mock
    private TypeDescription.Generic genericCacheFieldType;

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private ClassFileVersion classFileVersion;

    @Mock
    private Implementation.Context implementationContext;

    @Test
    public void testConstantCreationModernVisible() throws Exception {
        Mockito.when(classFileVersion.isAtLeast(ClassFileVersion.JAVA_V5)).thenReturn(true);
        Mockito.when(declaringType.isVisibleTo(instrumentedType)).thenReturn(true);
        StackManipulation stackManipulation = new FieldConstant(fieldDescription);
        Assert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        Assert.assertThat(size.getSizeImpact(), CoreMatchers.is(1));
        Assert.assertThat(size.getMaximalSize(), CoreMatchers.is(2));
        Mockito.verify(methodVisitor).visitLdcInsn(Type.getObjectType(FieldConstantTest.QUX));
        Mockito.verify(methodVisitor).visitLdcInsn(FieldConstantTest.BAR);
        Mockito.verify(methodVisitor).visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getDeclaredField", "(Ljava/lang/String;)Ljava/lang/reflect/Field;", false);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }

    @Test
    public void testConstantCreationModernInvisible() throws Exception {
        Mockito.when(classFileVersion.isAtLeast(ClassFileVersion.JAVA_V5)).thenReturn(true);
        Mockito.when(declaringType.isVisibleTo(instrumentedType)).thenReturn(false);
        StackManipulation stackManipulation = new FieldConstant(fieldDescription);
        Assert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        Assert.assertThat(size.getSizeImpact(), CoreMatchers.is(1));
        Assert.assertThat(size.getMaximalSize(), CoreMatchers.is(2));
        Mockito.verify(methodVisitor).visitLdcInsn(FieldConstantTest.BAZ);
        Mockito.verify(methodVisitor).visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(Class.class), "forName", Type.getMethodDescriptor(Type.getType(Class.class), Type.getType(String.class)), false);
        Mockito.verify(methodVisitor).visitLdcInsn(FieldConstantTest.BAR);
        Mockito.verify(methodVisitor).visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getDeclaredField", "(Ljava/lang/String;)Ljava/lang/reflect/Field;", false);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }

    @Test
    public void testConstantCreationLegacy() throws Exception {
        Mockito.when(classFileVersion.isAtLeast(ClassFileVersion.JAVA_V5)).thenReturn(false);
        Mockito.when(declaringType.isVisibleTo(instrumentedType)).thenReturn(true);
        StackManipulation stackManipulation = new FieldConstant(fieldDescription);
        Assert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        Assert.assertThat(size.getSizeImpact(), CoreMatchers.is(1));
        Assert.assertThat(size.getMaximalSize(), CoreMatchers.is(2));
        Mockito.verify(methodVisitor).visitLdcInsn(FieldConstantTest.BAZ);
        Mockito.verify(methodVisitor).visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(Class.class), "forName", Type.getMethodDescriptor(Type.getType(Class.class), Type.getType(String.class)), false);
        Mockito.verify(methodVisitor).visitLdcInsn(FieldConstantTest.BAR);
        Mockito.verify(methodVisitor).visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getDeclaredField", "(Ljava/lang/String;)Ljava/lang/reflect/Field;", false);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }

    @Test
    public void testCached() throws Exception {
        StackManipulation stackManipulation = new FieldConstant(fieldDescription).cached();
        Assert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        Assert.assertThat(size.getSizeImpact(), CoreMatchers.is(1));
        Assert.assertThat(size.getMaximalSize(), CoreMatchers.is(1));
        Mockito.verify(implementationContext).cache(new FieldConstant(fieldDescription), of(Field.class));
        Mockito.verifyNoMoreInteractions(implementationContext);
        Mockito.verify(methodVisitor).visitFieldInsn(Opcodes.GETSTATIC, FieldConstantTest.BAZ, ((FieldConstantTest.FOO) + (FieldConstantTest.BAR)), ((FieldConstantTest.QUX) + (FieldConstantTest.BAZ)));
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }
}

