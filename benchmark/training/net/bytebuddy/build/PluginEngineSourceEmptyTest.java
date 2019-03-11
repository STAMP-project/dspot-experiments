package net.bytebuddy.build;


import java.util.jar.Manifest;
import net.bytebuddy.dynamic.ClassFileLocator;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;

import static net.bytebuddy.dynamic.ClassFileLocator.NoOp.INSTANCE;


public class PluginEngineSourceEmptyTest {
    @Test
    public void testNonOperational() throws Exception {
        MatcherAssert.assertThat(Engine.getClassFileLocator(), Is.is(((ClassFileLocator) (INSTANCE))));
        MatcherAssert.assertThat(Engine.getManifest(), CoreMatchers.nullValue(Manifest.class));
        MatcherAssert.assertThat(Engine.iterator().hasNext(), Is.is(false));
    }
}

