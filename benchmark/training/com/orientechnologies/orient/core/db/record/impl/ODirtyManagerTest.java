package com.orientechnologies.orient.core.db.record.impl;


import OType.EMBEDDED;
import OType.EMBEDDEDLIST;
import OType.EMBEDDEDMAP;
import OType.EMBEDDEDSET;
import OType.LINKLIST;
import OType.LINKMAP;
import OType.LINKSET;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.db.record.ridbag.ORidBag;
import com.orientechnologies.orient.core.record.ORecordInternal;
import com.orientechnologies.orient.core.record.impl.ODirtyManager;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.record.impl.ODocumentInternal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;


public class ODirtyManagerTest {
    public ODirtyManagerTest() {
    }

    @Test
    public void testBasic() {
        ODocument doc = new ODocument();
        doc.field("test", "ddd");
        ODirtyManager manager = ORecordInternal.getDirtyManager(doc);
        Assert.assertEquals(1, manager.getNewRecords().size());
    }

    @Test
    public void testEmbeddedDocument() {
        ODocument doc = new ODocument();
        ODocument doc1 = new ODocument();
        doc.field("test", doc1, EMBEDDED);
        ODocument doc2 = new ODocument();
        doc1.field("test2", doc2);
        ODirtyManager manager = ORecordInternal.getDirtyManager(doc);
        Assert.assertEquals(2, manager.getNewRecords().size());
        Assert.assertEquals(1, manager.getPointed(doc).size());
        Assert.assertEquals(doc2, manager.getPointed(doc).get(0));
    }

    @Test
    public void testLink() {
        ODocument doc = new ODocument();
        doc.field("test", "ddd");
        ODocument doc2 = new ODocument();
        doc.field("test1", doc2);
        ODirtyManager manager = ORecordInternal.getDirtyManager(doc);
        Assert.assertEquals(2, manager.getNewRecords().size());
        Assert.assertEquals(1, manager.getPointed(doc).size());
        Assert.assertEquals(doc2, manager.getPointed(doc).get(0));
    }

    @Test
    public void testRemoveLink() {
        ODocument doc = new ODocument();
        doc.field("test", "ddd");
        ODocument doc2 = new ODocument();
        doc.field("test1", doc2);
        doc.removeField("test1");
        ODirtyManager manager = ORecordInternal.getDirtyManager(doc);
        Assert.assertEquals(2, manager.getNewRecords().size());
        Assert.assertEquals(0, manager.getPointed(doc).size());
    }

    @Test
    public void testSetToNullLink() {
        ODocument doc = new ODocument();
        doc.field("test", "ddd");
        ODocument doc2 = new ODocument();
        doc.field("test1", doc2);
        doc.field("test1", ((Object) (null)));
        ODirtyManager manager = ORecordInternal.getDirtyManager(doc);
        Assert.assertEquals(2, manager.getNewRecords().size());
        Assert.assertEquals(0, manager.getPointed(doc).size());
    }

    @Test
    public void testLinkOther() {
        ODocument doc = new ODocument();
        doc.field("test", "ddd");
        ODocument doc1 = new ODocument();
        doc.field("test1", doc1);
        ODirtyManager manager = ORecordInternal.getDirtyManager(doc1);
        Assert.assertEquals(2, manager.getNewRecords().size());
        Assert.assertEquals(1, manager.getPointed(doc).size());
        Assert.assertEquals(doc1, manager.getPointed(doc).get(0));
    }

    @Test
    public void testLinkCollection() {
        ODocument doc = new ODocument();
        doc.field("test", "ddd");
        List<ODocument> lst = new ArrayList<ODocument>();
        ODocument doc1 = new ODocument();
        lst.add(doc1);
        doc.field("list", lst);
        Set<ODocument> set = new HashSet<ODocument>();
        ODocument doc2 = new ODocument();
        set.add(doc2);
        doc.field("set", set);
        ODocumentInternal.convertAllMultiValuesToTrackedVersions(doc);
        ODirtyManager manager = ORecordInternal.getDirtyManager(doc);
        Assert.assertEquals(3, manager.getNewRecords().size());
        Assert.assertEquals(2, manager.getPointed(doc).size());
        TestCase.assertTrue(manager.getPointed(doc).contains(doc1));
        TestCase.assertTrue(manager.getPointed(doc).contains(doc2));
    }

