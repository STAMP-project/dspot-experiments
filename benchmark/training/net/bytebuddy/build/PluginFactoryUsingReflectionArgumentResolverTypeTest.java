package net.bytebuddy.build;


import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class PluginFactoryUsingReflectionArgumentResolverTypeTest {
    private final Class<?> type;

    private final Class<?> wrapperType;

    private final Object value;

    public PluginFactoryUsingReflectionArgumentResolverTypeTest(Class<?> type, Class<?> wrapperType, Object value) {
        this.type = type;
        this.wrapperType = wrapperType;
        this.value = value;
    }

    @Test
    public void testCanResolveByTypePrimitive() {
        Plugin.Factory.UsingReflection.ArgumentResolver.Resolution resolution = new Plugin.Factory.UsingReflection.ArgumentResolver.ForIndex(0, value).resolve(0, type);
        MatcherAssert.assertThat(resolution.isResolved(), Is.is(true));
        MatcherAssert.assertThat(resolution.getArgument(), Is.is(value));
    }

    @Test
    public void testCanResolveByTypeBoxed() {
        Plugin.Factory.UsingReflection.ArgumentResolver.Resolution resolution = new Plugin.Factory.UsingReflection.ArgumentResolver.ForIndex(0, value).resolve(0, wrapperType);
        MatcherAssert.assertThat(resolution.isResolved(), Is.is(true));
        MatcherAssert.assertThat(resolution.getArgument(), Is.is(value));
    }

    @Test
    public void testCanResolveByTypeUnresolved() {
        MatcherAssert.assertThat(new Plugin.Factory.UsingReflection.ArgumentResolver.ForIndex(0, value).resolve(0, Void.class).isResolved(), Is.is(false));
    }

    @Test
    public void testCanResolveByTypePrimitiveDynamic() {
        Plugin.Factory.UsingReflection.ArgumentResolver.Resolution resolution = new Plugin.Factory.UsingReflection.ArgumentResolver.ForIndex.WithDynamicType(0, value.toString()).resolve(0, type);
        MatcherAssert.assertThat(resolution.isResolved(), Is.is(true));
        MatcherAssert.assertThat(resolution.getArgument(), Is.is(value));
    }

    @Test
    public void testCanResolveByTypeBoxedDynamic() {
        Plugin.Factory.UsingReflection.ArgumentResolver.Resolution resolution = new Plugin.Factory.UsingReflection.ArgumentResolver.ForIndex.WithDynamicType(0, value.toString()).resolve(0, wrapperType);
        MatcherAssert.assertThat(resolution.isResolved(), Is.is(true));
        MatcherAssert.assertThat(resolution.getArgument(), Is.is(value));
    }

    @Test
    public void testCanResolveByTypeBoxedUnresolved() {
        MatcherAssert.assertThat(new Plugin.Factory.UsingReflection.ArgumentResolver.ForIndex.WithDynamicType(0, value.toString()).resolve(0, Void.class).isResolved(), Is.is(false));
    }
}

