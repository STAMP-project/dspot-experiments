package com.orientechnologies.orient.core.sql.executor;


import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Luigi Dell'Aquila (l.dellaquila-(at)-orientdb.com)
 */
public class ODropUserStatementExecutionTest {
    static ODatabaseDocument db;

    @Test
    public void testPlain() {
        String name = "testPlain";
        OResultSet result = ODropUserStatementExecutionTest.db.command("CREATE USER test IDENTIFIED BY foo ROLE admin");
        result.close();
        result = ODropUserStatementExecutionTest.db.query("SELECT name, roles.name as roles FROM OUser WHERE name = 'test'");
        Assert.assertTrue(result.hasNext());
        OResult user = result.next();
        Assert.assertEquals("test", user.getProperty("name"));
        List<String> roles = user.getProperty("roles");
        Assert.assertEquals(1, roles.size());
        Assert.assertEquals("admin", roles.get(0));
        result.close();
        result = ODropUserStatementExecutionTest.db.command("DROP USER test");
        result.close();
        result = ODropUserStatementExecutionTest.db.query("SELECT name, roles.name as roles FROM OUser WHERE name = 'test'");
        Assert.assertFalse(result.hasNext());
        result.close();
    }
}

