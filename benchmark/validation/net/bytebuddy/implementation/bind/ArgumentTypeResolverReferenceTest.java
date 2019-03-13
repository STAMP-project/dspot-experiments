package net.bytebuddy.implementation.bind;


import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.implementation.bind.MethodDelegationBinder.AmbiguityResolver.Resolution.AMBIGUOUS;
import static net.bytebuddy.implementation.bind.MethodDelegationBinder.AmbiguityResolver.Resolution.LEFT;
import static net.bytebuddy.implementation.bind.MethodDelegationBinder.AmbiguityResolver.Resolution.RIGHT;


public class ArgumentTypeResolverReferenceTest extends AbstractArgumentTypeResolverTest {
    @Mock
    private TypeDescription weakTargetType;

    @Mock
    private TypeDescription dominantTargetType;

    @Mock
    private TypeDescription.Generic genericWeakTargetType;

    @Mock
    private TypeDescription.Generic genericDominantTargetType;

    @Mock
    private ParameterDescription weakTargetParameter;

    @Mock
    private ParameterDescription dominantTargetParameter;

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodWithoutArguments() throws Exception {
        Mockito.when(source.getParameters()).thenReturn(((ParameterList) (sourceParameterList)));
        MethodDelegationBinder.AmbiguityResolver.Resolution resolution = ArgumentTypeResolver.INSTANCE.resolve(source, left, right);
        MatcherAssert.assertThat(resolution.isUnresolved(), CoreMatchers.is(true));
        Mockito.verify(source, Mockito.atLeast(1)).getParameters();
        Mockito.verifyZeroInteractions(left);
        Mockito.verifyZeroInteractions(right);
    }

