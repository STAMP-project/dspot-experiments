package net.bytebuddy.pool;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TypePoolDefaultPrimitiveTypeTest {
    private final Class<?> primitiveType;

    private TypePool typePool;

    public TypePoolDefaultPrimitiveTypeTest(Class<?> primitiveType) {
        this.primitiveType = primitiveType;
    }

    @Test
    public void testPrimitiveLookup() throws Exception {
        MatcherAssert.assertThat(typePool.describe(primitiveType.getName()).resolve().represents(primitiveType), CoreMatchers.is(true));
    }
}

