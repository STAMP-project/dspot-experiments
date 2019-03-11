package com.orientechnologies.orient.server.tx;


import ODatabaseType.MEMORY;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import com.orientechnologies.orient.server.OServer;
import org.junit.Assert;
import org.junit.Test;

import static DISTRIBUTED_EXECUTION_MODE.SOURCE_NODE;
import static RESULT.RECORD_NOT_CHANGED;


/**
 * Created by tglman on 23/05/17.
 */
public class RemoteTransactionHookTest {
    private static final String SERVER_DIRECTORY = "./target/hook-transaction";

    private OServer server;

    private OrientDB orientDB;

    private ODatabaseDocument database;

    @Test
    public void testCalledInClientTx() {
        OrientDB orientDB = new OrientDB("embedded:", OrientDBConfig.defaultConfig());
        orientDB.create("test", MEMORY);
        ODatabaseDocument database = orientDB.open("test", "admin", "admin");
        RemoteTransactionHookTest.CountCallHook calls = new RemoteTransactionHookTest.CountCallHook(database);
        database.registerHook(calls);
        database.createClassIfNotExist("SomeTx");
        database.begin();
        ODocument doc = new ODocument("SomeTx");
        doc.setProperty("name", "some");
        database.save(doc);
        database.command("insert into SomeTx set name='aa' ").close();
        OResultSet res = database.command("update SomeTx set name=\'bb\' where name=\"some\"");
        Assert.assertEquals(((Long) (1L)), ((Long) (res.next().getProperty("count"))));
        res.close();
        database.command("delete from SomeTx where name='aa'").close();
        database.commit();
        Assert.assertEquals(2, calls.getBeforeCreate());
        Assert.assertEquals(2, calls.getAfterCreate());
        Assert.assertEquals(1, calls.getBeforeUpdate());
        Assert.assertEquals(1, calls.getAfterUpdate());
        Assert.assertEquals(1, calls.getBeforeDelete());
        Assert.assertEquals(1, calls.getAfterDelete());
        database.close();
        orientDB.close();
        this.database.activateOnCurrentThread();
    }

    @Test
    public void testCalledInTxServer() {
        database.begin();
        ODocument doc = new ODocument("SomeTx");
        doc.setProperty("name", "some");
        database.save(doc);
        database.command("insert into SomeTx set name='aa' ").close();
        OResultSet res = database.command("update SomeTx set name=\'bb\' where name=\"some\"");
        Assert.assertEquals(((Long) (1L)), ((Long) (res.next().getProperty("count"))));
        res.close();
        database.command("delete from SomeTx where name='aa'").close();
        database.commit();
        RemoteTransactionHookTest.CountCallHookServer calls = RemoteTransactionHookTest.CountCallHookServer.instance;
        Assert.assertEquals(2, calls.getBeforeCreate());
        Assert.assertEquals(2, calls.getAfterCreate());
        Assert.assertEquals(1, calls.getBeforeUpdate());
        Assert.assertEquals(1, calls.getAfterUpdate());
        Assert.assertEquals(1, calls.getBeforeDelete());
        Assert.assertEquals(1, calls.getAfterDelete());
    }

    public static class CountCallHookServer extends RemoteTransactionHookTest.CountCallHook {
        public CountCallHookServer(ODatabaseDocument database) {
            super(database);
            RemoteTransactionHookTest.CountCallHookServer.instance = this;
        }

        public static RemoteTransactionHookTest.CountCallHookServer instance;
    }

    public static class CountCallHook extends ODocumentHookAbstract {
        private int beforeCreate = 0;

        private int beforeUpdate = 0;

        private int beforeDelete = 0;

        private int afterUpdate = 0;

        private int afterCreate = 0;

        private int afterDelete = 0;

        public CountCallHook(ODatabaseDocument database) {
            super(database);
        }

        @Override
        public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
            return SOURCE_NODE;
        }

        @Override
        public RESULT onRecordBeforeCreate(ODocument iDocument) {
            (beforeCreate)++;
            return RECORD_NOT_CHANGED;
        }

        @Override
        public void onRecordAfterCreate(ODocument iDocument) {
            (afterCreate)++;
        }

        @Override
        public RESULT onRecordBeforeUpdate(ODocument iDocument) {
            (beforeUpdate)++;
            return RECORD_NOT_CHANGED;
        }

        @Override
        public void onRecordAfterUpdate(ODocument iDocument) {
            (afterUpdate)++;
        }

        @Override
        public RESULT onRecordBeforeDelete(ODocument iDocument) {
            (beforeDelete)++;
            return RECORD_NOT_CHANGED;
        }

        @Override
        public void onRecordAfterDelete(ODocument iDocument) {
            (afterDelete)++;
        }

        public int getAfterCreate() {
            return afterCreate;
        }

        public int getAfterDelete() {
            return afterDelete;
        }

        public int getAfterUpdate() {
            return afterUpdate;
        }

        public int getBeforeCreate() {
            return beforeCreate;
        }

        public int getBeforeDelete() {
            return beforeDelete;
        }

        public int getBeforeUpdate() {
            return beforeUpdate;
        }
    }
}

