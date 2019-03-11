/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast;


import java.util.Set;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ParserTstUtil;
import org.junit.Assert;
import org.junit.Test;


public class ASTPrimarySuffixTest {
    @Test
    public void testArrayDereference() {
        Set<ASTPrimarySuffix> ops = ParserTstUtil.getNodes(ASTPrimarySuffix.class, ASTPrimarySuffixTest.TEST1);
        Assert.assertTrue(ops.iterator().next().isArrayDereference());
    }

    @Test
    public void testArguments() {
        Set<ASTPrimarySuffix> ops = ParserTstUtil.getNodes(ASTPrimarySuffix.class, ASTPrimarySuffixTest.TEST2);
        Assert.assertTrue(ops.iterator().next().isArguments());
    }

    private static final String TEST1 = ((("public class Foo {" + (PMD.EOL)) + "  {x[0] = 2;}") + (PMD.EOL)) + "}";

    private static final String TEST2 = ((("public class Foo {" + (PMD.EOL)) + "  {foo(a);}") + (PMD.EOL)) + "}";
}

