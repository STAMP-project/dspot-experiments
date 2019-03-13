/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.ant;


import java.io.File;
import net.sourceforge.pmd.renderers.CSVRenderer;
import net.sourceforge.pmd.renderers.HTMLRenderer;
import net.sourceforge.pmd.renderers.TextRenderer;
import net.sourceforge.pmd.renderers.XMLRenderer;
import org.junit.Assert;
import org.junit.Test;


public class FormatterTest {
    @Test
    public void testType() {
        Formatter f = new Formatter();
        f.setType("xml");
        Assert.assertTrue(((f.createRenderer()) instanceof XMLRenderer));
        f.setType("text");
        Assert.assertTrue(((f.createRenderer()) instanceof TextRenderer));
        f.setType("csv");
        Assert.assertTrue(((f.createRenderer()) instanceof CSVRenderer));
        f.setType("html");
        Assert.assertTrue(((f.createRenderer()) instanceof HTMLRenderer));
        try {
            f.setType("FAIL");
            f.createRenderer();
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException be) {
            Assert.assertTrue(be.getMessage().startsWith("Can't find the custom format FAIL"));
        }
    }

    @Test
    public void testNull() {
        Formatter f = new Formatter();
        Assert.assertTrue("Formatter toFile should start off null!", f.isNoOutputSupplied());
        f.setToFile(new File("foo"));
        Assert.assertFalse("Formatter toFile should not be null!", f.isNoOutputSupplied());
    }
}

