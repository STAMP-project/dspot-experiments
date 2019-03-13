/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.jsp;


import java.nio.file.Paths;
import net.sourceforge.pmd.lang.ast.Node;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for JSP parsing.
 */
public class JspParserTest {
    /**
     * Verifies bug #939 Jsp parser fails on $
     */
    @Test
    public void testParseDollar() {
        Node node = parse("<span class=\"CostUnit\">$</span><span class=\"CostMain\">129</span><span class=\"CostFrac\">.00</span>");
        Assert.assertNotNull(node);
    }

    @Test
    public void testParseELAttribute() {
        Node node = parse("<div ${something ? \'class=\"red\"\' : \'\'}> Div content here.</div>");
        Assert.assertNotNull(node);
    }

    @Test
    public void testParseELAttributeValue() {
        Node node = parse("<div class=\"${something == 0 ? \'zero_something\' : something == 1 ? \'one_something\' : \'other_something\'}\">Div content here.</div>");
        Assert.assertNotNull(node);
    }

    /**
     * Verifies bug #311 Jsp parser fails on boolean attribute
     */
    @Test
    public void testParseBooleanAttribute() {
        Node node = parse("<label><input type='checkbox' checked name=cheese disabled=''> Cheese</label>");
        Assert.assertNotNull(node);
    }

    @Test
    public void testParseJsp() {
        testInternalJspFile(Paths.get("sample.jsp").toFile());
        testInternalJspFile(Paths.get("sample.jspx").toFile());
    }

    @Test
    public void testParseTag() {
        testInternalJspFile(Paths.get("sample.tag").toFile());
    }

    @Test(expected = AssertionError.class)
    public void testParseWrong() {
        testInternalJspFile(Paths.get("sample.xxx").toFile());
    }
}

