package com.orientechnologies.orient.core.metadata.schema;


import OType.BINARY;
import OType.BOOLEAN;
import OType.BYTE;
import OType.CUSTOM;
import OType.DATETIME;
import OType.DECIMAL;
import OType.DOUBLE;
import OType.EMBEDDED;
import OType.EMBEDDEDLIST;
import OType.EMBEDDEDMAP;
import OType.EMBEDDEDSET;
import OType.FLOAT;
import OType.INTEGER;
import OType.LINK;
import OType.LINKBAG;
import OType.LINKLIST;
import OType.LINKMAP;
import OType.LINKSET;
import OType.LONG;
import OType.SHORT;
import OType.STRING;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.db.record.ORecordLazySet;
import com.orientechnologies.orient.core.db.record.OTrackedList;
import com.orientechnologies.orient.core.db.record.OTrackedMap;
import com.orientechnologies.orient.core.db.record.OTrackedSet;
import com.orientechnologies.orient.core.db.record.ridbag.ORidBag;
import com.orientechnologies.orient.core.exception.OSerializationException;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.record.impl.ODocumentInternal;
import com.orientechnologies.orient.core.serialization.ODocumentSerializable;
import com.orientechnologies.orient.core.serialization.OSerializableStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class TestOTypeDetection {
    @Test
    public void testOTypeFromClass() {
        Assert.assertEquals(BOOLEAN, OType.getTypeByClass(Boolean.class));
        Assert.assertEquals(BOOLEAN, OType.getTypeByClass(Boolean.TYPE));
        Assert.assertEquals(LONG, OType.getTypeByClass(Long.class));
        Assert.assertEquals(LONG, OType.getTypeByClass(Long.TYPE));
        Assert.assertEquals(INTEGER, OType.getTypeByClass(Integer.class));
        Assert.assertEquals(INTEGER, OType.getTypeByClass(Integer.TYPE));
        Assert.assertEquals(SHORT, OType.getTypeByClass(Short.class));
        Assert.assertEquals(SHORT, OType.getTypeByClass(Short.TYPE));
        Assert.assertEquals(FLOAT, OType.getTypeByClass(Float.class));
        Assert.assertEquals(FLOAT, OType.getTypeByClass(Float.TYPE));
        Assert.assertEquals(DOUBLE, OType.getTypeByClass(Double.class));
        Assert.assertEquals(DOUBLE, OType.getTypeByClass(Double.TYPE));
        Assert.assertEquals(BYTE, OType.getTypeByClass(Byte.class));
        Assert.assertEquals(BYTE, OType.getTypeByClass(Byte.TYPE));
        Assert.assertEquals(STRING, OType.getTypeByClass(Character.class));
        Assert.assertEquals(STRING, OType.getTypeByClass(Character.TYPE));
        Assert.assertEquals(STRING, OType.getTypeByClass(String.class));
        // assertEquals(OType.BINARY, OType.getTypeByClass(Byte[].class));
        Assert.assertEquals(BINARY, OType.getTypeByClass(byte[].class));
        Assert.assertEquals(DATETIME, OType.getTypeByClass(Date.class));
        Assert.assertEquals(DECIMAL, OType.getTypeByClass(BigDecimal.class));
        Assert.assertEquals(INTEGER, OType.getTypeByClass(BigInteger.class));
        Assert.assertEquals(LINK, OType.getTypeByClass(OIdentifiable.class));
        Assert.assertEquals(LINK, OType.getTypeByClass(ORecordId.class));
        Assert.assertEquals(LINK, OType.getTypeByClass(ORecord.class));
        Assert.assertEquals(LINK, OType.getTypeByClass(ODocument.class));
        Assert.assertEquals(EMBEDDEDLIST, OType.getTypeByClass(ArrayList.class));
        Assert.assertEquals(EMBEDDEDLIST, OType.getTypeByClass(List.class));
        Assert.assertEquals(EMBEDDEDLIST, OType.getTypeByClass(OTrackedList.class));
        Assert.assertEquals(EMBEDDEDSET, OType.getTypeByClass(Set.class));
        Assert.assertEquals(EMBEDDEDSET, OType.getTypeByClass(HashSet.class));
        Assert.assertEquals(EMBEDDEDSET, OType.getTypeByClass(OTrackedSet.class));
        Assert.assertEquals(EMBEDDEDMAP, OType.getTypeByClass(Map.class));
        Assert.assertEquals(EMBEDDEDMAP, OType.getTypeByClass(HashMap.class));
        Assert.assertEquals(EMBEDDEDMAP, OType.getTypeByClass(OTrackedMap.class));
        Assert.assertEquals(LINKSET, OType.getTypeByClass(ORecordLazySet.class));
        Assert.assertEquals(LINKLIST, OType.getTypeByClass(ORecordLazyList.class));
        Assert.assertEquals(LINKMAP, OType.getTypeByClass(ORecordLazyMap.class));
        Assert.assertEquals(LINKBAG, OType.getTypeByClass(ORidBag.class));
        Assert.assertEquals(CUSTOM, OType.getTypeByClass(OSerializableStream.class));
        Assert.assertEquals(CUSTOM, OType.getTypeByClass(TestOTypeDetection.CustomClass.class));
        Assert.assertEquals(EMBEDDEDLIST, OType.getTypeByClass(Object[].class));
        Assert.assertEquals(EMBEDDEDLIST, OType.getTypeByClass(String[].class));
        Assert.assertEquals(EMBEDDED, OType.getTypeByClass(ODocumentSerializable.class));
        Assert.assertEquals(EMBEDDED, OType.getTypeByClass(TestOTypeDetection.DocumentSer.class));
        Assert.assertEquals(CUSTOM, OType.getTypeByClass(TestOTypeDetection.ClassSerializable.class));
    }

    @Test
    public void testOTypeFromValue() {
        Assert.assertEquals(BOOLEAN, OType.getTypeByValue(true));
        Assert.assertEquals(LONG, OType.getTypeByValue(2L));
        Assert.assertEquals(INTEGER, OType.getTypeByValue(2));
        Assert.assertEquals(SHORT, OType.getTypeByValue(((short) (4))));
        Assert.assertEquals(FLOAT, OType.getTypeByValue(0.5F));
        Assert.assertEquals(DOUBLE, OType.getTypeByValue(0.7));
        Assert.assertEquals(BYTE, OType.getTypeByValue(((byte) (10))));
        Assert.assertEquals(STRING, OType.getTypeByValue('a'));
        Assert.assertEquals(STRING, OType.getTypeByValue("yaaahooooo"));
        Assert.assertEquals(BINARY, OType.getTypeByValue(new byte[]{ 0, 1, 2 }));
        Assert.assertEquals(DATETIME, OType.getTypeByValue(new Date()));
        Assert.assertEquals(DECIMAL, OType.getTypeByValue(new BigDecimal(10)));
        Assert.assertEquals(INTEGER, OType.getTypeByValue(new BigInteger("20")));
        Assert.assertEquals(LINK, OType.getTypeByValue(new ODocument()));
        Assert.assertEquals(LINK, OType.getTypeByValue(new ORecordId()));
        Assert.assertEquals(EMBEDDEDLIST, OType.getTypeByValue(new ArrayList<Object>()));
        Assert.assertEquals(EMBEDDEDLIST, OType.getTypeByValue(new OTrackedList<Object>(new ODocument())));
        Assert.assertEquals(EMBEDDEDSET, OType.getTypeByValue(new HashSet<Object>()));
        Assert.assertEquals(EMBEDDEDMAP, OType.getTypeByValue(new HashMap<Object, Object>()));
        Assert.assertEquals(LINKSET, OType.getTypeByValue(new ORecordLazySet(new ODocument())));
        Assert.assertEquals(LINKLIST, OType.getTypeByValue(new ORecordLazyList(new ODocument())));
        Assert.assertEquals(LINKMAP, OType.getTypeByValue(new ORecordLazyMap(new ODocument())));
        Assert.assertEquals(LINKBAG, OType.getTypeByValue(new ORidBag()));
        Assert.assertEquals(CUSTOM, OType.getTypeByValue(new TestOTypeDetection.CustomClass()));
        Assert.assertEquals(EMBEDDEDLIST, OType.getTypeByValue(new Object[]{  }));
        Assert.assertEquals(EMBEDDEDLIST, OType.getTypeByValue(new String[]{  }));
        Assert.assertEquals(EMBEDDED, OType.getTypeByValue(new TestOTypeDetection.DocumentSer()));
        Assert.assertEquals(CUSTOM, OType.getTypeByValue(new TestOTypeDetection.ClassSerializable()));
    }

    @Test
    public void testOTypeFromValueInternal() {
        Map<String, ORecordId> linkmap = new HashMap<String, ORecordId>();
        linkmap.put("some", new ORecordId());
        Assert.assertEquals(LINKMAP, OType.getTypeByValue(linkmap));
        Map<String, ORecord> linkmap2 = new HashMap<String, ORecord>();
        linkmap2.put("some", new ODocument());
        Assert.assertEquals(LINKMAP, OType.getTypeByValue(linkmap2));
        List<ORecordId> linkList = new ArrayList<ORecordId>();
        linkList.add(new ORecordId());
        Assert.assertEquals(LINKLIST, OType.getTypeByValue(linkList));
        List<ORecord> linkList2 = new ArrayList<ORecord>();
        linkList2.add(new ODocument());
        Assert.assertEquals(LINKLIST, OType.getTypeByValue(linkList2));
        Set<ORecordId> linkSet = new HashSet<ORecordId>();
        linkSet.add(new ORecordId());
        Assert.assertEquals(LINKSET, OType.getTypeByValue(linkSet));
        Set<ORecord> linkSet2 = new HashSet<ORecord>();
        linkSet2.add(new ODocument());
        Assert.assertEquals(LINKSET, OType.getTypeByValue(linkSet2));
        ODocument document = new ODocument();
        ODocumentInternal.addOwner(document, new ODocument());
        Assert.assertEquals(EMBEDDED, OType.getTypeByValue(document));
    }

    public class CustomClass implements OSerializableStream {
        @Override
        public byte[] toStream() throws OSerializationException {
            return null;
        }

        @Override
        public OSerializableStream fromStream(byte[] iStream) throws OSerializationException {
            return null;
        }
    }

    public class DocumentSer implements ODocumentSerializable {
        @Override
        public ODocument toDocument() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void fromDocument(ODocument document) {
            // TODO Auto-generated method stub
        }
    }

    public class ClassSerializable implements Serializable {
        private String aaa;
    }
}

