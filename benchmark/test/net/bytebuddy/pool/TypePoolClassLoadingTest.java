package net.bytebuddy.pool;


import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TypePoolClassLoadingTest {
    private final TypePool typePool;

    public TypePoolClassLoadingTest(TypePool typePool) {
        this.typePool = typePool;
    }

    @Test
    public void testLoadableBootstrapLoaderClass() throws Exception {
        TypePool.Resolution resolution = typePool.describe(Object.class.getName());
        MatcherAssert.assertThat(resolution.isResolved(), CoreMatchers.is(true));
        MatcherAssert.assertThat(resolution.resolve(), CoreMatchers.is(TypeDescription.OBJECT));
    }

    @Test
    public void testArrayClass() throws Exception {
        TypePool.Resolution resolution = typePool.describe(Object[].class.getName());
        MatcherAssert.assertThat(resolution.isResolved(), CoreMatchers.is(true));
        MatcherAssert.assertThat(resolution.resolve(), CoreMatchers.is(((TypeDescription) (of(Object[].class)))));
    }

    @Test
    public void testPrimitiveClass() throws Exception {
        TypePool.Resolution resolution = typePool.describe(int.class.getName());
        MatcherAssert.assertThat(resolution.isResolved(), CoreMatchers.is(true));
        MatcherAssert.assertThat(resolution.resolve(), CoreMatchers.is(((TypeDescription) (of(int.class)))));
    }

    @Test
    public void testClearRetainsFunctionality() throws Exception {
        TypePool.Resolution resolution = typePool.describe(Object.class.getName());
        MatcherAssert.assertThat(resolution.isResolved(), CoreMatchers.is(true));
        MatcherAssert.assertThat(resolution.resolve(), CoreMatchers.is(TypeDescription.OBJECT));
        typePool.clear();
        TypePool.Resolution otherResolution = typePool.describe(Object.class.getName());
        MatcherAssert.assertThat(otherResolution.isResolved(), CoreMatchers.is(true));
        MatcherAssert.assertThat(resolution.resolve(), CoreMatchers.is(TypeDescription.OBJECT));
    }
}

