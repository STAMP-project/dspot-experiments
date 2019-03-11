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
public class OGrantStatementExecutionTest {
    static ODatabaseDocument db;

    @Test
    public void testSimple() {
        ORole testRole = OGrantStatementExecutionTest.db.getMetadata().getSecurity().createRole("testRole", DENY_ALL_BUT);
        Assert.assertFalse(testRole.allow(SERVER, "server", PERMISSION_EXECUTE));
        OGrantStatementExecutionTest.db.command("GRANT execute on server.remove to testRole");
        testRole = OGrantStatementExecutionTest.db.getMetadata().getSecurity().getRole("testRole");
        Assert.assertTrue(testRole.allow(SERVER, "remove", PERMISSION_EXECUTE));
    }
}

