package net.bytebuddy.pool;


import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.objectweb.asm.Type;

import static net.bytebuddy.pool.TypePool.Default.ParameterBag.<init>;


public class TypePoolDefaultParameterBagTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    @Test
    public void testFullResolutionStaticMethod() throws Exception {
        Type[] type = new Type[3];
        type[0] = Type.getType(Object.class);
        type[1] = Type.getType(long.class);
        type[2] = Type.getType(String.class);
        TypePool.Default.ParameterBag parameterBag = new TypePool.Default.ParameterBag(type);
        parameterBag.register(0, TypePoolDefaultParameterBagTest.FOO);
        parameterBag.register(1, TypePoolDefaultParameterBagTest.BAR);
        parameterBag.register(3, TypePoolDefaultParameterBagTest.QUX);
        List<TypePool.Default.LazyTypeDescription.MethodToken.ParameterToken> tokens = parameterBag.resolve(true);
        MatcherAssert.assertThat(tokens.size(), CoreMatchers.is(3));
        MatcherAssert.assertThat(tokens.get(0).getName(), CoreMatchers.is(TypePoolDefaultParameterBagTest.FOO));
        MatcherAssert.assertThat(tokens.get(1).getName(), CoreMatchers.is(TypePoolDefaultParameterBagTest.BAR));
        MatcherAssert.assertThat(tokens.get(2).getName(), CoreMatchers.is(TypePoolDefaultParameterBagTest.QUX));
        MatcherAssert.assertThat(tokens.get(0).getModifiers(), CoreMatchers.nullValue(Integer.class));
        MatcherAssert.assertThat(tokens.get(1).getModifiers(), CoreMatchers.nullValue(Integer.class));
        MatcherAssert.assertThat(tokens.get(2).getModifiers(), CoreMatchers.nullValue(Integer.class));
    }

    @Test
    public void testFullResolutionNonStaticMethod() throws Exception {
        Type[] type = new Type[3];
        type[0] = Type.getType(Object.class);
        type[1] = Type.getType(long.class);
        type[2] = Type.getType(String.class);
        TypePool.Default.ParameterBag parameterBag = new TypePool.Default.ParameterBag(type);
        parameterBag.register(1, TypePoolDefaultParameterBagTest.FOO);
        parameterBag.register(2, TypePoolDefaultParameterBagTest.BAR);
        parameterBag.register(4, TypePoolDefaultParameterBagTest.QUX);
        List<TypePool.Default.LazyTypeDescription.MethodToken.ParameterToken> tokens = parameterBag.resolve(false);
        MatcherAssert.assertThat(tokens.size(), CoreMatchers.is(3));
        MatcherAssert.assertThat(tokens.get(0).getName(), CoreMatchers.is(TypePoolDefaultParameterBagTest.FOO));
        MatcherAssert.assertThat(tokens.get(1).getName(), CoreMatchers.is(TypePoolDefaultParameterBagTest.BAR));
        MatcherAssert.assertThat(tokens.get(2).getName(), CoreMatchers.is(TypePoolDefaultParameterBagTest.QUX));
        MatcherAssert.assertThat(tokens.get(0).getModifiers(), CoreMatchers.nullValue(Integer.class));
        MatcherAssert.assertThat(tokens.get(1).getModifiers(), CoreMatchers.nullValue(Integer.class));
        MatcherAssert.assertThat(tokens.get(2).getModifiers(), CoreMatchers.nullValue(Integer.class));
    }

    @Test
    public void testPartlyResolutionStaticMethod() throws Exception {
        Type[] type = new Type[3];
        type[0] = Type.getType(Object.class);
        type[1] = Type.getType(long.class);
        type[2] = Type.getType(String.class);
        TypePool.Default.ParameterBag parameterBag = new TypePool.Default.ParameterBag(type);
        parameterBag.register(0, TypePoolDefaultParameterBagTest.FOO);
        parameterBag.register(3, TypePoolDefaultParameterBagTest.QUX);
        List<TypePool.Default.LazyTypeDescription.MethodToken.ParameterToken> tokens = parameterBag.resolve(true);
        MatcherAssert.assertThat(tokens.size(), CoreMatchers.is(3));
        MatcherAssert.assertThat(tokens.get(0).getName(), CoreMatchers.is(TypePoolDefaultParameterBagTest.FOO));
        MatcherAssert.assertThat(tokens.get(1).getName(), CoreMatchers.nullValue(String.class));
        MatcherAssert.assertThat(tokens.get(2).getName(), CoreMatchers.is(TypePoolDefaultParameterBagTest.QUX));
        MatcherAssert.assertThat(tokens.get(0).getModifiers(), CoreMatchers.nullValue(Integer.class));
        MatcherAssert.assertThat(tokens.get(1).getModifiers(), CoreMatchers.nullValue(Integer.class));
        MatcherAssert.assertThat(tokens.get(2).getModifiers(), CoreMatchers.nullValue(Integer.class));
    }

    @Test
    public void testEmptyResolutionStaticMethod() throws Exception {
        Type[] type = new Type[3];
        type[0] = Type.getType(Object.class);
        type[1] = Type.getType(long.class);
        type[2] = Type.getType(String.class);
        TypePool.Default.ParameterBag parameterBag = new TypePool.Default.ParameterBag(type);
        List<TypePool.Default.LazyTypeDescription.MethodToken.ParameterToken> tokens = parameterBag.resolve(true);
        MatcherAssert.assertThat(tokens.size(), CoreMatchers.is(3));
        MatcherAssert.assertThat(tokens.get(0).getName(), CoreMatchers.nullValue(String.class));
        MatcherAssert.assertThat(tokens.get(1).getName(), CoreMatchers.nullValue(String.class));
        MatcherAssert.assertThat(tokens.get(2).getName(), CoreMatchers.nullValue(String.class));
        MatcherAssert.assertThat(tokens.get(0).getModifiers(), CoreMatchers.nullValue(Integer.class));
        MatcherAssert.assertThat(tokens.get(1).getModifiers(), CoreMatchers.nullValue(Integer.class));
        MatcherAssert.assertThat(tokens.get(2).getModifiers(), CoreMatchers.nullValue(Integer.class));
    }
}

