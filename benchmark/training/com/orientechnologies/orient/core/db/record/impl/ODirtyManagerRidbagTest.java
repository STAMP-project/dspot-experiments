package com.orientechnologies.orient.core.db.record.impl;


import OGlobalConfiguration.RID_BAG_EMBEDDED_TO_SBTREEBONSAI_THRESHOLD;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.ridbag.ORidBag;
import com.orientechnologies.orient.core.record.ORecordInternal;
import com.orientechnologies.orient.core.record.impl.ODirtyManager;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.record.impl.ODocumentInternal;
import org.junit.Assert;
import org.junit.Test;


public class ODirtyManagerRidbagTest {
    private ODatabaseDocument db;

    @Test
    public void testRidBagTree() {
        Object value = RID_BAG_EMBEDDED_TO_SBTREEBONSAI_THRESHOLD.getValue();
        RID_BAG_EMBEDDED_TO_SBTREEBONSAI_THRESHOLD.setValue((-1));
        try {
            ODocument doc = new ODocument();
            doc.field("test", "ddd");
            ORidBag bag = new ORidBag();
            ODocument doc1 = new ODocument();
            bag.add(doc1);
            doc.field("bag", bag);
            ODocumentInternal.convertAllMultiValuesToTrackedVersions(doc);
            ODirtyManager manager = ORecordInternal.getDirtyManager(doc1);
            Assert.assertEquals(2, manager.getNewRecords().size());
        } finally {
            RID_BAG_EMBEDDED_TO_SBTREEBONSAI_THRESHOLD.setValue(value);
        }
    }
}

