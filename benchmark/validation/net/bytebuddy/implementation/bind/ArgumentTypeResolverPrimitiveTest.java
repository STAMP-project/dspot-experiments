package net.bytebuddy.implementation.bind;


import net.bytebuddy.description.type.TypeDescription;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;

import static net.bytebuddy.implementation.bind.MethodDelegationBinder.AmbiguityResolver.Resolution.AMBIGUOUS;
import static net.bytebuddy.implementation.bind.MethodDelegationBinder.AmbiguityResolver.Resolution.LEFT;
import static net.bytebuddy.implementation.bind.MethodDelegationBinder.AmbiguityResolver.Resolution.RIGHT;


@RunWith(Parameterized.class)
public class ArgumentTypeResolverPrimitiveTest extends AbstractArgumentTypeResolverTest {
    private final Class<?> firstType;

    private final Class<?> secondType;

    @Mock
    private TypeDescription.Generic firstPrimitive;

    @Mock
    private TypeDescription.Generic secondPrimitive;

    @Mock
    private TypeDescription firstRawPrimitive;

    @Mock
    private TypeDescription secondRawPrimitive;

    public ArgumentTypeResolverPrimitiveTest(Class<?> firstType, Class<?> secondType) {
        this.firstType = firstType;
        this.secondType = secondType;
    }

    @Test
    public void testLeftDominance() throws Exception {
        testDominance(firstPrimitive, secondPrimitive, LEFT);
    }

    @Test
    public void testRightDominance() throws Exception {
        testDominance(secondPrimitive, firstPrimitive, RIGHT);
    }

    @Test
    public void testLeftNonDominance() throws Exception {
        testDominance(secondPrimitive, firstPrimitive, RIGHT);
    }

    @Test
    public void testRightNonDominance() throws Exception {
        testDominance(firstPrimitive, secondPrimitive, LEFT);
    }

    @Test
    public void testNonDominance() throws Exception {
        testDominance(firstPrimitive, firstPrimitive, AMBIGUOUS);
    }
}

