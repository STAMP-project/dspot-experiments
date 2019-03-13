package net.bytebuddy.build;


import Factory.UsingReflection.Priority;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;


public class PluginFactoryUsingReflectionTest {
    private static final String FOO = "foo";

    @Test
    public void testDefaultConstructor() {
        MatcherAssert.assertThat(new Plugin.Factory.UsingReflection(PluginFactoryUsingReflectionTest.SimplePlugin.class).make(), CoreMatchers.instanceOf(PluginFactoryUsingReflectionTest.SimplePlugin.class));
    }

    @Test
    public void testDefaultConstructorIgnoreArgument() {
        MatcherAssert.assertThat(new Plugin.Factory.UsingReflection(PluginFactoryUsingReflectionTest.SimplePluginTwoConstructors.class).make(), CoreMatchers.instanceOf(PluginFactoryUsingReflectionTest.SimplePluginTwoConstructors.class));
    }

    @Test
    public void testArgumentConstructor() {
        MatcherAssert.assertThat(new Plugin.Factory.UsingReflection(PluginFactoryUsingReflectionTest.SimplePluginArgumentConstructor.class).with(Factory.of(Void.class, null)).make(), CoreMatchers.instanceOf(PluginFactoryUsingReflectionTest.SimplePluginArgumentConstructor.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testArgumentConstructorNoResolver() {
        new Plugin.Factory.UsingReflection(PluginFactoryUsingReflectionTest.SimplePluginArgumentConstructor.class).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testConstructorAmbiguous() {
        new Plugin.Factory.UsingReflection(PluginFactoryUsingReflectionTest.SimplePluginTwoConstructors.class).with(Factory.of(Void.class, null)).make();
    }

    @Test
    public void testArgumentConstructorPrirorityLeft() {
        MatcherAssert.assertThat(new Plugin.Factory.UsingReflection(PluginFactoryUsingReflectionTest.SimplePluginPreferredConstructorLeft.class).with(Factory.of(Void.class, null)).make(), CoreMatchers.instanceOf(PluginFactoryUsingReflectionTest.SimplePluginPreferredConstructorLeft.class));
    }

    @Test
    public void testArgumentConstructorPrirorityRight() {
        MatcherAssert.assertThat(new Plugin.Factory.UsingReflection(PluginFactoryUsingReflectionTest.SimplePluginPreferredConstructorRight.class).with(Factory.of(Void.class, null)).make(), CoreMatchers.instanceOf(PluginFactoryUsingReflectionTest.SimplePluginPreferredConstructorRight.class));
    }

    @Test
    public void testArgumentResolverType() {
        Plugin.Factory.UsingReflection.ArgumentResolver argumentResolver = Factory.of(Void.class, null);
        Plugin.Factory.UsingReflection.ArgumentResolver.Resolution resolution = argumentResolver.resolve((-1), Void.class);
        MatcherAssert.assertThat(resolution.isResolved(), Is.is(true));
        MatcherAssert.assertThat(resolution.getArgument(), CoreMatchers.nullValue());
        MatcherAssert.assertThat(argumentResolver.resolve((-1), Object.class).isResolved(), Is.is(false));
    }

    @Test
    public void testArgumentResolverIndex() {
        Plugin.Factory.UsingReflection.ArgumentResolver argumentResolver = new Plugin.Factory.UsingReflection.ArgumentResolver.ForIndex(0, null);
        Plugin.Factory.UsingReflection.ArgumentResolver.Resolution resolution = argumentResolver.resolve(0, Object.class);
        MatcherAssert.assertThat(resolution.isResolved(), Is.is(true));
        MatcherAssert.assertThat(resolution.getArgument(), CoreMatchers.nullValue());
        MatcherAssert.assertThat(argumentResolver.resolve((-1), Object.class).isResolved(), Is.is(false));
    }

    @Test
    public void testArgumentResolverIndexWithDynamicTypeNotString() {
        Plugin.Factory.UsingReflection.ArgumentResolver argumentResolver = new Plugin.Factory.UsingReflection.ArgumentResolver.ForIndex.WithDynamicType(0, "42");
        Plugin.Factory.UsingReflection.ArgumentResolver.Resolution resolution = argumentResolver.resolve(0, Long.class);
        MatcherAssert.assertThat(resolution.isResolved(), Is.is(true));
        MatcherAssert.assertThat(resolution.getArgument(), Is.is(((Object) (42L))));
        MatcherAssert.assertThat(argumentResolver.resolve((-1), Long.class).isResolved(), Is.is(false));
        MatcherAssert.assertThat(argumentResolver.resolve(0, Void.class).isResolved(), Is.is(false));
    }

    @Test
    public void testArgumentResolverIndexWithDynamicTypeString() {
        Plugin.Factory.UsingReflection.ArgumentResolver argumentResolver = new Plugin.Factory.UsingReflection.ArgumentResolver.ForIndex.WithDynamicType(0, PluginFactoryUsingReflectionTest.FOO);
        Plugin.Factory.UsingReflection.ArgumentResolver.Resolution resolution = argumentResolver.resolve(0, String.class);
        MatcherAssert.assertThat(resolution.isResolved(), Is.is(true));
        MatcherAssert.assertThat(resolution.getArgument(), Is.is(((Object) (PluginFactoryUsingReflectionTest.FOO))));
        MatcherAssert.assertThat(argumentResolver.resolve((-1), String.class).isResolved(), Is.is(false));
        MatcherAssert.assertThat(argumentResolver.resolve(0, Void.class).isResolved(), Is.is(false));
    }

    @Test(expected = IllegalStateException.class)
    public void testArgumentResolverUnresolvedResolution() {
        Factory.getArgument();
    }

    public static class SimplePlugin implements Plugin {
        public boolean matches(TypeDescription target) {
            throw new AssertionError();
        }

        public DynamicType.Builder<?> apply(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassFileLocator classFileLocator) {
            throw new AssertionError();
        }

        public void close() {
            throw new AssertionError();
        }
    }

    public static class SimplePluginArgumentConstructor implements Plugin {
        public SimplePluginArgumentConstructor(Void unused) {
            /* empty */
        }

        public boolean matches(TypeDescription target) {
            throw new AssertionError();
        }

        public DynamicType.Builder<?> apply(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassFileLocator classFileLocator) {
            throw new AssertionError();
        }

        public void close() {
            throw new AssertionError();
        }
    }

    public static class SimplePluginTwoConstructors implements Plugin {
        public SimplePluginTwoConstructors() {
            /* empty */
        }

        public SimplePluginTwoConstructors(Void unused) {
            /* empty */
        }

        public boolean matches(TypeDescription target) {
            throw new AssertionError();
        }

        public DynamicType.Builder<?> apply(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassFileLocator classFileLocator) {
            throw new AssertionError();
        }

        public void close() {
            throw new AssertionError();
        }
    }

    public static class SimplePluginPreferredConstructorLeft implements Plugin {
        @Factory.UsingReflection.Priority(1)
        public SimplePluginPreferredConstructorLeft() {
            /* empty */
        }

        public SimplePluginPreferredConstructorLeft(Void unused) {
            /* empty */
        }

        public boolean matches(TypeDescription target) {
            throw new AssertionError();
        }

        public DynamicType.Builder<?> apply(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassFileLocator classFileLocator) {
            throw new AssertionError();
        }

        public void close() {
            throw new AssertionError();
        }
    }

    public static class SimplePluginPreferredConstructorRight implements Plugin {
        public SimplePluginPreferredConstructorRight() {
            /* empty */
        }

        @Factory.UsingReflection.Priority(1)
        public SimplePluginPreferredConstructorRight(Void unused) {
            /* empty */
        }

        public boolean matches(TypeDescription target) {
            throw new AssertionError();
        }

        public DynamicType.Builder<?> apply(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassFileLocator classFileLocator) {
            throw new AssertionError();
        }

        public void close() {
            throw new AssertionError();
        }
    }
}

