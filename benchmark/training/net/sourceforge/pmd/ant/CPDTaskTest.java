/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.ant;


import java.io.File;
import org.apache.tools.ant.BuildFileRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


/**
 *
 *
 * @author Romain Pelisse &lt;belaran@gmail.com&gt;
 */
public class CPDTaskTest {
    @Rule
    public final BuildFileRule buildRule = new BuildFileRule();

    @Test
    public void testBasic() {
        buildRule.executeTarget("testBasic");
        // FIXME: This clearly needs to be improved - but I don't like to write
        // test, so feel free to contribute :)
        Assert.assertTrue(new File("target/cpd.ant.tests").exists());
    }
}

