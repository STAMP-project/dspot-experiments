package net.bytebuddy.implementation.auxiliary;


import java.io.Serializable;
import java.util.Collections;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodAccessorFactory;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
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

import static net.bytebuddy.implementation.MethodAccessorFactory.AccessType.DEFAULT;
import static net.bytebuddy.implementation.auxiliary.TypeProxy.AbstractMethodErrorThrow.INSTANCE;


public class TypeProxyCreationTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private Implementation.Target implementationTarget;

    @Mock
    private TypeProxy.InvocationFactory invocationFactory;

    @Mock
    private MethodAccessorFactory methodAccessorFactory;

    @Mock
    private Implementation.SpecialMethodInvocation specialMethodInvocation;

    @Mock
    private MethodDescription.InDefinedShape proxyMethod;

    private TypeDescription foo;

    private MethodList<?> fooMethods;

    private int modifiers;

    @Test
    public void testAllIllegal() throws Exception {
        Mockito.when(implementationTarget.getInstrumentedType()).thenReturn(foo);
        Mockito.when(invocationFactory.invoke(ArgumentMatchers.eq(implementationTarget), ArgumentMatchers.eq(foo), ArgumentMatchers.any(MethodDescription.class))).thenReturn(specialMethodInvocation);
        TypeDescription dynamicType = new TypeProxy(foo, implementationTarget, invocationFactory, true, false).make(TypeProxyCreationTest.BAR, ClassFileVersion.ofThisVm(), methodAccessorFactory).getTypeDescription();
        MatcherAssert.assertThat(dynamicType.getModifiers(), CoreMatchers.is(modifiers));
        MatcherAssert.assertThat(dynamicType.getSuperClass().asErasure(), CoreMatchers.is(foo));
        MatcherAssert.assertThat(dynamicType.getInterfaces(), CoreMatchers.is(((TypeList.Generic) (new TypeList.Generic.Empty()))));
        MatcherAssert.assertThat(dynamicType.getName(), CoreMatchers.is(TypeProxyCreationTest.BAR));
        MatcherAssert.assertThat(dynamicType.getDeclaredMethods().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(dynamicType.isAssignableTo(Serializable.class), CoreMatchers.is(false));
        Mockito.verifyZeroInteractions(methodAccessorFactory);
        for (MethodDescription methodDescription : fooMethods) {
            Mockito.verify(invocationFactory).invoke(implementationTarget, foo, methodDescription);
        }
        Mockito.verifyNoMoreInteractions(invocationFactory);
        Mockito.verify(specialMethodInvocation, Mockito.times(fooMethods.size())).isValid();
        Mockito.verifyNoMoreInteractions(specialMethodInvocation);
    }

    @Test
    public void testAllLegal() throws Exception {
        Mockito.when(implementationTarget.getInstrumentedType()).thenReturn(foo);
        Mockito.when(invocationFactory.invoke(ArgumentMatchers.eq(implementationTarget), ArgumentMatchers.eq(foo), ArgumentMatchers.any(MethodDescription.class))).thenReturn(specialMethodInvocation);
        Mockito.when(specialMethodInvocation.isValid()).thenReturn(true);
        Mockito.when(specialMethodInvocation.apply(ArgumentMatchers.any(MethodVisitor.class), ArgumentMatchers.any(Implementation.Context.class))).thenReturn(new StackManipulation.Size(0, 0));
        Mockito.when(methodAccessorFactory.registerAccessorFor(specialMethodInvocation, DEFAULT)).thenReturn(proxyMethod);
        TypeDescription dynamicType = new TypeProxy(foo, implementationTarget, invocationFactory, true, false).make(TypeProxyCreationTest.BAR, ClassFileVersion.ofThisVm(), methodAccessorFactory).getTypeDescription();
        MatcherAssert.assertThat(dynamicType.getModifiers(), CoreMatchers.is(modifiers));
        MatcherAssert.assertThat(dynamicType.getSuperClass().asErasure(), CoreMatchers.is(foo));
        MatcherAssert.assertThat(dynamicType.getInterfaces(), CoreMatchers.is(((TypeList.Generic) (new TypeList.Generic.Empty()))));
        MatcherAssert.assertThat(dynamicType.getName(), CoreMatchers.is(TypeProxyCreationTest.BAR));
        MatcherAssert.assertThat(dynamicType.getDeclaredMethods().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(dynamicType.isAssignableTo(Serializable.class), CoreMatchers.is(false));
        Mockito.verify(methodAccessorFactory, Mockito.times(fooMethods.size())).registerAccessorFor(specialMethodInvocation, DEFAULT);
        for (MethodDescription methodDescription : fooMethods) {
            Mockito.verify(invocationFactory).invoke(implementationTarget, foo, methodDescription);
        }
        Mockito.verifyNoMoreInteractions(invocationFactory);
        Mockito.verify(specialMethodInvocation, Mockito.times(fooMethods.size())).isValid();
        Mockito.verifyNoMoreInteractions(specialMethodInvocation);
    }

    @Test
    public void testAllLegalSerializable() throws Exception {
        Mockito.when(implementationTarget.getInstrumentedType()).thenReturn(foo);
        Mockito.when(invocationFactory.invoke(ArgumentMatchers.eq(implementationTarget), ArgumentMatchers.eq(foo), ArgumentMatchers.any(MethodDescription.class))).thenReturn(specialMethodInvocation);
        Mockito.when(specialMethodInvocation.isValid()).thenReturn(true);
        Mockito.when(specialMethodInvocation.apply(ArgumentMatchers.any(MethodVisitor.class), ArgumentMatchers.any(Implementation.Context.class))).thenReturn(new StackManipulation.Size(0, 0));
        Mockito.when(methodAccessorFactory.registerAccessorFor(specialMethodInvocation, DEFAULT)).thenReturn(proxyMethod);
        TypeDescription dynamicType = new TypeProxy(foo, implementationTarget, invocationFactory, true, true).make(TypeProxyCreationTest.BAR, ClassFileVersion.ofThisVm(), methodAccessorFactory).getTypeDescription();
        MatcherAssert.assertThat(dynamicType.getModifiers(), CoreMatchers.is(modifiers));
        MatcherAssert.assertThat(dynamicType.getSuperClass().asErasure(), CoreMatchers.is(foo));
        MatcherAssert.assertThat(dynamicType.getInterfaces(), CoreMatchers.is(((TypeList.Generic) (new TypeList.Generic.ForLoadedTypes(Serializable.class)))));
        MatcherAssert.assertThat(dynamicType.getName(), CoreMatchers.is(TypeProxyCreationTest.BAR));
        MatcherAssert.assertThat(dynamicType.getDeclaredMethods().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(dynamicType.isAssignableTo(Serializable.class), CoreMatchers.is(true));
        Mockito.verify(methodAccessorFactory, Mockito.times(fooMethods.size())).registerAccessorFor(specialMethodInvocation, DEFAULT);
        for (MethodDescription methodDescription : fooMethods) {
            Mockito.verify(invocationFactory).invoke(implementationTarget, foo, methodDescription);
        }
        Mockito.verifyNoMoreInteractions(invocationFactory);
        Mockito.verify(specialMethodInvocation, Mockito.times(fooMethods.size())).isValid();
        Mockito.verifyNoMoreInteractions(specialMethodInvocation);
    }

    @Test
    public void testAllLegalNotIgnoreFinalizer() throws Exception {
        Mockito.when(implementationTarget.getInstrumentedType()).thenReturn(foo);
        Mockito.when(invocationFactory.invoke(ArgumentMatchers.eq(implementationTarget), ArgumentMatchers.eq(foo), ArgumentMatchers.any(MethodDescription.class))).thenReturn(specialMethodInvocation);
        Mockito.when(specialMethodInvocation.isValid()).thenReturn(true);
        Mockito.when(specialMethodInvocation.apply(ArgumentMatchers.any(MethodVisitor.class), ArgumentMatchers.any(Implementation.Context.class))).thenReturn(new StackManipulation.Size(0, 0));
        Mockito.when(methodAccessorFactory.registerAccessorFor(specialMethodInvocation, DEFAULT)).thenReturn(proxyMethod);
        TypeDescription dynamicType = new TypeProxy(foo, implementationTarget, invocationFactory, false, false).make(TypeProxyCreationTest.BAR, ClassFileVersion.ofThisVm(), methodAccessorFactory).getTypeDescription();
        MatcherAssert.assertThat(dynamicType.getModifiers(), CoreMatchers.is(modifiers));
        MatcherAssert.assertThat(dynamicType.getSuperClass().asErasure(), CoreMatchers.is(foo));
        MatcherAssert.assertThat(dynamicType.getInterfaces(), CoreMatchers.is(((TypeList.Generic) (new TypeList.Generic.Empty()))));
        MatcherAssert.assertThat(dynamicType.getName(), CoreMatchers.is(TypeProxyCreationTest.BAR));
        MatcherAssert.assertThat(dynamicType.getDeclaredMethods().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(dynamicType.isAssignableTo(Serializable.class), CoreMatchers.is(false));
        Mockito.verify(methodAccessorFactory, Mockito.times(((fooMethods.size()) + 1))).registerAccessorFor(specialMethodInvocation, DEFAULT);
        for (MethodDescription methodDescription : fooMethods) {
            Mockito.verify(invocationFactory).invoke(implementationTarget, foo, methodDescription);
        }
        Mockito.verify(invocationFactory).invoke(implementationTarget, foo, new MethodDescription.ForLoadedMethod(Object.class.getDeclaredMethod("finalize")));
        Mockito.verifyNoMoreInteractions(invocationFactory);
        Mockito.verify(specialMethodInvocation, Mockito.times(((fooMethods.size()) + 1))).isValid();
        Mockito.verifyNoMoreInteractions(specialMethodInvocation);
    }

    @Test
    public void testForConstructorConstruction() throws Exception {
        Mockito.when(implementationTarget.getInstrumentedType()).thenReturn(foo);
        Mockito.when(invocationFactory.invoke(ArgumentMatchers.eq(implementationTarget), ArgumentMatchers.eq(foo), ArgumentMatchers.any(MethodDescription.class))).thenReturn(specialMethodInvocation);
        Mockito.when(specialMethodInvocation.isValid()).thenReturn(true);
        Mockito.when(specialMethodInvocation.apply(ArgumentMatchers.any(MethodVisitor.class), ArgumentMatchers.any(Implementation.Context.class))).thenReturn(new StackManipulation.Size(0, 0));
        Mockito.when(methodAccessorFactory.registerAccessorFor(specialMethodInvocation, DEFAULT)).thenReturn(proxyMethod);
        StackManipulation stackManipulation = new TypeProxy.ForSuperMethodByConstructor(foo, implementationTarget, Collections.singletonList(((TypeDescription) (of(Void.class)))), true, false);
        MethodVisitor methodVisitor = Mockito.mock(MethodVisitor.class);
        Implementation.Context implementationContext = Mockito.mock(Implementation.Context.class);
        Mockito.when(implementationContext.register(ArgumentMatchers.any(AuxiliaryType.class))).thenReturn(foo);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(1));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(3));
        Mockito.verify(implementationContext).register(ArgumentMatchers.any(AuxiliaryType.class));
        Mockito.verifyNoMoreInteractions(implementationContext);
        Mockito.verify(methodVisitor).visitTypeInsn(Opcodes.NEW, Type.getInternalName(TypeProxyCreationTest.Foo.class));
        Mockito.verify(methodVisitor, Mockito.times(2)).visitInsn(Opcodes.DUP);
        Mockito.verify(methodVisitor).visitInsn(Opcodes.ACONST_NULL);
        Mockito.verify(methodVisitor).visitMethodInsn(Opcodes.INVOKESPECIAL, foo.getInternalName(), MethodDescription.CONSTRUCTOR_INTERNAL_NAME, foo.getDeclaredMethods().filter(ElementMatchers.isConstructor()).getOnly().getDescriptor(), false);
        Mockito.verify(methodVisitor).visitFieldInsn(Opcodes.PUTFIELD, foo.getInternalName(), TypeProxy.INSTANCE_FIELD, Type.getDescriptor(Void.class));
        Mockito.verify(methodVisitor).visitVarInsn(Opcodes.ALOAD, 0);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }

    @Test
    public void testForDefaultMethodConstruction() throws Exception {
        Mockito.when(implementationTarget.getInstrumentedType()).thenReturn(foo);
        Mockito.when(invocationFactory.invoke(ArgumentMatchers.eq(implementationTarget), ArgumentMatchers.eq(foo), ArgumentMatchers.any(MethodDescription.class))).thenReturn(specialMethodInvocation);
        Mockito.when(specialMethodInvocation.isValid()).thenReturn(true);
        Mockito.when(specialMethodInvocation.apply(ArgumentMatchers.any(MethodVisitor.class), ArgumentMatchers.any(Implementation.Context.class))).thenReturn(new StackManipulation.Size(0, 0));
        Mockito.when(methodAccessorFactory.registerAccessorFor(specialMethodInvocation, DEFAULT)).thenReturn(proxyMethod);
        StackManipulation stackManipulation = new TypeProxy.ForDefaultMethod(foo, implementationTarget, false);
        MethodVisitor methodVisitor = Mockito.mock(MethodVisitor.class);
        Implementation.Context implementationContext = Mockito.mock(Implementation.Context.class);
        Mockito.when(implementationContext.register(ArgumentMatchers.any(AuxiliaryType.class))).thenReturn(foo);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(0));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(2));
        Mockito.verify(implementationContext).register(ArgumentMatchers.any(AuxiliaryType.class));
        Mockito.verifyNoMoreInteractions(implementationContext);
        Mockito.verify(methodVisitor).visitTypeInsn(Opcodes.NEW, Type.getInternalName(TypeProxyCreationTest.Foo.class));
        Mockito.verify(methodVisitor, Mockito.times(2)).visitInsn(Opcodes.DUP);
        Mockito.verify(methodVisitor).visitMethodInsn(Opcodes.INVOKESPECIAL, foo.getInternalName(), MethodDescription.CONSTRUCTOR_INTERNAL_NAME, foo.getDeclaredMethods().filter(ElementMatchers.isConstructor()).getOnly().getDescriptor(), false);
        Mockito.verify(methodVisitor).visitFieldInsn(Opcodes.PUTFIELD, foo.getInternalName(), TypeProxy.INSTANCE_FIELD, Type.getDescriptor(Void.class));
        Mockito.verify(methodVisitor).visitVarInsn(Opcodes.ALOAD, 0);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }

    @Test
    public void testForReflectionFactoryConstruction() throws Exception {
        Mockito.when(implementationTarget.getInstrumentedType()).thenReturn(foo);
        Mockito.when(invocationFactory.invoke(ArgumentMatchers.eq(implementationTarget), ArgumentMatchers.eq(foo), ArgumentMatchers.any(MethodDescription.class))).thenReturn(specialMethodInvocation);
        Mockito.when(specialMethodInvocation.isValid()).thenReturn(true);
        Mockito.when(specialMethodInvocation.apply(ArgumentMatchers.any(MethodVisitor.class), ArgumentMatchers.any(Implementation.Context.class))).thenReturn(new StackManipulation.Size(0, 0));
        Mockito.when(methodAccessorFactory.registerAccessorFor(specialMethodInvocation, DEFAULT)).thenReturn(proxyMethod);
        StackManipulation stackManipulation = new TypeProxy.ForSuperMethodByReflectionFactory(foo, implementationTarget, true, false);
        MethodVisitor methodVisitor = Mockito.mock(MethodVisitor.class);
        Implementation.Context implementationContext = Mockito.mock(Implementation.Context.class);
        Mockito.when(implementationContext.register(ArgumentMatchers.any(AuxiliaryType.class))).thenReturn(of(TypeProxyCreationTest.FooProxyMake.class));
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(1));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(3));
        Mockito.verify(implementationContext).register(ArgumentMatchers.any(AuxiliaryType.class));
        Mockito.verifyNoMoreInteractions(implementationContext);
        Mockito.verify(methodVisitor).visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(TypeProxyCreationTest.FooProxyMake.class), TypeProxy.REFLECTION_METHOD, Type.getMethodDescriptor(TypeProxyCreationTest.FooProxyMake.class.getDeclaredMethod("make")), false);
        Mockito.verify(methodVisitor).visitInsn(Opcodes.DUP);
        Mockito.verify(methodVisitor).visitFieldInsn(Opcodes.PUTFIELD, Type.getInternalName(TypeProxyCreationTest.FooProxyMake.class), TypeProxy.INSTANCE_FIELD, Type.getDescriptor(Void.class));
        Mockito.verify(methodVisitor).visitVarInsn(Opcodes.ALOAD, 0);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }

    @Test
    public void testImplementationIsValid() throws Exception {
        MatcherAssert.assertThat(INSTANCE.isValid(), CoreMatchers.is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAccessorIsValid() throws Exception {
        TypeProxy typeProxy = new TypeProxy(Mockito.mock(TypeDescription.class), Mockito.mock(Implementation.Target.class), Mockito.mock(TypeProxy.InvocationFactory.class), false, false);
        TypeProxy.MethodCall methodCall = typeProxy.new MethodCall(Mockito.mock(MethodAccessorFactory.class));
        TypeDescription instrumentedType = Mockito.mock(TypeDescription.class);
        FieldList<FieldDescription.InDefinedShape> fieldList = Mockito.mock(FieldList.class);
        Mockito.when(fieldList.filter(ArgumentMatchers.any(ElementMatcher.class))).thenReturn(fieldList);
        Mockito.when(fieldList.getOnly()).thenReturn(Mockito.mock(FieldDescription.InDefinedShape.class));
        Mockito.when(instrumentedType.getDeclaredFields()).thenReturn(fieldList);
        TypeProxy.MethodCall.Appender appender = methodCall.new Appender(instrumentedType);
        Implementation.SpecialMethodInvocation specialMethodInvocation = Mockito.mock(Implementation.SpecialMethodInvocation.class);
        Mockito.when(specialMethodInvocation.isValid()).thenReturn(true);
        StackManipulation stackManipulation = appender.new AccessorMethodInvocation(Mockito.mock(MethodDescription.class), specialMethodInvocation);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        Mockito.verify(specialMethodInvocation).isValid();
        Mockito.verifyNoMoreInteractions(specialMethodInvocation);
    }

    @SuppressWarnings("unused")
    public static class Foo {
        private Void target;

        public Foo(Void argument) {
        }
    }

    @SuppressWarnings("unused")
    public static class FooProxyMake {
        private Void target;

        public static TypeProxyCreationTest.FooProxyMake make() {
            return null;
        }
    }
}

