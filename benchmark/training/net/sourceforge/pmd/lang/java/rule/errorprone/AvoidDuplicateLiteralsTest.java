/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.errorprone;


import AvoidDuplicateLiteralsRule.ExceptionParser;
import java.util.Set;
import net.sourceforge.pmd.testframework.PmdRuleTst;
import org.junit.Assert;
import org.junit.Test;


public class AvoidDuplicateLiteralsTest extends PmdRuleTst {
    @Test
    public void testStringParserEmptyString() {
        AvoidDuplicateLiteralsRule.ExceptionParser p = new AvoidDuplicateLiteralsRule.ExceptionParser(',');
        Set<String> res = p.parse("");
        Assert.assertTrue(res.isEmpty());
    }

    @Test
    public void testStringParserSimple() {
        AvoidDuplicateLiteralsRule.ExceptionParser p = new AvoidDuplicateLiteralsRule.ExceptionParser(',');
        Set<String> res = p.parse("a,b,c");
        Assert.assertEquals(3, res.size());
        Assert.assertTrue(res.contains("a"));
        Assert.assertTrue(res.contains("b"));
        Assert.assertTrue(res.contains("c"));
    }

    @Test
    public void testStringParserEscapedChar() {
        AvoidDuplicateLiteralsRule.ExceptionParser p = new AvoidDuplicateLiteralsRule.ExceptionParser(',');
        Set<String> res = p.parse("a,b,\\,");
        Assert.assertEquals(3, res.size());
        Assert.assertTrue(res.contains("a"));
        Assert.assertTrue(res.contains("b"));
        Assert.assertTrue(res.contains(","));
    }

    @Test
    public void testStringParserEscapedEscapedChar() {
        AvoidDuplicateLiteralsRule.ExceptionParser p = new AvoidDuplicateLiteralsRule.ExceptionParser(',');
        Set<String> res = p.parse("a,b,\\\\");
        Assert.assertEquals(3, res.size());
        Assert.assertTrue(res.contains("a"));
        Assert.assertTrue(res.contains("b"));
        Assert.assertTrue(res.contains("\\"));
    }
}

