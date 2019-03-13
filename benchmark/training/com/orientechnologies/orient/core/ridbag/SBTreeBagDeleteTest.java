package com.orientechnologies.orient.core.ridbag;


import OGlobalConfiguration.INDEX_EMBEDDED_TO_SBTREEBONSAI_THRESHOLD;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.db.record.ridbag.ORidBag;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.storage.index.sbtreebonsai.local.OSBTreeBonsai;
import com.orientechnologies.orient.core.storage.ridbag.sbtree.OBonsaiCollectionPointer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 01/07/16.
 */
public class SBTreeBagDeleteTest {
    private ODatabaseDocumentInternal db;

    @Test
    public void testDeleteRidbagTx() {
        ODocument doc = new ODocument();
        ORidBag bag = new ORidBag();
        int size = (INDEX_EMBEDDED_TO_SBTREEBONSAI_THRESHOLD.getValueAsInteger()) * 2;
        for (int i = 0; i < size; i++)
            bag.add(new ORecordId(10, i));

        doc.field("bag", bag);
        ORID id = db.save(doc, db.getClusterNameById(db.getDefaultClusterId())).getIdentity();
        bag = doc.field("bag");
        OBonsaiCollectionPointer pointer = bag.getPointer();
        db.begin();
        db.delete(doc);
        db.commit();
        doc = db.load(id);
        Assert.assertNull(doc);
        clear();
        OSBTreeBonsai<OIdentifiable, Integer> tree = db.getSbTreeCollectionManager().loadSBTree(pointer);
        Assert.assertNull(tree);
    }

    @Test
    public void testDeleteRidbagNoTx() {
        ODocument doc = new ODocument();
        ORidBag bag = new ORidBag();
        int size = (INDEX_EMBEDDED_TO_SBTREEBONSAI_THRESHOLD.getValueAsInteger()) * 2;
        for (int i = 0; i < size; i++)
            bag.add(new ORecordId(10, i));

        doc.field("bag", bag);
        ORID id = db.save(doc, db.getClusterNameById(db.getDefaultClusterId())).getIdentity();
        bag = doc.field("bag");
        OBonsaiCollectionPointer pointer = bag.getPointer();
        db.delete(doc);
        doc = db.load(id);
        Assert.assertNull(doc);
        clear();
        OSBTreeBonsai<OIdentifiable, Integer> tree = db.getSbTreeCollectionManager().loadSBTree(pointer);
        Assert.assertNull(tree);
    }
}

