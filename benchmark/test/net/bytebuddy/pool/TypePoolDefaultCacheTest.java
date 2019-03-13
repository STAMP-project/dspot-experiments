package net.bytebuddy.pool;


import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class TypePoolDefaultCacheTest {
    @Test
    public void testCache() throws Exception {
        TypePool typePool = ofSystemLoader();
        TypeDescription typeDescription = typePool.describe(Void.class.getName()).resolve();
        MatcherAssert.assertThat(typePool.describe(Void.class.getName()).resolve(), CoreMatchers.sameInstance(typeDescription));
        typePool.clear();
        MatcherAssert.assertThat(typePool.describe(Void.class.getName()).resolve(), CoreMatchers.not(CoreMatchers.sameInstance(typeDescription)));
    }
}

