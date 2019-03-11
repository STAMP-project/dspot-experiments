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
public class EqualsMethodTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private final Class<?> type;

    private final Object value;

    private final Object alternative;

    public EqualsMethodTest(Class<?> type, Object value, Object alternative) {
        this.type = type;
        this.value = value;
        this.alternative = alternative;
    }

    @Test
    public void testEqualsTrue() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Object.class).defineField(EqualsMethodTest.FOO, type, Visibility.PUBLIC).method(ElementMatchers.isEquals()).intercept(EqualsMethod.isolated()).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(1));
        Object left = loaded.getLoaded().getDeclaredConstructor().newInstance();
        Object right = loaded.getLoaded().getDeclaredConstructor().newInstance();
        left.getClass().getDeclaredField(EqualsMethodTest.FOO).set(left, value);
        right.getClass().getDeclaredField(EqualsMethodTest.FOO).set(right, value);
        MatcherAssert.assertThat(left, CoreMatchers.is(right));
    }

    @Test
    public void testEqualsFalse() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Object.class).defineField(EqualsMethodTest.FOO, type, Visibility.PUBLIC).method(ElementMatchers.isEquals()).intercept(EqualsMethod.isolated()).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(1));
        Object first = loaded.getLoaded().getDeclaredConstructor().newInstance();
        Object second = loaded.getLoaded().getDeclaredConstructor().newInstance();
        first.getClass().getDeclaredField(EqualsMethodTest.FOO).set(first, value);
        second.getClass().getDeclaredField(EqualsMethodTest.FOO).set(second, alternative);
        MatcherAssert.assertThat(first, CoreMatchers.not(second));
    }
}