    @Test
    public void testLinkCollectionRemove() {
        ODocument doc = new ODocument();
        doc.field("test", "ddd");
        List<ODocument> lst = new ArrayList<ODocument>();
        ODocument doc1 = new ODocument();
        lst.add(doc1);
        doc.field("list", lst);
        doc.removeField("list");
        Set<ODocument> set = new HashSet<ODocument>();
        ODocument doc2 = new ODocument();
        set.add(doc2);
        doc.field("set", set);
        doc.removeField("set");
        ODocumentInternal.convertAllMultiValuesToTrackedVersions(doc);
        ODirtyManager manager = ORecordInternal.getDirtyManager(doc);
        Assert.assertEquals(1, manager.getNewRecords().size());
        Assert.assertEquals(null, manager.getPointed(doc));
    }

    @Test
    public void testLinkCollectionOther() {
        ODocument doc = new ODocument();
        doc.field("test", "ddd");
        List<ODocument> lst = new ArrayList<ODocument>();
        ODocument doc1 = new ODocument();
        lst.add(doc1);
        doc.field("list", lst);
        Set<ODocument> set = new HashSet<ODocument>();
        ODocument doc2 = new ODocument();
        set.add(doc2);
        doc.field("set", set);
        ODocumentInternal.convertAllMultiValuesToTrackedVersions(doc);
        ODirtyManager manager = ORecordInternal.getDirtyManager(doc1);
        ODirtyManager manager2 = ORecordInternal.getDirtyManager(doc2);
        TestCase.assertTrue(manager2.isSame(manager));
        Assert.assertEquals(3, manager.getNewRecords().size());
    }

    @Test
    public void testLinkMapOther() {
        ODocument doc = new ODocument();
        doc.field("test", "ddd");
        Map<String, ODocument> map = new HashMap<String, ODocument>();
        ODocument doc1 = new ODocument();
        map.put("some", doc1);
        doc.field("list", map);
        ODocumentInternal.convertAllMultiValuesToTrackedVersions(doc);
        ODirtyManager manager = ORecordInternal.getDirtyManager(doc1);
        Assert.assertEquals(2, manager.getNewRecords().size());
        Assert.assertEquals(1, manager.getPointed(doc).size());
        TestCase.assertTrue(manager.getPointed(doc).contains(doc1));
    }

    @Test
    public void testEmbeddedMap() {
        ODocument doc = new ODocument();
        doc.field("test", "ddd");
        Map<String, Object> map = new HashMap<String, Object>();
        ODocument doc1 = new ODocument();
        map.put("bla", "bla");
        map.put("some", doc1);
        doc.field("list", map, EMBEDDEDMAP);
        ODocumentInternal.convertAllMultiValuesToTrackedVersions(doc);
        ODirtyManager manager = ORecordInternal.getDirtyManager(doc);
        Assert.assertEquals(1, manager.getNewRecords().size());
    }

    @Test
    public void testEmbeddedCollection() {
        ODocument doc = new ODocument();
        doc.field("test", "ddd");
        List<ODocument> lst = new ArrayList<ODocument>();
        ODocument doc1 = new ODocument();
        lst.add(doc1);
        doc.field("list", lst, EMBEDDEDLIST);
        Set<ODocument> set = new HashSet<ODocument>();
        ODocument doc2 = new ODocument();
        set.add(doc2);
        doc.field("set", set, EMBEDDEDSET);
        ODocumentInternal.convertAllMultiValuesToTrackedVersions(doc);
        ODirtyManager manager = ORecordInternal.getDirtyManager(doc);
        Assert.assertEquals(1, manager.getNewRecords().size());
    }

    @Test
    public void testRidBag() {
        ODocument doc = new ODocument();
        doc.field("test", "ddd");
        ORidBag bag = new ORidBag();
        ODocument doc1 = new ODocument();
        bag.add(doc1);
        doc.field("bag", bag);
        ODocumentInternal.convertAllMultiValuesToTrackedVersions(doc);
        ODirtyManager manager = ORecordInternal.getDirtyManager(doc1);
        Assert.assertEquals(2, manager.getNewRecords().size());
    }

