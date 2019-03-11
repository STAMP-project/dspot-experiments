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
public class ToStringMethodTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private final Class<?> type;

    private final Object value;

    private final String string;

    public ToStringMethodTest(Class<?> type, Object value, String string) {
        this.type = type;
        this.value = value;
        this.string = string;
    }

    @Test
    public void testEqualsTrue() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Object.class).defineField(ToStringMethodTest.FOO, type, Visibility.PUBLIC).method(ElementMatchers.isToString()).intercept(ToStringMethod.prefixedBy(ToStringMethodTest.FOO)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(1));
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        instance.getClass().getDeclaredField(ToStringMethodTest.FOO).set(instance, value);
        MatcherAssert.assertThat(instance.toString(), CoreMatchers.is(((((((ToStringMethodTest.FOO) + "{") + (ToStringMethodTest.FOO)) + "=") + (string)) + "}")));
    }
}

