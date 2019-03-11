package net.bytebuddy.dynamic.scaffold;


import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Collections;
import net.bytebuddy.description.ModifierReviewable;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.TypeVariableSource;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationSource;
import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.modifier.ModifierContributor;
import net.bytebuddy.description.type.PackageDescription;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.description.type.TypeVariableToken;
import net.bytebuddy.dynamic.TargetType;
import net.bytebuddy.dynamic.Transformer;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.LoadedTypeInitializer;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.packaging.PackagePrivateType;
import net.bytebuddy.test.utility.FieldByFieldComparison;
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

import static net.bytebuddy.description.annotation.AnnotationDescription.Builder.ofType;
import static net.bytebuddy.description.type.TypeDefinition.Sort.NON_GENERIC;
import static net.bytebuddy.description.type.TypeDefinition.Sort.describe;
import static net.bytebuddy.description.type.TypeDescription.Generic.Builder.rawType;
import static net.bytebuddy.description.type.TypeDescription.Generic.OBJECT;
import static net.bytebuddy.description.type.TypeDescription.Generic.UNDEFINED;
import static net.bytebuddy.description.type.TypeDescription.Generic.VOID;
import static net.bytebuddy.dynamic.scaffold.InstrumentedType.Factory.Default.MODIFIABLE;
import static net.bytebuddy.implementation.LoadedTypeInitializer.NoOp.INSTANCE;


public class InstrumentedTypeDefaultTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String BAZ = "baz";

    private static final String ILLEGAL_NAME = "<>";

    private static final int ILLEGAL_MODIFIERS = -1;

    private static final int OTHER_MODIFIERS = 42;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    @Mock
    private AnnotationDescription annotationDescription;

    @Test
    @SuppressWarnings("unchecked")
    public void testWithTypeVariable() throws Exception {
        TypeDescription.Generic boundType = Mockito.mock(TypeDescription.Generic.class);
        Mockito.when(boundType.asGenericType()).thenReturn(boundType);
        Mockito.when(boundType.accept(Mockito.any(TypeDescription.Generic.Visitor.class))).thenReturn(boundType);
        TypeDescription rawBoundType = Mockito.mock(TypeDescription.class);
        Mockito.when(boundType.asErasure()).thenReturn(rawBoundType);
        Mockito.when(rawBoundType.getName()).thenReturn(InstrumentedTypeDefaultTest.FOO);
        InstrumentedType instrumentedType = InstrumentedTypeDefaultTest.makePlainInstrumentedType();
        MatcherAssert.assertThat(instrumentedType.getTypeVariables().size(), CoreMatchers.is(0));
        instrumentedType = instrumentedType.withTypeVariable(new TypeVariableToken(InstrumentedTypeDefaultTest.BAR, Collections.singletonList(boundType)));
        MatcherAssert.assertThat(instrumentedType.getTypeVariables().size(), CoreMatchers.is(1));
        TypeDescription.Generic typeVariable = instrumentedType.getTypeVariables().get(0);
        MatcherAssert.assertThat(typeVariable.getTypeName(), CoreMatchers.is(InstrumentedTypeDefaultTest.BAR));
        MatcherAssert.assertThat(typeVariable.getTypeVariableSource(), CoreMatchers.sameInstance(((TypeVariableSource) (instrumentedType))));
        MatcherAssert.assertThat(typeVariable.getUpperBounds(), CoreMatchers.is(Collections.singletonList(boundType)));
    }

    @Test
    public void testWithTypeVariableWithInstrumentedType() throws Exception {
        InstrumentedType instrumentedType = InstrumentedTypeDefaultTest.makePlainInstrumentedType();
        MatcherAssert.assertThat(instrumentedType.getTypeVariables().size(), CoreMatchers.is(0));
        instrumentedType = instrumentedType.withTypeVariable(new TypeVariableToken(InstrumentedTypeDefaultTest.BAR, Collections.singletonList(TargetType.DESCRIPTION.asGenericType())));
        MatcherAssert.assertThat(instrumentedType.getTypeVariables().size(), CoreMatchers.is(1));
        TypeDescription.Generic typeVariable = instrumentedType.getTypeVariables().get(0);
        MatcherAssert.assertThat(typeVariable.getTypeName(), CoreMatchers.is(InstrumentedTypeDefaultTest.BAR));
        MatcherAssert.assertThat(typeVariable.getTypeVariableSource(), CoreMatchers.sameInstance(((TypeVariableSource) (instrumentedType))));
        MatcherAssert.assertThat(typeVariable.getUpperBounds(), CoreMatchers.is(Collections.singletonList(instrumentedType.asGenericType())));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testWithTypeVariableTransformed() throws Exception {
        TypeDescription.Generic boundType = Mockito.mock(TypeDescription.Generic.class);
        Mockito.when(boundType.asGenericType()).thenReturn(boundType);
        Mockito.when(boundType.accept(Mockito.any(TypeDescription.Generic.Visitor.class))).thenReturn(boundType);
        TypeDescription rawBoundType = Mockito.mock(TypeDescription.class);
        Mockito.when(boundType.asErasure()).thenReturn(rawBoundType);
        Mockito.when(rawBoundType.getName()).thenReturn(InstrumentedTypeDefaultTest.FOO);
        InstrumentedType.WithFlexibleName instrumentedType = InstrumentedTypeDefaultTest.makePlainInstrumentedType();
        MatcherAssert.assertThat(instrumentedType.getTypeVariables().size(), CoreMatchers.is(0));
        instrumentedType = instrumentedType.withTypeVariable(new TypeVariableToken(InstrumentedTypeDefaultTest.BAR, Collections.singletonList(boundType)));
        Transformer<TypeVariableToken> transformer = Mockito.mock(Transformer.class);
        TypeDescription.Generic otherBoundType = Mockito.mock(TypeDescription.Generic.class);
        Mockito.when(otherBoundType.asGenericType()).thenReturn(otherBoundType);
        Mockito.when(otherBoundType.accept(Mockito.any(TypeDescription.Generic.Visitor.class))).thenReturn(otherBoundType);
        TypeDescription rawOtherBoundType = Mockito.mock(TypeDescription.class);
        Mockito.when(otherBoundType.asErasure()).thenReturn(rawOtherBoundType);
        Mockito.when(transformer.transform(instrumentedType, new TypeVariableToken(InstrumentedTypeDefaultTest.BAR, Collections.singletonList(boundType)))).thenReturn(new TypeVariableToken(InstrumentedTypeDefaultTest.QUX, Collections.singletonList(otherBoundType)));
        instrumentedType = instrumentedType.withTypeVariables(ElementMatchers.named(InstrumentedTypeDefaultTest.BAR), transformer);
        MatcherAssert.assertThat(instrumentedType.getTypeVariables().size(), CoreMatchers.is(1));
        TypeDescription.Generic typeVariable = instrumentedType.getTypeVariables().get(0);
        MatcherAssert.assertThat(typeVariable.getTypeName(), CoreMatchers.is(InstrumentedTypeDefaultTest.QUX));
        MatcherAssert.assertThat(typeVariable.getTypeVariableSource(), CoreMatchers.sameInstance(((TypeVariableSource) (instrumentedType))));
        MatcherAssert.assertThat(typeVariable.getUpperBounds(), CoreMatchers.is(Collections.singletonList(otherBoundType)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testWithField() throws Exception {
        TypeDescription.Generic fieldType = Mockito.mock(TypeDescription.Generic.class);
        Mockito.when(fieldType.accept(Mockito.any(TypeDescription.Generic.Visitor.class))).thenReturn(fieldType);
        TypeDescription rawFieldType = Mockito.mock(TypeDescription.class);
        Mockito.when(fieldType.asErasure()).thenReturn(rawFieldType);
        Mockito.when(rawFieldType.getName()).thenReturn(InstrumentedTypeDefaultTest.FOO);
        InstrumentedType instrumentedType = InstrumentedTypeDefaultTest.makePlainInstrumentedType();
        MatcherAssert.assertThat(instrumentedType.getDeclaredFields().size(), CoreMatchers.is(0));
        instrumentedType = instrumentedType.withField(new FieldDescription.Token(InstrumentedTypeDefaultTest.BAR, Opcodes.ACC_PUBLIC, fieldType));
        MatcherAssert.assertThat(instrumentedType.getDeclaredFields().size(), CoreMatchers.is(1));
        FieldDescription.InDefinedShape fieldDescription = instrumentedType.getDeclaredFields().get(0);
        MatcherAssert.assertThat(fieldDescription.getType(), CoreMatchers.is(fieldType));
        MatcherAssert.assertThat(fieldDescription.getModifiers(), CoreMatchers.is(Opcodes.ACC_PUBLIC));
        MatcherAssert.assertThat(fieldDescription.getName(), CoreMatchers.is(InstrumentedTypeDefaultTest.BAR));
        MatcherAssert.assertThat(fieldDescription.getDeclaringType(), CoreMatchers.sameInstance(((TypeDescription) (instrumentedType))));
    }

    @Test
    public void testWithFieldOfInstrumentedType() throws Exception {
        InstrumentedType instrumentedType = InstrumentedTypeDefaultTest.makePlainInstrumentedType();
        MatcherAssert.assertThat(instrumentedType.getDeclaredFields().size(), CoreMatchers.is(0));
        instrumentedType = instrumentedType.withField(new FieldDescription.Token(InstrumentedTypeDefaultTest.BAR, Opcodes.ACC_PUBLIC, TargetType.DESCRIPTION.asGenericType()));
        MatcherAssert.assertThat(instrumentedType.getDeclaredFields().size(), CoreMatchers.is(1));
        FieldDescription.InDefinedShape fieldDescription = instrumentedType.getDeclaredFields().get(0);
        MatcherAssert.assertThat(fieldDescription.getType().asErasure(), CoreMatchers.sameInstance(((TypeDescription) (instrumentedType))));
        MatcherAssert.assertThat(fieldDescription.getModifiers(), CoreMatchers.is(Opcodes.ACC_PUBLIC));
        MatcherAssert.assertThat(fieldDescription.getName(), CoreMatchers.is(InstrumentedTypeDefaultTest.BAR));
        MatcherAssert.assertThat(fieldDescription.getDeclaringType(), CoreMatchers.sameInstance(((TypeDescription) (instrumentedType))));
    }

    @Test
    public void testWithFieldOfInstrumentedTypeAsArray() throws Exception {
        InstrumentedType instrumentedType = InstrumentedTypeDefaultTest.makePlainInstrumentedType();
        MatcherAssert.assertThat(instrumentedType.getDeclaredFields().size(), CoreMatchers.is(0));
        instrumentedType = instrumentedType.withField(new FieldDescription.Token(InstrumentedTypeDefaultTest.BAR, Opcodes.ACC_PUBLIC, new TypeDescription.Generic.OfGenericArray.Latent(TargetType.DESCRIPTION.asGenericType(), new AnnotationSource.Explicit(annotationDescription))));
        MatcherAssert.assertThat(instrumentedType.getDeclaredFields().size(), CoreMatchers.is(1));
        FieldDescription.InDefinedShape fieldDescription = instrumentedType.getDeclaredFields().get(0);
        MatcherAssert.assertThat(fieldDescription.getType().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(fieldDescription.getType().asErasure().isArray(), CoreMatchers.is(true));
        MatcherAssert.assertThat(fieldDescription.getType().asErasure().getComponentType(), CoreMatchers.sameInstance(((TypeDescription) (instrumentedType))));
        MatcherAssert.assertThat(fieldDescription.getModifiers(), CoreMatchers.is(Opcodes.ACC_PUBLIC));
        MatcherAssert.assertThat(fieldDescription.getName(), CoreMatchers.is(InstrumentedTypeDefaultTest.BAR));
        MatcherAssert.assertThat(fieldDescription.getType().getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(fieldDescription.getType().getDeclaredAnnotations().getOnly(), CoreMatchers.is(annotationDescription));
        MatcherAssert.assertThat(fieldDescription.getDeclaringType(), CoreMatchers.sameInstance(((TypeDescription) (instrumentedType))));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testWithMethod() throws Exception {
        TypeDescription.Generic returnType = Mockito.mock(TypeDescription.Generic.class);
        Mockito.when(returnType.accept(Mockito.any(TypeDescription.Generic.Visitor.class))).thenReturn(returnType);
        TypeDescription rawReturnType = Mockito.mock(TypeDescription.class);
        Mockito.when(returnType.asErasure()).thenReturn(rawReturnType);
        Mockito.when(rawReturnType.getName()).thenReturn(InstrumentedTypeDefaultTest.FOO);
        TypeDescription.Generic parameterType = Mockito.mock(TypeDescription.Generic.class);
        Mockito.when(parameterType.accept(Mockito.any(TypeDescription.Generic.Visitor.class))).thenReturn(parameterType);
        Mockito.when(parameterType.asGenericType()).thenReturn(parameterType);
        TypeDescription rawParameterType = Mockito.mock(TypeDescription.class);
        Mockito.when(parameterType.asErasure()).thenReturn(rawParameterType);
        Mockito.when(rawParameterType.getName()).thenReturn(InstrumentedTypeDefaultTest.QUX);
        Mockito.when(rawParameterType.getStackSize()).thenReturn(StackSize.ZERO);
        InstrumentedType instrumentedType = InstrumentedTypeDefaultTest.makePlainInstrumentedType();
        MatcherAssert.assertThat(instrumentedType.getDeclaredFields().size(), CoreMatchers.is(0));
        instrumentedType = instrumentedType.withMethod(new MethodDescription.Token(InstrumentedTypeDefaultTest.BAR, Opcodes.ACC_PUBLIC, returnType, Collections.singletonList(parameterType)));
        MatcherAssert.assertThat(instrumentedType.getDeclaredMethods().size(), CoreMatchers.is(1));
        MethodDescription.InDefinedShape methodDescription = instrumentedType.getDeclaredMethods().get(0);
        MatcherAssert.assertThat(methodDescription.getReturnType(), CoreMatchers.is(returnType));
        MatcherAssert.assertThat(methodDescription.getParameters().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(methodDescription.getParameters().asTypeList(), CoreMatchers.is(Collections.singletonList(parameterType)));
        MatcherAssert.assertThat(methodDescription.getExceptionTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(methodDescription.getModifiers(), CoreMatchers.is(Opcodes.ACC_PUBLIC));
        MatcherAssert.assertThat(methodDescription.getName(), CoreMatchers.is(InstrumentedTypeDefaultTest.BAR));
        MatcherAssert.assertThat(methodDescription.getDeclaringType(), CoreMatchers.sameInstance(((TypeDescription) (instrumentedType))));
    }

    @Test
    public void testWithMethodOfInstrumentedType() throws Exception {
        InstrumentedType instrumentedType = InstrumentedTypeDefaultTest.makePlainInstrumentedType();
        MatcherAssert.assertThat(instrumentedType.getDeclaredFields().size(), CoreMatchers.is(0));
        instrumentedType = instrumentedType.withMethod(new MethodDescription.Token(InstrumentedTypeDefaultTest.BAR, Opcodes.ACC_PUBLIC, TargetType.DESCRIPTION.asGenericType(), Collections.singletonList(TargetType.DESCRIPTION.asGenericType())));
        MatcherAssert.assertThat(instrumentedType.getDeclaredMethods().size(), CoreMatchers.is(1));
        MethodDescription.InDefinedShape methodDescription = instrumentedType.getDeclaredMethods().get(0);
        MatcherAssert.assertThat(methodDescription.getReturnType().asErasure(), CoreMatchers.sameInstance(((TypeDescription) (instrumentedType))));
        MatcherAssert.assertThat(methodDescription.getParameters().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(methodDescription.getParameters().asTypeList().get(0).asErasure(), CoreMatchers.sameInstance(((TypeDescription) (instrumentedType))));
        MatcherAssert.assertThat(methodDescription.getExceptionTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(methodDescription.getModifiers(), CoreMatchers.is(Opcodes.ACC_PUBLIC));
        MatcherAssert.assertThat(methodDescription.getName(), CoreMatchers.is(InstrumentedTypeDefaultTest.BAR));
        MatcherAssert.assertThat(methodDescription.getDeclaringType(), CoreMatchers.sameInstance(((TypeDescription) (instrumentedType))));
    }

    @Test
    public void testWithMethodOfInstrumentedTypeAsArray() throws Exception {
        InstrumentedType instrumentedType = InstrumentedTypeDefaultTest.makePlainInstrumentedType();
        MatcherAssert.assertThat(instrumentedType.getDeclaredFields().size(), CoreMatchers.is(0));
        instrumentedType = instrumentedType.withMethod(new MethodDescription.Token(InstrumentedTypeDefaultTest.BAR, Opcodes.ACC_PUBLIC, new TypeDescription.Generic.OfGenericArray.Latent(TargetType.DESCRIPTION.asGenericType(), new AnnotationSource.Explicit(annotationDescription)), Collections.singletonList(new TypeDescription.Generic.OfGenericArray.Latent(TargetType.DESCRIPTION.asGenericType(), new AnnotationSource.Explicit(annotationDescription)))));
        MatcherAssert.assertThat(instrumentedType.getDeclaredMethods().size(), CoreMatchers.is(1));
        MethodDescription.InDefinedShape methodDescription = instrumentedType.getDeclaredMethods().get(0);
        MatcherAssert.assertThat(methodDescription.getReturnType().asErasure().isArray(), CoreMatchers.is(true));
        MatcherAssert.assertThat(methodDescription.getReturnType().getComponentType().asErasure(), CoreMatchers.sameInstance(((TypeDescription) (instrumentedType))));
        MatcherAssert.assertThat(methodDescription.getParameters().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(methodDescription.getParameters().asTypeList().asErasures().get(0).isArray(), CoreMatchers.is(true));
        MatcherAssert.assertThat(methodDescription.getParameters().asTypeList().get(0).getComponentType().asErasure(), CoreMatchers.sameInstance(((TypeDescription) (instrumentedType))));
        MatcherAssert.assertThat(methodDescription.getExceptionTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(methodDescription.getReturnType().getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(methodDescription.getReturnType().getDeclaredAnnotations().getOnly(), CoreMatchers.is(annotationDescription));
        MatcherAssert.assertThat(methodDescription.getParameters().getOnly().getType().getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(methodDescription.getParameters().getOnly().getType().getDeclaredAnnotations().getOnly(), CoreMatchers.is(annotationDescription));
        MatcherAssert.assertThat(methodDescription.getModifiers(), CoreMatchers.is(Opcodes.ACC_PUBLIC));
        MatcherAssert.assertThat(methodDescription.getName(), CoreMatchers.is(InstrumentedTypeDefaultTest.BAR));
        MatcherAssert.assertThat(methodDescription.getDeclaringType(), CoreMatchers.sameInstance(((TypeDescription) (instrumentedType))));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testWithInterface() throws Exception {
        TypeDescription.Generic interfaceType = Mockito.mock(TypeDescription.Generic.class);
        Mockito.when(interfaceType.asGenericType()).thenReturn(interfaceType);
        Mockito.when(interfaceType.accept(Mockito.any(TypeDescription.Generic.Visitor.class))).thenReturn(interfaceType);
        TypeDescription rawBoundType = Mockito.mock(TypeDescription.class);
        Mockito.when(interfaceType.asErasure()).thenReturn(rawBoundType);
        Mockito.when(rawBoundType.getName()).thenReturn(InstrumentedTypeDefaultTest.FOO);
        InstrumentedType instrumentedType = InstrumentedTypeDefaultTest.makePlainInstrumentedType();
        MatcherAssert.assertThat(instrumentedType.getInterfaces().size(), CoreMatchers.is(0));
        instrumentedType = instrumentedType.withInterfaces(new TypeList.Generic.Explicit(interfaceType));
        MatcherAssert.assertThat(instrumentedType.getInterfaces().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(instrumentedType.getInterfaces(), CoreMatchers.is(Collections.singletonList(interfaceType)));
    }

    @Test
    public void testWithInterfaceOfInstrumentedType() throws Exception {
        InstrumentedType instrumentedType = InstrumentedTypeDefaultTest.makePlainInstrumentedType();
        MatcherAssert.assertThat(instrumentedType.getInterfaces().size(), CoreMatchers.is(0));
        instrumentedType = instrumentedType.withInterfaces(new TypeList.Generic.Explicit(TargetType.DESCRIPTION));
        MatcherAssert.assertThat(instrumentedType.getInterfaces().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(instrumentedType.getInterfaces(), CoreMatchers.is(Collections.singletonList(instrumentedType.asGenericType())));
    }

    @Test
    public void testWithAnnotation() throws Exception {
        AnnotationDescription annotationDescription = Mockito.mock(AnnotationDescription.class);
        InstrumentedType instrumentedType = InstrumentedTypeDefaultTest.makePlainInstrumentedType();
        MatcherAssert.assertThat(instrumentedType.getDeclaredAnnotations().size(), CoreMatchers.is(0));
        instrumentedType = instrumentedType.withAnnotations(Collections.singletonList(annotationDescription));
        MatcherAssert.assertThat(instrumentedType.getDeclaredAnnotations(), CoreMatchers.is(Collections.singletonList(annotationDescription)));
    }

    @Test
    public void testWithName() throws Exception {
        InstrumentedType.WithFlexibleName instrumentedType = InstrumentedTypeDefaultTest.makePlainInstrumentedType();
        MatcherAssert.assertThat(instrumentedType.getName(), CoreMatchers.is((((InstrumentedTypeDefaultTest.FOO) + ".") + (InstrumentedTypeDefaultTest.BAZ))));
        instrumentedType = instrumentedType.withName(InstrumentedTypeDefaultTest.BAR);
        MatcherAssert.assertThat(instrumentedType.getName(), CoreMatchers.is(InstrumentedTypeDefaultTest.BAR));
    }

    @Test
    public void testModifiers() throws Exception {
        InstrumentedType instrumentedType = InstrumentedTypeDefaultTest.makePlainInstrumentedType();
        MatcherAssert.assertThat(instrumentedType.getModifiers(), CoreMatchers.is(ModifierContributor.EMPTY_MASK));
        instrumentedType = instrumentedType.withModifiers(InstrumentedTypeDefaultTest.OTHER_MODIFIERS);
        MatcherAssert.assertThat(instrumentedType.getModifiers(), CoreMatchers.is(InstrumentedTypeDefaultTest.OTHER_MODIFIERS));
    }

    @Test
    public void testWithLoadedTypeInitializerInitial() throws Exception {
        LoadedTypeInitializer loadedTypeInitializer = InstrumentedTypeDefaultTest.makePlainInstrumentedType().getLoadedTypeInitializer();
        MatcherAssert.assertThat(loadedTypeInitializer.isAlive(), CoreMatchers.is(false));
    }

    @Test
    public void testWithLoadedTypeInitializerSingle() throws Exception {
        InstrumentedType instrumentedType = InstrumentedTypeDefaultTest.makePlainInstrumentedType();
        MatcherAssert.assertThat(instrumentedType.getDeclaredFields().size(), CoreMatchers.is(0));
        LoadedTypeInitializer loadedTypeInitializer = Mockito.mock(LoadedTypeInitializer.class);
        instrumentedType = instrumentedType.withInitializer(loadedTypeInitializer);
        MatcherAssert.assertThat(instrumentedType.getLoadedTypeInitializer(), FieldByFieldComparison.hasPrototype(((LoadedTypeInitializer) (new LoadedTypeInitializer.Compound(INSTANCE, loadedTypeInitializer)))));
    }

    @Test
    public void testWithLoadedTypeInitializerDouble() throws Exception {
        InstrumentedType instrumentedType = InstrumentedTypeDefaultTest.makePlainInstrumentedType();
        MatcherAssert.assertThat(instrumentedType.getDeclaredFields().size(), CoreMatchers.is(0));
        LoadedTypeInitializer first = Mockito.mock(LoadedTypeInitializer.class);
        LoadedTypeInitializer second = Mockito.mock(LoadedTypeInitializer.class);
        instrumentedType = instrumentedType.withInitializer(first).withInitializer(second);
        MatcherAssert.assertThat(instrumentedType.getLoadedTypeInitializer(), FieldByFieldComparison.hasPrototype(((LoadedTypeInitializer) (new LoadedTypeInitializer.Compound(new LoadedTypeInitializer.Compound(INSTANCE, first), second)))));
    }

    @Test
    public void testWithTypeInitializerInitial() throws Exception {
        TypeInitializer typeInitializer = InstrumentedTypeDefaultTest.makePlainInstrumentedType().getTypeInitializer();
        MatcherAssert.assertThat(typeInitializer.isDefined(), CoreMatchers.is(false));
    }

    @Test
    public void testWithTypeInitializerSingle() throws Exception {
        InstrumentedType instrumentedType = InstrumentedTypeDefaultTest.makePlainInstrumentedType();
        MatcherAssert.assertThat(instrumentedType.getDeclaredFields().size(), CoreMatchers.is(0));
        ByteCodeAppender byteCodeAppender = Mockito.mock(ByteCodeAppender.class);
        instrumentedType = instrumentedType.withInitializer(byteCodeAppender);
        TypeInitializer typeInitializer = instrumentedType.getTypeInitializer();
        MatcherAssert.assertThat(typeInitializer.isDefined(), CoreMatchers.is(true));
        MethodDescription methodDescription = Mockito.mock(MethodDescription.class);
        typeInitializer.apply(methodVisitor, implementationContext, methodDescription);
        Mockito.verify(byteCodeAppender).apply(methodVisitor, implementationContext, methodDescription);
    }

    @Test
    public void testWithTypeInitializerDouble() throws Exception {
        InstrumentedType instrumentedType = InstrumentedTypeDefaultTest.makePlainInstrumentedType();
        MatcherAssert.assertThat(instrumentedType.getDeclaredFields().size(), CoreMatchers.is(0));
        ByteCodeAppender first = Mockito.mock(ByteCodeAppender.class);
        ByteCodeAppender second = Mockito.mock(ByteCodeAppender.class);
        MethodDescription methodDescription = Mockito.mock(MethodDescription.class);
        Mockito.when(first.apply(methodVisitor, implementationContext, methodDescription)).thenReturn(new ByteCodeAppender.Size(0, 0));
        Mockito.when(second.apply(methodVisitor, implementationContext, methodDescription)).thenReturn(new ByteCodeAppender.Size(0, 0));
        instrumentedType = instrumentedType.withInitializer(first).withInitializer(second);
        TypeInitializer typeInitializer = instrumentedType.getTypeInitializer();
        MatcherAssert.assertThat(typeInitializer.isDefined(), CoreMatchers.is(true));
        typeInitializer.apply(methodVisitor, implementationContext, methodDescription);
        Mockito.verify(first).apply(methodVisitor, implementationContext, methodDescription);
        Mockito.verify(second).apply(methodVisitor, implementationContext, methodDescription);
    }

    @Test
    public void testGetStackSize() throws Exception {
        MatcherAssert.assertThat(InstrumentedTypeDefaultTest.makePlainInstrumentedType().getStackSize(), CoreMatchers.is(StackSize.SINGLE));
    }

    @Test
    public void testHashCode() throws Exception {
        InstrumentedType instrumentedType = InstrumentedTypeDefaultTest.makePlainInstrumentedType();
        MatcherAssert.assertThat(instrumentedType.hashCode(), CoreMatchers.is(instrumentedType.getName().hashCode()));
    }

    @Test
    public void testEquals() throws Exception {
        InstrumentedType instrumentedType = InstrumentedTypeDefaultTest.makePlainInstrumentedType();
        TypeDescription other = Mockito.mock(TypeDescription.class);
        Mockito.when(other.getName()).thenReturn(instrumentedType.getName());
        Mockito.when(other.getSort()).thenReturn(NON_GENERIC);
        Mockito.when(other.asErasure()).thenReturn(other);
        MatcherAssert.assertThat(instrumentedType, CoreMatchers.is(other));
        Mockito.verify(other, Mockito.atLeast(1)).getName();
    }

    @Test
    public void testIsAssignableFrom() {
        MatcherAssert.assertThat(InstrumentedTypeDefaultTest.makePlainInstrumentedType().isAssignableFrom(Object.class), CoreMatchers.is(false));
        MatcherAssert.assertThat(InstrumentedTypeDefaultTest.makePlainInstrumentedType().isAssignableFrom(Serializable.class), CoreMatchers.is(false));
        MatcherAssert.assertThat(InstrumentedTypeDefaultTest.makePlainInstrumentedType().isAssignableFrom(Integer.class), CoreMatchers.is(false));
        TypeDescription objectTypeDescription = TypeDescription.OBJECT;
        MatcherAssert.assertThat(InstrumentedTypeDefaultTest.makePlainInstrumentedType().isAssignableFrom(objectTypeDescription), CoreMatchers.is(false));
        TypeDescription serializableTypeDescription = TypeDescription.ForLoadedType.of(Serializable.class);
        MatcherAssert.assertThat(InstrumentedTypeDefaultTest.makePlainInstrumentedType().isAssignableFrom(serializableTypeDescription), CoreMatchers.is(false));
        TypeDescription integerTypeDescription = TypeDescription.ForLoadedType.of(Integer.class);
        MatcherAssert.assertThat(InstrumentedTypeDefaultTest.makePlainInstrumentedType().isAssignableFrom(integerTypeDescription), CoreMatchers.is(false));
    }

    @Test
    public void testIsAssignableTo() {
        MatcherAssert.assertThat(InstrumentedTypeDefaultTest.makePlainInstrumentedType().isAssignableTo(Object.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(InstrumentedTypeDefaultTest.makePlainInstrumentedType().isAssignableTo(InstrumentedTypeDefaultTest.makePlainInstrumentedType()), CoreMatchers.is(true));
        MatcherAssert.assertThat(InstrumentedTypeDefaultTest.makePlainInstrumentedType().isAssignableTo(Integer.class), CoreMatchers.is(false));
        MatcherAssert.assertThat(InstrumentedTypeDefaultTest.makePlainInstrumentedType().isAssignableTo(TypeDescription.OBJECT), CoreMatchers.is(true));
    }

    @Test
    public void testRepresents() {
        MatcherAssert.assertThat(InstrumentedTypeDefaultTest.makePlainInstrumentedType().represents(Object.class), CoreMatchers.is(false));
        MatcherAssert.assertThat(InstrumentedTypeDefaultTest.makePlainInstrumentedType().represents(Serializable.class), CoreMatchers.is(false));
        MatcherAssert.assertThat(InstrumentedTypeDefaultTest.makePlainInstrumentedType().represents(Integer.class), CoreMatchers.is(false));
    }

    @Test
    public void testSuperClass() {
        MatcherAssert.assertThat(InstrumentedTypeDefaultTest.makePlainInstrumentedType().getSuperClass(), CoreMatchers.is(OBJECT));
        MatcherAssert.assertThat(InstrumentedTypeDefaultTest.makePlainInstrumentedType().getSuperClass(), CoreMatchers.not(((TypeDescription.Generic) (of(Integer.class)))));
        MatcherAssert.assertThat(InstrumentedTypeDefaultTest.makePlainInstrumentedType().getSuperClass(), CoreMatchers.not(((TypeDescription.Generic) (of(Serializable.class)))));
    }

    @Test
    public void testInterfaces() {
        MatcherAssert.assertThat(InstrumentedTypeDefaultTest.makePlainInstrumentedType().getInterfaces().size(), CoreMatchers.is(0));
    }

    @Test
    public void testPackage() {
        MatcherAssert.assertThat(InstrumentedTypeDefaultTest.makePlainInstrumentedType().getPackage().getName(), CoreMatchers.is(InstrumentedTypeDefaultTest.FOO));
    }

    @Test
    public void testSimpleName() {
        MatcherAssert.assertThat(InstrumentedTypeDefaultTest.makePlainInstrumentedType().getSimpleName(), CoreMatchers.is(InstrumentedTypeDefaultTest.BAZ));
    }

    @Test
    public void testIsAnonymous() throws Exception {
        MatcherAssert.assertThat(isAnonymousType(), CoreMatchers.is(false));
    }

    @Test
    public void testCanonicalName() throws Exception {
        TypeDescription typeDescription = InstrumentedTypeDefaultTest.makePlainInstrumentedType();
        MatcherAssert.assertThat(typeDescription.getCanonicalName(), CoreMatchers.is(typeDescription.getName()));
    }

    @Test
    public void testIsMemberClass() throws Exception {
        MatcherAssert.assertThat(isMemberType(), CoreMatchers.is(false));
    }

    @Test
    public void testFieldTokenIsVisited() throws Exception {
        FieldDescription.Token token = Mockito.mock(FieldDescription.Token.class);
        InstrumentedType instrumentedType = InstrumentedTypeDefaultTest.makePlainInstrumentedType();
        MatcherAssert.assertThat(instrumentedType.withField(token), CoreMatchers.is(instrumentedType));
        Mockito.verify(token).accept(FieldByFieldComparison.matchesPrototype(TypeDescription.Generic.Visitor.Substitutor.ForDetachment.of(instrumentedType)));
        Mockito.verifyNoMoreInteractions(token);
    }

    @Test
    public void testMethodTokenIsVisited() throws Exception {
        MethodDescription.Token token = Mockito.mock(MethodDescription.Token.class);
        InstrumentedType instrumentedType = InstrumentedTypeDefaultTest.makePlainInstrumentedType();
        MatcherAssert.assertThat(instrumentedType.withMethod(token), CoreMatchers.is(instrumentedType));
        Mockito.verify(token).accept(FieldByFieldComparison.matchesPrototype(TypeDescription.Generic.Visitor.Substitutor.ForDetachment.of(instrumentedType)));
        Mockito.verifyNoMoreInteractions(token);
    }

    @Test
    public void testTypeVariableIsVisited() throws Exception {
        TypeVariableToken token = Mockito.mock(TypeVariableToken.class);
        InstrumentedType instrumentedType = InstrumentedTypeDefaultTest.makePlainInstrumentedType();
        MatcherAssert.assertThat(instrumentedType.withTypeVariable(token), CoreMatchers.is(instrumentedType));
        Mockito.verify(token).accept(FieldByFieldComparison.matchesPrototype(TypeDescription.Generic.Visitor.Substitutor.ForDetachment.of(instrumentedType)));
        Mockito.verifyNoMoreInteractions(token);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInterfaceTypesVisited() throws Exception {
        TypeDescription.Generic typeDescription = Mockito.mock(TypeDescription.Generic.class);
        Mockito.when(typeDescription.asGenericType()).thenReturn(typeDescription);
        Mockito.when(typeDescription.accept(Mockito.any(TypeDescription.Generic.Visitor.class))).thenReturn(typeDescription);
        InstrumentedType instrumentedType = InstrumentedTypeDefaultTest.makePlainInstrumentedType();
        MatcherAssert.assertThat(instrumentedType.withInterfaces(new TypeList.Generic.Explicit(typeDescription)), CoreMatchers.is(instrumentedType));
        Mockito.verify(typeDescription).accept(FieldByFieldComparison.matchesPrototype(TypeDescription.Generic.Visitor.Substitutor.ForDetachment.of(instrumentedType)));
        Mockito.verify(typeDescription, Mockito.times(2)).asGenericType();
        Mockito.verifyNoMoreInteractions(typeDescription);
    }

    @Test
    public void testDeclaringType() throws Exception {
        TypeDescription typeDescription = Mockito.mock(TypeDescription.class);
        InstrumentedType instrumentedType = InstrumentedTypeDefaultTest.makePlainInstrumentedType();
        MatcherAssert.assertThat(instrumentedType.getDeclaringType(), CoreMatchers.nullValue(TypeDescription.class));
        InstrumentedType transformed = withDeclaringType(typeDescription);
        MatcherAssert.assertThat(transformed.getDeclaringType(), CoreMatchers.is(typeDescription));
    }

    @Test
    public void testDeclaredTypes() throws Exception {
        TypeDescription typeDescription = Mockito.mock(TypeDescription.class);
        InstrumentedType instrumentedType = InstrumentedTypeDefaultTest.makePlainInstrumentedType();
        MatcherAssert.assertThat(instrumentedType.getDeclaredTypes().size(), CoreMatchers.is(0));
        InstrumentedType transformed = withDeclaredTypes(new TypeList.Explicit(typeDescription));
        MatcherAssert.assertThat(transformed.getDeclaredTypes(), CoreMatchers.hasItems(typeDescription));
    }

    @Test
    public void testEnclosingType() throws Exception {
        TypeDescription typeDescription = Mockito.mock(TypeDescription.class);
        InstrumentedType instrumentedType = InstrumentedTypeDefaultTest.makePlainInstrumentedType();
        MatcherAssert.assertThat(instrumentedType.getEnclosingType(), CoreMatchers.nullValue(TypeDescription.class));
        InstrumentedType transformed = withEnclosingType(typeDescription);
        MatcherAssert.assertThat(transformed.getEnclosingType(), CoreMatchers.is(typeDescription));
        MatcherAssert.assertThat(transformed.getEnclosingMethod(), CoreMatchers.nullValue(MethodDescription.InDefinedShape.class));
    }

    @Test
    public void testEnclosingMethod() throws Exception {
        MethodDescription.InDefinedShape methodDescription = Mockito.mock(MethodDescription.InDefinedShape.class);
        InstrumentedType instrumentedType = InstrumentedTypeDefaultTest.makePlainInstrumentedType();
        MatcherAssert.assertThat(instrumentedType.getEnclosingMethod(), CoreMatchers.nullValue(MethodDescription.InDefinedShape.class));
        InstrumentedType transformed = withEnclosingMethod(methodDescription);
        MatcherAssert.assertThat(transformed.getEnclosingType(), CoreMatchers.nullValue(TypeDescription.class));
        MatcherAssert.assertThat(transformed.getEnclosingMethod(), CoreMatchers.is(methodDescription));
    }

    @Test
    public void testNestHost() throws Exception {
        TypeDescription typeDescription = Mockito.mock(TypeDescription.class);
        InstrumentedType instrumentedType = InstrumentedTypeDefaultTest.makePlainInstrumentedType();
        MatcherAssert.assertThat(getNestHost(), CoreMatchers.is(((TypeDescription) (instrumentedType))));
        InstrumentedType transformed = withNestHost(typeDescription);
        MatcherAssert.assertThat(getNestHost(), CoreMatchers.is(typeDescription));
    }

    @Test
    public void testNestMates() throws Exception {
        TypeDescription typeDescription = Mockito.mock(TypeDescription.class);
        Mockito.when(typeDescription.getSort()).thenReturn(NON_GENERIC);
        Mockito.when(typeDescription.asErasure()).thenReturn(typeDescription);
        InstrumentedType instrumentedType = InstrumentedTypeDefaultTest.makePlainInstrumentedType();
        MatcherAssert.assertThat(getNestMembers().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(getNestMembers(), CoreMatchers.hasItems(((TypeDescription) (instrumentedType))));
        InstrumentedType transformed = withNestMembers(new TypeList.Explicit(typeDescription));
        MatcherAssert.assertThat(getNestHost(), CoreMatchers.is(((TypeDescription) (transformed))));
        MatcherAssert.assertThat(getNestMembers(), CoreMatchers.hasItems(transformed, typeDescription));
    }

    @Test
    public void testLocalClass() throws Exception {
        InstrumentedType instrumentedType = InstrumentedTypeDefaultTest.makePlainInstrumentedType();
        MatcherAssert.assertThat(isLocalType(), CoreMatchers.is(false));
        TypeDescription transformed = withLocalClass(true);
        MatcherAssert.assertThat(isLocalType(), CoreMatchers.is(true));
    }

    @Test
    public void testMemberClass() throws Exception {
        InstrumentedType instrumentedType = InstrumentedTypeDefaultTest.makePlainInstrumentedType();
        MatcherAssert.assertThat(isMemberType(), CoreMatchers.is(false));
        TypeDescription transformed = instrumentedType.withLocalClass(true).withDeclaringType(Mockito.mock(TypeDescription.class));
        MatcherAssert.assertThat(isLocalType(), CoreMatchers.is(true));
    }

    @Test
    public void testAnonymousClass() throws Exception {
        InstrumentedType instrumentedType = InstrumentedTypeDefaultTest.makePlainInstrumentedType();
        MatcherAssert.assertThat(isAnonymousType(), CoreMatchers.is(false));
        TypeDescription transformed = withAnonymousClass(true);
        MatcherAssert.assertThat(isAnonymousType(), CoreMatchers.is(true));
    }

    @Test(expected = IllegalStateException.class)
    public void testTypeIllegalName() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withName(InstrumentedTypeDefaultTest.ILLEGAL_NAME).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testTypeIllegalEndName() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withName(((InstrumentedTypeDefaultTest.FOO) + (InstrumentedTypeDefaultTest.ILLEGAL_NAME))).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testTypeEmptyEndName() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withName(NamedElement.EMPTY_NAME).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testTypeChainedEmptyEndName() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withName(("." + (InstrumentedTypeDefaultTest.FOO))).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testTypeIllegalKeywordName() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withName(void.class.getName()).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testTypeIllegalSubType() throws Exception {
        MODIFIABLE.subclass(InstrumentedTypeDefaultTest.FOO, ModifierContributor.EMPTY_MASK, describe(Serializable.class)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testTypeInvisibleSubType() throws Exception {
        MODIFIABLE.subclass(InstrumentedTypeDefaultTest.FOO, ModifierContributor.EMPTY_MASK, describe(PackagePrivateType.TYPE)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testTypeIllegalModifiers() throws Exception {
        MODIFIABLE.subclass(InstrumentedTypeDefaultTest.FOO, InstrumentedTypeDefaultTest.ILLEGAL_MODIFIERS, describe(Object.class)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testPackageTypeIllegalModifiers() throws Exception {
        MODIFIABLE.subclass((((InstrumentedTypeDefaultTest.FOO) + ".") + (PackageDescription.PACKAGE_CLASS_NAME)), ModifierContributor.EMPTY_MASK, describe(Object.class)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testTypeIllegalInterfaceType() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withInterfaces(new TypeList.Generic.Explicit(OBJECT)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testInvisibleInterfaceType() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withInterfaces(new TypeList.Generic.Explicit(describe(PackagePrivateType.INTERFACE_TYPE))).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testTypeDuplicateInterface() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withInterfaces(new TypeList.Generic.Explicit(of(Serializable.class), of(Serializable.class))).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testTypeThrowableWithGenerics() throws Exception {
        MODIFIABLE.represent(of(Exception.class)).withTypeVariable(new TypeVariableToken(InstrumentedTypeDefaultTest.FOO, Collections.singletonList(OBJECT))).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testTypeDuplicateTypeVariableName() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withTypeVariable(new TypeVariableToken(InstrumentedTypeDefaultTest.FOO, Collections.singletonList(OBJECT))).withTypeVariable(new TypeVariableToken(InstrumentedTypeDefaultTest.FOO, Collections.singletonList(OBJECT))).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testTypeTypeVariableIllegalName() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withTypeVariable(new TypeVariableToken(InstrumentedTypeDefaultTest.ILLEGAL_NAME, Collections.singletonList(OBJECT))).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testTypeTypeVariableMissingBound() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withTypeVariable(new TypeVariableToken(InstrumentedTypeDefaultTest.FOO, Collections.<TypeDescription.Generic>emptyList())).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testTypeTypeVariableDuplicateBound() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withTypeVariable(new TypeVariableToken(InstrumentedTypeDefaultTest.FOO, Arrays.asList(describe(Serializable.class), describe(Serializable.class)))).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testTypeTypeVariableIllegalBound() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withTypeVariable(new TypeVariableToken(InstrumentedTypeDefaultTest.FOO, Collections.singletonList(VOID))).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testTypeTypeVariableDoubleClassBound() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withTypeVariable(new TypeVariableToken(InstrumentedTypeDefaultTest.FOO, Arrays.asList(OBJECT, describe(String.class)))).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testDeclaringTypeArray() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withDeclaringType(of(Object[].class)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testDeclaringTypePrimitive() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withDeclaringType(of(void.class)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testDeclaredTypeArray() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withDeclaredTypes(new TypeList.Explicit(of(Object[].class))).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testDeclaredTypePrimitive() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withDeclaredTypes(new TypeList.Explicit(of(void.class))).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testDeclaredTypesDuplicate() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withDeclaredTypes(new TypeList.Explicit(TypeDescription.OBJECT, TypeDescription.OBJECT)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testEnclosingTypeArray() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withEnclosingType(of(Object[].class)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testEnclosingTypePrimitive() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withEnclosingType(of(void.class)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testStandaloneLocalClass() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withLocalClass(true).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testStandaloneAnonymousClass() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withAnonymousClass(true).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testNestHostArray() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withNestHost(of(Object[].class)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testNestHostPrimitive() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withNestHost(of(void.class)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testNestHostForeignPackage() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withNestHost(TypeDescription.OBJECT).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testNestMemberArray() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withNestMembers(new TypeList.Explicit(of(Object[].class))).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testNestMemberPrimitive() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withNestMembers(new TypeList.Explicit(of(void.class))).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testNestMemberDuplicate() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withName("java.lang.Test").withNestMembers(new TypeList.Explicit(TypeDescription.OBJECT, TypeDescription.OBJECT)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testNestMemberForeignPackage() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withNestMembers(new TypeList.Explicit(TypeDescription.OBJECT)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testTypeDuplicateAnnotation() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withAnnotations(Arrays.asList(ofType(InstrumentedTypeDefaultTest.SampleAnnotation.class).build(), ofType(InstrumentedTypeDefaultTest.SampleAnnotation.class).build())).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testTypeIncompatibleAnnotation() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withAnnotations(Collections.singletonList(ofType(InstrumentedTypeDefaultTest.IncompatibleAnnotation.class).build())).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testPackageIncompatibleAnnotation() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withName((((InstrumentedTypeDefaultTest.FOO) + ".") + (PackageDescription.PACKAGE_CLASS_NAME))).withAnnotations(Collections.singletonList(ofType(InstrumentedTypeDefaultTest.IncompatibleAnnotation.class).build())).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testAnnotationTypeIncompatibleAnnotation() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withModifiers(((Opcodes.ACC_ANNOTATION) | (Opcodes.ACC_ABSTRACT))).withAnnotations(Collections.singletonList(ofType(InstrumentedTypeDefaultTest.IncompatibleAnnotation.class).build())).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testAnnotationTypeIncompatibleSuperClassTypeAnnotation() throws Exception {
        MODIFIABLE.subclass(InstrumentedTypeDefaultTest.FOO, ModifierReviewable.EMPTY_MASK, rawType(Object.class).build(ofType(InstrumentedTypeDefaultTest.IncompatibleAnnotation.class).build())).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testAnnotationTypeIncompatibleInterfaceTypeAnnotation() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withInterfaces(new TypeList.Generic.Explicit(rawType(Runnable.class).build(ofType(InstrumentedTypeDefaultTest.IncompatibleAnnotation.class).build()))).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testAnnotationTypeIncompatibleTypeVariableTypeAnnotation() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withTypeVariable(new TypeVariableToken(InstrumentedTypeDefaultTest.FOO, Collections.singletonList(OBJECT), Collections.singletonList(ofType(InstrumentedTypeDefaultTest.IncompatibleAnnotation.class).build()))).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testAnnotationTypeIncompatibleTypeVariableBoundTypeAnnotation() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withTypeVariable(new TypeVariableToken(InstrumentedTypeDefaultTest.FOO, Collections.singletonList(rawType(Object.class).build(ofType(InstrumentedTypeDefaultTest.IncompatibleAnnotation.class).build())))).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testFieldDuplicateName() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withField(new FieldDescription.Token(InstrumentedTypeDefaultTest.FOO, ModifierContributor.EMPTY_MASK, OBJECT)).withField(new FieldDescription.Token(InstrumentedTypeDefaultTest.FOO, ModifierContributor.EMPTY_MASK, OBJECT)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testFieldIllegalName() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withField(new FieldDescription.Token(InstrumentedTypeDefaultTest.ILLEGAL_NAME, ModifierContributor.EMPTY_MASK, OBJECT)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testFieldIllegalModifiers() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withField(new FieldDescription.Token(InstrumentedTypeDefaultTest.FOO, InstrumentedTypeDefaultTest.ILLEGAL_MODIFIERS, OBJECT)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testFieldIllegalType() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withField(new FieldDescription.Token(InstrumentedTypeDefaultTest.ILLEGAL_NAME, ModifierContributor.EMPTY_MASK, VOID)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testFieldInvisibleType() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withField(new FieldDescription.Token(InstrumentedTypeDefaultTest.FOO, ModifierContributor.EMPTY_MASK, describe(PackagePrivateType.TYPE))).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void tesFieldDuplicateAnnotation() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withField(new FieldDescription.Token(InstrumentedTypeDefaultTest.FOO, ModifierContributor.EMPTY_MASK, OBJECT, Arrays.asList(ofType(InstrumentedTypeDefaultTest.SampleAnnotation.class).build(), ofType(InstrumentedTypeDefaultTest.SampleAnnotation.class).build()))).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void tesFieldIncompatibleAnnotation() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withField(new FieldDescription.Token(InstrumentedTypeDefaultTest.FOO, ModifierContributor.EMPTY_MASK, OBJECT, Collections.singletonList(ofType(InstrumentedTypeDefaultTest.IncompatibleAnnotation.class).build()))).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testFieldIncompatibleTypeAnnotation() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withField(new FieldDescription.Token(InstrumentedTypeDefaultTest.FOO, ModifierContributor.EMPTY_MASK, rawType(Runnable.class).build(ofType(InstrumentedTypeDefaultTest.IncompatibleAnnotation.class).build()))).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodDuplicateErasure() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withMethod(new MethodDescription.Token(InstrumentedTypeDefaultTest.FOO, ModifierContributor.EMPTY_MASK, OBJECT)).withMethod(new MethodDescription.Token(InstrumentedTypeDefaultTest.FOO, ModifierContributor.EMPTY_MASK, OBJECT)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodTypeInitializer() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withMethod(new MethodDescription.Token(MethodDescription.TYPE_INITIALIZER_INTERNAL_NAME, ModifierContributor.EMPTY_MASK, VOID)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testConstructorNonVoidReturnType() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withMethod(new MethodDescription.Token(MethodDescription.CONSTRUCTOR_INTERNAL_NAME, ModifierContributor.EMPTY_MASK, OBJECT)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodInvisibleReturnType() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withMethod(new MethodDescription.Token(InstrumentedTypeDefaultTest.FOO, ModifierContributor.EMPTY_MASK, describe(PackagePrivateType.TYPE))).validated();
    }

    @Test
    public void testMethodInvisibleReturnTypeSynthetic() throws Exception {
        MatcherAssert.assertThat(InstrumentedTypeDefaultTest.makePlainInstrumentedType().withMethod(new MethodDescription.Token(InstrumentedTypeDefaultTest.FOO, Opcodes.ACC_SYNTHETIC, describe(PackagePrivateType.TYPE))).validated(), CoreMatchers.instanceOf(TypeDescription.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodIllegalName() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withMethod(new MethodDescription.Token(InstrumentedTypeDefaultTest.ILLEGAL_NAME, ModifierContributor.EMPTY_MASK, OBJECT)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodIllegalModifiers() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withMethod(new MethodDescription.Token(InstrumentedTypeDefaultTest.FOO, InstrumentedTypeDefaultTest.ILLEGAL_MODIFIERS, OBJECT)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodDuplicateAnnotation() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withMethod(new MethodDescription.Token(InstrumentedTypeDefaultTest.FOO, ModifierContributor.EMPTY_MASK, Collections.<TypeVariableToken>emptyList(), OBJECT, Collections.<ParameterDescription.Token>emptyList(), Collections.<TypeDescription.Generic>emptyList(), Arrays.asList(ofType(InstrumentedTypeDefaultTest.SampleAnnotation.class).build(), ofType(InstrumentedTypeDefaultTest.SampleAnnotation.class).build()), AnnotationValue.UNDEFINED, UNDEFINED)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodIncompatibleAnnotation() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withMethod(new MethodDescription.Token(InstrumentedTypeDefaultTest.FOO, ModifierContributor.EMPTY_MASK, Collections.<TypeVariableToken>emptyList(), OBJECT, Collections.<ParameterDescription.Token>emptyList(), Collections.<TypeDescription.Generic>emptyList(), Collections.singletonList(ofType(InstrumentedTypeDefaultTest.IncompatibleAnnotation.class).build()), AnnotationValue.UNDEFINED, UNDEFINED)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodIncompatibleReturnTypeTypeAnnotation() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withMethod(new MethodDescription.Token(InstrumentedTypeDefaultTest.FOO, ModifierContributor.EMPTY_MASK, rawType(Runnable.class).build(ofType(InstrumentedTypeDefaultTest.IncompatibleAnnotation.class).build()))).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodIllegalTypeVariableTypeAnnotation() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withMethod(new MethodDescription.Token(InstrumentedTypeDefaultTest.FOO, ModifierContributor.EMPTY_MASK, Collections.singletonList(new TypeVariableToken(InstrumentedTypeDefaultTest.FOO, Collections.singletonList(OBJECT), Collections.singletonList(ofType(InstrumentedTypeDefaultTest.IncompatibleAnnotation.class).build()))), OBJECT, Collections.<ParameterDescription.Token>emptyList(), Collections.<TypeDescription.Generic>emptyList(), Collections.<AnnotationDescription>emptyList(), AnnotationValue.UNDEFINED, UNDEFINED)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodIllegalTypeVariableBoundTypeAnnotation() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withMethod(new MethodDescription.Token(InstrumentedTypeDefaultTest.FOO, ModifierContributor.EMPTY_MASK, Collections.singletonList(new TypeVariableToken(InstrumentedTypeDefaultTest.FOO, Collections.singletonList(rawType(Object.class).build(ofType(InstrumentedTypeDefaultTest.IncompatibleAnnotation.class).build())))), OBJECT, Collections.<ParameterDescription.Token>emptyList(), Collections.<TypeDescription.Generic>emptyList(), Collections.<AnnotationDescription>emptyList(), AnnotationValue.UNDEFINED, UNDEFINED)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodIllegalTypeVariableName() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withMethod(new MethodDescription.Token(InstrumentedTypeDefaultTest.FOO, ModifierContributor.EMPTY_MASK, Collections.singletonList(new TypeVariableToken(InstrumentedTypeDefaultTest.ILLEGAL_NAME, Collections.singletonList(OBJECT))), OBJECT, Collections.<ParameterDescription.Token>emptyList(), Collections.<TypeDescription.Generic>emptyList(), Collections.<AnnotationDescription>emptyList(), AnnotationValue.UNDEFINED, UNDEFINED)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodDuplicateTypeVariableName() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withMethod(new MethodDescription.Token(InstrumentedTypeDefaultTest.FOO, ModifierContributor.EMPTY_MASK, Arrays.asList(new TypeVariableToken(InstrumentedTypeDefaultTest.FOO, Collections.singletonList(OBJECT)), new TypeVariableToken(InstrumentedTypeDefaultTest.FOO, Collections.singletonList(OBJECT))), OBJECT, Collections.<ParameterDescription.Token>emptyList(), Collections.<TypeDescription.Generic>emptyList(), Collections.<AnnotationDescription>emptyList(), AnnotationValue.UNDEFINED, UNDEFINED)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodTypeVariableMissingBound() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withMethod(new MethodDescription.Token(InstrumentedTypeDefaultTest.FOO, ModifierContributor.EMPTY_MASK, Collections.singletonList(new TypeVariableToken(InstrumentedTypeDefaultTest.FOO, Collections.<TypeDescription.Generic>emptyList())), OBJECT, Collections.<ParameterDescription.Token>emptyList(), Collections.<TypeDescription.Generic>emptyList(), Collections.<AnnotationDescription>emptyList(), AnnotationValue.UNDEFINED, UNDEFINED)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodTypeVariableIllegalBound() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withMethod(new MethodDescription.Token(InstrumentedTypeDefaultTest.FOO, ModifierContributor.EMPTY_MASK, Collections.singletonList(new TypeVariableToken(InstrumentedTypeDefaultTest.FOO, Collections.singletonList(VOID))), OBJECT, Collections.<ParameterDescription.Token>emptyList(), Collections.<TypeDescription.Generic>emptyList(), Collections.<AnnotationDescription>emptyList(), AnnotationValue.UNDEFINED, UNDEFINED)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodTypeVariableDuplicateBound() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withMethod(new MethodDescription.Token(InstrumentedTypeDefaultTest.FOO, ModifierContributor.EMPTY_MASK, Collections.singletonList(new TypeVariableToken(InstrumentedTypeDefaultTest.FOO, Arrays.asList(describe(Serializable.class), describe(Serializable.class)))), OBJECT, Collections.<ParameterDescription.Token>emptyList(), Collections.<TypeDescription.Generic>emptyList(), Collections.<AnnotationDescription>emptyList(), AnnotationValue.UNDEFINED, UNDEFINED)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodTypeVariableDoubleClassBound() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withMethod(new MethodDescription.Token(InstrumentedTypeDefaultTest.FOO, ModifierContributor.EMPTY_MASK, Collections.singletonList(new TypeVariableToken(InstrumentedTypeDefaultTest.FOO, Arrays.asList(OBJECT, describe(String.class)))), OBJECT, Collections.<ParameterDescription.Token>emptyList(), Collections.<TypeDescription.Generic>emptyList(), Collections.<AnnotationDescription>emptyList(), AnnotationValue.UNDEFINED, UNDEFINED)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodParameterIllegalName() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withMethod(new MethodDescription.Token(InstrumentedTypeDefaultTest.FOO, ModifierContributor.EMPTY_MASK, Collections.<TypeVariableToken>emptyList(), OBJECT, Collections.singletonList(new ParameterDescription.Token(OBJECT, InstrumentedTypeDefaultTest.ILLEGAL_NAME, 0)), Collections.<TypeDescription.Generic>emptyList(), Collections.<AnnotationDescription>emptyList(), AnnotationValue.UNDEFINED, UNDEFINED)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodParameterIllegalType() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withMethod(new MethodDescription.Token(InstrumentedTypeDefaultTest.FOO, ModifierContributor.EMPTY_MASK, Collections.<TypeVariableToken>emptyList(), OBJECT, Collections.singletonList(new ParameterDescription.Token(VOID)), Collections.<TypeDescription.Generic>emptyList(), Collections.<AnnotationDescription>emptyList(), AnnotationValue.UNDEFINED, UNDEFINED)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodParameterInvisibleType() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withMethod(new MethodDescription.Token(InstrumentedTypeDefaultTest.FOO, ModifierContributor.EMPTY_MASK, Collections.<TypeVariableToken>emptyList(), OBJECT, Collections.singletonList(new ParameterDescription.Token(describe(PackagePrivateType.TYPE))), Collections.<TypeDescription.Generic>emptyList(), Collections.<AnnotationDescription>emptyList(), AnnotationValue.UNDEFINED, UNDEFINED)).validated();
    }

    @Test
    public void testMethodParameterInvisibleTypeSynthetic() throws Exception {
        MatcherAssert.assertThat(InstrumentedTypeDefaultTest.makePlainInstrumentedType().withMethod(new MethodDescription.Token(InstrumentedTypeDefaultTest.FOO, Opcodes.ACC_SYNTHETIC, Collections.<TypeVariableToken>emptyList(), OBJECT, Collections.singletonList(new ParameterDescription.Token(describe(PackagePrivateType.TYPE))), Collections.<TypeDescription.Generic>emptyList(), Collections.<AnnotationDescription>emptyList(), AnnotationValue.UNDEFINED, UNDEFINED)).validated(), CoreMatchers.notNullValue(TypeDescription.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodParameterDuplicateName() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withMethod(new MethodDescription.Token(InstrumentedTypeDefaultTest.FOO, ModifierContributor.EMPTY_MASK, Collections.<TypeVariableToken>emptyList(), OBJECT, Arrays.asList(new ParameterDescription.Token(OBJECT, InstrumentedTypeDefaultTest.FOO, 0), new ParameterDescription.Token(OBJECT, InstrumentedTypeDefaultTest.FOO, 0)), Collections.<TypeDescription.Generic>emptyList(), Collections.<AnnotationDescription>emptyList(), AnnotationValue.UNDEFINED, UNDEFINED)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodParameterIllegalModifiers() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withMethod(new MethodDescription.Token(InstrumentedTypeDefaultTest.FOO, ModifierContributor.EMPTY_MASK, Collections.<TypeVariableToken>emptyList(), OBJECT, Collections.singletonList(new ParameterDescription.Token(OBJECT, InstrumentedTypeDefaultTest.FOO, (-1))), Collections.<TypeDescription.Generic>emptyList(), Collections.<AnnotationDescription>emptyList(), AnnotationValue.UNDEFINED, UNDEFINED)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodParameterDuplicateAnnotation() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withMethod(new MethodDescription.Token(InstrumentedTypeDefaultTest.FOO, ModifierContributor.EMPTY_MASK, Collections.<TypeVariableToken>emptyList(), OBJECT, Collections.singletonList(new ParameterDescription.Token(OBJECT, Arrays.asList(ofType(InstrumentedTypeDefaultTest.SampleAnnotation.class).build(), ofType(InstrumentedTypeDefaultTest.SampleAnnotation.class).build()))), Collections.<TypeDescription.Generic>emptyList(), Collections.<AnnotationDescription>emptyList(), AnnotationValue.UNDEFINED, UNDEFINED)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodParameterIncompatibleAnnotation() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withMethod(new MethodDescription.Token(InstrumentedTypeDefaultTest.FOO, ModifierContributor.EMPTY_MASK, Collections.<TypeVariableToken>emptyList(), OBJECT, Collections.singletonList(new ParameterDescription.Token(OBJECT, Collections.singletonList(ofType(InstrumentedTypeDefaultTest.IncompatibleAnnotation.class).build()))), Collections.<TypeDescription.Generic>emptyList(), Collections.<AnnotationDescription>emptyList(), AnnotationValue.UNDEFINED, UNDEFINED)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodIllegalExceptionType() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withMethod(new MethodDescription.Token(InstrumentedTypeDefaultTest.FOO, ModifierContributor.EMPTY_MASK, Collections.<TypeVariableToken>emptyList(), OBJECT, Collections.<ParameterDescription.Token>emptyList(), Collections.singletonList(OBJECT), Collections.<AnnotationDescription>emptyList(), AnnotationValue.UNDEFINED, UNDEFINED)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodIncompatibleExceptionTypeTypeAnnotation() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withMethod(new MethodDescription.Token(InstrumentedTypeDefaultTest.FOO, ModifierContributor.EMPTY_MASK, Collections.<TypeVariableToken>emptyList(), OBJECT, Collections.<ParameterDescription.Token>emptyList(), Collections.singletonList(rawType(Exception.class).build(ofType(InstrumentedTypeDefaultTest.IncompatibleAnnotation.class).build())), Collections.<AnnotationDescription>emptyList(), AnnotationValue.UNDEFINED, UNDEFINED)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodInvisibleExceptionType() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withMethod(new MethodDescription.Token(InstrumentedTypeDefaultTest.FOO, ModifierContributor.EMPTY_MASK, Collections.<TypeVariableToken>emptyList(), OBJECT, Collections.<ParameterDescription.Token>emptyList(), Collections.singletonList(describe(PackagePrivateType.EXCEPTION_TYPE)), Collections.<AnnotationDescription>emptyList(), AnnotationValue.UNDEFINED, UNDEFINED)).validated();
    }

    @Test
    public void testMethodInvisibleExceptionSynthetic() throws Exception {
        MatcherAssert.assertThat(InstrumentedTypeDefaultTest.makePlainInstrumentedType().withMethod(new MethodDescription.Token(InstrumentedTypeDefaultTest.FOO, Opcodes.ACC_SYNTHETIC, Collections.<TypeVariableToken>emptyList(), OBJECT, Collections.<ParameterDescription.Token>emptyList(), Collections.singletonList(describe(PackagePrivateType.EXCEPTION_TYPE)), Collections.<AnnotationDescription>emptyList(), AnnotationValue.UNDEFINED, UNDEFINED)).validated(), CoreMatchers.notNullValue(TypeDescription.class));
    }

    @Test
    public void testMethodDuplicateExceptionType() throws Exception {
        MatcherAssert.assertThat(InstrumentedTypeDefaultTest.makePlainInstrumentedType().withMethod(new MethodDescription.Token(InstrumentedTypeDefaultTest.FOO, ModifierContributor.EMPTY_MASK, Collections.<TypeVariableToken>emptyList(), OBJECT, Collections.<ParameterDescription.Token>emptyList(), Arrays.asList(describe(Exception.class), describe(Exception.class)), Collections.<AnnotationDescription>emptyList(), AnnotationValue.UNDEFINED, UNDEFINED)).validated(), CoreMatchers.notNullValue(TypeDescription.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodIllegalDefaultValue() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withMethod(new MethodDescription.Token(InstrumentedTypeDefaultTest.FOO, ModifierContributor.EMPTY_MASK, Collections.<TypeVariableToken>emptyList(), OBJECT, Collections.<ParameterDescription.Token>emptyList(), Collections.<TypeDescription.Generic>emptyList(), Collections.<AnnotationDescription>emptyList(), AnnotationValue.ForConstant.of(InstrumentedTypeDefaultTest.FOO), UNDEFINED)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testNonNullReceiverStaticMethod() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withMethod(new MethodDescription.Token(InstrumentedTypeDefaultTest.FOO, Opcodes.ACC_STATIC, Collections.<TypeVariableToken>emptyList(), OBJECT, Collections.<ParameterDescription.Token>emptyList(), Collections.<TypeDescription.Generic>emptyList(), Collections.<AnnotationDescription>emptyList(), AnnotationValue.UNDEFINED, OBJECT)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testInconsistentReceiverNonStaticMethod() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withMethod(new MethodDescription.Token(InstrumentedTypeDefaultTest.FOO, ModifierContributor.EMPTY_MASK, Collections.<TypeVariableToken>emptyList(), OBJECT, Collections.<ParameterDescription.Token>emptyList(), Collections.<TypeDescription.Generic>emptyList(), Collections.<AnnotationDescription>emptyList(), AnnotationValue.UNDEFINED, OBJECT)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testInconsistentReceiverConstructor() throws Exception {
        InstrumentedTypeDefaultTest.makePlainInstrumentedType().withMethod(new MethodDescription.Token(MethodDescription.CONSTRUCTOR_INTERNAL_NAME, ModifierContributor.EMPTY_MASK, Collections.<TypeVariableToken>emptyList(), OBJECT, Collections.<ParameterDescription.Token>emptyList(), Collections.<TypeDescription.Generic>emptyList(), Collections.<AnnotationDescription>emptyList(), AnnotationValue.UNDEFINED, OBJECT)).validated();
    }

    @Test(expected = IllegalStateException.class)
    public void testInconsistentReceiverConstructorInnerClass() throws Exception {
        MODIFIABLE.represent(of(InstrumentedTypeDefaultTest.Foo.class)).withMethod(new MethodDescription.Token(MethodDescription.CONSTRUCTOR_INTERNAL_NAME, ModifierContributor.EMPTY_MASK, Collections.<TypeVariableToken>emptyList(), OBJECT, Collections.<ParameterDescription.Token>emptyList(), Collections.<TypeDescription.Generic>emptyList(), Collections.<AnnotationDescription>emptyList(), AnnotationValue.UNDEFINED, describe(InstrumentedTypeDefaultTest.Foo.class))).validated();
    }

    @Test
    public void testTypeVariableOutOfScopeIsErased() throws Exception {
        TypeDescription typeDescription = new InstrumentedType.Default("foo", Opcodes.ACC_PUBLIC, of(InstrumentedTypeDefaultTest.AbstractOuter.ExtendedInner.class), Collections.<TypeVariableToken>emptyList(), Collections.<TypeDescription.Generic>emptyList(), Collections.<FieldDescription.Token>emptyList(), Collections.singletonList(new MethodDescription.Token("foo", Opcodes.ACC_BRIDGE, VOID, Collections.<TypeDescription.Generic>emptyList())), Collections.<AnnotationDescription>emptyList(), TypeInitializer.None.INSTANCE, INSTANCE, TypeDescription.UNDEFINED, MethodDescription.UNDEFINED, TypeDescription.UNDEFINED, Collections.<TypeDescription>emptyList(), false, false, TargetType.DESCRIPTION, Collections.<TypeDescription>emptyList());
        MethodDescription methodDescription = typeDescription.getSuperClass().getSuperClass().getDeclaredMethods().filter(ElementMatchers.named(InstrumentedTypeDefaultTest.FOO)).getOnly();
        MatcherAssert.assertThat(methodDescription.getReturnType(), CoreMatchers.is(OBJECT));
    }

    public @interface SampleAnnotation {}

    @Target({  })
    public @interface IncompatibleAnnotation {}

    /* empty */
    private class Foo {}

    public abstract static class AbstractOuter<T> {
        public abstract class Inner {
            public abstract T foo();
        }

        /* empty */
        public abstract class ExtendedInner extends InstrumentedTypeDefaultTest.AbstractOuter<T>.Inner {}
    }
}

