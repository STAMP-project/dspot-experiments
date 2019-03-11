package com.orientechnologies.orient.core.sql;


import INDEX_TYPE.NOTUNIQUE;
import INDEX_TYPE.UNIQUE;
import OType.LINK;
import OType.STRING;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class OChainIndexFetchTest {
    private ODatabaseDocument db;

    @Test
    public void testFetchChaninedIndex() {
        OClass baseClass = db.getMetadata().getSchema().createClass("BaseClass");
        OProperty propr = baseClass.createProperty("ref", LINK);
        OClass linkedClass = db.getMetadata().getSchema().createClass("LinkedClass");
        OProperty id = linkedClass.createProperty("id", STRING);
        id.createIndex(UNIQUE);
        propr.setLinkedClass(linkedClass);
        propr.createIndex(NOTUNIQUE);
        ODocument doc = new ODocument(linkedClass);
        doc.field("id", "referred");
        db.save(doc);
        ODocument doc1 = new ODocument(baseClass);
        doc1.field("ref", doc);
        db.save(doc1);
        List<ODocument> res = db.query(new OSQLSynchQuery(" select from BaseClass where ref.id ='wrong_referred' "));
        Assert.assertEquals(0, res.size());
    }
}

