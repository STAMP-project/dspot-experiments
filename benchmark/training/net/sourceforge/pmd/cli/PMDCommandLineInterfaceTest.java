/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cli;


import PMDCommandLineInterface.NO_EXIT_AFTER_RUN;
import PMDCommandLineInterface.STATUS_CODE_PROPERTY;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.cache.NoopAnalysisCache;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;


/**
 * Unit test for {@link PMDCommandLineInterface}
 */
public class PMDCommandLineInterfaceTest {
    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    // Restores system properties after test
    @Rule
    public final RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties();

    @Test
    public void testProperties() {
        PMDParameters params = new PMDParameters();
        String[] args = new String[]{ "-d", "source_folder", "-f", "yahtml", "-P", "outputDir=output_folder", "-R", "java-empty" };
        PMDCommandLineInterface.extractParameters(params, args, "PMD");
        Assert.assertEquals("output_folder", params.getProperties().getProperty("outputDir"));
    }

    @Test
    public void testMultipleProperties() {
        PMDParameters params = new PMDParameters();
        String[] args = new String[]{ "-d", "source_folder", "-f", "ideaj", "-P", "sourcePath=/home/user/source/", "-P", "fileName=Foo.java", "-P", "classAndMethodName=Foo.method", "-R", "java-empty" };
        PMDCommandLineInterface.extractParameters(params, args, "PMD");
        Assert.assertEquals("/home/user/source/", params.getProperties().getProperty("sourcePath"));
        Assert.assertEquals("Foo.java", params.getProperties().getProperty("fileName"));
        Assert.assertEquals("Foo.method", params.getProperties().getProperty("classAndMethodName"));
    }

    @Test
    public void testNoCacheSwitch() {
        PMDParameters params = new PMDParameters();
        String[] args = new String[]{ "-d", "source_folder", "-f", "ideaj", "-R", "java-empty", "-cache", "/home/user/.pmd/cache", "-no-cache" };
        PMDCommandLineInterface.extractParameters(params, args, "PMD");
        Assert.assertTrue(params.isIgnoreIncrementalAnalysis());
        PMDConfiguration config = params.toConfiguration();
        Assert.assertTrue(config.isIgnoreIncrementalAnalysis());
        Assert.assertTrue(((config.getAnalysisCache()) instanceof NoopAnalysisCache));
    }

    @Test
    public void testSetStatusCodeOrExitDoExit() {
        exit.expectSystemExitWithStatus(0);
        PMDCommandLineInterface.setStatusCodeOrExit(0);
    }

    @Test
    public void testSetStatusCodeOrExitSetStatus() {
        System.setProperty(NO_EXIT_AFTER_RUN, "1");
        PMDCommandLineInterface.setStatusCodeOrExit(0);
        Assert.assertEquals(System.getProperty(STATUS_CODE_PROPERTY), "0");
    }
}

