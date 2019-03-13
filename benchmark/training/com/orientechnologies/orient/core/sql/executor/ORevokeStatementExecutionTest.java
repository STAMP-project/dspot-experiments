package com.orientechnologies.orient.core.sql.executor;


import ORole.PERMISSION_EXECUTE;
import ORule.ResourceGeneric.SERVER;
import OSecurityRole.ALLOW_MODES.DENY_ALL_BUT;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.security.ORole;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Luigi Dell'Aquila (l.dellaquila-(at)-orientdb.com)
 */
public class ORevokeStatementExecutionTest {
    static ODatabaseDocument db;

    @Test
    public void testSimple() {
        ORole testRole = ORevokeStatementExecutionTest.db.getMetadata().getSecurity().createRole("testRole", DENY_ALL_BUT);
        Assert.assertFalse(testRole.allow(SERVER, "server", PERMISSION_EXECUTE));
        ORevokeStatementExecutionTest.db.command("GRANT execute on server.remove to testRole");
        testRole = ORevokeStatementExecutionTest.db.getMetadata().getSecurity().getRole("testRole");
        Assert.assertTrue(testRole.allow(SERVER, "remove", PERMISSION_EXECUTE));
        ORevokeStatementExecutionTest.db.command("REVOKE execute on server.remove from testRole");
        testRole = ORevokeStatementExecutionTest.db.getMetadata().getSecurity().getRole("testRole");
        Assert.assertFalse(testRole.allow(SERVER, "remove", PERMISSION_EXECUTE));
    }
}

