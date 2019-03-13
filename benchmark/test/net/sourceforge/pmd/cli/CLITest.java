/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cli;


import java.io.File;
import net.sourceforge.pmd.util.FileUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Romain Pelisse &lt;belaran@gmail.com&gt;
 */
public class CLITest extends BaseCLITest {
    @Test
    public void useEcmaScript() {
        String[] args = new String[]{ "-d", SOURCE_FOLDER, "-f", "xml", "-R", "ecmascript-basic", "-version", "3", "-l", "ecmascript", "-debug" };
        String resultFilename = runTest(args, "useEcmaScript");
        Assert.assertTrue("Invalid JavaScript version", FileUtil.findPatternInFile(new File(resultFilename), "Using Ecmascript version: Ecmascript 3"));
    }
}

