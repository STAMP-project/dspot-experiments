/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.coverage;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.StandardErrorStreamLog;
import org.junit.contrib.java.lang.system.StandardOutputStreamLog;


public class PMDCoverageTest {
    @Rule
    public StandardOutputStreamLog output = new StandardOutputStreamLog();

    @Rule
    public StandardErrorStreamLog errorStream = new StandardErrorStreamLog();

    /**
     * Test some of the PMD command line options
     */
    @Test
    public void testPmdOptions() {
        runPmd("-d src/main/java/net/sourceforge/pmd/lang/java/rule/design -f text -R rulesets/internal/all-java.xml -language java -stress -benchmark");
    }

    /**
     * Name of the configuration file used by testResourceFileCommands().
     */
    private static final String PMD_CONFIG_FILE = "pmd_tests.conf";

    /**
     * Run PMD using the command lines found in PMD_CONFIG_FILE.
     */
    @Test
    public void testResourceFileCommands() {
        InputStream is = getClass().getResourceAsStream(PMDCoverageTest.PMD_CONFIG_FILE);
        if (is != null) {
            try {
                BufferedReader r = new BufferedReader(new InputStreamReader(is));
                String l;
                while ((l = r.readLine()) != null) {
                    l = l.trim();
                    if (((l.length()) == 0) || ((l.charAt(0)) == '#')) {
                        continue;
                    }
                    runPmd(l);
                } 
                r.close();
            } catch (IOException ioe) {
                Assert.fail(("Problem reading config file: " + (ioe.getLocalizedMessage())));
            }
        } else {
            Assert.fail(("Missing config file: " + (PMDCoverageTest.PMD_CONFIG_FILE)));
        }
    }
}

