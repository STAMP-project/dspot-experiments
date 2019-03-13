package com.orientechnologies.orient.core.metadata.index;


import OClass.INDEX_TYPE.UNIQUE;
import ODatabaseType.PLOCAL;
import OType.STRING;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.storage.ORecordDuplicatedException;
import org.junit.Assert;
import org.junit.Test;


public class TestImmutableIndexLoad {
    @Test
    public void testLoadAndUseIndexOnOpen() {
        OrientDB orientDB = new OrientDB("embedded:./target/", OrientDBConfig.defaultConfig());
        orientDB.create(TestImmutableIndexLoad.class.getSimpleName(), PLOCAL);
        ODatabaseSession db = orientDB.open(TestImmutableIndexLoad.class.getSimpleName(), "admin", "admin");
        OClass one = db.createClass("One");
        OProperty property = one.createProperty("one", STRING);
        property.createIndex(UNIQUE);
        db.close();
        orientDB.close();
        orientDB = new OrientDB("embedded:./target/", OrientDBConfig.defaultConfig());
        db = orientDB.open(TestImmutableIndexLoad.class.getSimpleName(), "admin", "admin");
        ODocument doc = new ODocument("One");
        doc.setProperty("one", "a");
        db.save(doc);
        try {
            ODocument doc1 = new ODocument("One");
            doc1.setProperty("one", "a");
            db.save(doc1);
            Assert.fail("It should fail the unique index");
        } catch (ORecordDuplicatedException e) {
            // EXPEXTED
        }
        db.close();
        orientDB.drop(TestImmutableIndexLoad.class.getSimpleName());
        orientDB.close();
    }
}

