package net.bytebuddy.build;


import java.io.ByteArrayOutputStream;
import java.util.Collections;
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
import static net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.read;


public class PluginEngineSourceInMemoryTest {
    @Test
    public void testNoManifest() throws Exception {
        Plugin.Engine.Source.Origin origin = new Plugin.Engine.Source.InMemory(Collections.singletonMap("foo/Bar.class", new byte[]{ 1, 2, 3 })).read();
        try {
            MatcherAssert.assertThat(origin.getClassFileLocator().locate("foo.Bar").isResolved(), Is.is(true));
            MatcherAssert.assertThat(origin.getClassFileLocator().locate("foo.Bar").resolve(), Is.is(new byte[]{ 1, 2, 3 }));
            MatcherAssert.assertThat(origin.getClassFileLocator().locate("qux.Baz").isResolved(), Is.is(false));
            MatcherAssert.assertThat(origin.getManifest(), CoreMatchers.nullValue(Manifest.class));
            Iterator<Plugin.Engine.Source.Element> iterator = origin.iterator();
            MatcherAssert.assertThat(iterator.hasNext(), Is.is(true));
            Plugin.Engine.Source.Element element = iterator.next();
            MatcherAssert.assertThat(element.getName(), Is.is("foo/Bar.class"));
            MatcherAssert.assertThat(element.resolveAs(Object.class), CoreMatchers.nullValue(Object.class));
            MatcherAssert.assertThat(StreamDrainer.DEFAULT.drain(element.getInputStream()), Is.is(new byte[]{ 1, 2, 3 }));
            MatcherAssert.assertThat(iterator.hasNext(), Is.is(false));
        } finally {
            origin.close();
        }
    }

    @Test
    public void testOfTypes() throws Exception {
        Plugin.Engine.Source.Origin origin = Engine.ofTypes(PluginEngineSourceInMemoryTest.Foo.class).read();
        try {
            MatcherAssert.assertThat(origin.getClassFileLocator().locate(PluginEngineSourceInMemoryTest.Foo.class.getName()).isResolved(), Is.is(true));
            MatcherAssert.assertThat(origin.getClassFileLocator().locate(PluginEngineSourceInMemoryTest.Foo.class.getName()).resolve(), Is.is(read(PluginEngineSourceInMemoryTest.Foo.class)));
            MatcherAssert.assertThat(origin.getClassFileLocator().locate("qux.Baz").isResolved(), Is.is(false));
            MatcherAssert.assertThat(origin.getManifest(), CoreMatchers.nullValue(Manifest.class));
            Iterator<Plugin.Engine.Source.Element> iterator = origin.iterator();
            MatcherAssert.assertThat(iterator.hasNext(), Is.is(true));
            Plugin.Engine.Source.Element element = iterator.next();
            MatcherAssert.assertThat(element.getName(), Is.is(((PluginEngineSourceInMemoryTest.Foo.class.getName().replace('.', '/')) + ".class")));
            MatcherAssert.assertThat(element.resolveAs(Object.class), CoreMatchers.nullValue(Object.class));
            MatcherAssert.assertThat(StreamDrainer.DEFAULT.drain(element.getInputStream()), Is.is(read(PluginEngineSourceInMemoryTest.Foo.class)));
            MatcherAssert.assertThat(iterator.hasNext(), Is.is(false));
        } finally {
            origin.close();
        }
    }

    @Test
    public void testManifest() throws Exception {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().putValue(MANIFEST_VERSION.toString(), "1.0");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        manifest.write(outputStream);
        Plugin.Engine.Source.Origin origin = new Plugin.Engine.Source.InMemory(Collections.singletonMap(JarFile.MANIFEST_NAME, outputStream.toByteArray())).read();
        try {
            MatcherAssert.assertThat(origin.getClassFileLocator().locate("foo.Bar").isResolved(), Is.is(false));
            MatcherAssert.assertThat(origin.getManifest(), CoreMatchers.notNullValue(Manifest.class));
            MatcherAssert.assertThat(origin.getManifest().getMainAttributes().getValue(MANIFEST_VERSION), Is.is(((Object) ("1.0"))));
        } finally {
            origin.close();
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIteratorNoRemoval() throws Exception {
        Plugin.Engine.Source.Origin origin = new Plugin.Engine.Source.InMemory(Collections.singletonMap("foo/Bar.class", new byte[]{ 1, 2, 3 })).read();
        try {
            Iterator<Plugin.Engine.Source.Element> iterator = origin.iterator();
            MatcherAssert.assertThat(iterator.next(), CoreMatchers.notNullValue(.class));
            iterator.remove();
        } finally {
            origin.close();
        }
    }

    /* empty */
    static class Foo {}
}

