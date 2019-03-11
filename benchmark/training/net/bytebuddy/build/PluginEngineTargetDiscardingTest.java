package net.bytebuddy.build;


import java.util.Collections;
import java.util.jar.Manifest;
import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockito.Mockito;


public class PluginEngineTargetDiscardingTest {
    @Test
    public void testDiscarding() throws Exception {
        MatcherAssert.assertThat(Engine.write(Engine), Is.is(((Plugin.Engine.Target.Sink) (Engine))));
        MatcherAssert.assertThat(Engine.write(new Manifest()), Is.is(((Plugin.Engine.Target.Sink) (Engine))));
        Plugin.Engine.Source.Element eleement = Mockito.mock(.class);
        Engine.write(Engine).retain(eleement);
        Mockito.verifyZeroInteractions(eleement);
        Engine.write(Engine).store(Collections.singletonMap(TypeDescription.OBJECT, new byte[]{ 1, 2, 3 }));
    }
}

