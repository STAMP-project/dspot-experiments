package net.bytebuddy.build;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import net.bytebuddy.utility.StreamDrainer;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;

import static java.util.jar.Attributes.Name.MANIFEST_VERSION;


public class PluginEngineSourceForFolderTest {
    private File folder;

    @Test
    public void testEmpty() throws Exception {
        Plugin.Engine.Source.Origin origin = new Plugin.Engine.Source.ForFolder(folder).read();
        try {
            MatcherAssert.assertThat(origin.getManifest(), CoreMatchers.nullValue(Manifest.class));
            MatcherAssert.assertThat(origin.getClassFileLocator().locate(Object.class.getName()).isResolved(), Is.is(false));
            MatcherAssert.assertThat(origin.iterator().hasNext(), Is.is(false));
        } finally {
            origin.close();
        }
    }

    @Test
    public void testFile() throws Exception {
        File file = new File(folder, "Foo.class");
        OutputStream outputStream = new FileOutputStream(file);
        try {
            outputStream.write(new byte[]{ 1, 2, 3 });
        } finally {
            outputStream.close();
        }
        Plugin.Engine.Source.Origin origin = new Plugin.Engine.Source.ForFolder(folder).read();
        try {
            MatcherAssert.assertThat(origin.getManifest(), CoreMatchers.nullValue(Manifest.class));
            MatcherAssert.assertThat(origin.getClassFileLocator().locate("Foo").isResolved(), Is.is(true));
            MatcherAssert.assertThat(origin.getClassFileLocator().locate("Foo").resolve(), Is.is(new byte[]{ 1, 2, 3 }));
            MatcherAssert.assertThat(origin.getClassFileLocator().locate("Bar").isResolved(), Is.is(false));
            Iterator<Plugin.Engine.Source.Element> iterator = origin.iterator();
            MatcherAssert.assertThat(iterator.hasNext(), Is.is(true));
            Plugin.Engine.Source.Element element = iterator.next();
            MatcherAssert.assertThat(element.getName(), Is.is("Foo.class"));
            MatcherAssert.assertThat(element.resolveAs(Object.class), CoreMatchers.nullValue(Object.class));
            MatcherAssert.assertThat(element.resolveAs(File.class), Is.is(file));
            InputStream inputStream = element.getInputStream();
            try {
                MatcherAssert.assertThat(StreamDrainer.DEFAULT.drain(inputStream), Is.is(new byte[]{ 1, 2, 3 }));
            } finally {
                inputStream.close();
            }
            MatcherAssert.assertThat(iterator.hasNext(), Is.is(false));
        } finally {
            origin.close();
        }
        MatcherAssert.assertThat(file.delete(), Is.is(true));
    }

    @Test
    public void testFileInSubFolder() throws Exception {
        File file = new File(folder, "bar/Foo.class");
        MatcherAssert.assertThat(file.getParentFile().mkdir(), Is.is(true));
        OutputStream outputStream = new FileOutputStream(file);
        try {
            outputStream.write(new byte[]{ 1, 2, 3 });
        } finally {
            outputStream.close();
        }
        Plugin.Engine.Source.Origin origin = new Plugin.Engine.Source.ForFolder(folder).read();
        try {
            MatcherAssert.assertThat(origin.getManifest(), CoreMatchers.nullValue(Manifest.class));
            MatcherAssert.assertThat(origin.getClassFileLocator().locate("bar.Foo").isResolved(), Is.is(true));
            MatcherAssert.assertThat(origin.getClassFileLocator().locate("bar.Foo").resolve(), Is.is(new byte[]{ 1, 2, 3 }));
            MatcherAssert.assertThat(origin.getClassFileLocator().locate("Bar").isResolved(), Is.is(false));
            Iterator<Plugin.Engine.Source.Element> iterator = origin.iterator();
            MatcherAssert.assertThat(iterator.hasNext(), Is.is(true));
            Plugin.Engine.Source.Element element = iterator.next();
            MatcherAssert.assertThat(element.getName(), Is.is("bar/Foo.class"));
            MatcherAssert.assertThat(element.resolveAs(Object.class), CoreMatchers.nullValue(Object.class));
            MatcherAssert.assertThat(element.resolveAs(File.class), Is.is(file));
            InputStream inputStream = element.getInputStream();
            try {
                MatcherAssert.assertThat(StreamDrainer.DEFAULT.drain(inputStream), Is.is(new byte[]{ 1, 2, 3 }));
            } finally {
                inputStream.close();
            }
            MatcherAssert.assertThat(iterator.hasNext(), Is.is(false));
        } finally {
            origin.close();
        }
        MatcherAssert.assertThat(file.delete(), Is.is(true));
        MatcherAssert.assertThat(file.getParentFile().delete(), Is.is(true));
    }

    @Test
    public void testManifest() throws Exception {
        File file = new File(folder, JarFile.MANIFEST_NAME);
        MatcherAssert.assertThat(file.getParentFile().mkdir(), Is.is(true));
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(MANIFEST_VERSION, "1.0");
        OutputStream outputStream = new FileOutputStream(file);
        try {
            manifest.write(outputStream);
        } finally {
            outputStream.close();
        }
        Plugin.Engine.Source.Origin origin = new Plugin.Engine.Source.ForFolder(folder).read();
        try {
            Manifest readManifest = origin.getManifest();
            MatcherAssert.assertThat(readManifest, CoreMatchers.notNullValue(Manifest.class));
            MatcherAssert.assertThat(readManifest.getMainAttributes().getValue(MANIFEST_VERSION), Is.is("1.0"));
        } finally {
            origin.close();
        }
        MatcherAssert.assertThat(file.delete(), Is.is(true));
        MatcherAssert.assertThat(file.getParentFile().delete(), Is.is(true));
    }
}

