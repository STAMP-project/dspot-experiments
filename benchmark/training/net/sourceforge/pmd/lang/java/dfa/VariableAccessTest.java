/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.dfa;


import net.sourceforge.pmd.lang.dfa.VariableAccess;
import org.junit.Assert;
import org.junit.Test;


public class VariableAccessTest {
    @Test
    public void testGetVariableName() {
        VariableAccess va = new VariableAccess(VariableAccess.DEFINITION, "foo.bar");
        Assert.assertEquals("foo", va.getVariableName());
        va = new VariableAccess(VariableAccess.DEFINITION, ".foobar");
        Assert.assertEquals("", va.getVariableName());
        va = new VariableAccess(VariableAccess.DEFINITION, "foobar.");
        Assert.assertEquals("foobar", va.getVariableName());
        va = new VariableAccess(VariableAccess.DEFINITION, "foobar");
        Assert.assertEquals("foobar", va.getVariableName());
    }
}

