/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.plsql.ast;


import java.util.List;
import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;
import org.junit.Assert;
import org.junit.Test;


public class ASTComparisonConditionTest extends AbstractPLSQLParserTst {
    @Test
    public void testOperator() {
        ASTInput input = parsePLSQL("BEGIN SELECT COUNT(1) INTO MY_TABLE FROM USERS_TABLE WHERE user_id = 1; END;");
        List<ASTComparisonCondition> conditions = input.findDescendantsOfType(ASTComparisonCondition.class);
        Assert.assertEquals(1, conditions.size());
        Assert.assertEquals("=", conditions.get(0).getOperator());
    }
}

