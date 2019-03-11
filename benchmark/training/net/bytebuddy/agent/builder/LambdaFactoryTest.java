package net.bytebuddy.agent.builder;


import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;


public class LambdaFactoryTest {
    private static final String FOO = "foo";

    private static final byte[] BAR = new byte[]{ 1, 2, 3 };

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private Object a1;

    @Mock
    private Object a3;

    @Mock
    private Object a4;

    @Mock
    private Object a5;

    @Mock
    private Object a6;

    @Mock
    private List<?> a8;

    @Mock
    private List<?> a9;

    @Mock
    private ClassFileTransformer classFileTransformer;

    @Mock
    private ClassFileTransformer otherTransformer;

    @Test
    public void testValidFactory() throws Exception {
        LambdaFactoryTest.PseudoFactory pseudoFactory = new LambdaFactoryTest.PseudoFactory();
        MatcherAssert.assertThat(LambdaFactory.register(classFileTransformer, pseudoFactory), CoreMatchers.is(true));
        try {
            MatcherAssert.assertThat(LambdaFactory.class.getMethod("make", Object.class, String.class, Object.class, Object.class, Object.class, Object.class, boolean.class, List.class, List.class).invoke(null, a1, LambdaFactoryTest.FOO, a3, a4, a5, a6, true, a8, a9), CoreMatchers.is(((Object) (LambdaFactoryTest.BAR))));
            MatcherAssert.assertThat(pseudoFactory.args[0], CoreMatchers.is(a1));
            MatcherAssert.assertThat(pseudoFactory.args[1], CoreMatchers.is(((Object) (LambdaFactoryTest.FOO))));
            MatcherAssert.assertThat(pseudoFactory.args[2], CoreMatchers.is(a3));
            MatcherAssert.assertThat(pseudoFactory.args[3], CoreMatchers.is(a4));
            MatcherAssert.assertThat(pseudoFactory.args[4], CoreMatchers.is(a5));
            MatcherAssert.assertThat(pseudoFactory.args[5], CoreMatchers.is(a6));
            MatcherAssert.assertThat(pseudoFactory.args[6], CoreMatchers.is(((Object) (true))));
            MatcherAssert.assertThat(pseudoFactory.args[7], CoreMatchers.is(((Object) (a8))));
            MatcherAssert.assertThat(pseudoFactory.args[8], CoreMatchers.is(((Object) (a9))));
            MatcherAssert.assertThat(pseudoFactory.args[9], CoreMatchers.is(((Object) (Collections.singleton(classFileTransformer)))));
        } finally {
            MatcherAssert.assertThat(LambdaFactory.release(classFileTransformer), CoreMatchers.is(true));
        }
    }

    @Test
    public void testUnknownTransformer() throws Exception {
        MatcherAssert.assertThat(LambdaFactory.release(classFileTransformer), CoreMatchers.is(false));
    }

    @Test
    public void testPreviousTransformer() throws Exception {
        LambdaFactoryTest.PseudoFactory pseudoFactory = new LambdaFactoryTest.PseudoFactory();
        try {
            MatcherAssert.assertThat(LambdaFactory.register(classFileTransformer, pseudoFactory), CoreMatchers.is(true));
            MatcherAssert.assertThat(LambdaFactory.register(otherTransformer, pseudoFactory), CoreMatchers.is(false));
        } finally {
            MatcherAssert.assertThat(LambdaFactory.release(classFileTransformer), CoreMatchers.is(false));
            MatcherAssert.assertThat(LambdaFactory.release(otherTransformer), CoreMatchers.is(true));
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalTransformer() throws Exception {
        LambdaFactory.register(classFileTransformer, new Object());
    }

    @Test
    public void testTypeAndMethodArePublic() throws Exception {
        MatcherAssert.assertThat(Modifier.isPublic(LambdaFactory.class.getModifiers()), CoreMatchers.is(true));
        MatcherAssert.assertThat(Modifier.isPublic(LambdaFactory.class.getDeclaredMethod("make", Object.class, String.class, Object.class, Object.class, Object.class, Object.class, boolean.class, List.class, List.class).getModifiers()), CoreMatchers.is(true));
        MatcherAssert.assertThat(Modifier.isStatic(LambdaFactory.class.getDeclaredMethod("make", Object.class, String.class, Object.class, Object.class, Object.class, Object.class, boolean.class, List.class, List.class).getModifiers()), CoreMatchers.is(true));
    }

    public static class PseudoFactory {
        private Object[] args;

        public byte[] make(Object a1, String a2, Object a3, Object a4, Object a5, Object a6, boolean a7, List<?> a8, List<?> a9, Collection<?> a10) {
            args = new Object[]{ a1, a2, a3, a4, a5, a6, a7, a8, a9, a10 };
            return LambdaFactoryTest.BAR;
        }
    }
}

