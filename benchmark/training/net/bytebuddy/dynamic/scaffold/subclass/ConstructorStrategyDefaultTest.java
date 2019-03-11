package net.bytebuddy.dynamic.scaffold.subclass;


import java.util.Collections;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeVariableToken;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.dynamic.scaffold.MethodRegistry;
import net.bytebuddy.implementation.attribute.MethodAttributeAppender;
import net.bytebuddy.matcher.LatentMatcher;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.Opcodes;

import static net.bytebuddy.description.type.TypeDescription.Generic.UNDEFINED;
import static net.bytebuddy.dynamic.Transformer.NoOp.make;
import static net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy.Default.DEFAULT_CONSTRUCTOR;
import static net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy.Default.IMITATE_SUPER_CLASS;
import static net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy.Default.IMITATE_SUPER_CLASS_OPENING;
import static net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy.Default.IMITATE_SUPER_CLASS_PUBLIC;
import static net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy.Default.NO_CONSTRUCTORS;
import static net.bytebuddy.implementation.attribute.MethodAttributeAppender.ForInstrumentedMethod.EXCLUDING_RECEIVER;
import static net.bytebuddy.implementation.attribute.MethodAttributeAppender.NoOp.INSTANCE;


public class ConstructorStrategyDefaultTest {
    private static final String FOO = "foo";

    private static final int MODIFIERS = 42;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodRegistry methodRegistry;

    @Mock
    private InstrumentedType instrumentedType;

    @Mock
    private TypeDescription.Generic superClass;

    @Mock
    private TypeDescription.Generic typeDescription;

    @Mock
    private MethodDescription.InGenericShape methodDescription;

    @Mock
    private MethodDescription.Token token;

    @Mock
    private AnnotationValue<?, ?> defaultValue;

    private MethodDescription.Token stripped;

