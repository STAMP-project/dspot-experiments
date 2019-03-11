package net.bytebuddy.build;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.utility.StreamDrainer;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockito.Mockito;

import static java.util.jar.Attributes.Name.MANIFEST_VERSION;


public class PluginEngineTargetForJarFileTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private File file;

    @Test
    public void testWriteType() throws Exception {
        Plugin.Engine.Target target = new Plugin.Engine.Target.ForJarFile(file);
        Plugin.Engine.Target.Sink sink = target.write(Engine);
        try {
            sink.store(Collections.singletonMap(TypeDescription.OBJECT, new byte[]{ 1, 2, 3 }));
        } finally {
            sink.close();
        }
        JarInputStream inputStream = new JarInputStream(new FileInputStream(file));
        try {
            MatcherAssert.assertThat(inputStream.getManifest(), CoreMatchers.nullValue(Manifest.class));
            JarEntry entry = inputStream.getNextJarEntry();
            MatcherAssert.assertThat(entry.getName(), Is.is(((TypeDescription.OBJECT.getInternalName()) + ".class")));
            MatcherAssert.assertThat(StreamDrainer.DEFAULT.drain(inputStream), Is.is(new byte[]{ 1, 2, 3 }));
            MatcherAssert.assertThat(inputStream.getNextJarEntry(), CoreMatchers.nullValue(JarEntry.class));
        } finally {
            inputStream.close();
        }
    }

    @Test
    public void testWriteResource() throws Exception {
        Plugin.Engine.Target target = new Plugin.Engine.Target.ForJarFile(file);
        Plugin.Engine.Source.Element element = Mockito.mock(.class);
        Mockito.when(element.getName()).thenReturn((((PluginEngineTargetForJarFileTest.FOO) + "/") + (PluginEngineTargetForJarFileTest.BAR)));
        Mockito.when(element.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{ 1, 2, 3 }));
        Plugin.Engine.Target.Sink sink = target.write(Engine);
        try {
            sink.retain(element);
        } finally {
            sink.close();
        }
        JarInputStream inputStream = new JarInputStream(new FileInputStream(file));
        try {
            MatcherAssert.assertThat(inputStream.getManifest(), CoreMatchers.nullValue(Manifest.class));
            JarEntry entry = inputStream.getNextJarEntry();
            MatcherAssert.assertThat(entry.getName(), Is.is((((PluginEngineTargetForJarFileTest.FOO) + "/") + (PluginEngineTargetForJarFileTest.BAR))));
            MatcherAssert.assertThat(StreamDrainer.DEFAULT.drain(inputStream), Is.is(new byte[]{ 1, 2, 3 }));
            MatcherAssert.assertThat(inputStream.getNextJarEntry(), CoreMatchers.nullValue(JarEntry.class));
        } finally {
            inputStream.close();
        }
    }

    @Test
    public void testWriteResourceOriginal() throws Exception {
        Plugin.Engine.Target target = new Plugin.Engine.Target.ForJarFile(file);
        Plugin.Engine.Source.Element element = Mockito.mock(.class);
        Mockito.when(element.getName()).thenReturn((((PluginEngineTargetForJarFileTest.FOO) + "/") + (PluginEngineTargetForJarFileTest.BAR)));
        Mockito.when(element.resolveAs(JarEntry.class)).thenReturn(new JarEntry((((PluginEngineTargetForJarFileTest.FOO) + "/") + (PluginEngineTargetForJarFileTest.BAR))));
        Mockito.when(element.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{ 1, 2, 3 }));
        Plugin.Engine.Target.Sink sink = target.write(Engine);
        try {
            sink.retain(element);
        } finally {
            sink.close();
        }
        MatcherAssert.assertThat(file.isFile(), Is.is(true));
        JarInputStream inputStream = new JarInputStream(new FileInputStream(file));
        try {
            MatcherAssert.assertThat(inputStream.getManifest(), CoreMatchers.nullValue(Manifest.class));
            JarEntry entry = inputStream.getNextJarEntry();
            MatcherAssert.assertThat(entry.getName(), Is.is((((PluginEngineTargetForJarFileTest.FOO) + "/") + (PluginEngineTargetForJarFileTest.BAR))));
            MatcherAssert.assertThat(StreamDrainer.DEFAULT.drain(inputStream), Is.is(new byte[]{ 1, 2, 3 }));
            MatcherAssert.assertThat(inputStream.getNextJarEntry(), CoreMatchers.nullValue(JarEntry.class));
        } finally {
            inputStream.close();
        }
    }

    @Test
    public void testManifest() throws Exception {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(MANIFEST_VERSION, "1.0");
        Plugin.Engine.Target target = new Plugin.Engine.Target.ForJarFile(file);
        target.write(manifest).close();
        JarInputStream inputStream = new JarInputStream(new FileInputStream(file));
        try {
            Manifest readManifest = inputStream.getManifest();
            MatcherAssert.assertThat(readManifest.getMainAttributes().get(MANIFEST_VERSION), Is.is(((Object) ("1.0"))));
            MatcherAssert.assertThat(inputStream.getNextJarEntry(), CoreMatchers.nullValue(JarEntry.class));
        } finally {
            inputStream.close();
        }
    }
}

