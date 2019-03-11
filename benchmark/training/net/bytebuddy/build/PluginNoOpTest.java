package net.bytebuddy.build;


import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockito.Mockito;


public class PluginNoOpTest {
    private Plugin.NoOp plugin;

    @Test
    public void testFactory() {
        MatcherAssert.assertThat(plugin.make(), CoreMatchers.sameInstance(((Plugin) (plugin))));
    }

    @Test(expected = IllegalStateException.class)
    public void testNoOpApplication() {
        plugin.apply(Mockito.mock(DynamicType.Builder.class), Mockito.mock(TypeDescription.class), Mockito.mock(ClassFileLocator.class));
    }

    @Test
    public void testMatch() {
        MatcherAssert.assertThat(plugin.matches(Mockito.mock(TypeDescription.class)), Is.is(false));
    }
}

