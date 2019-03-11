package net.bytebuddy.implementation;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


@RunWith(Parameterized.class)
public class HashCodeMethodTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private final Class<?> type;

    private final Object value;

    private final int hashOffset;

    public HashCodeMethodTest(Class<?> type, Object value, int hashOffset) {
        this.type = type;
        this.value = value;
        this.hashOffset = hashOffset;
    }

    @Test
    public void testHashCode() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Object.class).defineField(HashCodeMethodTest.FOO, type, Visibility.PUBLIC).method(ElementMatchers.isHashCode()).intercept(HashCodeMethod.usingOffset(0)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(1));
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        instance.getClass().getDeclaredField(HashCodeMethodTest.FOO).set(instance, value);
        MatcherAssert.assertThat(instance.hashCode(), CoreMatchers.is(hashOffset));
    }
}

