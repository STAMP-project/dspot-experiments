package com.orientechnologies.orient.core.db.hook;


import OClass.INDEX_TYPE.NOTUNIQUE;
import OType.INTEGER;
import OType.STRING;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;
import java.util.List;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;

import static DISTRIBUTED_EXECUTION_MODE.BOTH;
import static DISTRIBUTED_EXECUTION_MODE.SOURCE_NODE;


public class CheckHookCallCountTest {
    private final String CLASS_NAME = "Data";

    private final String FIELD_ID = "ID";

    private final String FIELD_STATUS = "STATUS";

    private final String STATUS = "processed";

    @Test
    public void testMultipleCallHook() {
        ODatabaseDocument db = new ODatabaseDocumentTx(("memory:" + (CheckHookCallCountTest.class.getSimpleName())));
        db.create();
        try {
            OClass aClass = db.getMetadata().getSchema().createClass(CLASS_NAME);
            aClass.createProperty(FIELD_ID, STRING);
            aClass.createProperty(FIELD_STATUS, STRING);
            aClass.createIndex("IDX", NOTUNIQUE, FIELD_ID);
            CheckHookCallCountTest.TestHook hook = new CheckHookCallCountTest.TestHook();
            db.registerHook(hook);
            String id = UUID.randomUUID().toString();
            ODocument first = new ODocument(CLASS_NAME);
            first.field(FIELD_ID, id);
            first.field(FIELD_STATUS, STATUS);
            db.save(first);
            System.out.println("WITHOUT INDEX: onRecordAfterRead will be called twice");
            db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>((((((("SELECT FROM " + (CLASS_NAME)) + " WHERE ") + (FIELD_STATUS)) + " = '") + (STATUS)) + "'")));
            Assert.assertEquals(hook.readCount, 1);
            hook.readCount = 0;
            System.out.println("WITH INDEX: onRecordAfterRead will be called only once");
            db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>((((((("SELECT FROM " + (CLASS_NAME)) + " WHERE ") + (FIELD_ID)) + " = '") + id) + "'")));
            Assert.assertEquals(hook.readCount, 1);
        } finally {
            db.drop();
        }
    }

    @Test
    public void testInHook() throws Exception {
        ODatabaseDocument db = new ODatabaseDocumentTx(("memory:" + (CheckHookCallCountTest.class.getSimpleName())));
        db.create();
        try {
            OSchema schema = db.getMetadata().getSchema();
            OClass oClass = schema.createClass("TestInHook");
            oClass.createProperty("a", INTEGER);
            oClass.createProperty("b", INTEGER);
            oClass.createProperty("c", INTEGER);
            ODocument doc = new ODocument(oClass);
            doc.field("a", 2);
            doc.field("b", 2);
            doc.save();
            doc.reload();
            Assert.assertEquals(Integer.valueOf(2), doc.field("a"));
            Assert.assertEquals(Integer.valueOf(2), doc.field("b"));
            Assert.assertNull(doc.field("c"));
            db.registerHook(new ODocumentHookAbstract(db) {
                {
                    setIncludeClasses("TestInHook");
                }

                @Override
                public void onRecordAfterCreate(ODocument iDocument) {
                    onRecordAfterRead(iDocument);
                }

                @Override
                public void onRecordAfterRead(ODocument iDocument) {
                    String script = "select sum(a, b) as value from " + (iDocument.getIdentity());
                    List<ODocument> calculated = database.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<Object>(script));
                    if ((calculated != null) && (!(calculated.isEmpty()))) {
                        iDocument.field("c", calculated.get(0).<Object>field("value"));
                    }
                }

                @Override
                public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
                    return SOURCE_NODE;
                }
            });
            doc.reload();
            Assert.assertEquals(Integer.valueOf(2), doc.field("a"));
            Assert.assertEquals(Integer.valueOf(2), doc.field("b"));
            Assert.assertEquals(Integer.valueOf(4), doc.field("c"));
            doc = new ODocument(oClass);
            doc.field("a", 3);
            doc.field("b", 3);
            doc.save();// FAILING here: infinite recursion

            Assert.assertEquals(Integer.valueOf(3), doc.field("a"));
            Assert.assertEquals(Integer.valueOf(3), doc.field("b"));
            Assert.assertEquals(Integer.valueOf(6), doc.field("c"));
        } finally {
            db.drop();
        }
    }

    public class TestHook extends ODocumentHookAbstract {
        public int readCount;

        @Override
        public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
            return BOTH;
        }

        @Override
        public void onRecordAfterRead(ODocument iDocument) {
            (readCount)++;
        }
    }
}

