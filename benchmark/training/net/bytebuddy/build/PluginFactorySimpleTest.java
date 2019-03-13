package net.bytebuddy.build;


import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockito.Mockito;


public class PluginFactorySimpleTest {
    @Test
    public void testFactory() {
        Plugin plugin = Mockito.mock(Plugin.class);
        MatcherAssert.assertThat(new Plugin.Factory.Simple(plugin).make(), Is.is(plugin));
    }
}