    @Test
    public void testMethodWithoutMappedArguments() throws Exception {
        Mockito.when(sourceParameterList.size()).thenReturn(1);
        Mockito.when(left.getTargetParameterIndex(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0)))).thenReturn(null);
        Mockito.when(right.getTargetParameterIndex(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0)))).thenReturn(null);
        MethodDelegationBinder.AmbiguityResolver.Resolution resolution = ArgumentTypeResolver.INSTANCE.resolve(source, left, right);
        MatcherAssert.assertThat(resolution, CoreMatchers.is(AMBIGUOUS));
        Mockito.verify(source, Mockito.atLeast(1)).getParameters();
        Mockito.verify(left, Mockito.atLeast(1)).getTargetParameterIndex(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0)));
        Mockito.verify(left, Mockito.never()).getTargetParameterIndex(AdditionalMatchers.not(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0))));
        Mockito.verify(right, Mockito.atLeast(1)).getTargetParameterIndex(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0)));
        Mockito.verify(right, Mockito.never()).getTargetParameterIndex(AdditionalMatchers.not(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0))));
    }

    @Test
    public void testMethodWithoutSeveralUnmappedArguments() throws Exception {
        Mockito.when(sourceParameterList.size()).thenReturn(3);
        Mockito.when(left.getTargetParameterIndex(ArgumentMatchers.any())).thenReturn(null);
        Mockito.when(right.getTargetParameterIndex(ArgumentMatchers.any())).thenReturn(null);
        MethodDelegationBinder.AmbiguityResolver.Resolution resolution = ArgumentTypeResolver.INSTANCE.resolve(source, left, right);
        MatcherAssert.assertThat(resolution, CoreMatchers.is(AMBIGUOUS));
        Mockito.verify(source, Mockito.atLeast(1)).getParameters();
        Mockito.verify(left, Mockito.atLeast(1)).getTargetParameterIndex(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0)));
        Mockito.verify(left, Mockito.atLeast(1)).getTargetParameterIndex(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(1)));
        Mockito.verify(left, Mockito.atLeast(1)).getTargetParameterIndex(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(2)));
        Mockito.verify(left, Mockito.never()).getTargetParameterIndex(AdditionalMatchers.not(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0, 1, 2))));
        Mockito.verify(right, Mockito.atLeast(1)).getTargetParameterIndex(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0)));
        Mockito.verify(right, Mockito.atLeast(1)).getTargetParameterIndex(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(1)));
        Mockito.verify(right, Mockito.atLeast(1)).getTargetParameterIndex(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(2)));
        Mockito.verify(right, Mockito.never()).getTargetParameterIndex(AdditionalMatchers.not(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0, 1, 2))));
    }

    @Test
    public void testLeftMethodDominantByType() throws Exception {
        Mockito.when(sourceParameterList.size()).thenReturn(1);
        Mockito.when(leftParameterList.get(0)).thenReturn(dominantTargetParameter);
        Mockito.when(left.getTargetParameterIndex(ArgumentMatchers.any())).thenAnswer(new AbstractArgumentTypeResolverTest.TokenAnswer(new int[][]{ new int[]{ 0, 0 } }));
        Mockito.when(rightParameterList.get(1)).thenReturn(weakTargetParameter);
        Mockito.when(right.getTargetParameterIndex(ArgumentMatchers.any())).thenAnswer(new AbstractArgumentTypeResolverTest.TokenAnswer(new int[][]{ new int[]{ 0, 1 } }));
        MethodDelegationBinder.AmbiguityResolver.Resolution resolution = ArgumentTypeResolver.INSTANCE.resolve(source, left, right);
        MatcherAssert.assertThat(resolution, CoreMatchers.is(LEFT));
        Mockito.verify(source, Mockito.atLeast(1)).getParameters();
        Mockito.verify(left, Mockito.atLeast(1)).getTargetParameterIndex(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0)));
        Mockito.verify(left, Mockito.never()).getTargetParameterIndex(AdditionalMatchers.not(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0))));
        Mockito.verify(right, Mockito.atLeast(1)).getTargetParameterIndex(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0)));
        Mockito.verify(right, Mockito.never()).getTargetParameterIndex(AdditionalMatchers.not(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0))));
    }

    @Test
    public void testRightMethodDominantByType() throws Exception {
        Mockito.when(sourceParameterList.size()).thenReturn(1);
        Mockito.when(leftParameterList.get(0)).thenReturn(weakTargetParameter);
        Mockito.when(left.getTargetParameterIndex(ArgumentMatchers.any())).thenAnswer(new AbstractArgumentTypeResolverTest.TokenAnswer(new int[][]{ new int[]{ 0, 0 } }));
        Mockito.when(rightParameterList.get(1)).thenReturn(dominantTargetParameter);
        Mockito.when(right.getTargetParameterIndex(ArgumentMatchers.any())).thenAnswer(new AbstractArgumentTypeResolverTest.TokenAnswer(new int[][]{ new int[]{ 0, 1 } }));
        MethodDelegationBinder.AmbiguityResolver.Resolution resolution = ArgumentTypeResolver.INSTANCE.resolve(source, left, right);
        MatcherAssert.assertThat(resolution, CoreMatchers.is(RIGHT));
        Mockito.verify(source, Mockito.atLeast(1)).getParameters();
        Mockito.verify(left, Mockito.atLeast(1)).getTargetParameterIndex(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0)));
        Mockito.verify(left, Mockito.never()).getTargetParameterIndex(AdditionalMatchers.not(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0))));
        Mockito.verify(right, Mockito.atLeast(1)).getTargetParameterIndex(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0)));
        Mockito.verify(right, Mockito.never()).getTargetParameterIndex(AdditionalMatchers.not(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0))));
    }

    @Test
    public void testAmbiguousByCrossAssignableType() throws Exception {
        Mockito.when(sourceParameterList.size()).thenReturn(1);
        Mockito.when(leftParameterList.get(0)).thenReturn(weakTargetParameter);
        Mockito.when(left.getTargetParameterIndex(ArgumentMatchers.any(ArgumentTypeResolver.ParameterIndexToken.class))).thenAnswer(new AbstractArgumentTypeResolverTest.TokenAnswer(new int[][]{ new int[]{ 0, 0 } }));
        Mockito.when(rightParameterList.get(0)).thenReturn(weakTargetParameter);
        Mockito.when(right.getTargetParameterIndex(ArgumentMatchers.any(ArgumentTypeResolver.ParameterIndexToken.class))).thenAnswer(new AbstractArgumentTypeResolverTest.TokenAnswer(new int[][]{ new int[]{ 0, 0 } }));
        MethodDelegationBinder.AmbiguityResolver.Resolution resolution = ArgumentTypeResolver.INSTANCE.resolve(source, left, right);
        MatcherAssert.assertThat(resolution, CoreMatchers.is(AMBIGUOUS));
        Mockito.verify(source, Mockito.atLeast(1)).getParameters();
        Mockito.verify(leftMethod, Mockito.atLeast(1)).getParameters();
        Mockito.verify(rightMethod, Mockito.atLeast(1)).getParameters();
        Mockito.verify(left, Mockito.atLeast(1)).getTargetParameterIndex(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0)));
        Mockito.verify(left, Mockito.never()).getTargetParameterIndex(AdditionalMatchers.not(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0))));
        Mockito.verify(right, Mockito.atLeast(1)).getTargetParameterIndex(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0)));
        Mockito.verify(right, Mockito.never()).getTargetParameterIndex(AdditionalMatchers.not(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0))));
    }

    @Test
    public void testAmbiguousByNonCrossAssignableType() throws Exception {
        Mockito.when(sourceParameterList.size()).thenReturn(1);
        Mockito.when(leftParameterList.get(0)).thenReturn(dominantTargetParameter);
        Mockito.when(left.getTargetParameterIndex(ArgumentMatchers.any(ArgumentTypeResolver.ParameterIndexToken.class))).thenAnswer(new AbstractArgumentTypeResolverTest.TokenAnswer(new int[][]{ new int[]{ 0, 0 } }));
        Mockito.when(rightParameterList.get(0)).thenReturn(dominantTargetParameter);
        Mockito.when(right.getTargetParameterIndex(ArgumentMatchers.any(ArgumentTypeResolver.ParameterIndexToken.class))).thenAnswer(new AbstractArgumentTypeResolverTest.TokenAnswer(new int[][]{ new int[]{ 0, 0 } }));
        MethodDelegationBinder.AmbiguityResolver.Resolution resolution = ArgumentTypeResolver.INSTANCE.resolve(source, left, right);
        MatcherAssert.assertThat(resolution, CoreMatchers.is(AMBIGUOUS));
        Mockito.verify(source, Mockito.atLeast(1)).getParameters();
        Mockito.verify(leftMethod, Mockito.atLeast(1)).getParameters();
        Mockito.verify(rightMethod, Mockito.atLeast(1)).getParameters();
        Mockito.verify(left, Mockito.atLeast(1)).getTargetParameterIndex(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0)));
        Mockito.verify(left, Mockito.never()).getTargetParameterIndex(AdditionalMatchers.not(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0))));
        Mockito.verify(right, Mockito.atLeast(1)).getTargetParameterIndex(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0)));
        Mockito.verify(right, Mockito.never()).getTargetParameterIndex(AdditionalMatchers.not(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0))));
    }

    @Test
    public void testAmbiguousByDifferentIndexedAssignableType() throws Exception {
        Mockito.when(sourceParameterList.size()).thenReturn(2);
        Mockito.when(leftParameterList.get(0)).thenReturn(dominantTargetParameter);
        Mockito.when(leftParameterList.get(1)).thenReturn(weakTargetParameter);
        Mockito.when(left.getTargetParameterIndex(ArgumentMatchers.any(ArgumentTypeResolver.ParameterIndexToken.class))).thenAnswer(new AbstractArgumentTypeResolverTest.TokenAnswer(new int[][]{ new int[]{ 0, 0 }, new int[]{ 1, 1 } }));
        Mockito.when(rightParameterList.get(0)).thenReturn(weakTargetParameter);
        Mockito.when(rightParameterList.get(1)).thenReturn(dominantTargetParameter);
        Mockito.when(right.getTargetParameterIndex(ArgumentMatchers.any(ArgumentTypeResolver.ParameterIndexToken.class))).thenAnswer(new AbstractArgumentTypeResolverTest.TokenAnswer(new int[][]{ new int[]{ 0, 0 }, new int[]{ 1, 1 } }));
        MethodDelegationBinder.AmbiguityResolver.Resolution resolution = ArgumentTypeResolver.INSTANCE.resolve(source, left, right);
        MatcherAssert.assertThat(resolution, CoreMatchers.is(AMBIGUOUS));
        Mockito.verify(source, Mockito.atLeast(1)).getParameters();
        Mockito.verify(leftMethod, Mockito.atLeast(1)).getParameters();
        Mockito.verify(rightMethod, Mockito.atLeast(1)).getParameters();
        Mockito.verify(left, Mockito.atLeast(1)).getTargetParameterIndex(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0)));
        Mockito.verify(left, Mockito.atLeast(1)).getTargetParameterIndex(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(1)));
        Mockito.verify(left, Mockito.never()).getTargetParameterIndex(AdditionalMatchers.not(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0, 1))));
        Mockito.verify(right, Mockito.atLeast(1)).getTargetParameterIndex(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0)));
        Mockito.verify(right, Mockito.atLeast(1)).getTargetParameterIndex(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(1)));
        Mockito.verify(right, Mockito.never()).getTargetParameterIndex(AdditionalMatchers.not(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0, 1))));
    }

    @Test
    public void testLeftMethodDominantByScore() throws Exception {
        Mockito.when(sourceParameterList.size()).thenReturn(2);
        Mockito.when(leftParameterList.get(0)).thenReturn(dominantTargetParameter);
        Mockito.when(leftParameterList.get(1)).thenReturn(weakTargetParameter);
        Mockito.when(left.getTargetParameterIndex(ArgumentMatchers.any(ArgumentTypeResolver.ParameterIndexToken.class))).thenAnswer(new AbstractArgumentTypeResolverTest.TokenAnswer(new int[][]{ new int[]{ 0, 0 }, new int[]{ 1, 1 } }));
        Mockito.when(rightParameterList.get(0)).thenReturn(dominantTargetParameter);
        Mockito.when(right.getTargetParameterIndex(ArgumentMatchers.any(ArgumentTypeResolver.ParameterIndexToken.class))).thenAnswer(new AbstractArgumentTypeResolverTest.TokenAnswer(new int[][]{ new int[]{ 0, 0 } }));
        MethodDelegationBinder.AmbiguityResolver.Resolution resolution = ArgumentTypeResolver.INSTANCE.resolve(source, left, right);
        MatcherAssert.assertThat(resolution, CoreMatchers.is(LEFT));
        Mockito.verify(source, Mockito.atLeast(1)).getParameters();
        Mockito.verify(leftMethod, Mockito.atLeast(1)).getParameters();
        Mockito.verify(rightMethod, Mockito.atLeast(1)).getParameters();
        Mockito.verify(left, Mockito.atLeast(1)).getTargetParameterIndex(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0)));
        Mockito.verify(left, Mockito.atLeast(1)).getTargetParameterIndex(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(1)));
        Mockito.verify(left, Mockito.never()).getTargetParameterIndex(AdditionalMatchers.not(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0, 1))));
        Mockito.verify(right, Mockito.atLeast(1)).getTargetParameterIndex(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0)));
        Mockito.verify(right, Mockito.atLeast(1)).getTargetParameterIndex(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(1)));
        Mockito.verify(right, Mockito.never()).getTargetParameterIndex(AdditionalMatchers.not(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0, 1))));
    }

    @Test
    public void testRightMethodDominantByScore() throws Exception {
        Mockito.when(sourceParameterList.size()).thenReturn(2);
        Mockito.when(leftParameterList.get(0)).thenReturn(dominantTargetParameter);
        Mockito.when(left.getTargetParameterIndex(ArgumentMatchers.any(ArgumentTypeResolver.ParameterIndexToken.class))).thenAnswer(new AbstractArgumentTypeResolverTest.TokenAnswer(new int[][]{ new int[]{ 0, 0 } }));
        Mockito.when(rightParameterList.get(0)).thenReturn(dominantTargetParameter);
        Mockito.when(rightParameterList.get(1)).thenReturn(dominantTargetParameter);
        Mockito.when(right.getTargetParameterIndex(ArgumentMatchers.any(ArgumentTypeResolver.ParameterIndexToken.class))).thenAnswer(new AbstractArgumentTypeResolverTest.TokenAnswer(new int[][]{ new int[]{ 0, 0 }, new int[]{ 1, 1 } }));
        MethodDelegationBinder.AmbiguityResolver.Resolution resolution = ArgumentTypeResolver.INSTANCE.resolve(source, left, right);
        MatcherAssert.assertThat(resolution, CoreMatchers.is(RIGHT));
        Mockito.verify(source, Mockito.atLeast(1)).getParameters();
        Mockito.verify(leftMethod, Mockito.atLeast(1)).getParameters();
        Mockito.verify(rightMethod, Mockito.atLeast(1)).getParameters();
        Mockito.verify(left, Mockito.atLeast(1)).getTargetParameterIndex(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0)));
        Mockito.verify(left, Mockito.atLeast(1)).getTargetParameterIndex(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(1)));
        Mockito.verify(left, Mockito.never()).getTargetParameterIndex(AdditionalMatchers.not(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0, 1))));
        Mockito.verify(right, Mockito.atLeast(1)).getTargetParameterIndex(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0)));
        Mockito.verify(right, Mockito.atLeast(1)).getTargetParameterIndex(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(1)));
        Mockito.verify(right, Mockito.never()).getTargetParameterIndex(AdditionalMatchers.not(ArgumentMatchers.argThat(AbstractArgumentTypeResolverTest.describesArgument(0, 1))));
    }
}

