/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.vf;


import net.sourceforge.pmd.lang.ast.Node;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author sergey.gorbaty
 */
public class VfParserTest {
    @Test
    public void testSingleDoubleQuoteAndEL() {
        Node node = parse("<span escape=\'false\' attrib=\"{!call}\">${!\'yes\'}</span>");
        Assert.assertNotNull(node);
    }

    @Test
    public void testSingleDoubleQuoteAndELFunction() {
        Node node = parse("<span escape=\'false\' attrib=\"{!call}\">${!method}</span>");
        Assert.assertNotNull(node);
    }

    @Test
    public void testSingleDoubleQuote() {
        Node node = parse("<span escape=\'false\' attrib=\"{!call}\">${\"yes\"}</span>");
        Assert.assertNotNull(node);
    }
}

