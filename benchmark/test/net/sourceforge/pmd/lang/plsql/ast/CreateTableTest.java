/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.plsql.ast;


import java.nio.charset.StandardCharsets;
import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;


public class CreateTableTest extends AbstractPLSQLParserTst {
    @Test
    public void parseCreateTable() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("CreateTable.pls"), StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
        Assert.assertNotNull(input);
    }
}

