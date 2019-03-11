package com.orientechnologies.orient.core.db.record;


import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.record.impl.ONestedMultiValueChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 11/03/16.
 */
public class ODocumentTrackingNestedCollectionsTest {
    private ODatabaseDocument db;

    @Test
    public void testTrackingNestedSet() {
        ORID orid;
        ODocument document = new ODocument();
        Set objects = new HashSet();
        document.field("objects", objects);
        document.save(db.getClusterNameById(db.getDefaultClusterId()));
        objects = document.field("objects");
        Set subObjects = new HashSet();
        objects.add(subObjects);
        document.save(db.getClusterNameById(db.getDefaultClusterId()));
        orid = document.getIdentity();
        objects = document.field("objects");
        subObjects = ((Set) (objects.iterator().next()));
        ODocument nestedDoc = new ODocument();
        subObjects.add(nestedDoc);
        document.save(db.getClusterNameById(db.getDefaultClusterId()));
        db.getLocalCache().clear();
        document = db.load(orid);
        objects = document.field("objects");
        subObjects = ((Set) (objects.iterator().next()));
        Assert.assertTrue((!(subObjects.isEmpty())));
    }

    @Test
    public void testChangesValuesNestedTrackingSet() {
        ODocument document = new ODocument();
        Set objects = new HashSet();
        document.field("objects", objects);
        Set subObjects = new HashSet();
        objects.add(subObjects);
        ODocument nestedDoc = new ODocument();
        subObjects.add(nestedDoc);
        document.save(db.getClusterNameById(db.getDefaultClusterId()));
        objects = document.field("objects");
        subObjects = ((Set) (objects.iterator().next()));
        subObjects.add("one");
        OMultiValueChangeTimeLine<Object, Object> timeLine = document.getCollectionTimeLine("objects");
        Assert.assertEquals(1, timeLine.getMultiValueChangeEvents().size());
        Assert.assertTrue(((timeLine.getMultiValueChangeEvents().get(0)) instanceof ONestedMultiValueChangeEvent));
        ONestedMultiValueChangeEvent nesetedEvent = ((ONestedMultiValueChangeEvent) (timeLine.getMultiValueChangeEvents().get(0)));
        Assert.assertEquals(1, nesetedEvent.getTimeLine().getMultiValueChangeEvents().size());
        List<OMultiValueChangeEvent<?, ?>> multiValueChangeEvents = nesetedEvent.getTimeLine().getMultiValueChangeEvents();
        Assert.assertEquals("one", multiValueChangeEvents.get(0).getValue());
    }

    @Test
    public void testChangesValuesNestedTrackingList() {
        ODocument document = new ODocument();
        List objects = new ArrayList();
        document.field("objects", objects);
        List subObjects = new ArrayList();
        objects.add(subObjects);
        ODocument nestedDoc = new ODocument();
        subObjects.add(nestedDoc);
        document.save(db.getClusterNameById(db.getDefaultClusterId()));
        objects = document.field("objects");
        subObjects = ((List) (objects.iterator().next()));
        subObjects.add("one");
        subObjects.add(new ODocument());
        OMultiValueChangeTimeLine<Object, Object> timeLine = document.getCollectionTimeLine("objects");
        Assert.assertEquals(1, timeLine.getMultiValueChangeEvents().size());
        Assert.assertTrue(((timeLine.getMultiValueChangeEvents().get(0)) instanceof ONestedMultiValueChangeEvent));
        ONestedMultiValueChangeEvent nesetedEvent = ((ONestedMultiValueChangeEvent) (timeLine.getMultiValueChangeEvents().get(0)));
        Assert.assertEquals(2, nesetedEvent.getTimeLine().getMultiValueChangeEvents().size());
        List<OMultiValueChangeEvent<?, ?>> multiValueChangeEvents = nesetedEvent.getTimeLine().getMultiValueChangeEvents();
        Assert.assertEquals(1, multiValueChangeEvents.get(0).getKey());
        Assert.assertEquals("one", multiValueChangeEvents.get(0).getValue());
        Assert.assertEquals(2, multiValueChangeEvents.get(1).getKey());
        Assert.assertTrue(((multiValueChangeEvents.get(1).getValue()) instanceof ODocument));
    }

    @Test
    public void testChangesValuesNestedTrackingMap() {
        ODocument document = new ODocument();
        Map objects = new HashMap();
        document.field("objects", objects);
        Map subObjects = new HashMap();
        objects.put("first", subObjects);
        ODocument nestedDoc = new ODocument();
        subObjects.put("one", nestedDoc);
        document.save(db.getClusterNameById(db.getDefaultClusterId()));
        objects = document.field("objects");
        subObjects = ((Map) (objects.values().iterator().next()));
        subObjects.put("one", "String");
        subObjects.put("two", new ODocument());
        OMultiValueChangeTimeLine<Object, Object> timeLine = document.getCollectionTimeLine("objects");
        Assert.assertEquals(1, timeLine.getMultiValueChangeEvents().size());
        Assert.assertTrue(((timeLine.getMultiValueChangeEvents().get(0)) instanceof ONestedMultiValueChangeEvent));
        ONestedMultiValueChangeEvent nesetedEvent = ((ONestedMultiValueChangeEvent) (timeLine.getMultiValueChangeEvents().get(0)));
        Assert.assertEquals(2, nesetedEvent.getTimeLine().getMultiValueChangeEvents().size());
        List<OMultiValueChangeEvent<?, ?>> multiValueChangeEvents = nesetedEvent.getTimeLine().getMultiValueChangeEvents();
        Assert.assertEquals("one", multiValueChangeEvents.get(0).getKey());
        Assert.assertEquals("String", multiValueChangeEvents.get(0).getValue());
        Assert.assertEquals("two", multiValueChangeEvents.get(1).getKey());
        Assert.assertTrue(((multiValueChangeEvents.get(1).getValue()) instanceof ODocument));
    }
}

