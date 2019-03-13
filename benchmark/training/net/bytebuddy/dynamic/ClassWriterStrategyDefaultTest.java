package net.bytebuddy.dynamic;


import ClassWriterStrategy.Default.CONSTANT_POOL_DISCARDING;
import ClassWriterStrategy.Default.CONSTANT_POOL_RETAINING;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;


public class ClassWriterStrategyDefaultTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypePool typePool;

    @Mock
    private ClassReader classReader;

    @Test
    public void testConstantPoolRetention() {
        ClassWriter withoutReader = CONSTANT_POOL_RETAINING.resolve(0, typePool);
        ClassWriter withReader = CONSTANT_POOL_RETAINING.resolve(0, typePool, classReader);
        MatcherAssert.assertThat(((withReader.toByteArray().length) > (withoutReader.toByteArray().length)), Is.is(true));
    }

    @Test
    public void testConstantPoolDiscarding() {
        ClassWriter withoutReader = CONSTANT_POOL_DISCARDING.resolve(0, typePool);
        ClassWriter withReader = CONSTANT_POOL_DISCARDING.resolve(0, typePool, classReader);
        MatcherAssert.assertThat(((withReader.toByteArray().length) == (withoutReader.toByteArray().length)), Is.is(true));
    }
}

