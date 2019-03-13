package net.bytebuddy.implementation;


import ToStringMethod.PrefixResolver;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Ownership;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class ToStringMethodOtherTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Test
    public void testFullyQualifiedPrefix() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Object.class).defineField(ToStringMethodOtherTest.FOO, Object.class, Visibility.PUBLIC).method(ElementMatchers.isToString()).intercept(ToStringMethod.prefixedByFullyQualifiedClassName()).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(1));
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        instance.getClass().getDeclaredField(ToStringMethodOtherTest.FOO).set(instance, ToStringMethodOtherTest.FOO);
        MatcherAssert.assertThat(instance.toString(), CoreMatchers.startsWith(instance.getClass().getName()));
    }

    @Test
    public void testCanonicalPrefix() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Object.class).defineField(ToStringMethodOtherTest.FOO, Object.class, Visibility.PUBLIC).method(ElementMatchers.isToString()).intercept(ToStringMethod.prefixedByCanonicalClassName()).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(1));
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        instance.getClass().getDeclaredField(ToStringMethodOtherTest.FOO).set(instance, ToStringMethodOtherTest.FOO);
        MatcherAssert.assertThat(instance.toString(), CoreMatchers.startsWith(instance.getClass().getCanonicalName()));
    }

    @Test
    public void testSimplePrefix() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Object.class).defineField(ToStringMethodOtherTest.FOO, Object.class, Visibility.PUBLIC).method(ElementMatchers.isToString()).intercept(ToStringMethod.prefixedBySimpleClassName()).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(1));
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        instance.getClass().getDeclaredField(ToStringMethodOtherTest.FOO).set(instance, ToStringMethodOtherTest.FOO);
        MatcherAssert.assertThat(instance.toString(), CoreMatchers.startsWith(instance.getClass().getSimpleName()));
    }

    @Test
    public void testIgnoredField() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Object.class).defineField(ToStringMethodOtherTest.FOO, Object.class, Visibility.PUBLIC).method(ElementMatchers.isToString()).intercept(ToStringMethod.prefixedBy(ToStringMethodOtherTest.FOO).withIgnoredFields(ElementMatchers.named(ToStringMethodOtherTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(1));
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        instance.getClass().getDeclaredField(ToStringMethodOtherTest.FOO).set(instance, ToStringMethodOtherTest.FOO);
        MatcherAssert.assertThat(instance.toString(), CoreMatchers.is("foo{}"));
    }

    @Test
    public void testTokens() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Object.class).defineField(ToStringMethodOtherTest.FOO, Object.class, Visibility.PUBLIC).defineField(ToStringMethodOtherTest.BAR, Object.class, Visibility.PUBLIC).method(ElementMatchers.isToString()).intercept(ToStringMethod.prefixedBy(ToStringMethodOtherTest.FOO).withTokens("a", "b", "c", "d")).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(2));
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        instance.getClass().getDeclaredField(ToStringMethodOtherTest.FOO).set(instance, ToStringMethodOtherTest.FOO);
        instance.getClass().getDeclaredField(ToStringMethodOtherTest.BAR).set(instance, ToStringMethodOtherTest.BAR);
        MatcherAssert.assertThat(instance.toString(), CoreMatchers.is(((((((((((ToStringMethodOtherTest.FOO) + "a") + (ToStringMethodOtherTest.FOO)) + "d") + (ToStringMethodOtherTest.FOO)) + "c") + (ToStringMethodOtherTest.BAR)) + "d") + (ToStringMethodOtherTest.BAR)) + "b")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullPrefix() {
        ToStringMethod.prefixedBy(((String) (null)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTokenStartNull() {
        ToStringMethod.prefixedBy(ToStringMethodOtherTest.FOO).withTokens(null, "", "", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTokenEndNull() {
        ToStringMethod.prefixedBy(ToStringMethodOtherTest.FOO).withTokens("", null, "", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTokenSeparatorNull() {
        ToStringMethod.prefixedBy(ToStringMethodOtherTest.FOO).withTokens("", "", null, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTokenDefinerNull() {
        ToStringMethod.prefixedBy(ToStringMethodOtherTest.FOO).withTokens("", "", "", null);
    }

    @Test(expected = IllegalStateException.class)
    public void testNullPrefixResolved() {
        new ByteBuddy().makeInterface().method(ElementMatchers.isToString()).intercept(ToStringMethod.prefixedBy(Mockito.mock(PrefixResolver.class))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testInterface() throws Exception {
        new ByteBuddy().makeInterface().method(ElementMatchers.isToString()).intercept(ToStringMethod.prefixedBy(ToStringMethodOtherTest.FOO)).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testIncompatibleReturn() throws Exception {
        new ByteBuddy().subclass(Object.class).defineMethod(ToStringMethodOtherTest.FOO, Void.class).intercept(ToStringMethod.prefixedBy(ToStringMethodOtherTest.FOO)).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testStaticMethod() throws Exception {
        new ByteBuddy().subclass(Object.class).defineMethod(ToStringMethodOtherTest.FOO, String.class, Ownership.STATIC).intercept(ToStringMethod.prefixedBy(ToStringMethodOtherTest.FOO)).make();
    }
}

