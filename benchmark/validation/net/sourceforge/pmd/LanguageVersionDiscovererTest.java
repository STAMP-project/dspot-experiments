/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;


import PLSQLLanguageModule.NAME;
import java.io.File;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import org.junit.Assert;
import org.junit.Test;


public class LanguageVersionDiscovererTest {
    /**
     * Test on PLSQL file with default version
     */
    @Test
    public void testPlsql() {
        LanguageVersionDiscoverer discoverer = new LanguageVersionDiscoverer();
        File plsqlFile = new File("/path/to/MY_PACKAGE.sql");
        LanguageVersion languageVersion = discoverer.getDefaultLanguageVersionForFile(plsqlFile);
        Assert.assertEquals("LanguageVersion must be PLSQL!", LanguageRegistry.getLanguage(NAME).getDefaultVersion(), languageVersion);
    }
}