    @Test
    public void testEmbendedWithEmbeddedCollection() {
        ODocument doc = new ODocument();
        doc.field("test", "ddd");
        ODocument emb = new ODocument();
        doc.field("emb", emb, EMBEDDED);
        ODocument embedInList = new ODocument();
        List<ODocument> lst = new ArrayList<ODocument>();
        lst.add(embedInList);
        emb.field("lst", lst, EMBEDDEDLIST);
        ODocument link = new ODocument();
        embedInList.field("set", link);
        ODocumentInternal.convertAllMultiValuesToTrackedVersions(doc);
        ODirtyManager manager = ORecordInternal.getDirtyManager(doc);
        Assert.assertEquals(2, manager.getNewRecords().size());
        Assert.assertEquals(1, manager.getPointed(doc).size());
        TestCase.assertTrue(manager.getPointed(doc).contains(link));
    }

    @Test
    public void testDoubleLevelEmbeddedCollection() {
        ODocument doc = new ODocument();
        doc.field("test", "ddd");
        List<ODocument> lst = new ArrayList<ODocument>();
        ODocument embeddedInList = new ODocument();
        ODocument link = new ODocument();
        embeddedInList.field("link", link);
        lst.add(embeddedInList);
        Set<ODocument> set = new HashSet<ODocument>();
        ODocument embeddedInSet = new ODocument();
        embeddedInSet.field("list", lst, EMBEDDEDLIST);
        set.add(embeddedInSet);
        doc.field("set", set, EMBEDDEDSET);
        ODocumentInternal.convertAllMultiValuesToTrackedVersions(doc);
        ODirtyManager manager = ORecordInternal.getDirtyManager(doc);
        ODirtyManager managerNested = ORecordInternal.getDirtyManager(embeddedInSet);
        TestCase.assertTrue(manager.isSame(managerNested));
        Assert.assertEquals(2, manager.getNewRecords().size());
        Assert.assertEquals(1, manager.getPointed(doc).size());
        TestCase.assertTrue(manager.getPointed(doc).contains(link));
    }

    @Test
    public void testDoubleCollectionEmbedded() {
        ODocument doc = new ODocument();
        doc.field("test", "ddd");
        List<ODocument> lst = new ArrayList<ODocument>();
        ODocument embeddedInList = new ODocument();
        ODocument link = new ODocument();
        embeddedInList.field("link", link);
        lst.add(embeddedInList);
        Set<Object> set = new HashSet<Object>();
        set.add(lst);
        doc.field("set", set, EMBEDDEDSET);
        ODocumentInternal.convertAllMultiValuesToTrackedVersions(doc);
        ODirtyManager manager = ORecordInternal.getDirtyManager(doc);
        Assert.assertEquals(2, manager.getNewRecords().size());
        Assert.assertEquals(1, manager.getPointed(doc).size());
        TestCase.assertTrue(manager.getPointed(doc).contains(link));
    }

    @Test
    public void testDoubleCollectionDocumentEmbedded() {
        ODocument doc = new ODocument();
        doc.field("test", "ddd");
        List<ODocument> lst = new ArrayList<ODocument>();
        ODocument embeddedInList = new ODocument();
        ODocument link = new ODocument();
        ODocument embInDoc = new ODocument();
        embInDoc.field("link", link);
        embeddedInList.field("some", embInDoc, EMBEDDED);
        lst.add(embeddedInList);
        Set<Object> set = new HashSet<Object>();
        set.add(lst);
        doc.field("set", set, EMBEDDEDSET);
        ODocumentInternal.convertAllMultiValuesToTrackedVersions(doc);
        ODirtyManager manager = ORecordInternal.getDirtyManager(doc);
        Assert.assertEquals(2, manager.getNewRecords().size());
        Assert.assertEquals(1, manager.getPointed(doc).size());
        TestCase.assertTrue(manager.getPointed(doc).contains(link));
    }

    @Test
    public void testDoubleMapEmbedded() {
        ODocument doc = new ODocument();
        doc.field("test", "ddd");
        List<ODocument> lst = new ArrayList<ODocument>();
        ODocument embeddedInList = new ODocument();
        ODocument link = new ODocument();
        embeddedInList.field("link", link);
        lst.add(embeddedInList);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("some", lst);
        doc.field("set", map, EMBEDDEDMAP);
        ODocumentInternal.convertAllMultiValuesToTrackedVersions(doc);
        ODirtyManager manager = ORecordInternal.getDirtyManager(doc);
        Assert.assertEquals(2, manager.getNewRecords().size());
        Assert.assertEquals(1, manager.getPointed(doc).size());
        TestCase.assertTrue(manager.getPointed(doc).contains(link));
    }

