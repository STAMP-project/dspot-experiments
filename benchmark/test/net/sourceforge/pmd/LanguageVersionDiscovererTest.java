/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;


import VfLanguageModule.NAME;
import java.io.File;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author sergey.gorbaty
 */
public class LanguageVersionDiscovererTest {
    /**
     * Test on VF file.
     */
    @Test
    public void testVFFile() {
        LanguageVersionDiscoverer discoverer = new LanguageVersionDiscoverer();
        File vfFile = new File("/path/to/MyPage.page");
        LanguageVersion languageVersion = discoverer.getDefaultLanguageVersionForFile(vfFile);
        Assert.assertEquals("LanguageVersion must be VF!", LanguageRegistry.getLanguage(NAME).getDefaultVersion(), languageVersion);
    }

    @Test
    public void testComponentFile() {
        LanguageVersionDiscoverer discoverer = new LanguageVersionDiscoverer();
        File vfFile = new File("/path/to/MyPage.component");
        LanguageVersion languageVersion = discoverer.getDefaultLanguageVersionForFile(vfFile);
        Assert.assertEquals("LanguageVersion must be VF!", LanguageRegistry.getLanguage(NAME).getDefaultVersion(), languageVersion);
    }
}

