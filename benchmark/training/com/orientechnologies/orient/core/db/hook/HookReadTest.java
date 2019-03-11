package com.orientechnologies.orient.core.db.hook;


import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import static RESULT.RECORD_CHANGED;
import static TYPE.AFTER_READ;


/**
 * Created by tglman on 01/06/16.
 */
public class HookReadTest {
    private ODatabaseDocument database;

    @Test
    public void testSelectChangedInHook() {
        database.registerHook(new ORecordHook() {
            @Override
            public void onUnregister() {
            }

            @Override
            public RESULT onTrigger(TYPE iType, ORecord iRecord) {
                if (iType == (AFTER_READ))
                    field("read", "test");

                return RECORD_CHANGED;
            }

            @Override
            public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
                return null;
            }
        });
        database.save(new ODocument("TestClass"));
        List<ODocument> res = database.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<Object>("select from TestClass"));
        Assert.assertEquals(res.get(0).field("read"), "test");
    }
}

