package org.embulk.plugin.jar;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import org.embulk.EmbulkTestRuntime;
import org.embulk.plugin.PluginClassLoader;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Test cases in this class depend on JARs built by "test-helpers" project.
 *
 * See "deployJarsForEmbulkTestMavenPlugin" task in test-helpers/build.gradle for more details
 */
public class TestJarPluginLoader {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public EmbulkTestRuntime testRuntime = new EmbulkTestRuntime();

    @Test
    public void testLoadPluginClassForFlatJar() throws Exception {
        final Path pluginJarPath = TestJarPluginLoader.BUILT_JARS_DIR.resolve("embulk-test-maven-plugin-flat.jar");
        final Path dependencyJarPath = TestJarPluginLoader.BUILT_JARS_DIR.resolve("embulk-test-maven-plugin-deps.jar");
        final ArrayList<Path> dependencyJarPaths = new ArrayList<>();
        dependencyJarPaths.add(dependencyJarPath);
        final Class<?> loadedClass;
        try (final JarPluginLoader loader = JarPluginLoader.load(pluginJarPath, Collections.unmodifiableList(dependencyJarPaths), testRuntime.getPluginClassLoaderFactory())) {
            Assert.assertEquals(0, loader.getPluginSpiVersion());
            loadedClass = loader.getPluginMainClass();
        }
        final ClassLoader classLoader = loadedClass.getClassLoader();
        Assert.assertTrue((classLoader instanceof PluginClassLoader));
        TestJarPluginLoader.verifyMainClass(loadedClass);
        Assert.assertEquals("Hello", TestJarPluginLoader.readResource(classLoader, "embulk-test-maven-plugin/main.txt"));
        Assert.assertEquals("World", TestJarPluginLoader.readResource(classLoader, "embulk-test-maven-plugin/deps.txt"));
    }

    // TODO: Remove the feature soon (see: https://github.com/embulk/embulk/issues/1110)
    @Test
    public void testLoadPluginClassForNestedJar() throws Exception {
        final Path pluginJarPath = TestJarPluginLoader.BUILT_JARS_DIR.resolve("embulk-test-maven-plugin-nested.jar");
        final Class<?> loadedClass;
        try (final JarPluginLoader loader = JarPluginLoader.load(pluginJarPath, Collections.emptyList(), testRuntime.getPluginClassLoaderFactory())) {
            Assert.assertEquals(0, loader.getPluginSpiVersion());
            loadedClass = loader.getPluginMainClass();
        }
        final ClassLoader classLoader = loadedClass.getClassLoader();
        Assert.assertTrue((classLoader instanceof PluginClassLoader));
        TestJarPluginLoader.verifyMainClass(loadedClass);
        // Probably a bug of ClassLoader, but as the nested style will be removed soon, so keep the behavior as-is.
        Assert.assertNull(classLoader.getResourceAsStream("embulk-test-maven-plugin/main.txt"));
        Assert.assertNull(classLoader.getResourceAsStream("embulk-test-maven-plugin/deps.txt"));
    }

    private static Path EMBULK_ROOT_DIR = Paths.get(System.getProperty("user.dir")).getParent();

    private static Path BUILT_JARS_DIR = TestJarPluginLoader.EMBULK_ROOT_DIR.resolve("test-helpers").resolve("build").resolve("embulk-test-maven-plugin");
}

