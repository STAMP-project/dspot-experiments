/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;


import java.io.File;
import org.junit.Assert;
import org.junit.Test;


public class RuleContextTest {
    @Test
    public void testReport() {
        RuleContext ctx = new RuleContext();
        Assert.assertEquals(0, ctx.getReport().size());
        Report r = new Report();
        ctx.setReport(r);
        Report r2 = ctx.getReport();
        Assert.assertEquals("report object mismatch", r, r2);
    }

    @Test
    public void testSourceCodeFilename() {
        RuleContext ctx = new RuleContext();
        Assert.assertNull("filename should be null", ctx.getSourceCodeFilename());
        ctx.setSourceCodeFilename("foo");
        Assert.assertEquals("filename mismatch", "foo", ctx.getSourceCodeFilename());
    }

    @Test
    public void testSourceCodeFile() {
        RuleContext ctx = new RuleContext();
        Assert.assertNull("file should be null", ctx.getSourceCodeFile());
        ctx.setSourceCodeFile(new File("somefile.java"));
        Assert.assertEquals("filename mismatch", new File("somefile.java"), ctx.getSourceCodeFile());
    }

    @Test
    public void testAttributes() {
        RuleContext ctx1 = new RuleContext();
        Object obj1 = new Object();
        Object obj2 = new Object();
        Assert.assertNull("attribute should be null", ctx1.getAttribute("attribute"));
        boolean set = ctx1.setAttribute("attribute", obj1);
        Assert.assertTrue("attribute should have been set", set);
        Assert.assertNotNull("attribute should not be null", ctx1.getAttribute("attribute"));
        Assert.assertSame("attribute should be expected instance", ctx1.getAttribute("attribute"), obj1);
        set = ctx1.setAttribute("attribute", obj2);
        Assert.assertFalse("attribute should not have been set", set);
        Assert.assertSame("attribute should be expected instance", ctx1.getAttribute("attribute"), obj1);
        Object value = ctx1.removeAttribute("attribute");
        Assert.assertSame("attribute value should be expected instance", value, obj1);
        Assert.assertNull("attribute should be null", ctx1.getAttribute("attribute"));
    }

    @Test
    public void testSharedAttributes() {
        RuleContext ctx1 = new RuleContext();
        RuleContext ctx2 = new RuleContext(ctx1);
        StringBuilder obj1 = new StringBuilder();
        StringBuilder obj2 = new StringBuilder();
        ctx1.setAttribute("attribute1", obj1);
        ctx2.setAttribute("attribute2", obj2);
        Assert.assertNotNull("attribute should not be null", ctx1.getAttribute("attribute1"));
        Assert.assertNotNull("attribute should not be null", ctx1.getAttribute("attribute2"));
        Assert.assertNotNull("attribute should not be null", ctx2.getAttribute("attribute1"));
        Assert.assertNotNull("attribute should not be null", ctx2.getAttribute("attribute2"));
        Assert.assertSame("attribute should be expected instance", ctx1.getAttribute("attribute1"), obj1);
        Assert.assertSame("attribute should be expected instance", ctx1.getAttribute("attribute2"), obj2);
        Assert.assertSame("attribute should be expected instance", ctx2.getAttribute("attribute1"), obj1);
        Assert.assertSame("attribute should be expected instance", ctx2.getAttribute("attribute2"), obj2);
        ctx1.removeAttribute("attribute1");
        Assert.assertNull("attribute should be null", ctx1.getAttribute("attribute1"));
        Assert.assertNull("attribute should be null", ctx2.getAttribute("attribute1"));
        Assert.assertNotNull("attribute should not be null", ctx1.getAttribute("attribute2"));
        Assert.assertNotNull("attribute should not be null", ctx2.getAttribute("attribute2"));
        StringBuilder value = ((StringBuilder) (ctx1.getAttribute("attribute2")));
        Assert.assertEquals("attribute value should be empty", "", value.toString());
        value.append("x");
        StringBuilder value1 = ((StringBuilder) (ctx1.getAttribute("attribute2")));
        Assert.assertEquals("attribute value should be 'x'", "x", value1.toString());
        StringBuilder value2 = ((StringBuilder) (ctx2.getAttribute("attribute2")));
        Assert.assertEquals("attribute value should be 'x'", "x", value2.toString());
    }
}

