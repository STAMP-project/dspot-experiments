/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;


import JavaLanguageModule.NAME;
import java.io.File;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import org.junit.Assert;
import org.junit.Test;


public class LanguageVersionDiscovererTest {
    /**
     * Test on Java file with default options.
     */
    @Test
    public void testJavaFileUsingDefaults() {
        LanguageVersionDiscoverer discoverer = new LanguageVersionDiscoverer();
        File javaFile = new File("/path/to/MyClass.java");
        LanguageVersion languageVersion = discoverer.getDefaultLanguageVersionForFile(javaFile);
        Assert.assertEquals("LanguageVersion must be Java 11 !", LanguageRegistry.getLanguage(NAME).getVersion("11"), languageVersion);
    }

    /**
     * Test on Java file with Java version set to 1.4.
     */
    @Test
    public void testJavaFileUsing14() {
        LanguageVersionDiscoverer discoverer = new LanguageVersionDiscoverer();
        discoverer.setDefaultLanguageVersion(LanguageRegistry.getLanguage(NAME).getVersion("1.4"));
        File javaFile = new File("/path/to/MyClass.java");
        LanguageVersion languageVersion = discoverer.getDefaultLanguageVersionForFile(javaFile);
        Assert.assertEquals("LanguageVersion must be Java 1.4!", LanguageRegistry.getLanguage(NAME).getVersion("1.4"), languageVersion);
    }

    @Test
    public void testLanguageVersionDiscoverer() {
        PMDConfiguration configuration = new PMDConfiguration();
        LanguageVersionDiscoverer languageVersionDiscoverer = configuration.getLanguageVersionDiscoverer();
        Assert.assertEquals("Default Java version", LanguageRegistry.getLanguage(NAME).getVersion("11"), languageVersionDiscoverer.getDefaultLanguageVersion(LanguageRegistry.getLanguage(NAME)));
        configuration.setDefaultLanguageVersion(LanguageRegistry.getLanguage(NAME).getVersion("1.5"));
        Assert.assertEquals("Modified Java version", LanguageRegistry.getLanguage(NAME).getVersion("1.5"), languageVersionDiscoverer.getDefaultLanguageVersion(LanguageRegistry.getLanguage(NAME)));
    }
}