    @Test
    public void testLinkSet() {
        ODocument doc = new ODocument();
        doc.field("test", "ddd");
        Set<ODocument> set = new HashSet<ODocument>();
        ODocument link = new ODocument();
        set.add(link);
        doc.field("set", set);
        ODocumentInternal.convertAllMultiValuesToTrackedVersions(doc);
        ODirtyManager manager = ORecordInternal.getDirtyManager(doc);
        Assert.assertEquals(2, manager.getNewRecords().size());
        Assert.assertEquals(1, manager.getPointed(doc).size());
        TestCase.assertTrue(manager.getPointed(doc).contains(link));
    }

    @Test
    public void testLinkSetNoConvert() {
        ODocument doc = new ODocument();
        doc.field("test", "ddd");
        Set<OIdentifiable> set = new com.orientechnologies.orient.core.db.record.ORecordLazySet(doc);
        ODocument link = new ODocument();
        set.add(link);
        doc.field("set", set, LINKSET);
        ODirtyManager manager = ORecordInternal.getDirtyManager(doc);
        Assert.assertEquals(2, manager.getNewRecords().size());
        Assert.assertEquals(1, manager.getPointed(doc).size());
        TestCase.assertTrue(manager.getPointed(doc).contains(link));
    }

    @Test
    public void testLinkList() {
        ODocument doc = new ODocument();
        doc.field("test", "ddd");
        List<ODocument> list = new ArrayList<ODocument>();
        ODocument link = new ODocument();
        list.add(link);
        doc.field("list", list, LINKLIST);
        ODocument[] linkeds = new ODocument[]{ new ODocument().field("name", "linked2"), new ODocument().field("name", "linked3") };
        doc.field("linkeds", linkeds, LINKLIST);
        ODocumentInternal.convertAllMultiValuesToTrackedVersions(doc);
        ODirtyManager manager = ORecordInternal.getDirtyManager(doc);
        Assert.assertEquals(4, manager.getNewRecords().size());
        Assert.assertEquals(3, manager.getPointed(doc).size());
        TestCase.assertTrue(manager.getPointed(doc).contains(link));
    }

    @Test
    public void testLinkMap() {
        ODocument doc = new ODocument();
        doc.field("test", "ddd");
        Map<String, ODocument> map = new HashMap<String, ODocument>();
        ODocument link = new ODocument();
        map.put("bla", link);
        doc.field("map", map, LINKMAP);
        ODocumentInternal.convertAllMultiValuesToTrackedVersions(doc);
        ODirtyManager manager = ORecordInternal.getDirtyManager(doc);
        Assert.assertEquals(2, manager.getNewRecords().size());
        Assert.assertEquals(1, manager.getPointed(doc).size());
        TestCase.assertTrue(manager.getPointed(doc).contains(link));
    }

    @Test
    public void testNestedMapDocRidBag() {
        ODocument doc = new ODocument();
        Map<String, ODocument> embeddedMap = new HashMap<String, ODocument>();
        ODocument embeddedMapDoc = new ODocument();
        ORidBag embeddedMapDocRidBag = new ORidBag();
        ODocument link = new ODocument();
        embeddedMapDocRidBag.add(link);
        embeddedMapDoc.field("ridBag", embeddedMapDocRidBag);
        embeddedMap.put("k1", embeddedMapDoc);
        doc.field("embeddedMap", embeddedMap, EMBEDDEDMAP);
        ODocumentInternal.convertAllMultiValuesToTrackedVersions(doc);
        ODirtyManager manager = ORecordInternal.getDirtyManager(doc);
        Assert.assertEquals(2, manager.getNewRecords().size());
        Assert.assertEquals(1, manager.getPointed(doc).size());
        // TODO: double check this, it's an overhead
        Assert.assertEquals(1, manager.getPointed(embeddedMapDoc).size());
        TestCase.assertTrue(manager.getPointed(doc).contains(link));
    }
}

