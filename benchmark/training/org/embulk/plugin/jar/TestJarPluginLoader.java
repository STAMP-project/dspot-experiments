package org.embulk.plugin.jar;


import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import org.embulk.EmbulkTestRuntime;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class TestJarPluginLoader {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public EmbulkTestRuntime testRuntime = new EmbulkTestRuntime();

    @Test
    public void test() throws Exception {
        final Path temporaryDirectory = createTemporaryJarDirectory();
        final Path pluginJarPath = createTemporaryPluginJarFile(temporaryDirectory);
        final JarBuilder pluginJarBuilder = new JarBuilder();
        pluginJarBuilder.addManifestV0(ExampleJarSpiV0.class.getName());
        pluginJarBuilder.addClass(ExampleJarSpiV0.class);
        pluginJarBuilder.build(pluginJarPath);
        final Path dependencyJarPath = createTemporaryDependencyJarFile(temporaryDirectory);
        final JarBuilder dependencyJarBuilder = new JarBuilder();
        pluginJarBuilder.addClass(ExampleDependencyJar.class);
        pluginJarBuilder.build(dependencyJarPath);
        final ArrayList<Path> dependencyJarPaths = new ArrayList<>();
        dependencyJarPaths.add(dependencyJarPath);
        final Class<?> loadedClass;
        try (final JarPluginLoader loader = JarPluginLoader.load(pluginJarPath, Collections.unmodifiableList(dependencyJarPaths), testRuntime.getPluginClassLoaderFactory())) {
            Assert.assertEquals(0, loader.getPluginSpiVersion());
            loadedClass = loader.getPluginMainClass();
        }
        final Object instanceObject = loadedClass.newInstance();
        Assert.assertTrue((instanceObject instanceof ExampleJarSpiV0));
        final ExampleJarSpiV0 instance = ((ExampleJarSpiV0) (instanceObject));
        Assert.assertEquals("foobar", instance.getTestString());
        final ExampleDependencyJar dependencyInstance = instance.getDependencyObject();
        Assert.assertEquals("hoge", dependencyInstance.getTestDependencyString());
    }
}

