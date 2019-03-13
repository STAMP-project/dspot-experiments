package org.robolectric.plugins;


import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.robolectric.internal.dependency.DependencyJar;
import org.robolectric.internal.dependency.DependencyResolver;
import org.robolectric.plugins.LegacyDependencyResolver.DefinitelyNotAClassLoader;
import org.robolectric.res.Fs;
import org.robolectric.util.TempDirectory;


@RunWith(JUnit4.class)
public class LegacyDependencyResolverTest {
    private static final String VERSION = "4.3_r2-robolectric-r1";

    private static final DependencyJar DEPENDENCY_COORDS = new DependencyJar("org.robolectric", "android-all", LegacyDependencyResolverTest.VERSION);

    private TempDirectory tempDirectory;

    private Properties properties;

    private DefinitelyNotAClassLoader mockClassLoader;

    @Test
    public void whenRobolectricDepsPropertiesProperty() throws Exception {
        Path depsPath = tempDirectory.createFile("deps.properties", (("org.robolectric\\:android-all\\:" + (LegacyDependencyResolverTest.VERSION)) + ": file-123.jar"));
        Path jarPath = tempDirectory.createFile("file-123.jar", "...");
        properties.setProperty("robolectric-deps.properties", depsPath.toString());
        DependencyResolver resolver = new LegacyDependencyResolver(properties, mockClassLoader);
        URL jarUrl = resolver.getLocalArtifactUrl(LegacyDependencyResolverTest.DEPENDENCY_COORDS);
        assertThat(Fs.fromUrl(jarUrl)).isEqualTo(jarPath);
    }

    @Test
    public void whenRobolectricDepsPropertiesPropertyAndOfflineProperty() throws Exception {
        Path depsPath = tempDirectory.createFile("deps.properties", (("org.robolectric\\:android-all\\:" + (LegacyDependencyResolverTest.VERSION)) + ": file-123.jar"));
        Path jarPath = tempDirectory.createFile("file-123.jar", "...");
        properties.setProperty("robolectric-deps.properties", depsPath.toString());
        properties.setProperty("robolectric.offline", "true");
        DependencyResolver resolver = new LegacyDependencyResolver(properties, mockClassLoader);
        URL jarUrl = resolver.getLocalArtifactUrl(LegacyDependencyResolverTest.DEPENDENCY_COORDS);
        assertThat(Fs.fromUrl(jarUrl)).isEqualTo(jarPath);
    }

    @Test
    public void whenRobolectricDepsPropertiesResource() throws Exception {
        Path depsPath = tempDirectory.createFile("deps.properties", (("org.robolectric\\:android-all\\:" + (LegacyDependencyResolverTest.VERSION)) + ": file-123.jar"));
        Mockito.when(mockClassLoader.getResource("robolectric-deps.properties")).thenReturn(LegacyDependencyResolverTest.meh(depsPath));
        DependencyResolver resolver = new LegacyDependencyResolver(properties, mockClassLoader);
        URL jarUrl = resolver.getLocalArtifactUrl(LegacyDependencyResolverTest.DEPENDENCY_COORDS);
        assertThat(Fs.fromUrl(jarUrl).toString()).endsWith("file-123.jar");
    }

    @Test
    public void whenRobolectricDependencyDirProperty() throws Exception {
        Path jarsPath = tempDirectory.create("jars");
        Path sdkJarPath = tempDirectory.createFile((("jars/android-all-" + (LegacyDependencyResolverTest.VERSION)) + ".jar"), "...");
        properties.setProperty("robolectric.dependency.dir", jarsPath.toString());
        DependencyResolver resolver = new LegacyDependencyResolver(properties, mockClassLoader);
        URL jarUrl = resolver.getLocalArtifactUrl(LegacyDependencyResolverTest.DEPENDENCY_COORDS);
        assertThat(Fs.fromUrl(jarUrl)).isEqualTo(sdkJarPath);
    }

    @Test
    public void whenNoPropertiesOrResourceFile() throws Exception {
        Mockito.when(mockClassLoader.getResource("robolectric-deps.properties")).thenReturn(null);
        Mockito.when(mockClassLoader.loadClass("org.robolectric.plugins.CachedMavenDependencyResolver")).thenReturn(((Class) (LegacyDependencyResolverTest.FakeMavenDependencyResolver.class)));
        DependencyResolver resolver = new LegacyDependencyResolver(properties, mockClassLoader);
        URL jarUrl = resolver.getLocalArtifactUrl(LegacyDependencyResolverTest.DEPENDENCY_COORDS);
        assertThat(Fs.fromUrl(jarUrl)).isEqualTo(Paths.get("/some/fake/file.jar").toAbsolutePath());
    }

    public static class FakeMavenDependencyResolver implements DependencyResolver {
        @Override
        public URL getLocalArtifactUrl(DependencyJar dependency) {
            return LegacyDependencyResolverTest.meh(Paths.get("/some/fake/file.jar"));
        }
    }
}