    @Test
    public void testNoConstructorsStrategy() throws Exception {
        MatcherAssert.assertThat(NO_CONSTRUCTORS.extractConstructors(instrumentedType).size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(NO_CONSTRUCTORS.inject(instrumentedType, methodRegistry), CoreMatchers.is(methodRegistry));
        Mockito.verifyZeroInteractions(methodRegistry);
        Mockito.verifyZeroInteractions(instrumentedType);
    }

    @Test
    public void testNoConstructorsStrategyWithAttributeAppender() throws Exception {
        MethodAttributeAppender.Factory methodAttributeAppenderFactory = Mockito.mock(MethodAttributeAppender.Factory.class);
        ConstructorStrategy constructorStrategy = NO_CONSTRUCTORS.with(methodAttributeAppenderFactory);
        MatcherAssert.assertThat(constructorStrategy.extractConstructors(instrumentedType).size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(constructorStrategy.inject(instrumentedType, methodRegistry), CoreMatchers.is(methodRegistry));
        Mockito.verifyZeroInteractions(methodRegistry);
        Mockito.verifyZeroInteractions(instrumentedType);
    }

    @Test
    public void testNoConstructorsStrategyWithInheritedAnnotations() throws Exception {
        ConstructorStrategy constructorStrategy = NO_CONSTRUCTORS.withInheritedAnnotations();
        MatcherAssert.assertThat(constructorStrategy.extractConstructors(instrumentedType).size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(constructorStrategy.inject(instrumentedType, methodRegistry), CoreMatchers.is(methodRegistry));
        Mockito.verifyZeroInteractions(methodRegistry);
        Mockito.verifyZeroInteractions(instrumentedType);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testImitateSuperClassStrategy() throws Exception {
        MatcherAssert.assertThat(IMITATE_SUPER_CLASS.extractConstructors(instrumentedType), CoreMatchers.is(Collections.singletonList(stripped)));
        MatcherAssert.assertThat(IMITATE_SUPER_CLASS.inject(instrumentedType, methodRegistry), CoreMatchers.is(methodRegistry));
        Mockito.verify(methodRegistry).append(ArgumentMatchers.any(LatentMatcher.class), ArgumentMatchers.any(MethodRegistry.Handler.class), ArgumentMatchers.eq(INSTANCE), ArgumentMatchers.eq(<MethodDescription>make()));
        Mockito.verifyNoMoreInteractions(methodRegistry);
        Mockito.verify(instrumentedType, Mockito.atLeastOnce()).getSuperClass();
        Mockito.verifyNoMoreInteractions(instrumentedType);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testImitateSuperClassStrategyWithAttributeAppender() throws Exception {
        Mockito.when(methodDescription.getModifiers()).thenReturn(Opcodes.ACC_PUBLIC);
        MethodAttributeAppender.Factory methodAttributeAppenderFactory = Mockito.mock(MethodAttributeAppender.Factory.class);
        ConstructorStrategy constructorStrategy = IMITATE_SUPER_CLASS.with(methodAttributeAppenderFactory);
        MatcherAssert.assertThat(constructorStrategy.extractConstructors(instrumentedType), CoreMatchers.is(Collections.singletonList(stripped)));
        MatcherAssert.assertThat(constructorStrategy.inject(instrumentedType, methodRegistry), CoreMatchers.is(methodRegistry));
        Mockito.verify(methodRegistry).append(ArgumentMatchers.any(LatentMatcher.class), ArgumentMatchers.any(MethodRegistry.Handler.class), ArgumentMatchers.eq(methodAttributeAppenderFactory), ArgumentMatchers.eq(<MethodDescription>make()));
        Mockito.verifyNoMoreInteractions(methodRegistry);
        Mockito.verify(instrumentedType, Mockito.atLeastOnce()).getSuperClass();
        Mockito.verifyNoMoreInteractions(instrumentedType);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testImitateSuperClassStrategyWithInheritedAnnotations() throws Exception {
        Mockito.when(methodDescription.getModifiers()).thenReturn(Opcodes.ACC_PUBLIC);
        ConstructorStrategy constructorStrategy = IMITATE_SUPER_CLASS.withInheritedAnnotations();
        MatcherAssert.assertThat(constructorStrategy.extractConstructors(instrumentedType), CoreMatchers.is(Collections.singletonList(stripped)));
        MatcherAssert.assertThat(constructorStrategy.inject(instrumentedType, methodRegistry), CoreMatchers.is(methodRegistry));
        Mockito.verify(methodRegistry).append(ArgumentMatchers.any(LatentMatcher.class), ArgumentMatchers.any(MethodRegistry.Handler.class), ArgumentMatchers.eq(EXCLUDING_RECEIVER), ArgumentMatchers.eq(<MethodDescription>make()));
        Mockito.verifyNoMoreInteractions(methodRegistry);
        Mockito.verify(instrumentedType, Mockito.atLeastOnce()).getSuperClass();
        Mockito.verifyNoMoreInteractions(instrumentedType);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testImitateSuperClassPublicStrategy() throws Exception {
        Mockito.when(methodDescription.getModifiers()).thenReturn(Opcodes.ACC_PUBLIC);
        MatcherAssert.assertThat(IMITATE_SUPER_CLASS_PUBLIC.extractConstructors(instrumentedType), CoreMatchers.is(Collections.singletonList(stripped)));
        MatcherAssert.assertThat(IMITATE_SUPER_CLASS_PUBLIC.inject(instrumentedType, methodRegistry), CoreMatchers.is(methodRegistry));
        Mockito.verify(methodRegistry).append(ArgumentMatchers.any(LatentMatcher.class), ArgumentMatchers.any(MethodRegistry.Handler.class), ArgumentMatchers.eq(INSTANCE), ArgumentMatchers.eq(<MethodDescription>make()));
        Mockito.verifyNoMoreInteractions(methodRegistry);
        Mockito.verify(instrumentedType, Mockito.atLeastOnce()).getSuperClass();
        Mockito.verifyNoMoreInteractions(instrumentedType);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testImitateSuperClassPublicStrategyWithAttributeAppender() throws Exception {
        Mockito.when(methodDescription.getModifiers()).thenReturn(Opcodes.ACC_PUBLIC);
        MethodAttributeAppender.Factory methodAttributeAppenderFactory = Mockito.mock(MethodAttributeAppender.Factory.class);
        ConstructorStrategy constructorStrategy = IMITATE_SUPER_CLASS_PUBLIC.with(methodAttributeAppenderFactory);
        MatcherAssert.assertThat(constructorStrategy.extractConstructors(instrumentedType), CoreMatchers.is(Collections.singletonList(stripped)));
        MatcherAssert.assertThat(constructorStrategy.inject(instrumentedType, methodRegistry), CoreMatchers.is(methodRegistry));
        Mockito.verify(methodRegistry).append(ArgumentMatchers.any(LatentMatcher.class), ArgumentMatchers.any(MethodRegistry.Handler.class), ArgumentMatchers.eq(methodAttributeAppenderFactory), ArgumentMatchers.eq(<MethodDescription>make()));
        Mockito.verifyNoMoreInteractions(methodRegistry);
        Mockito.verify(instrumentedType, Mockito.atLeastOnce()).getSuperClass();
        Mockito.verifyNoMoreInteractions(instrumentedType);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testImitateSuperClassPublicStrategyWithInheritedAnnotations() throws Exception {
        Mockito.when(methodDescription.getModifiers()).thenReturn(Opcodes.ACC_PUBLIC);
        ConstructorStrategy constructorStrategy = IMITATE_SUPER_CLASS_PUBLIC.withInheritedAnnotations();
        MatcherAssert.assertThat(constructorStrategy.extractConstructors(instrumentedType), CoreMatchers.is(Collections.singletonList(stripped)));
        MatcherAssert.assertThat(constructorStrategy.inject(instrumentedType, methodRegistry), CoreMatchers.is(methodRegistry));
        Mockito.verify(methodRegistry).append(ArgumentMatchers.any(LatentMatcher.class), ArgumentMatchers.any(MethodRegistry.Handler.class), ArgumentMatchers.eq(EXCLUDING_RECEIVER), ArgumentMatchers.eq(<MethodDescription>make()));
        Mockito.verifyNoMoreInteractions(methodRegistry);
        Mockito.verify(instrumentedType, Mockito.atLeastOnce()).getSuperClass();
        Mockito.verifyNoMoreInteractions(instrumentedType);
    }

    @Test
    public void testImitateSuperClassPublicStrategyDoesNotSeeNonPublic() throws Exception {
        Mockito.when(methodDescription.getModifiers()).thenReturn(0);
        MatcherAssert.assertThat(IMITATE_SUPER_CLASS_PUBLIC.extractConstructors(instrumentedType).size(), CoreMatchers.is(0));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDefaultConstructorStrategy() throws Exception {
        Mockito.when(methodDescription.getParameters()).thenReturn(new ParameterList.Empty<ParameterDescription.InGenericShape>());
        MatcherAssert.assertThat(DEFAULT_CONSTRUCTOR.extractConstructors(instrumentedType), CoreMatchers.is(Collections.singletonList(new MethodDescription.Token(Opcodes.ACC_PUBLIC))));
        MatcherAssert.assertThat(DEFAULT_CONSTRUCTOR.inject(instrumentedType, methodRegistry), CoreMatchers.is(methodRegistry));
        Mockito.verify(methodRegistry).append(ArgumentMatchers.any(LatentMatcher.class), ArgumentMatchers.any(MethodRegistry.Handler.class), ArgumentMatchers.eq(INSTANCE), ArgumentMatchers.eq(<MethodDescription>make()));
        Mockito.verifyNoMoreInteractions(methodRegistry);
        Mockito.verify(instrumentedType).getSuperClass();
        Mockito.verifyNoMoreInteractions(instrumentedType);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDefaultConstructorStrategyWithAttributeAppender() throws Exception {
        Mockito.when(methodDescription.getParameters()).thenReturn(new ParameterList.Empty<ParameterDescription.InGenericShape>());
        MethodAttributeAppender.Factory methodAttributeAppenderFactory = Mockito.mock(MethodAttributeAppender.Factory.class);
        ConstructorStrategy constructorStrategy = DEFAULT_CONSTRUCTOR.with(methodAttributeAppenderFactory);
        MatcherAssert.assertThat(constructorStrategy.extractConstructors(instrumentedType), CoreMatchers.is(Collections.singletonList(new MethodDescription.Token(Opcodes.ACC_PUBLIC))));
        MatcherAssert.assertThat(constructorStrategy.inject(instrumentedType, methodRegistry), CoreMatchers.is(methodRegistry));
        Mockito.verify(methodRegistry).append(ArgumentMatchers.any(LatentMatcher.class), ArgumentMatchers.any(MethodRegistry.Handler.class), ArgumentMatchers.eq(methodAttributeAppenderFactory), ArgumentMatchers.eq(<MethodDescription>make()));
        Mockito.verifyNoMoreInteractions(methodRegistry);
        Mockito.verify(instrumentedType).getSuperClass();
        Mockito.verifyNoMoreInteractions(instrumentedType);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDefaultConstructorStrategyWithInheritedAnnotations() throws Exception {
        Mockito.when(methodDescription.getParameters()).thenReturn(new ParameterList.Empty<ParameterDescription.InGenericShape>());
        ConstructorStrategy constructorStrategy = DEFAULT_CONSTRUCTOR.withInheritedAnnotations();
        MatcherAssert.assertThat(constructorStrategy.extractConstructors(instrumentedType), CoreMatchers.is(Collections.singletonList(new MethodDescription.Token(Opcodes.ACC_PUBLIC))));
        MatcherAssert.assertThat(constructorStrategy.inject(instrumentedType, methodRegistry), CoreMatchers.is(methodRegistry));
        Mockito.verify(methodRegistry).append(ArgumentMatchers.any(LatentMatcher.class), ArgumentMatchers.any(MethodRegistry.Handler.class), ArgumentMatchers.eq(EXCLUDING_RECEIVER), ArgumentMatchers.eq(<MethodDescription>make()));
        Mockito.verifyNoMoreInteractions(methodRegistry);
        Mockito.verify(instrumentedType).getSuperClass();
        Mockito.verifyNoMoreInteractions(instrumentedType);
    }

    @Test(expected = IllegalArgumentException.class)
    @SuppressWarnings("unchecked")
    public void testDefaultConstructorStrategyNoDefault() throws Exception {
        Mockito.when(methodDescription.getParameters()).thenReturn(new ParameterList.Explicit<ParameterDescription.InGenericShape>(Mockito.mock(ParameterDescription.InGenericShape.class)));
        DEFAULT_CONSTRUCTOR.extractConstructors(instrumentedType);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testImitateSuperClassOpeningStrategyNonVisible() throws Exception {
        Mockito.when(methodDescription.isVisibleTo(instrumentedType)).thenReturn(false);
        MatcherAssert.assertThat(IMITATE_SUPER_CLASS_OPENING.extractConstructors(instrumentedType).isEmpty(), CoreMatchers.is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testImitateSuperClassOpeningStrategy() throws Exception {
        MatcherAssert.assertThat(IMITATE_SUPER_CLASS_OPENING.extractConstructors(instrumentedType), CoreMatchers.is(Collections.singletonList(new MethodDescription.Token(ConstructorStrategyDefaultTest.FOO, Opcodes.ACC_PUBLIC, Collections.<TypeVariableToken>emptyList(), typeDescription, Collections.<ParameterDescription.Token>emptyList(), Collections.<TypeDescription.Generic>emptyList(), Collections.<AnnotationDescription>emptyList(), defaultValue, UNDEFINED))));
        MatcherAssert.assertThat(IMITATE_SUPER_CLASS_OPENING.inject(instrumentedType, methodRegistry), CoreMatchers.is(methodRegistry));
        Mockito.verify(methodRegistry).append(ArgumentMatchers.any(LatentMatcher.class), ArgumentMatchers.any(MethodRegistry.Handler.class), ArgumentMatchers.eq(INSTANCE), ArgumentMatchers.eq(<MethodDescription>make()));
        Mockito.verifyNoMoreInteractions(methodRegistry);
        Mockito.verify(instrumentedType, Mockito.atLeastOnce()).getSuperClass();
        Mockito.verifyNoMoreInteractions(instrumentedType);
    }
}

