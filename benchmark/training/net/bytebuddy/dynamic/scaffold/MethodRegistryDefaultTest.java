package net.bytebuddy.dynamic.scaffold;


import VisibilityBridgeStrategy.Default.ALWAYS;
import VisibilityBridgeStrategy.Default.NEVER;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.Transformer;
import net.bytebuddy.dynamic.VisibilityBridgeStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.LoadedTypeInitializer;
import net.bytebuddy.implementation.attribute.MethodAttributeAppender;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
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


public class MethodRegistryDefaultTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private ClassFileVersion classFileVersion;

    @Mock
    private LatentMatcher<MethodDescription> firstMatcher;

    @Mock
    private LatentMatcher<MethodDescription> secondMatcher;

    @Mock
    private LatentMatcher<MethodDescription> methodFilter;

    @Mock
    private MethodRegistry.Handler firstHandler;

    @Mock
    private MethodRegistry.Handler secondHandler;

    @Mock
    private MethodRegistry.Handler.Compiled firstCompiledHandler;

    @Mock
    private MethodRegistry.Handler.Compiled secondCompiledHandler;

    @Mock
    private TypeWriter.MethodPool.Record firstRecord;

    @Mock
    private TypeWriter.MethodPool.Record secondRecord;

    @Mock
    private MethodAttributeAppender.Factory firstFactory;

    @Mock
    private MethodAttributeAppender.Factory secondFactory;

    @Mock
    private MethodAttributeAppender firstAppender;

    @Mock
    private MethodAttributeAppender secondAppender;

    @Mock
    private InstrumentedType firstType;

    @Mock
    private InstrumentedType secondType;

    @Mock
    private InstrumentedType thirdType;

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private MethodDescription instrumentedMethod;

    @Mock
    private MethodGraph.Compiler methodGraphCompiler;

    @Mock
    private MethodGraph.Linked methodGraph;

    @Mock
    private TypeInitializer typeInitializer;

    @Mock
    private LoadedTypeInitializer loadedTypeInitializer;

    @Mock
    private ElementMatcher<? super MethodDescription> resolvedMethodFilter;

    @Mock
    private ElementMatcher<? super MethodDescription> firstFilter;

    @Mock
    private ElementMatcher<? super MethodDescription> secondFilter;

    @Mock
    private Implementation.Target.Factory implementationTargetFactory;

    @Mock
    private Implementation.Target implementationTarget;

    @Mock
    private Transformer<MethodDescription> transformer;

    @Mock
    private TypeDescription returnType;

    @Mock
    private TypeDescription parameterType;

    @Mock
    private TypeDescription.Generic genericReturnType;

    @Mock
    private TypeDescription.Generic genericParameterType;

    @Mock
    private ParameterDescription.InDefinedShape parameterDescription;

    @Mock
    private VisibilityBridgeStrategy visibilityBridgeStrategy;

    @Test
    public void testNonMatchedIsNotIncluded() throws Exception {
        Mockito.when(resolvedMethodFilter.matches(instrumentedMethod)).thenReturn(true);
        MethodRegistry.Prepared methodRegistry = new MethodRegistry.Default().append(firstMatcher, firstHandler, firstFactory, transformer).append(secondMatcher, secondHandler, secondFactory, transformer).prepare(firstType, methodGraphCompiler, TypeValidation.ENABLED, ALWAYS, methodFilter);
        MatcherAssert.assertThat(methodRegistry.getInstrumentedType(), CoreMatchers.is(typeDescription));
        MatcherAssert.assertThat(methodRegistry.getInstrumentedMethods().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(methodRegistry.getTypeInitializer(), CoreMatchers.is(typeInitializer));
        MatcherAssert.assertThat(methodRegistry.getLoadedTypeInitializer(), CoreMatchers.is(loadedTypeInitializer));
        Mockito.verify(firstHandler).prepare(firstType);
        Mockito.verify(secondHandler).prepare(secondType);
    }

    @Test
    public void testIgnoredIsNotIncluded() throws Exception {
        Mockito.when(firstFilter.matches(instrumentedMethod)).thenReturn(true);
        Mockito.when(secondFilter.matches(instrumentedMethod)).thenReturn(true);
        MethodRegistry.Prepared methodRegistry = new MethodRegistry.Default().append(firstMatcher, firstHandler, firstFactory, transformer).append(secondMatcher, secondHandler, secondFactory, transformer).prepare(firstType, methodGraphCompiler, TypeValidation.ENABLED, ALWAYS, methodFilter);
        MatcherAssert.assertThat(methodRegistry.getInstrumentedType(), CoreMatchers.is(typeDescription));
        MatcherAssert.assertThat(methodRegistry.getInstrumentedMethods().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(methodRegistry.getTypeInitializer(), CoreMatchers.is(typeInitializer));
        MatcherAssert.assertThat(methodRegistry.getLoadedTypeInitializer(), CoreMatchers.is(loadedTypeInitializer));
        Mockito.verify(firstHandler).prepare(firstType);
        Mockito.verify(secondHandler).prepare(secondType);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMatchedFirst() throws Exception {
        Mockito.when(resolvedMethodFilter.matches(instrumentedMethod)).thenReturn(true);
        Mockito.when(firstFilter.matches(instrumentedMethod)).thenReturn(true);
        MethodRegistry.Prepared methodRegistry = new MethodRegistry.Default().append(firstMatcher, firstHandler, firstFactory, transformer).append(secondMatcher, secondHandler, secondFactory, transformer).prepare(firstType, methodGraphCompiler, TypeValidation.ENABLED, ALWAYS, methodFilter);
        MatcherAssert.assertThat(methodRegistry.getInstrumentedType(), CoreMatchers.is(typeDescription));
        MatcherAssert.assertThat(methodRegistry.getInstrumentedMethods(), CoreMatchers.is(((MethodList) (new MethodList.Explicit(instrumentedMethod)))));
        MatcherAssert.assertThat(methodRegistry.getTypeInitializer(), CoreMatchers.is(typeInitializer));
        MatcherAssert.assertThat(methodRegistry.getLoadedTypeInitializer(), CoreMatchers.is(loadedTypeInitializer));
        Mockito.verify(firstHandler).prepare(firstType);
        Mockito.verify(secondHandler).prepare(secondType);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMatchedSecond() throws Exception {
        Mockito.when(resolvedMethodFilter.matches(instrumentedMethod)).thenReturn(true);
        Mockito.when(secondFilter.matches(instrumentedMethod)).thenReturn(true);
        MethodRegistry.Prepared methodRegistry = new MethodRegistry.Default().append(firstMatcher, firstHandler, firstFactory, transformer).append(secondMatcher, secondHandler, secondFactory, transformer).prepare(firstType, methodGraphCompiler, TypeValidation.ENABLED, ALWAYS, methodFilter);
        MatcherAssert.assertThat(methodRegistry.getInstrumentedType(), CoreMatchers.is(typeDescription));
        MatcherAssert.assertThat(methodRegistry.getInstrumentedMethods(), CoreMatchers.is(((MethodList) (new MethodList.Explicit(instrumentedMethod)))));
        MatcherAssert.assertThat(methodRegistry.getTypeInitializer(), CoreMatchers.is(typeInitializer));
        MatcherAssert.assertThat(methodRegistry.getLoadedTypeInitializer(), CoreMatchers.is(loadedTypeInitializer));
        Mockito.verify(firstHandler).prepare(firstType);
        Mockito.verify(secondHandler).prepare(secondType);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMultipleRegistryDoesNotPrepareMultipleTimes() throws Exception {
        Mockito.when(resolvedMethodFilter.matches(instrumentedMethod)).thenReturn(true);
        Mockito.when(firstFilter.matches(instrumentedMethod)).thenReturn(true);
        Mockito.when(secondFilter.matches(instrumentedMethod)).thenReturn(true);
        MethodRegistry.Prepared methodRegistry = new MethodRegistry.Default().append(firstMatcher, firstHandler, firstFactory, transformer).append(firstMatcher, firstHandler, firstFactory, transformer).append(secondMatcher, firstHandler, firstFactory, transformer).append(secondMatcher, firstHandler, secondFactory, transformer).append(secondMatcher, secondHandler, secondFactory, transformer).append(firstMatcher, secondHandler, secondFactory, transformer).append(firstMatcher, firstHandler, secondFactory, transformer).append(firstMatcher, secondHandler, firstFactory, transformer).prepare(firstType, methodGraphCompiler, TypeValidation.ENABLED, ALWAYS, methodFilter);
        MatcherAssert.assertThat(methodRegistry.getInstrumentedType(), CoreMatchers.is(typeDescription));
        MatcherAssert.assertThat(methodRegistry.getInstrumentedMethods(), CoreMatchers.is(((MethodList) (new MethodList.Explicit(instrumentedMethod)))));
        MatcherAssert.assertThat(methodRegistry.getTypeInitializer(), CoreMatchers.is(typeInitializer));
        MatcherAssert.assertThat(methodRegistry.getLoadedTypeInitializer(), CoreMatchers.is(loadedTypeInitializer));
        Mockito.verify(firstHandler).prepare(firstType);
        Mockito.verify(secondHandler).prepare(secondType);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCompiledAppendingMatchesFirstAppended() throws Exception {
        Mockito.when(resolvedMethodFilter.matches(instrumentedMethod)).thenReturn(true);
        Mockito.when(firstFilter.matches(instrumentedMethod)).thenReturn(true);
        Mockito.when(secondFilter.matches(instrumentedMethod)).thenReturn(true);
        Mockito.when(resolvedMethodFilter.matches(instrumentedMethod)).thenReturn(true);
        MethodRegistry.Compiled methodRegistry = new MethodRegistry.Default().append(firstMatcher, firstHandler, firstFactory, transformer).append(secondMatcher, secondHandler, secondFactory, transformer).prepare(firstType, methodGraphCompiler, TypeValidation.ENABLED, ALWAYS, methodFilter).compile(implementationTargetFactory, classFileVersion);
        MatcherAssert.assertThat(methodRegistry.getInstrumentedType(), CoreMatchers.is(typeDescription));
        MatcherAssert.assertThat(methodRegistry.getInstrumentedMethods(), CoreMatchers.is(((MethodList) (new MethodList.Explicit(instrumentedMethod)))));
        MatcherAssert.assertThat(methodRegistry.getTypeInitializer(), CoreMatchers.is(typeInitializer));
        MatcherAssert.assertThat(methodRegistry.getLoadedTypeInitializer(), CoreMatchers.is(loadedTypeInitializer));
        Mockito.verify(firstHandler).prepare(firstType);
        Mockito.verify(secondHandler).prepare(secondType);
        Mockito.verify(firstFactory).make(typeDescription);
        Mockito.verifyZeroInteractions(secondFactory);
        MatcherAssert.assertThat(methodRegistry.target(instrumentedMethod), CoreMatchers.is(firstRecord));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCompiledPrependingMatchesLastPrepended() throws Exception {
        Mockito.when(resolvedMethodFilter.matches(instrumentedMethod)).thenReturn(true);
        Mockito.when(firstFilter.matches(instrumentedMethod)).thenReturn(true);
        Mockito.when(secondFilter.matches(instrumentedMethod)).thenReturn(true);
        Mockito.when(resolvedMethodFilter.matches(instrumentedMethod)).thenReturn(true);
        MethodRegistry.Compiled methodRegistry = new MethodRegistry.Default().append(secondMatcher, secondHandler, secondFactory, transformer).prepend(firstMatcher, firstHandler, firstFactory, transformer).prepare(firstType, methodGraphCompiler, TypeValidation.ENABLED, ALWAYS, methodFilter).compile(implementationTargetFactory, classFileVersion);
        MatcherAssert.assertThat(methodRegistry.getInstrumentedType(), CoreMatchers.is(typeDescription));
        MatcherAssert.assertThat(methodRegistry.getInstrumentedMethods(), CoreMatchers.is(((MethodList) (new MethodList.Explicit(instrumentedMethod)))));
        MatcherAssert.assertThat(methodRegistry.getTypeInitializer(), CoreMatchers.is(typeInitializer));
        MatcherAssert.assertThat(methodRegistry.getLoadedTypeInitializer(), CoreMatchers.is(loadedTypeInitializer));
        Mockito.verify(firstHandler).prepare(firstType);
        Mockito.verify(secondHandler).prepare(secondType);
        Mockito.verify(firstFactory).make(typeDescription);
        Mockito.verifyZeroInteractions(secondFactory);
        MatcherAssert.assertThat(methodRegistry.target(instrumentedMethod), CoreMatchers.is(firstRecord));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCompiledAppendingMatchesSecondAppendedIfFirstDoesNotMatch() throws Exception {
        Mockito.when(resolvedMethodFilter.matches(instrumentedMethod)).thenReturn(true);
        Mockito.when(firstFilter.matches(instrumentedMethod)).thenReturn(false);
        Mockito.when(secondFilter.matches(instrumentedMethod)).thenReturn(true);
        Mockito.when(resolvedMethodFilter.matches(instrumentedMethod)).thenReturn(true);
        MethodRegistry.Compiled methodRegistry = new MethodRegistry.Default().append(firstMatcher, firstHandler, firstFactory, transformer).append(secondMatcher, secondHandler, secondFactory, transformer).prepare(firstType, methodGraphCompiler, TypeValidation.ENABLED, ALWAYS, methodFilter).compile(implementationTargetFactory, classFileVersion);
        MatcherAssert.assertThat(methodRegistry.getInstrumentedType(), CoreMatchers.is(typeDescription));
        MatcherAssert.assertThat(methodRegistry.getInstrumentedMethods(), CoreMatchers.is(((MethodList) (new MethodList.Explicit(instrumentedMethod)))));
        MatcherAssert.assertThat(methodRegistry.getTypeInitializer(), CoreMatchers.is(typeInitializer));
        MatcherAssert.assertThat(methodRegistry.getLoadedTypeInitializer(), CoreMatchers.is(loadedTypeInitializer));
        Mockito.verify(firstHandler).prepare(firstType);
        Mockito.verify(secondHandler).prepare(secondType);
        Mockito.verifyZeroInteractions(firstFactory);
        Mockito.verify(secondFactory).make(typeDescription);
        MatcherAssert.assertThat(methodRegistry.target(instrumentedMethod), CoreMatchers.is(secondRecord));
    }

    @Test
    public void testSkipEntryIfNotMatchedAndVisible() throws Exception {
        Mockito.when(resolvedMethodFilter.matches(instrumentedMethod)).thenReturn(true);
        Mockito.when(firstFilter.matches(instrumentedMethod)).thenReturn(false);
        Mockito.when(secondFilter.matches(instrumentedMethod)).thenReturn(false);
        Mockito.when(resolvedMethodFilter.matches(instrumentedMethod)).thenReturn(true);
        TypeDescription declaringType = Mockito.mock(TypeDescription.class);
        Mockito.when(declaringType.asErasure()).thenReturn(declaringType);
        Mockito.when(instrumentedMethod.getDeclaringType()).thenReturn(declaringType);
        MethodRegistry.Compiled methodRegistry = new MethodRegistry.Default().append(firstMatcher, firstHandler, firstFactory, transformer).append(secondMatcher, secondHandler, secondFactory, transformer).prepare(firstType, methodGraphCompiler, TypeValidation.ENABLED, ALWAYS, methodFilter).compile(implementationTargetFactory, classFileVersion);
        MatcherAssert.assertThat(methodRegistry.getInstrumentedType(), CoreMatchers.is(typeDescription));
        MatcherAssert.assertThat(methodRegistry.getInstrumentedMethods().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(methodRegistry.getTypeInitializer(), CoreMatchers.is(typeInitializer));
        MatcherAssert.assertThat(methodRegistry.getLoadedTypeInitializer(), CoreMatchers.is(loadedTypeInitializer));
        Mockito.verify(firstHandler).prepare(firstType);
        Mockito.verify(secondHandler).prepare(secondType);
        Mockito.verifyZeroInteractions(firstFactory);
        Mockito.verifyZeroInteractions(secondFactory);
        MatcherAssert.assertThat(methodRegistry.target(instrumentedMethod), CoreMatchers.instanceOf(TypeWriter.MethodPool.Record.ForNonImplementedMethod.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testVisibilityBridgeIfNotMatchedAndVisible() throws Exception {
        Mockito.when(instrumentedMethod.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(parameterDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(resolvedMethodFilter.matches(instrumentedMethod)).thenReturn(true);
        Mockito.when(firstFilter.matches(instrumentedMethod)).thenReturn(false);
        Mockito.when(secondFilter.matches(instrumentedMethod)).thenReturn(false);
        Mockito.when(resolvedMethodFilter.matches(instrumentedMethod)).thenReturn(true);
        TypeDescription declaringType = Mockito.mock(TypeDescription.class);
        Mockito.when(declaringType.asErasure()).thenReturn(declaringType);
        Mockito.when(instrumentedMethod.getDeclaringType()).thenReturn(declaringType);
        Mockito.when(thirdType.isPublic()).thenReturn(true);
        Mockito.when(instrumentedMethod.isPublic()).thenReturn(true);
        Mockito.when(declaringType.isPackagePrivate()).thenReturn(true);
        TypeDescription.Generic superClass = Mockito.mock(TypeDescription.Generic.class);
        TypeDescription rawSuperClass = Mockito.mock(TypeDescription.class);
        Mockito.when(superClass.asErasure()).thenReturn(rawSuperClass);
        Mockito.when(typeDescription.getSuperClass()).thenReturn(superClass);
        MethodDescription.Token methodToken = Mockito.mock(MethodDescription.Token.class);
        Mockito.when(instrumentedMethod.asToken(ElementMatchers.is(typeDescription))).thenReturn(methodToken);
        Mockito.when(methodToken.accept(ArgumentMatchers.any(TypeDescription.Generic.Visitor.class))).thenReturn(methodToken);
        Mockito.when(classFileVersion.isAtLeast(ClassFileVersion.JAVA_V5)).thenReturn(true);
        MethodRegistry.Compiled methodRegistry = new MethodRegistry.Default().append(firstMatcher, firstHandler, firstFactory, transformer).append(secondMatcher, secondHandler, secondFactory, transformer).prepare(firstType, methodGraphCompiler, TypeValidation.ENABLED, ALWAYS, methodFilter).compile(implementationTargetFactory, classFileVersion);
        MatcherAssert.assertThat(methodRegistry.getInstrumentedType(), CoreMatchers.is(typeDescription));
        MatcherAssert.assertThat(methodRegistry.getInstrumentedMethods().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(methodRegistry.getTypeInitializer(), CoreMatchers.is(typeInitializer));
        MatcherAssert.assertThat(methodRegistry.getLoadedTypeInitializer(), CoreMatchers.is(loadedTypeInitializer));
        Mockito.verify(firstHandler).prepare(firstType);
        Mockito.verify(secondHandler).prepare(secondType);
        Mockito.verifyZeroInteractions(firstFactory);
        Mockito.verifyZeroInteractions(secondFactory);
        MatcherAssert.assertThat(methodRegistry.target(instrumentedMethod), CoreMatchers.instanceOf(TypeWriter.MethodPool.Record.ForDefinedMethod.OfVisibilityBridge.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testVisibilityBridgeIfNotMatchedAndVisibleBridgesDisabled() throws Exception {
        Mockito.when(instrumentedMethod.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(parameterDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(resolvedMethodFilter.matches(instrumentedMethod)).thenReturn(true);
        Mockito.when(firstFilter.matches(instrumentedMethod)).thenReturn(false);
        Mockito.when(secondFilter.matches(instrumentedMethod)).thenReturn(false);
        Mockito.when(resolvedMethodFilter.matches(instrumentedMethod)).thenReturn(true);
        TypeDescription declaringType = Mockito.mock(TypeDescription.class);
        Mockito.when(declaringType.asErasure()).thenReturn(declaringType);
        Mockito.when(instrumentedMethod.getDeclaringType()).thenReturn(declaringType);
        Mockito.when(thirdType.isPublic()).thenReturn(true);
        Mockito.when(instrumentedMethod.isPublic()).thenReturn(true);
        Mockito.when(declaringType.isPackagePrivate()).thenReturn(true);
        TypeDescription.Generic superClass = Mockito.mock(TypeDescription.Generic.class);
        TypeDescription rawSuperClass = Mockito.mock(TypeDescription.class);
        Mockito.when(superClass.asErasure()).thenReturn(rawSuperClass);
        Mockito.when(typeDescription.getSuperClass()).thenReturn(superClass);
        MethodDescription.Token methodToken = Mockito.mock(MethodDescription.Token.class);
        Mockito.when(instrumentedMethod.asToken(ElementMatchers.is(typeDescription))).thenReturn(methodToken);
        Mockito.when(methodToken.accept(ArgumentMatchers.any(TypeDescription.Generic.Visitor.class))).thenReturn(methodToken);
        Mockito.when(classFileVersion.isAtLeast(ClassFileVersion.JAVA_V5)).thenReturn(true);
        MethodRegistry.Compiled methodRegistry = new MethodRegistry.Default().append(firstMatcher, firstHandler, firstFactory, transformer).append(secondMatcher, secondHandler, secondFactory, transformer).prepare(firstType, methodGraphCompiler, TypeValidation.ENABLED, NEVER, methodFilter).compile(implementationTargetFactory, classFileVersion);
        MatcherAssert.assertThat(methodRegistry.getInstrumentedType(), CoreMatchers.is(typeDescription));
        MatcherAssert.assertThat(methodRegistry.getInstrumentedMethods().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(methodRegistry.getTypeInitializer(), CoreMatchers.is(typeInitializer));
        MatcherAssert.assertThat(methodRegistry.getLoadedTypeInitializer(), CoreMatchers.is(loadedTypeInitializer));
        Mockito.verify(firstHandler).prepare(firstType);
        Mockito.verify(secondHandler).prepare(secondType);
        Mockito.verifyZeroInteractions(firstFactory);
        Mockito.verifyZeroInteractions(secondFactory);
        MatcherAssert.assertThat(methodRegistry.target(instrumentedMethod), CoreMatchers.instanceOf(TypeWriter.MethodPool.Record.ForNonImplementedMethod.class));
    }
}

