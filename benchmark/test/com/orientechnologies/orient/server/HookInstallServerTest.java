package com.orientechnologies.orient.server;


import com.orientechnologies.orient.core.db.OPartitionedDatabasePool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.junit.Assert;
import org.junit.Test;

import static DISTRIBUTED_EXECUTION_MODE.TARGET_NODE;


public class HookInstallServerTest {
    private static final String SERVER_DIRECTORY = "./target/dbfactory";

    public static class MyHook extends ODocumentHookAbstract {
        public MyHook() {
        }

        @Override
        public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
            return TARGET_NODE;
        }

        @Override
        public void onRecordAfterCreate(ODocument iDocument) {
            (HookInstallServerTest.count)++;
        }
    }

    private static int count = 0;

    private OServer server;

    @Test
    public void test() {
        final int initValue = HookInstallServerTest.count;
        OPartitionedDatabasePool pool = new OPartitionedDatabasePool("remote:localhost/test", "admin", "admin");
        for (int i = 0; i < 10; i++) {
            ODatabaseDocument some = pool.acquire();
            try {
                some.save(new ODocument("Test").field("entry", i));
            } finally {
                some.close();
            }
        }
        pool.close();
        Assert.assertEquals((initValue + 10), HookInstallServerTest.count);
    }
}

