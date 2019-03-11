package net.bytebuddy.build;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.test.utility.JavaVersionRule;
import net.bytebuddy.utility.StreamDrainer;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.mockito.Mockito;

import static java.util.jar.Attributes.Name.MANIFEST_VERSION;


public class PluginEngineTargetForFolderTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    private File folder;

    @Test
    public void testWriteType() throws Exception {
        Plugin.Engine.Target target = new Plugin.Engine.Target.ForFolder(folder);
        Plugin.Engine.Target.Sink sink = target.write(Engine);
        try {
            sink.store(Collections.singletonMap(TypeDescription.OBJECT, new byte[]{ 1, 2, 3 }));
        } finally {
            sink.close();
        }
        File file = new File(folder, ((TypeDescription.OBJECT.getInternalName()) + ".class"));
        MatcherAssert.assertThat(file.isFile(), Is.is(true));
        InputStream inputStream = new FileInputStream(file);
        try {
            MatcherAssert.assertThat(StreamDrainer.DEFAULT.drain(inputStream), Is.is(new byte[]{ 1, 2, 3 }));
        } finally {
            inputStream.close();
        }
        MatcherAssert.assertThat(file.delete(), Is.is(true));
        MatcherAssert.assertThat(file.getParentFile().delete(), Is.is(true));
        MatcherAssert.assertThat(file.getParentFile().getParentFile().delete(), Is.is(true));
    }

    @Test
    public void testWriteResource() throws Exception {
        Plugin.Engine.Target target = new Plugin.Engine.Target.ForFolder(folder);
        Plugin.Engine.Source.Element element = Mockito.mock(.class);
        Mockito.when(element.getName()).thenReturn((((PluginEngineTargetForFolderTest.FOO) + "/") + (PluginEngineTargetForFolderTest.BAR)));
        Mockito.when(element.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{ 1, 2, 3 }));
        Plugin.Engine.Target.Sink sink = target.write(Engine);
        try {
            sink.retain(element);
        } finally {
            sink.close();
        }
        File file = new File(folder, (((PluginEngineTargetForFolderTest.FOO) + "/") + (PluginEngineTargetForFolderTest.BAR)));
        MatcherAssert.assertThat(file.isFile(), Is.is(true));
        InputStream inputStream = new FileInputStream(file);
        try {
            MatcherAssert.assertThat(StreamDrainer.DEFAULT.drain(inputStream), Is.is(new byte[]{ 1, 2, 3 }));
        } finally {
            inputStream.close();
        }
        MatcherAssert.assertThat(file.delete(), Is.is(true));
        MatcherAssert.assertThat(file.getParentFile().delete(), Is.is(true));
    }

    @Test
    public void testWriteResourceFromFile() throws Exception {
        Plugin.Engine.Target target = new Plugin.Engine.Target.ForFolder(folder);
        Plugin.Engine.Source.Element element = Mockito.mock(.class);
        Mockito.when(element.getName()).thenReturn((((PluginEngineTargetForFolderTest.FOO) + "/") + (PluginEngineTargetForFolderTest.BAR)));
        Mockito.when(element.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{ 1, 2, 3 }));
        File original = File.createTempFile("qux", "baz");
        try {
            FileOutputStream outputStream = new FileOutputStream(original);
            try {
                outputStream.write(new byte[]{ 1, 2, 3 });
            } finally {
                outputStream.close();
            }
            Mockito.when(element.resolveAs(File.class)).thenReturn(original);
            Plugin.Engine.Target.Sink sink = target.write(Engine);
            try {
                sink.retain(element);
            } finally {
                sink.close();
            }
            File file = new File(folder, (((PluginEngineTargetForFolderTest.FOO) + "/") + (PluginEngineTargetForFolderTest.BAR)));
            MatcherAssert.assertThat(file.isFile(), Is.is(true));
            InputStream inputStream = new FileInputStream(file);
            try {
                MatcherAssert.assertThat(StreamDrainer.DEFAULT.drain(inputStream), Is.is(new byte[]{ 1, 2, 3 }));
            } finally {
                inputStream.close();
            }
            MatcherAssert.assertThat(file.delete(), Is.is(true));
            MatcherAssert.assertThat(file.getParentFile().delete(), Is.is(true));
        } finally {
            MatcherAssert.assertThat(original.delete(), Is.is(true));
        }
        Mockito.verify(element, Mockito.times((Engine.isAlive() ? 0 : 1))).getInputStream();
    }

    @Test
    public void testManifest() throws Exception {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(MANIFEST_VERSION, "1.0");
        Plugin.Engine.Target target = new Plugin.Engine.Target.ForFolder(folder);
        target.write(manifest).close();
        File file = new File(folder, JarFile.MANIFEST_NAME);
        MatcherAssert.assertThat(file.isFile(), Is.is(true));
        InputStream inputStream = new FileInputStream(file);
        try {
            Manifest readManifest = new Manifest(inputStream);
            MatcherAssert.assertThat(readManifest.getMainAttributes().get(MANIFEST_VERSION), Is.is(((Object) ("1.0"))));
        } finally {
            inputStream.close();
        }
        MatcherAssert.assertThat(file.delete(), Is.is(true));
        MatcherAssert.assertThat(file.getParentFile().delete(), Is.is(true));
    }

    @Test
    public void testIgnoreFolderElement() throws Exception {
        Plugin.Engine.Source.Element element = Mockito.mock(.class);
        Mockito.when(element.getName()).thenReturn(((PluginEngineTargetForFolderTest.FOO) + "/"));
        Plugin.Engine.Target.Sink sink = new Plugin.Engine.Target.ForFolder(folder).write(Engine);
        try {
            sink.retain(element);
        } finally {
            sink.close();
        }
        Mockito.verify(element).getName();
        Mockito.verifyNoMoreInteractions(element);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCannotWriteRelativeLocation() throws Exception {
        Plugin.Engine.Target target = new Plugin.Engine.Target.ForFolder(folder);
        Plugin.Engine.Source.Element element = Mockito.mock(.class);
        Mockito.when(element.getName()).thenReturn("../illegal");
        target.write(Engine).retain(element);
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testCanUseNio2() {
        MatcherAssert.assertThat(Engine.isAlive(), Is.is(true));
    }
}

