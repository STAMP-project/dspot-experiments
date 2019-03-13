package org.cf.smalivm.configuration;


import Configuration.FRAMEWORK_CLASSES_PATH;
import Configuration.IMMUTABLE_CLASSES_PATH;
import Configuration.SAFE_CLASSES_PATH;
import Configuration.SAFE_FRAMEWORK_CLASSES_PATH;
import SmaliClassLoader.FRAMEWORK_STUBS_JAR;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;


public class ConfigurationTest {
    private Configuration configuration;

    @Test
    public void stringIsImmutable() {
        Assert.assertTrue(configuration.isImmutable("Ljava/lang/String;"));
    }

    @Test
    public void stringArrayIsImmutable() {
        Assert.assertFalse(configuration.isImmutable("[Ljava/lang/String;"));
    }

    @Test
    public void stringBuilderIsImmutable() {
        Assert.assertFalse(configuration.isImmutable("Ljava/lang/StringBuilder;"));
    }

    @Test
    public void safeClassesCanBeLoaded() throws IOException, ClassNotFoundException {
        ensureClassesExist(SAFE_CLASSES_PATH);
    }

    @Test
    public void immutableClassesCanBeLoaded() throws IOException, ClassNotFoundException {
        List<String> lines = ConfigurationLoader.load(IMMUTABLE_CLASSES_PATH);
        // Ignore primitives and inner classes
        List<String> classNames = lines.stream().filter(( l) -> (l.startsWith("L")) && (!(l.contains("$")))).collect(Collectors.toList());
        ensureClassesExist(classNames, IMMUTABLE_CLASSES_PATH, FRAMEWORK_STUBS_JAR);
    }

    @Test
    public void safeFrameworkClassesExist() throws IOException, ClassNotFoundException {
        List<String> lines = ConfigurationLoader.load(SAFE_FRAMEWORK_CLASSES_PATH);
        ensureClassesExist(lines, SAFE_FRAMEWORK_CLASSES_PATH, FRAMEWORK_STUBS_JAR);
    }

    @Test
    public void frameworkClassesExist() throws IOException, ClassNotFoundException {
        List<String> lines = ConfigurationLoader.load(FRAMEWORK_CLASSES_PATH);
        List<String> configClasses = lines.stream().map(( l) -> l.substring(0, l.indexOf(':'))).collect(Collectors.toList());
        ensureClassesExist(configClasses, FRAMEWORK_CLASSES_PATH, FRAMEWORK_STUBS_JAR);
    }
}

