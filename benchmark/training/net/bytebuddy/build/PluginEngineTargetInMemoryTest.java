package net.bytebuddy.build;


import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockito.Mockito;

import static java.util.jar.Attributes.Name.MANIFEST_VERSION;


public class PluginEngineTargetInMemoryTest {
    private static final String FOO = "foo";

    @Test
    public void testWriteType() throws Exception {
        Plugin.Engine.Target.InMemory target = new Plugin.Engine.Target.InMemory();
        Plugin.Engine.Target.Sink sink = target.write(Engine);
        sink.store(Collections.singletonMap(TypeDescription.OBJECT, new byte[]{ 1, 2, 3 }));
        sink.close();
        MatcherAssert.assertThat(target.getStorage().size(), Is.is(1));
        MatcherAssert.assertThat(target.getStorage().get(((TypeDescription.OBJECT.getInternalName()) + ".class")), Is.is(new byte[]{ 1, 2, 3 }));
        MatcherAssert.assertThat(target.toTypeMap().size(), Is.is(1));
        MatcherAssert.assertThat(target.toTypeMap().get(TypeDescription.OBJECT.getName()), Is.is(new byte[]{ 1, 2, 3 }));
    }

    @Test
    public void testWriteResource() throws Exception {
        Plugin.Engine.Target.InMemory target = new Plugin.Engine.Target.InMemory();
        Plugin.Engine.Target.Sink sink = target.write(Engine);
        Plugin.Engine.Source.Element element = Mockito.mock(.class);
        Mockito.when(element.getName()).thenReturn(PluginEngineTargetInMemoryTest.FOO);
        Mockito.when(element.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{ 1, 2, 3 }));
        sink.retain(element);
        sink.close();
        MatcherAssert.assertThat(target.getStorage().size(), Is.is(1));
        MatcherAssert.assertThat(target.getStorage().get(PluginEngineTargetInMemoryTest.FOO), Is.is(new byte[]{ 1, 2, 3 }));
        MatcherAssert.assertThat(target.toTypeMap().size(), Is.is(0));
    }

    @Test
    public void testManifest() throws Exception {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(MANIFEST_VERSION, "1.0");
        Plugin.Engine.Target.InMemory target = new Plugin.Engine.Target.InMemory();
        target.write(manifest).close();
        MatcherAssert.assertThat(target.getStorage().size(), Is.is(1));
        Manifest readManifest = new Manifest(new ByteArrayInputStream(target.getStorage().get(JarFile.MANIFEST_NAME)));
        MatcherAssert.assertThat(readManifest.getMainAttributes().get(MANIFEST_VERSION), Is.is(((Object) ("1.0"))));
    }

    @Test
    public void testIgnoreFolderElement() throws Exception {
        Plugin.Engine.Source.Element element = Mockito.mock(.class);
        Mockito.when(element.getName()).thenReturn(((PluginEngineTargetInMemoryTest.FOO) + "/"));
        Plugin.Engine.Target.Sink sink = new Plugin.Engine.Target.InMemory().write(Engine);
        try {
            sink.retain(element);
        } finally {
            sink.close();
        }
        Mockito.verify(element).getName();
        Mockito.verifyNoMoreInteractions(element);
    }
}

