package net.bytebuddy.implementation.bind.annotation;


import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bind.ArgumentTypeResolver;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.MethodVisitor;

import static net.bytebuddy.description.type.TypeDescription.Generic.OBJECT;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.STATIC;


public class TargetMethodAnnotationDrivenBinderTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String BAZ = "baz";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TargetMethodAnnotationDrivenBinder.ParameterBinder<?> firstParameterBinder;

    @Mock
    private TargetMethodAnnotationDrivenBinder.ParameterBinder<?> secondParameterBinder;

    @Mock
    private MethodDelegationBinder.TerminationHandler terminationHandler;

    @Mock
    private Assigner assigner;

    private Assigner.Typing typing = STATIC;

    @Mock
    private StackManipulation assignmentBinding;

    @Mock
    private StackManipulation methodInvocation;

    @Mock
    private StackManipulation termination;

    @Mock
    private MethodDelegationBinder.MethodInvoker methodInvoker;

    @Mock
    private Implementation.Target implementationTarget;

    @Mock
    private MethodDescription sourceMethod;

    @Mock
    private MethodDescription targetMethod;

    @Mock
    private TypeDescription.Generic sourceTypeDescription;

    @Mock
    private TypeDescription.Generic targetTypeDescription;

    @Mock
    private TypeDescription instrumentedType;

    @Mock
    private AnnotationDescription.ForLoadedAnnotation<TargetMethodAnnotationDrivenBinderTest.FirstPseudoAnnotation> firstPseudoAnnotation;

    @Mock
    private AnnotationDescription.ForLoadedAnnotation<TargetMethodAnnotationDrivenBinderTest.SecondPseudoAnnotation> secondPseudoAnnotation;

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    @Mock
    private ParameterDescription firstParameter;

    @Mock
    private ParameterDescription secondParameter;

    @Test(expected = IllegalArgumentException.class)
    public void testConflictingBinderBinding() throws Exception {
        Mockito.doReturn(TargetMethodAnnotationDrivenBinderTest.FirstPseudoAnnotation.class).when(firstParameterBinder).getHandledType();
        Mockito.doReturn(TargetMethodAnnotationDrivenBinderTest.FirstPseudoAnnotation.class).when(secondParameterBinder).getHandledType();
        TargetMethodAnnotationDrivenBinder.of(Arrays.<TargetMethodAnnotationDrivenBinder.ParameterBinder<?>>asList(firstParameterBinder, secondParameterBinder));
    }

    @Test
    public void testIgnoreForBindingAnnotation() throws Exception {
        Mockito.when(targetMethod.isAccessibleTo(instrumentedType)).thenReturn(true);
        AnnotationDescription ignoreForBinding = Mockito.mock(AnnotationDescription.class);
        Mockito.when(ignoreForBinding.getAnnotationType()).thenReturn(of(IgnoreForBinding.class));
        Mockito.when(targetMethod.getDeclaredAnnotations()).thenReturn(new AnnotationList.Explicit(Collections.singletonList(ignoreForBinding)));
        Mockito.when(termination.isValid()).thenReturn(true);
        MethodDelegationBinder methodDelegationBinder = TargetMethodAnnotationDrivenBinder.of(Collections.<TargetMethodAnnotationDrivenBinder.ParameterBinder<?>>emptyList());
        MatcherAssert.assertThat(methodDelegationBinder.compile(targetMethod).bind(implementationTarget, sourceMethod, terminationHandler, methodInvoker, assigner).isValid(), CoreMatchers.is(false));
        Mockito.verifyZeroInteractions(assigner);
        Mockito.verifyZeroInteractions(implementationTarget);
        Mockito.verifyZeroInteractions(sourceMethod);
    }

    @Test
    public void testNonAccessible() throws Exception {
        Mockito.when(targetMethod.isAccessibleTo(instrumentedType)).thenReturn(false);
        Mockito.when(assignmentBinding.isValid()).thenReturn(true);
        Mockito.when(methodInvocation.isValid()).thenReturn(true);
        Mockito.when(termination.isValid()).thenReturn(true);
        Mockito.when(targetMethod.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(firstParameter.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(secondParameter.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        MethodDelegationBinder methodDelegationBinder = TargetMethodAnnotationDrivenBinder.of(Collections.<TargetMethodAnnotationDrivenBinder.ParameterBinder<?>>emptyList());
        MatcherAssert.assertThat(methodDelegationBinder.compile(targetMethod).bind(implementationTarget, sourceMethod, terminationHandler, methodInvoker, assigner).isValid(), CoreMatchers.is(false));
        Mockito.verifyZeroInteractions(terminationHandler);
        Mockito.verifyZeroInteractions(assigner);
        Mockito.verifyZeroInteractions(methodInvoker);
    }

    @Test
    public void testTerminationBinderMismatch() throws Exception {
        Mockito.when(targetMethod.isAccessibleTo(instrumentedType)).thenReturn(true);
        Mockito.when(assignmentBinding.isValid()).thenReturn(false);
        Mockito.when(methodInvocation.isValid()).thenReturn(true);
        Mockito.when(termination.isValid()).thenReturn(false);
        Mockito.when(targetMethod.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(firstParameter.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(secondParameter.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        MethodDelegationBinder methodDelegationBinder = TargetMethodAnnotationDrivenBinder.of(Collections.<TargetMethodAnnotationDrivenBinder.ParameterBinder<?>>emptyList());
        MatcherAssert.assertThat(methodDelegationBinder.compile(targetMethod).bind(implementationTarget, sourceMethod, terminationHandler, methodInvoker, assigner).isValid(), CoreMatchers.is(false));
        Mockito.verify(terminationHandler).resolve(assigner, typing, sourceMethod, targetMethod);
        Mockito.verifyNoMoreInteractions(terminationHandler);
        Mockito.verifyZeroInteractions(assigner);
        Mockito.verifyZeroInteractions(methodInvoker);
    }

    @Test
    public void testDoNotBindOnIllegalMethodInvocation() throws Exception {
        Mockito.when(targetMethod.isAccessibleTo(instrumentedType)).thenReturn(true);
        Mockito.when(assignmentBinding.isValid()).thenReturn(true);
        Mockito.when(methodInvocation.isValid()).thenReturn(false);
        Mockito.when(termination.isValid()).thenReturn(true);
        Mockito.when(targetMethod.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(firstParameter.getDeclaredAnnotations()).thenReturn(new AnnotationList.Explicit(Collections.singletonList(firstPseudoAnnotation)));
        Mockito.when(secondParameter.getDeclaredAnnotations()).thenReturn(new AnnotationList.Explicit(Collections.singletonList(secondPseudoAnnotation)));
        MethodDelegationBinder.ParameterBinding<?> firstBinding = TargetMethodAnnotationDrivenBinderTest.prepareArgumentBinder(firstParameterBinder, TargetMethodAnnotationDrivenBinderTest.FirstPseudoAnnotation.class, new TargetMethodAnnotationDrivenBinderTest.Key(TargetMethodAnnotationDrivenBinderTest.FOO));
        MethodDelegationBinder.ParameterBinding<?> secondBinding = TargetMethodAnnotationDrivenBinderTest.prepareArgumentBinder(secondParameterBinder, TargetMethodAnnotationDrivenBinderTest.SecondPseudoAnnotation.class, new TargetMethodAnnotationDrivenBinderTest.Key(TargetMethodAnnotationDrivenBinderTest.BAR));
        MethodDelegationBinder methodDelegationBinder = TargetMethodAnnotationDrivenBinder.of(Arrays.<TargetMethodAnnotationDrivenBinder.ParameterBinder<?>>asList(firstParameterBinder, secondParameterBinder));
        MethodDelegationBinder.MethodBinding methodBinding = methodDelegationBinder.compile(targetMethod).bind(implementationTarget, sourceMethod, terminationHandler, methodInvoker, assigner);
        MatcherAssert.assertThat(methodBinding.isValid(), CoreMatchers.is(false));
        Mockito.verify(firstBinding).isValid();
        Mockito.verify(secondBinding).isValid();
        Mockito.verify(termination).isValid();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBindingByDefault() throws Exception {
        Mockito.when(targetMethod.isAccessibleTo(instrumentedType)).thenReturn(true);
        Mockito.when(assignmentBinding.isValid()).thenReturn(true);
        Mockito.when(methodInvocation.isValid()).thenReturn(true);
        Mockito.when(termination.isValid()).thenReturn(true);
        Mockito.when(targetMethod.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(firstParameter.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(secondParameter.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(firstParameter.getType()).thenReturn(OBJECT);
        Mockito.when(secondParameter.getType()).thenReturn(OBJECT);
        Mockito.when(sourceMethod.getParameters()).thenReturn(new ParameterList.Explicit(firstParameter, secondParameter));
        MethodDelegationBinder methodDelegationBinder = TargetMethodAnnotationDrivenBinder.of(Collections.<TargetMethodAnnotationDrivenBinder.ParameterBinder<?>>emptyList());
        MethodDelegationBinder.MethodBinding methodBinding = methodDelegationBinder.compile(targetMethod).bind(implementationTarget, sourceMethod, terminationHandler, methodInvoker, assigner);
        MatcherAssert.assertThat(methodBinding.isValid(), CoreMatchers.is(true));
        MatcherAssert.assertThat(methodBinding.getTarget(), CoreMatchers.is(targetMethod));
        MatcherAssert.assertThat(methodBinding.getTargetParameterIndex(new ArgumentTypeResolver.ParameterIndexToken(0)), CoreMatchers.is(0));
        MatcherAssert.assertThat(methodBinding.getTargetParameterIndex(new ArgumentTypeResolver.ParameterIndexToken(1)), CoreMatchers.is(1));
        StackManipulation.Size size = methodBinding.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(2));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(2));
        Mockito.verify(firstParameter, Mockito.atLeast(1)).getDeclaredAnnotations();
        Mockito.verify(secondParameter, Mockito.atLeast(1)).getDeclaredAnnotations();
        Mockito.verify(targetMethod, Mockito.atLeast(1)).getDeclaredAnnotations();
        Mockito.verify(terminationHandler).resolve(assigner, typing, sourceMethod, targetMethod);
        Mockito.verifyNoMoreInteractions(terminationHandler);
        Mockito.verify(methodInvoker).invoke(targetMethod);
        Mockito.verifyNoMoreInteractions(methodInvoker);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBindingByParameterAnnotations() throws Exception {
        Mockito.when(targetMethod.isAccessibleTo(instrumentedType)).thenReturn(true);
        Mockito.when(assignmentBinding.isValid()).thenReturn(true);
        Mockito.when(methodInvocation.isValid()).thenReturn(true);
        Mockito.when(termination.isValid()).thenReturn(true);
        Mockito.when(targetMethod.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(firstParameter.getDeclaredAnnotations()).thenReturn(new AnnotationList.Explicit(Collections.singletonList(secondPseudoAnnotation)));
        Mockito.when(secondParameter.getDeclaredAnnotations()).thenReturn(new AnnotationList.Explicit(Collections.singletonList(firstPseudoAnnotation)));
        MethodDelegationBinder.ParameterBinding<?> firstBinding = TargetMethodAnnotationDrivenBinderTest.prepareArgumentBinder(firstParameterBinder, TargetMethodAnnotationDrivenBinderTest.FirstPseudoAnnotation.class, new TargetMethodAnnotationDrivenBinderTest.Key(TargetMethodAnnotationDrivenBinderTest.FOO));
        MethodDelegationBinder.ParameterBinding<?> secondBinding = TargetMethodAnnotationDrivenBinderTest.prepareArgumentBinder(secondParameterBinder, TargetMethodAnnotationDrivenBinderTest.SecondPseudoAnnotation.class, new TargetMethodAnnotationDrivenBinderTest.Key(TargetMethodAnnotationDrivenBinderTest.BAR));
        MethodDelegationBinder methodDelegationBinder = TargetMethodAnnotationDrivenBinder.of(Arrays.<TargetMethodAnnotationDrivenBinder.ParameterBinder<?>>asList(firstParameterBinder, secondParameterBinder));
        MethodDelegationBinder.MethodBinding methodBinding = methodDelegationBinder.compile(targetMethod).bind(implementationTarget, sourceMethod, terminationHandler, methodInvoker, assigner);
        MatcherAssert.assertThat(methodBinding.isValid(), CoreMatchers.is(true));
        MatcherAssert.assertThat(methodBinding.getTarget(), CoreMatchers.is(targetMethod));
        MatcherAssert.assertThat(methodBinding.getTargetParameterIndex(new TargetMethodAnnotationDrivenBinderTest.Key(TargetMethodAnnotationDrivenBinderTest.FOO)), CoreMatchers.is(1));
        MatcherAssert.assertThat(methodBinding.getTargetParameterIndex(new TargetMethodAnnotationDrivenBinderTest.Key(TargetMethodAnnotationDrivenBinderTest.BAR)), CoreMatchers.is(0));
        StackManipulation.Size size = methodBinding.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(0));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(0));
        Mockito.verifyZeroInteractions(methodVisitor);
        Mockito.verify(targetMethod, Mockito.atLeast(1)).getDeclaredAnnotations();
        Mockito.verify(firstParameter, Mockito.atLeast(1)).getDeclaredAnnotations();
        Mockito.verify(secondParameter, Mockito.atLeast(1)).getDeclaredAnnotations();
        Mockito.verifyNoMoreInteractions(assigner);
        Mockito.verify(terminationHandler).resolve(assigner, typing, sourceMethod, targetMethod);
        Mockito.verifyNoMoreInteractions(terminationHandler);
        Mockito.verify(methodInvoker).invoke(targetMethod);
        Mockito.verifyNoMoreInteractions(methodInvoker);
        Mockito.verify(firstParameterBinder, Mockito.atLeast(1)).getHandledType();
        Mockito.verify(((TargetMethodAnnotationDrivenBinder.ParameterBinder) (firstParameterBinder))).bind(firstPseudoAnnotation, sourceMethod, secondParameter, implementationTarget, assigner, STATIC);
        Mockito.verifyNoMoreInteractions(firstParameterBinder);
        Mockito.verify(secondParameterBinder, Mockito.atLeast(1)).getHandledType();
        Mockito.verify(((TargetMethodAnnotationDrivenBinder.ParameterBinder) (secondParameterBinder))).bind(secondPseudoAnnotation, sourceMethod, firstParameter, implementationTarget, assigner, STATIC);
        Mockito.verifyNoMoreInteractions(secondParameterBinder);
        Mockito.verify(firstBinding, Mockito.atLeast(1)).isValid();
        Mockito.verify(firstBinding).getIdentificationToken();
        Mockito.verify(secondBinding, Mockito.atLeast(1)).isValid();
        Mockito.verify(secondBinding).getIdentificationToken();
    }

    @Test
    public void testAnnotation() throws Exception {
        Argument argument = new TargetMethodAnnotationDrivenBinder.DelegationProcessor.Handler.Unbound.DefaultArgument(0);
        Argument sample = ((Argument) (TargetMethodAnnotationDrivenBinderTest.Sample.class.getDeclaredMethod(TargetMethodAnnotationDrivenBinderTest.FOO, Object.class).getParameterAnnotations()[0][0]));
        MatcherAssert.assertThat(argument.toString(), CoreMatchers.is(sample.toString()));
        MatcherAssert.assertThat(argument.hashCode(), CoreMatchers.is(sample.hashCode()));
        MatcherAssert.assertThat(argument, CoreMatchers.is(sample));
        MatcherAssert.assertThat(argument, CoreMatchers.is(argument));
        MatcherAssert.assertThat(argument, CoreMatchers.not(CoreMatchers.equalTo(null)));
        MatcherAssert.assertThat(argument, CoreMatchers.not(new Object()));
    }

    private interface Sample {
        void foo(@Argument(0)
        Object foo);
    }

    private @interface FirstPseudoAnnotation {}

    private @interface SecondPseudoAnnotation {}

    private static class Key {
        private final String value;

        private Key(String value) {
            this.value = value;
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public boolean equals(Object other) {
            return ((this) == other) || ((!((other == null) || ((getClass()) != (other.getClass())))) && (value.equals(((TargetMethodAnnotationDrivenBinderTest.Key) (other)).value)));
        }
    }
}

