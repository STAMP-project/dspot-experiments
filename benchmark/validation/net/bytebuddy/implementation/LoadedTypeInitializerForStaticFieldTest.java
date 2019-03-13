package net.bytebuddy.implementation;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class LoadedTypeInitializerForStaticFieldTest {
    private static final String FOO = "foo";

    @Test
    public void testAccessibleField() throws Exception {
        Object object = new Object();
        LoadedTypeInitializer loadedTypeInitializer = new LoadedTypeInitializer.ForStaticField(LoadedTypeInitializerForStaticFieldTest.FOO, object);
        MatcherAssert.assertThat(loadedTypeInitializer.isAlive(), CoreMatchers.is(true));
        loadedTypeInitializer.onLoad(LoadedTypeInitializerForStaticFieldTest.Foo.class);
        MatcherAssert.assertThat(LoadedTypeInitializerForStaticFieldTest.Foo.foo, CoreMatchers.is(object));
    }

    @Test
    public void testNonAccessibleField() throws Exception {
        Object object = new Object();
        LoadedTypeInitializer loadedTypeInitializer = new LoadedTypeInitializer.ForStaticField(LoadedTypeInitializerForStaticFieldTest.FOO, object);
        MatcherAssert.assertThat(loadedTypeInitializer.isAlive(), CoreMatchers.is(true));
        loadedTypeInitializer.onLoad(LoadedTypeInitializerForStaticFieldTest.Bar.class);
        MatcherAssert.assertThat(LoadedTypeInitializerForStaticFieldTest.Bar.foo, CoreMatchers.is(object));
    }

    @Test
    public void testNonAccessibleType() throws Exception {
        Object object = new Object();
        LoadedTypeInitializer loadedTypeInitializer = new LoadedTypeInitializer.ForStaticField(LoadedTypeInitializerForStaticFieldTest.FOO, object);
        MatcherAssert.assertThat(loadedTypeInitializer.isAlive(), CoreMatchers.is(true));
        loadedTypeInitializer.onLoad(LoadedTypeInitializerForStaticFieldTest.Qux.class);
        MatcherAssert.assertThat(LoadedTypeInitializerForStaticFieldTest.Qux.foo, CoreMatchers.is(object));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonAssignableField() throws Exception {
        new LoadedTypeInitializer.ForStaticField(LoadedTypeInitializerForStaticFieldTest.FOO, new Object()).onLoad(LoadedTypeInitializerForStaticFieldTest.FooBar.class);
    }

    @SuppressWarnings("unused")
    public static class Foo {
        public static Object foo;
    }

    @SuppressWarnings("unused")
    public static class Bar {
        private static Object foo;
    }

    @SuppressWarnings("unused")
    private static class Qux {
        public static Object foo;
    }

    @SuppressWarnings("unused")
    private static class Baz {
        String foo;

        String bar;
    }

    @SuppressWarnings("unused")
    public static class FooBar {
        public static String foo;
    }
}

