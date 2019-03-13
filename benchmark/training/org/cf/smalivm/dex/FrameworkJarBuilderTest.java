package org.cf.smalivm.dex;


import Configuration.FRAMEWORK_CLASSES_PATH;
import SmaliClassLoader.FRAMEWORK_STUBS_JAR;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import org.cf.smalivm.configuration.ConfigurationLoader;
import org.cf.util.ClassNameUtils;
import org.junit.Test;


public class FrameworkJarBuilderTest {
    @Test
    public void frameworkClassesExistAndCanBeLoaded() throws IOException, ClassNotFoundException {
        List<String> lines = ConfigurationLoader.load(FRAMEWORK_CLASSES_PATH);
        URL jarURL = FrameworkJarBuilderTest.class.getResource(FRAMEWORK_STUBS_JAR);
        URLClassLoader jarLoader = new URLClassLoader(new URL[]{ jarURL });
        for (String line : lines) {
            String internalName = line.substring(0, line.indexOf(':'));
            if ((internalName.startsWith("Ljava/")) || (internalName.startsWith("Lsun/security/"))) {
                // Skip loading prohibited package names to avoid error. This isn't ideal, but
                // I can't think of an easy work around. This still loads a majority of classes.
                continue;
            }
            String sourceName = ClassNameUtils.internalToSource(internalName);
            // If the class was generated badly, this should error.
            jarLoader.loadClass(sourceName);
        }
    }
}

