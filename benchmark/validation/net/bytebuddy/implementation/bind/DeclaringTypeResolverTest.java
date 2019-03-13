package net.bytebuddy.implementation.bind;


import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.implementation.bind.MethodDelegationBinder.AmbiguityResolver.Resolution.AMBIGUOUS;
import static net.bytebuddy.implementation.bind.MethodDelegationBinder.AmbiguityResolver.Resolution.LEFT;
import static net.bytebuddy.implementation.bind.MethodDelegationBinder.AmbiguityResolver.Resolution.RIGHT;


public class DeclaringTypeResolverTest extends AbstractAmbiguityResolverTest {
    @Mock
    private TypeDescription leftType;

    @Mock
    private TypeDescription rightType;

    @Test
    public void testEquals() throws Exception {
        Mockito.when(leftType.isAssignableFrom(rightType)).thenReturn(true);
        MatcherAssert.assertThat(DeclaringTypeResolver.INSTANCE.resolve(source, left, left), CoreMatchers.is(AMBIGUOUS));
        Mockito.verify(leftType, Mockito.times(2)).asErasure();
        Mockito.verifyNoMoreInteractions(leftType);
    }

    @Test
    public void testLeftAssignable() throws Exception {
        Mockito.when(leftType.isAssignableFrom(rightType)).thenReturn(true);
        MatcherAssert.assertThat(DeclaringTypeResolver.INSTANCE.resolve(source, left, right), CoreMatchers.is(RIGHT));
        Mockito.verify(leftType).isAssignableFrom(rightType);
        Mockito.verify(leftType).asErasure();
        Mockito.verifyNoMoreInteractions(leftType);
        Mockito.verify(rightType).asErasure();
        Mockito.verifyNoMoreInteractions(rightType);
    }

    @Test
    public void testRightAssignable() throws Exception {
        Mockito.when(leftType.isAssignableTo(rightType)).thenReturn(true);
        MatcherAssert.assertThat(DeclaringTypeResolver.INSTANCE.resolve(source, left, right), CoreMatchers.is(LEFT));
        Mockito.verify(leftType).isAssignableFrom(rightType);
        Mockito.verify(leftType).isAssignableTo(rightType);
        Mockito.verify(leftType).asErasure();
        Mockito.verifyNoMoreInteractions(leftType);
        Mockito.verify(rightType).asErasure();
        Mockito.verifyNoMoreInteractions(rightType);
    }

    @Test
    public void testNonAssignable() throws Exception {
        MatcherAssert.assertThat(DeclaringTypeResolver.INSTANCE.resolve(source, left, right), CoreMatchers.is(AMBIGUOUS));
        Mockito.verify(leftType).isAssignableFrom(rightType);
        Mockito.verify(leftType).isAssignableTo(rightType);
        Mockito.verify(leftType).asErasure();
        Mockito.verifyNoMoreInteractions(leftType);
        Mockito.verify(rightType).asErasure();
        Mockito.verifyNoMoreInteractions(rightType);
    }
}

