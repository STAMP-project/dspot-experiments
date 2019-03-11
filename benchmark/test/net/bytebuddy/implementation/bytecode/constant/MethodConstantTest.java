package net.bytebuddy.implementation.bytecode.constant;


import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.auxiliary.AuxiliaryType;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;


public class MethodConstantTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String BAZ = "baz";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodDescription.InDefinedShape methodDescription;

    @Mock
    private MethodDescription.InDefinedShape auxiliaryConstructor;

    @Mock
    private TypeDescription declaringType;

    @Mock
    private TypeDescription parameterType;

    @Mock
    private TypeDescription fieldType;

    @Mock
    private TypeDescription instrumentedType;

    @Mock
    private TypeDescription auxiliaryType;

    @Mock
    private ClassFileVersion classFileVersion;

    @Mock
    private TypeDescription.Generic genericFieldType;

    @Mock
    private ParameterList<?> parameterList;

    @Mock
    private TypeList.Generic typeList;

    @Mock
    private TypeList rawTypeList;

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    @Mock
    private FieldDescription.InDefinedShape fieldDescription;

    @Test
    public void testMethod() throws Exception {
        StackManipulation.Size size = MethodConstant.of(methodDescription).apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(1));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(6));
        Mockito.verify(methodVisitor).visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Class.class), "getDeclaredMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);
    }

    @Test
    public void testMethodPublic() throws Exception {
        Mockito.when(methodDescription.isPublic()).thenReturn(true);
        StackManipulation.Size size = MethodConstant.of(methodDescription).apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(1));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(6));
        Mockito.verify(methodVisitor).visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Class.class), "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);
    }

    @Test
    public void testMethodCached() throws Exception {
        Mockito.when(implementationContext.cache(ArgumentMatchers.any(StackManipulation.class), ArgumentMatchers.any(TypeDescription.class))).thenReturn(fieldDescription);
        StackManipulation.Size size = MethodConstant.of(methodDescription).cached().apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(1));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(1));
        Mockito.verify(methodVisitor).visitFieldInsn(Opcodes.GETSTATIC, MethodConstantTest.BAZ, MethodConstantTest.FOO, MethodConstantTest.QUX);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verify(implementationContext).cache(MethodConstant.of(methodDescription), TypeDescription.ForLoadedType.of(Method.class));
        Mockito.verifyNoMoreInteractions(implementationContext);
    }

    @Test
    public void testMethodPrivileged() throws Exception {
        Mockito.when(methodDescription.isMethod()).thenReturn(true);
        Mockito.when(implementationContext.register(ArgumentMatchers.any(AuxiliaryType.class))).thenReturn(auxiliaryType);
        Mockito.when(auxiliaryType.getDeclaredMethods()).thenReturn(new MethodList.Explicit<MethodDescription.InDefinedShape>(auxiliaryConstructor));
        StackManipulation.Size size = ofPrivileged(methodDescription).apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(5));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(8));
        Mockito.verify(methodVisitor).visitMethodInsn(Opcodes.INVOKESPECIAL, MethodConstantTest.QUX, MethodConstantTest.BAR, MethodConstantTest.FOO, false);
    }

    @Test
    public void testMethodPrivilegedCached() throws Exception {
        Mockito.when(implementationContext.cache(ArgumentMatchers.any(StackManipulation.class), ArgumentMatchers.any(TypeDescription.class))).thenReturn(fieldDescription);
        StackManipulation.Size size = ofPrivileged(methodDescription).cached().apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(1));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(1));
        Mockito.verify(methodVisitor).visitFieldInsn(Opcodes.GETSTATIC, MethodConstantTest.BAZ, MethodConstantTest.FOO, MethodConstantTest.QUX);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verify(implementationContext).cache(ofPrivileged(methodDescription), TypeDescription.ForLoadedType.of(Method.class));
        Mockito.verifyNoMoreInteractions(implementationContext);
    }

    @Test
    public void testConstructor() throws Exception {
        Mockito.when(methodDescription.isConstructor()).thenReturn(true);
        StackManipulation.Size size = MethodConstant.of(methodDescription).apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(1));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(5));
        Mockito.verify(methodVisitor).visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Class.class), "getDeclaredConstructor", "([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;", false);
    }

    @Test
    public void testConstructorPublic() {
        Mockito.when(methodDescription.isConstructor()).thenReturn(true);
        Mockito.when(methodDescription.isPublic()).thenReturn(true);
        StackManipulation.Size size = MethodConstant.of(methodDescription).apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(1));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(5));
        Mockito.verify(methodVisitor).visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Class.class), "getConstructor", "([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;", false);
    }

    @Test
    public void testConstructorCached() throws Exception {
        Mockito.when(methodDescription.isConstructor()).thenReturn(true);
        Mockito.when(implementationContext.cache(ArgumentMatchers.any(StackManipulation.class), ArgumentMatchers.any(TypeDescription.class))).thenReturn(fieldDescription);
        StackManipulation.Size size = MethodConstant.of(methodDescription).cached().apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(1));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(1));
        Mockito.verify(methodVisitor).visitFieldInsn(Opcodes.GETSTATIC, MethodConstantTest.BAZ, MethodConstantTest.FOO, MethodConstantTest.QUX);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verify(implementationContext).cache(MethodConstant.of(methodDescription), TypeDescription.ForLoadedType.of(Constructor.class));
        Mockito.verifyNoMoreInteractions(implementationContext);
    }

    @Test
    public void testConstructorPrivileged() throws Exception {
        Mockito.when(methodDescription.isConstructor()).thenReturn(true);
        Mockito.when(implementationContext.register(ArgumentMatchers.any(AuxiliaryType.class))).thenReturn(auxiliaryType);
        Mockito.when(auxiliaryType.getDeclaredMethods()).thenReturn(new MethodList.Explicit<MethodDescription.InDefinedShape>(auxiliaryConstructor));
        StackManipulation.Size size = ofPrivileged(methodDescription).apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(4));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(7));
        Mockito.verify(methodVisitor).visitMethodInsn(Opcodes.INVOKESPECIAL, MethodConstantTest.QUX, MethodConstantTest.BAR, MethodConstantTest.FOO, false);
    }

    @Test
    public void testConstructorPrivilegedCached() throws Exception {
        Mockito.when(methodDescription.isConstructor()).thenReturn(true);
        Mockito.when(implementationContext.cache(ArgumentMatchers.any(StackManipulation.class), ArgumentMatchers.any(TypeDescription.class))).thenReturn(fieldDescription);
        StackManipulation.Size size = ofPrivileged(methodDescription).cached().apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(1));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(1));
        Mockito.verify(methodVisitor).visitFieldInsn(Opcodes.GETSTATIC, MethodConstantTest.BAZ, MethodConstantTest.FOO, MethodConstantTest.QUX);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verify(implementationContext).cache(ofPrivileged(methodDescription), TypeDescription.ForLoadedType.of(Constructor.class));
        Mockito.verifyNoMoreInteractions(implementationContext);
    }

    @Test(expected = IllegalStateException.class)
    public void testTypeInitializer() throws Exception {
        Mockito.when(methodDescription.isTypeInitializer()).thenReturn(true);
        MethodConstant.CanCache methodConstant = of(methodDescription);
        MatcherAssert.assertThat(methodConstant.isValid(), CoreMatchers.is(false));
        MatcherAssert.assertThat(methodConstant.cached().isValid(), CoreMatchers.is(false));
        methodConstant.apply(methodVisitor, implementationContext);
    }
}

