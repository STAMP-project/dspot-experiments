package net.bytebuddy.implementation;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Ownership;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class HashCodeMethodOtherTest {
    private static final String FOO = "foo";

    @Test(expected = NullPointerException.class)
    public void testNullableField() throws Exception {
        new ByteBuddy().subclass(Object.class).defineField(HashCodeMethodOtherTest.FOO, Object.class, Visibility.PUBLIC).method(ElementMatchers.isHashCode()).intercept(HashCodeMethod.usingDefaultOffset().withNonNullableFields(ElementMatchers.named(HashCodeMethodOtherTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded().getDeclaredConstructor().newInstance().hashCode();
    }

    @Test
    public void testIgnoredField() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Object.class).defineField(HashCodeMethodOtherTest.FOO, Object.class, Visibility.PUBLIC).method(ElementMatchers.isHashCode()).intercept(HashCodeMethod.usingOffset(0).withIgnoredFields(ElementMatchers.named(HashCodeMethodOtherTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(1));
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        instance.getClass().getDeclaredField(HashCodeMethodOtherTest.FOO).set(instance, HashCodeMethodOtherTest.FOO);
        MatcherAssert.assertThat(instance.hashCode(), CoreMatchers.is(0));
    }

    @Test
    public void testSuperMethod() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(HashCodeMethodOtherTest.HashCodeBase.class).defineField(HashCodeMethodOtherTest.FOO, Object.class, Visibility.PUBLIC).method(ElementMatchers.isHashCode()).intercept(HashCodeMethod.usingSuperClassOffset()).make().load(HashCodeMethodOtherTest.HashCodeBase.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(1));
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        instance.getClass().getDeclaredField(HashCodeMethodOtherTest.FOO).set(instance, HashCodeMethodOtherTest.FOO);
        MatcherAssert.assertThat(instance.hashCode(), CoreMatchers.is(((42 * 31) + (HashCodeMethodOtherTest.FOO.hashCode()))));
    }

    @Test
    public void testMultiplier() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Object.class).defineField(HashCodeMethodOtherTest.FOO, Object.class, Visibility.PUBLIC).method(ElementMatchers.isHashCode()).intercept(HashCodeMethod.usingOffset(0).withMultiplier(1)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(1));
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        instance.getClass().getDeclaredField(HashCodeMethodOtherTest.FOO).set(instance, HashCodeMethodOtherTest.FOO);
        MatcherAssert.assertThat(instance.hashCode(), CoreMatchers.is(101574));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testZeroMultiplier() {
        HashCodeMethod.usingDefaultOffset().withMultiplier(0);
    }

    @Test(expected = IllegalStateException.class)
    public void testInterface() throws Exception {
        new ByteBuddy().makeInterface().method(ElementMatchers.isHashCode()).intercept(HashCodeMethod.usingDefaultOffset()).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testIncompatibleReturn() throws Exception {
        new ByteBuddy().subclass(Object.class).defineMethod(HashCodeMethodOtherTest.FOO, Object.class).intercept(HashCodeMethod.usingDefaultOffset()).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testStaticMethod() throws Exception {
        new ByteBuddy().subclass(Object.class).defineMethod(HashCodeMethodOtherTest.FOO, int.class, Ownership.STATIC).intercept(HashCodeMethod.usingDefaultOffset()).make();
    }

    public static class HashCodeBase {
        public int hashCode() {
            return 42;
        }
    }
}

