package net.bytebuddy.implementation;


import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.TypeInitializer;
import net.bytebuddy.implementation.attribute.AnnotationValueFilter;
import net.bytebuddy.implementation.auxiliary.AuxiliaryType;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static net.bytebuddy.description.type.TypeDescription.Generic.VOID;
import static net.bytebuddy.implementation.Implementation.Context.Default.FIELD_CACHE_PREFIX;
import static net.bytebuddy.implementation.MethodAccessorFactory.AccessType.DEFAULT;


@RunWith(Parameterized.class)
public class ImplementationContextDefaultTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String BAZ = "baz";

    private final boolean interfaceType;

    private final int accessorMethodModifiers;

    private final int cacheFieldModifiers;

    public ImplementationContextDefaultTest(boolean interfaceType, int accessorMethodModifiers, int cacheFieldModifiers) {
        this.interfaceType = interfaceType;
        this.accessorMethodModifiers = accessorMethodModifiers;
        this.cacheFieldModifiers = cacheFieldModifiers;
    }

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription instrumentedType;

    @Mock
    private TypeDescription firstDescription;

    @Mock
    private TypeDescription secondDescription;

    @Mock
    private TypeInitializer typeInitializer;

    @Mock
    private TypeInitializer otherTypeInitializer;

    @Mock
    private TypeInitializer thirdTypeInitializer;

    @Mock
    private ClassFileVersion classFileVersion;

    @Mock
    private ClassFileVersion auxiliaryClassFileVersion;

    @Mock
    private ClassVisitor classVisitor;

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private FieldVisitor fieldVisitor;

    @Mock
    private AuxiliaryType auxiliaryType;

    @Mock
    private AuxiliaryType otherAuxiliaryType;

    @Mock
    private DynamicType firstDynamicType;

    @Mock
    private DynamicType secondDynamicType;

    @Mock
    private TypeDescription.Generic firstFieldType;

    @Mock
    private TypeDescription.Generic secondFieldType;

    @Mock
    private TypeDescription firstRawFieldType;

    @Mock
    private TypeDescription secondRawFieldType;

    @Mock
    private StackManipulation firstFieldValue;

    @Mock
    private StackManipulation secondFieldValue;

    @Mock
    private TypeDescription.Generic firstSpecialReturnType;

    @Mock
    private TypeDescription.Generic secondSpecialReturnType;

    @Mock
    private TypeDescription firstRawSpecialReturnType;

    @Mock
    private TypeDescription secondRawSpecialReturnType;

    @Mock
    private TypeDescription.Generic firstSpecialParameterType;

    @Mock
    private TypeDescription.Generic secondSpecialParameterType;

    @Mock
    private TypeDescription firstRawSpecialParameterType;

    @Mock
    private TypeDescription secondRawSpecialParameterType;

    @Mock
    private TypeDescription.Generic firstSpecialExceptionType;

    @Mock
    private TypeDescription.Generic secondSpecialExceptionType;

    @Mock
    private TypeDescription firstRawSpecialExceptionType;

    @Mock
    private TypeDescription secondRawSpecialExceptionType;

    @Mock
    private ByteCodeAppender injectedCodeAppender;

    @Mock
    private ByteCodeAppender terminationAppender;

    @Mock
    private Implementation.SpecialMethodInvocation firstSpecialInvocation;

    @Mock
    private Implementation.SpecialMethodInvocation secondSpecialInvocation;

    @Mock
    private MethodDescription.InDefinedShape firstSpecialMethod;

    @Mock
    private MethodDescription.InDefinedShape secondSpecialMethod;

    @Mock
    private AuxiliaryType.NamingStrategy auxiliaryTypeNamingStrategy;

    @Mock
    private TypeDescription firstSpecialType;

    @Mock
    private TypeDescription secondSpecialType;

    @Mock
    private FieldDescription.InDefinedShape firstField;

    @Mock
    private FieldDescription.InDefinedShape secondField;

    @Mock
    private TypeDescription firstFieldDeclaringType;

    @Mock
    private TypeDescription secondFieldDeclaringType;

    private TypeList.Generic firstSpecialExceptionTypes;

    private TypeList.Generic secondSpecialExceptionTypes;

    @Mock
    private AnnotationValueFilter.Factory annotationValueFilterFactory;

    @Mock
    private TypeInitializer.Drain drain;

    @Test
    public void testInitialContextIsEmpty() throws Exception {
        Implementation.Context.ExtractableView implementationContext = new Implementation.Context.Default(instrumentedType, classFileVersion, auxiliaryTypeNamingStrategy, typeInitializer, auxiliaryClassFileVersion);
        MatcherAssert.assertThat(implementationContext.getAuxiliaryTypes().size(), CoreMatchers.is(0));
        implementationContext.drain(drain, classVisitor, annotationValueFilterFactory);
        Mockito.verifyZeroInteractions(classVisitor);
        Mockito.verify(drain).apply(classVisitor, typeInitializer, implementationContext);
        Mockito.verifyNoMoreInteractions(drain);
    }

    @Test
    public void testAuxiliaryTypeRegistration() throws Exception {
        Implementation.Context.ExtractableView implementationContext = new Implementation.Context.Default(instrumentedType, classFileVersion, auxiliaryTypeNamingStrategy, typeInitializer, auxiliaryClassFileVersion);
        MatcherAssert.assertThat(implementationContext.getAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(implementationContext.register(auxiliaryType), CoreMatchers.is(firstDescription));
        MatcherAssert.assertThat(implementationContext.getAuxiliaryTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(implementationContext.getAuxiliaryTypes().contains(firstDynamicType), CoreMatchers.is(true));
        MatcherAssert.assertThat(implementationContext.register(otherAuxiliaryType), CoreMatchers.is(secondDescription));
        MatcherAssert.assertThat(implementationContext.getAuxiliaryTypes().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(implementationContext.getAuxiliaryTypes().contains(firstDynamicType), CoreMatchers.is(true));
        MatcherAssert.assertThat(implementationContext.getAuxiliaryTypes().contains(secondDynamicType), CoreMatchers.is(true));
        MatcherAssert.assertThat(implementationContext.register(auxiliaryType), CoreMatchers.is(firstDescription));
        MatcherAssert.assertThat(implementationContext.getAuxiliaryTypes().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(implementationContext.getAuxiliaryTypes().contains(firstDynamicType), CoreMatchers.is(true));
        MatcherAssert.assertThat(implementationContext.getAuxiliaryTypes().contains(secondDynamicType), CoreMatchers.is(true));
    }

    @Test
    public void testDrainEmpty() throws Exception {
        Implementation.Context.ExtractableView implementationContext = new Implementation.Context.Default(instrumentedType, classFileVersion, auxiliaryTypeNamingStrategy, typeInitializer, auxiliaryClassFileVersion);
        implementationContext.drain(drain, classVisitor, annotationValueFilterFactory);
        Mockito.verifyZeroInteractions(classVisitor);
        Mockito.verify(drain).apply(classVisitor, typeInitializer, implementationContext);
        Mockito.verifyNoMoreInteractions(drain);
    }

    @Test
    public void testDrainNoUserCodeNoInjectedCodeNoTypeInitializer() throws Exception {
        Implementation.Context.ExtractableView implementationContext = new Implementation.Context.Default(instrumentedType, classFileVersion, auxiliaryTypeNamingStrategy, typeInitializer, auxiliaryClassFileVersion);
        implementationContext.drain(drain, classVisitor, annotationValueFilterFactory);
        Mockito.verifyZeroInteractions(classVisitor);
        Mockito.verifyZeroInteractions(typeInitializer);
        Mockito.verify(drain).apply(classVisitor, typeInitializer, implementationContext);
        Mockito.verifyNoMoreInteractions(drain);
    }

    @Test
    public void testDrainUserCodeNoInjectedCodeNoTypeInitializer() throws Exception {
        Implementation.Context.ExtractableView implementationContext = new Implementation.Context.Default(instrumentedType, classFileVersion, auxiliaryTypeNamingStrategy, typeInitializer, auxiliaryClassFileVersion);
        implementationContext.drain(drain, classVisitor, annotationValueFilterFactory);
        Mockito.verifyZeroInteractions(classVisitor);
        Mockito.verifyZeroInteractions(typeInitializer);
        Mockito.verify(drain).apply(classVisitor, typeInitializer, implementationContext);
        Mockito.verifyNoMoreInteractions(drain);
    }

    @Test
    public void testDrainFieldCacheEntries() throws Exception {
        Implementation.Context.ExtractableView implementationContext = new Implementation.Context.Default(instrumentedType, classFileVersion, auxiliaryTypeNamingStrategy, typeInitializer, auxiliaryClassFileVersion);
        FieldDescription firstField = implementationContext.cache(firstFieldValue, firstRawFieldType);
        MatcherAssert.assertThat(implementationContext.cache(firstFieldValue, firstRawFieldType), CoreMatchers.is(firstField));
        FieldDescription secondField = implementationContext.cache(secondFieldValue, secondRawFieldType);
        MatcherAssert.assertThat(implementationContext.cache(secondFieldValue, secondRawFieldType), CoreMatchers.is(secondField));
        MatcherAssert.assertThat(firstField.getName(), CoreMatchers.not(secondField.getName()));
        Mockito.when(typeInitializer.expandWith(ArgumentMatchers.any(ByteCodeAppender.class))).thenReturn(otherTypeInitializer);
        Mockito.when(otherTypeInitializer.expandWith(ArgumentMatchers.any(ByteCodeAppender.class))).thenReturn(thirdTypeInitializer);
        Mockito.when(thirdTypeInitializer.isDefined()).thenReturn(true);
        implementationContext.drain(drain, classVisitor, annotationValueFilterFactory);
        Mockito.verify(classVisitor).visitField(ArgumentMatchers.eq(cacheFieldModifiers), Mockito.startsWith(FIELD_CACHE_PREFIX), ArgumentMatchers.eq(ImplementationContextDefaultTest.BAR), Mockito.<String>isNull(), Mockito.isNull());
        Mockito.verify(classVisitor).visitField(ArgumentMatchers.eq(cacheFieldModifiers), Mockito.startsWith(FIELD_CACHE_PREFIX), ArgumentMatchers.eq(ImplementationContextDefaultTest.QUX), Mockito.<String>isNull(), Mockito.isNull());
        Mockito.verify(typeInitializer).expandWith(ArgumentMatchers.any(ByteCodeAppender.class));
        Mockito.verify(otherTypeInitializer).expandWith(ArgumentMatchers.any(ByteCodeAppender.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testCannotRegisterFieldAfterDraining() throws Exception {
        Implementation.Context.ExtractableView implementationContext = new Implementation.Context.Default(instrumentedType, classFileVersion, auxiliaryTypeNamingStrategy, typeInitializer, auxiliaryClassFileVersion);
        implementationContext.drain(drain, classVisitor, annotationValueFilterFactory);
        Mockito.verifyZeroInteractions(classVisitor);
        Mockito.verify(drain).apply(classVisitor, typeInitializer, implementationContext);
        Mockito.verifyNoMoreInteractions(drain);
        implementationContext.cache(firstFieldValue, firstRawFieldType);
    }

    @Test
    public void testAccessorMethodRegistration() throws Exception {
        Implementation.Context.Default implementationContext = new Implementation.Context.Default(instrumentedType, classFileVersion, auxiliaryTypeNamingStrategy, typeInitializer, auxiliaryClassFileVersion);
        MethodDescription.InDefinedShape firstMethodDescription = implementationContext.registerAccessorFor(firstSpecialInvocation, DEFAULT);
        MatcherAssert.assertThat(firstMethodDescription.getParameters(), CoreMatchers.is(((ParameterList) (new ParameterList.Explicit.ForTypes(firstMethodDescription, firstSpecialParameterType)))));
        MatcherAssert.assertThat(firstMethodDescription.getReturnType(), CoreMatchers.is(firstSpecialReturnType));
        MatcherAssert.assertThat(firstMethodDescription.getInternalName(), CoreMatchers.startsWith(ImplementationContextDefaultTest.FOO));
        MatcherAssert.assertThat(firstMethodDescription.getModifiers(), CoreMatchers.is(accessorMethodModifiers));
        MatcherAssert.assertThat(firstMethodDescription.getExceptionTypes(), CoreMatchers.is(firstSpecialExceptionTypes));
        MatcherAssert.assertThat(implementationContext.registerAccessorFor(firstSpecialInvocation, DEFAULT), CoreMatchers.is(firstMethodDescription));
        Mockito.when(secondSpecialMethod.isStatic()).thenReturn(true);
        MethodDescription.InDefinedShape secondMethodDescription = implementationContext.registerAccessorFor(secondSpecialInvocation, DEFAULT);
        MatcherAssert.assertThat(secondMethodDescription.getParameters(), CoreMatchers.is(((ParameterList) (new ParameterList.Explicit.ForTypes(secondMethodDescription, secondSpecialParameterType)))));
        MatcherAssert.assertThat(secondMethodDescription.getReturnType(), CoreMatchers.is(secondSpecialReturnType));
        MatcherAssert.assertThat(secondMethodDescription.getInternalName(), CoreMatchers.startsWith(ImplementationContextDefaultTest.BAR));
        MatcherAssert.assertThat(secondMethodDescription.getModifiers(), CoreMatchers.is(((accessorMethodModifiers) | (Opcodes.ACC_STATIC))));
        MatcherAssert.assertThat(secondMethodDescription.getExceptionTypes(), CoreMatchers.is(secondSpecialExceptionTypes));
        MatcherAssert.assertThat(implementationContext.registerAccessorFor(firstSpecialInvocation, DEFAULT), CoreMatchers.is(firstMethodDescription));
        MatcherAssert.assertThat(implementationContext.registerAccessorFor(secondSpecialInvocation, DEFAULT), CoreMatchers.is(secondMethodDescription));
        implementationContext.drain(drain, classVisitor, annotationValueFilterFactory);
        Mockito.verify(classVisitor).visitMethod(ArgumentMatchers.eq(accessorMethodModifiers), Mockito.startsWith(ImplementationContextDefaultTest.FOO), ArgumentMatchers.eq(((("(" + (ImplementationContextDefaultTest.BAZ)) + ")") + (ImplementationContextDefaultTest.QUX))), Mockito.<String>isNull(), AdditionalMatchers.aryEq(new String[]{ ImplementationContextDefaultTest.FOO }));
        Mockito.verify(classVisitor).visitMethod(ArgumentMatchers.eq(((accessorMethodModifiers) | (Opcodes.ACC_STATIC))), Mockito.startsWith(ImplementationContextDefaultTest.BAR), ArgumentMatchers.eq(((("(" + (ImplementationContextDefaultTest.BAR)) + ")") + (ImplementationContextDefaultTest.FOO))), Mockito.<String>isNull(), AdditionalMatchers.aryEq(new String[]{ ImplementationContextDefaultTest.BAZ }));
    }

    @Test
    public void testAccessorMethodRegistrationWritesFirst() throws Exception {
        Implementation.Context.Default implementationContext = new Implementation.Context.Default(instrumentedType, classFileVersion, auxiliaryTypeNamingStrategy, typeInitializer, auxiliaryClassFileVersion);
        MethodDescription firstMethodDescription = implementationContext.registerAccessorFor(firstSpecialInvocation, DEFAULT);
        MatcherAssert.assertThat(implementationContext.registerAccessorFor(firstSpecialInvocation, DEFAULT), CoreMatchers.is(firstMethodDescription));
        implementationContext.drain(drain, classVisitor, annotationValueFilterFactory);
        Mockito.verify(classVisitor).visitMethod(ArgumentMatchers.eq(accessorMethodModifiers), Mockito.startsWith(ImplementationContextDefaultTest.FOO), ArgumentMatchers.eq(((("(" + (ImplementationContextDefaultTest.BAZ)) + ")") + (ImplementationContextDefaultTest.QUX))), Mockito.<String>isNull(), AdditionalMatchers.aryEq(new String[]{ ImplementationContextDefaultTest.FOO }));
        Mockito.verify(methodVisitor).visitCode();
        Mockito.verify(methodVisitor).visitVarInsn(Opcodes.ALOAD, 0);
        Mockito.verify(methodVisitor).visitVarInsn(Opcodes.ALOAD, 1);
        Mockito.verify(firstSpecialInvocation).apply(methodVisitor, implementationContext);
        Mockito.verify(methodVisitor).visitInsn(Opcodes.ARETURN);
        Mockito.verify(methodVisitor).visitMaxs(2, 1);
        Mockito.verify(methodVisitor).visitEnd();
    }

    @Test
    public void testAccessorMethodRegistrationWritesSecond() throws Exception {
        Mockito.when(secondSpecialMethod.isStatic()).thenReturn(true);
        Implementation.Context.Default implementationContext = new Implementation.Context.Default(instrumentedType, classFileVersion, auxiliaryTypeNamingStrategy, typeInitializer, auxiliaryClassFileVersion);
        MethodDescription secondMethodDescription = implementationContext.registerAccessorFor(secondSpecialInvocation, DEFAULT);
        MatcherAssert.assertThat(implementationContext.registerAccessorFor(secondSpecialInvocation, DEFAULT), CoreMatchers.is(secondMethodDescription));
        implementationContext.drain(drain, classVisitor, annotationValueFilterFactory);
        Mockito.verify(classVisitor).visitMethod(ArgumentMatchers.eq(((accessorMethodModifiers) | (Opcodes.ACC_STATIC))), Mockito.startsWith(ImplementationContextDefaultTest.BAR), ArgumentMatchers.eq(((("(" + (ImplementationContextDefaultTest.BAR)) + ")") + (ImplementationContextDefaultTest.FOO))), Mockito.<String>isNull(), AdditionalMatchers.aryEq(new String[]{ ImplementationContextDefaultTest.BAZ }));
        Mockito.verify(methodVisitor).visitCode();
        Mockito.verify(methodVisitor).visitVarInsn(Opcodes.ALOAD, 0);
        Mockito.verify(secondSpecialInvocation).apply(methodVisitor, implementationContext);
        Mockito.verify(methodVisitor).visitInsn(Opcodes.ARETURN);
        Mockito.verify(methodVisitor).visitMaxs(1, 0);
        Mockito.verify(methodVisitor).visitEnd();
    }

    @Test
    public void testFieldGetterRegistration() throws Exception {
        Implementation.Context.Default implementationContext = new Implementation.Context.Default(instrumentedType, classFileVersion, auxiliaryTypeNamingStrategy, typeInitializer, auxiliaryClassFileVersion);
        MethodDescription firstFieldGetter = implementationContext.registerGetterFor(firstField, DEFAULT);
        MatcherAssert.assertThat(firstFieldGetter.getParameters(), CoreMatchers.is(((ParameterList) (new ParameterList.Empty<net.bytebuddy.description.method.ParameterDescription>()))));
        MatcherAssert.assertThat(firstFieldGetter.getReturnType(), CoreMatchers.is(firstFieldType));
        MatcherAssert.assertThat(firstFieldGetter.getInternalName(), CoreMatchers.startsWith(ImplementationContextDefaultTest.FOO));
        MatcherAssert.assertThat(firstFieldGetter.getModifiers(), CoreMatchers.is(accessorMethodModifiers));
        MatcherAssert.assertThat(firstFieldGetter.getExceptionTypes(), CoreMatchers.is(((TypeList.Generic) (new TypeList.Generic.Empty()))));
        MatcherAssert.assertThat(implementationContext.registerGetterFor(firstField, DEFAULT), CoreMatchers.is(firstFieldGetter));
        Mockito.when(secondField.isStatic()).thenReturn(true);
        MethodDescription secondFieldGetter = implementationContext.registerGetterFor(secondField, DEFAULT);
        MatcherAssert.assertThat(secondFieldGetter.getParameters(), CoreMatchers.is(((ParameterList) (new ParameterList.Empty<net.bytebuddy.description.method.ParameterDescription>()))));
        MatcherAssert.assertThat(secondFieldGetter.getReturnType(), CoreMatchers.is(secondFieldType));
        MatcherAssert.assertThat(secondFieldGetter.getInternalName(), CoreMatchers.startsWith(ImplementationContextDefaultTest.BAR));
        MatcherAssert.assertThat(secondFieldGetter.getModifiers(), CoreMatchers.is(((accessorMethodModifiers) | (Opcodes.ACC_STATIC))));
        MatcherAssert.assertThat(secondFieldGetter.getExceptionTypes(), CoreMatchers.is(((TypeList.Generic) (new TypeList.Generic.Empty()))));
        MatcherAssert.assertThat(implementationContext.registerGetterFor(firstField, DEFAULT), CoreMatchers.is(firstFieldGetter));
        MatcherAssert.assertThat(implementationContext.registerGetterFor(secondField, DEFAULT), CoreMatchers.is(secondFieldGetter));
        implementationContext.drain(drain, classVisitor, annotationValueFilterFactory);
        Mockito.verify(classVisitor).visitMethod(ArgumentMatchers.eq(accessorMethodModifiers), Mockito.startsWith(ImplementationContextDefaultTest.FOO), ArgumentMatchers.eq(("()" + (ImplementationContextDefaultTest.BAR))), Mockito.<String>isNull(), Mockito.<String[]>isNull());
        Mockito.verify(classVisitor).visitMethod(ArgumentMatchers.eq(((accessorMethodModifiers) | (Opcodes.ACC_STATIC))), Mockito.startsWith(ImplementationContextDefaultTest.BAR), ArgumentMatchers.eq(("()" + (ImplementationContextDefaultTest.QUX))), Mockito.<String>isNull(), Mockito.<String[]>isNull());
    }

    @Test
    public void testFieldGetterRegistrationWritesFirst() throws Exception {
        Implementation.Context.Default implementationContext = new Implementation.Context.Default(instrumentedType, classFileVersion, auxiliaryTypeNamingStrategy, typeInitializer, auxiliaryClassFileVersion);
        MethodDescription firstMethodDescription = implementationContext.registerGetterFor(firstField, DEFAULT);
        MatcherAssert.assertThat(implementationContext.registerGetterFor(firstField, DEFAULT), CoreMatchers.is(firstMethodDescription));
        implementationContext.drain(drain, classVisitor, annotationValueFilterFactory);
        Mockito.verify(classVisitor).visitMethod(ArgumentMatchers.eq(accessorMethodModifiers), Mockito.startsWith(ImplementationContextDefaultTest.FOO), ArgumentMatchers.eq(("()" + (ImplementationContextDefaultTest.BAR))), Mockito.<String>isNull(), Mockito.<String[]>isNull());
        Mockito.verify(methodVisitor).visitCode();
        Mockito.verify(methodVisitor).visitVarInsn(Opcodes.ALOAD, 0);
        Mockito.verify(methodVisitor).visitFieldInsn(Opcodes.GETFIELD, ImplementationContextDefaultTest.QUX, ImplementationContextDefaultTest.FOO, ImplementationContextDefaultTest.BAR);
        Mockito.verify(methodVisitor).visitInsn(Opcodes.ARETURN);
        Mockito.verify(methodVisitor).visitMaxs(1, 1);
        Mockito.verify(methodVisitor).visitEnd();
    }

    @Test
    public void testFieldGetterRegistrationWritesSecond() throws Exception {
        Mockito.when(secondField.isStatic()).thenReturn(true);
        Implementation.Context.Default implementationContext = new Implementation.Context.Default(instrumentedType, classFileVersion, auxiliaryTypeNamingStrategy, typeInitializer, auxiliaryClassFileVersion);
        MethodDescription secondMethodDescription = implementationContext.registerGetterFor(secondField, DEFAULT);
        MatcherAssert.assertThat(implementationContext.registerGetterFor(secondField, DEFAULT), CoreMatchers.is(secondMethodDescription));
        implementationContext.drain(drain, classVisitor, annotationValueFilterFactory);
        Mockito.verify(classVisitor).visitMethod(ArgumentMatchers.eq(((accessorMethodModifiers) | (Opcodes.ACC_STATIC))), Mockito.startsWith(ImplementationContextDefaultTest.BAR), ArgumentMatchers.eq(("()" + (ImplementationContextDefaultTest.QUX))), Mockito.<String>isNull(), Mockito.<String[]>isNull());
        Mockito.verify(methodVisitor).visitCode();
        Mockito.verify(methodVisitor).visitFieldInsn(Opcodes.GETSTATIC, ImplementationContextDefaultTest.BAZ, ImplementationContextDefaultTest.BAR, ImplementationContextDefaultTest.FOO);
        Mockito.verify(methodVisitor).visitInsn(Opcodes.ARETURN);
        Mockito.verify(methodVisitor).visitMaxs(0, 0);
        Mockito.verify(methodVisitor).visitEnd();
    }

    @Test
    public void testFieldSetterRegistration() throws Exception {
        Implementation.Context.Default implementationContext = new Implementation.Context.Default(instrumentedType, classFileVersion, auxiliaryTypeNamingStrategy, typeInitializer, auxiliaryClassFileVersion);
        MethodDescription.InDefinedShape firstFieldSetter = implementationContext.registerSetterFor(firstField, DEFAULT);
        MatcherAssert.assertThat(firstFieldSetter.getParameters(), CoreMatchers.is(((ParameterList) (new ParameterList.Explicit.ForTypes(firstFieldSetter, firstFieldType)))));
        MatcherAssert.assertThat(firstFieldSetter.getReturnType(), CoreMatchers.is(VOID));
        MatcherAssert.assertThat(firstFieldSetter.getInternalName(), CoreMatchers.startsWith(ImplementationContextDefaultTest.FOO));
        MatcherAssert.assertThat(firstFieldSetter.getModifiers(), CoreMatchers.is(accessorMethodModifiers));
        MatcherAssert.assertThat(firstFieldSetter.getExceptionTypes(), CoreMatchers.is(((TypeList.Generic) (new TypeList.Generic.Empty()))));
        MatcherAssert.assertThat(implementationContext.registerSetterFor(firstField, DEFAULT), CoreMatchers.is(firstFieldSetter));
        Mockito.when(secondField.isStatic()).thenReturn(true);
        MethodDescription.InDefinedShape secondFieldSetter = implementationContext.registerSetterFor(secondField, DEFAULT);
        MatcherAssert.assertThat(secondFieldSetter.getParameters(), CoreMatchers.is(((ParameterList) (new ParameterList.Explicit.ForTypes(secondFieldSetter, secondFieldType)))));
        MatcherAssert.assertThat(secondFieldSetter.getReturnType(), CoreMatchers.is(VOID));
        MatcherAssert.assertThat(secondFieldSetter.getInternalName(), CoreMatchers.startsWith(ImplementationContextDefaultTest.BAR));
        MatcherAssert.assertThat(secondFieldSetter.getModifiers(), CoreMatchers.is(((accessorMethodModifiers) | (Opcodes.ACC_STATIC))));
        MatcherAssert.assertThat(secondFieldSetter.getExceptionTypes(), CoreMatchers.is(((TypeList.Generic) (new TypeList.Generic.Empty()))));
        MatcherAssert.assertThat(implementationContext.registerSetterFor(firstField, DEFAULT), CoreMatchers.is(firstFieldSetter));
        MatcherAssert.assertThat(implementationContext.registerSetterFor(secondField, DEFAULT), CoreMatchers.is(secondFieldSetter));
        implementationContext.drain(drain, classVisitor, annotationValueFilterFactory);
        Mockito.verify(classVisitor).visitMethod(ArgumentMatchers.eq(accessorMethodModifiers), Mockito.startsWith(ImplementationContextDefaultTest.FOO), ArgumentMatchers.eq((("(" + (ImplementationContextDefaultTest.BAR)) + ")V")), Mockito.<String>isNull(), Mockito.<String[]>isNull());
        Mockito.verify(classVisitor).visitMethod(ArgumentMatchers.eq(((accessorMethodModifiers) | (Opcodes.ACC_STATIC))), Mockito.startsWith(ImplementationContextDefaultTest.BAR), ArgumentMatchers.eq((("(" + (ImplementationContextDefaultTest.QUX)) + ")V")), Mockito.<String>isNull(), Mockito.<String[]>isNull());
    }

    @Test
    public void testFieldSetterRegistrationWritesFirst() throws Exception {
        Implementation.Context.Default implementationContext = new Implementation.Context.Default(instrumentedType, classFileVersion, auxiliaryTypeNamingStrategy, typeInitializer, auxiliaryClassFileVersion);
        MethodDescription firstMethodDescription = implementationContext.registerSetterFor(firstField, DEFAULT);
        MatcherAssert.assertThat(implementationContext.registerSetterFor(firstField, DEFAULT), CoreMatchers.is(firstMethodDescription));
        implementationContext.drain(drain, classVisitor, annotationValueFilterFactory);
        Mockito.verify(classVisitor).visitMethod(ArgumentMatchers.eq(accessorMethodModifiers), Mockito.startsWith(ImplementationContextDefaultTest.FOO), ArgumentMatchers.eq((("(" + (ImplementationContextDefaultTest.BAR)) + ")V")), Mockito.<String>isNull(), Mockito.<String[]>isNull());
        Mockito.verify(methodVisitor).visitCode();
        Mockito.verify(methodVisitor).visitVarInsn(Opcodes.ALOAD, 0);
        Mockito.verify(methodVisitor).visitVarInsn(Opcodes.ALOAD, 1);
        Mockito.verify(methodVisitor).visitFieldInsn(Opcodes.PUTFIELD, ImplementationContextDefaultTest.QUX, ImplementationContextDefaultTest.FOO, ImplementationContextDefaultTest.BAR);
        Mockito.verify(methodVisitor).visitInsn(Opcodes.RETURN);
        Mockito.verify(methodVisitor).visitMaxs(2, 1);
        Mockito.verify(methodVisitor).visitEnd();
    }

    @Test
    public void testFieldSetterRegistrationWritesSecond() throws Exception {
        Mockito.when(secondField.isStatic()).thenReturn(true);
        Implementation.Context.Default implementationContext = new Implementation.Context.Default(instrumentedType, classFileVersion, auxiliaryTypeNamingStrategy, typeInitializer, auxiliaryClassFileVersion);
        MethodDescription secondMethodDescription = implementationContext.registerSetterFor(secondField, DEFAULT);
        MatcherAssert.assertThat(implementationContext.registerSetterFor(secondField, DEFAULT), CoreMatchers.is(secondMethodDescription));
        implementationContext.drain(drain, classVisitor, annotationValueFilterFactory);
        Mockito.verify(classVisitor).visitMethod(ArgumentMatchers.eq(((accessorMethodModifiers) | (Opcodes.ACC_STATIC))), Mockito.startsWith(ImplementationContextDefaultTest.BAR), ArgumentMatchers.eq((("(" + (ImplementationContextDefaultTest.QUX)) + ")V")), Mockito.<String>isNull(), Mockito.<String[]>isNull());
        Mockito.verify(methodVisitor).visitCode();
        Mockito.verify(methodVisitor).visitVarInsn(Opcodes.ALOAD, 0);
        Mockito.verify(methodVisitor).visitFieldInsn(Opcodes.PUTSTATIC, ImplementationContextDefaultTest.BAZ, ImplementationContextDefaultTest.BAR, ImplementationContextDefaultTest.FOO);
        Mockito.verify(methodVisitor).visitInsn(Opcodes.RETURN);
        Mockito.verify(methodVisitor).visitMaxs(1, 0);
        Mockito.verify(methodVisitor).visitEnd();
    }
}

