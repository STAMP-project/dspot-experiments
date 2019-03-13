package com.orientechnologies.orient.core.sql.orderby;


import ATTRIBUTES.LOCALECOUNTRY;
import ATTRIBUTES.LOCALELANGUAGE;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import java.util.List;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


public class TestOrderBy {
    @Test
    public void testGermanOrderBy() {
        ODatabaseDocument db = new ODatabaseDocumentTx("memory:testGermanOrderBy");
        db.set(LOCALECOUNTRY, Locale.GERMANY.getCountry());
        db.set(LOCALELANGUAGE, Locale.GERMANY.getLanguage());
        db.create();
        try {
            db.getMetadata().getSchema().createClass("test");
            ORecord res1 = db.save(new ODocument("test").field("name", "?hhhh"));
            ORecord res2 = db.save(new ODocument("test").field("name", "Ahhhh"));
            ORecord res3 = db.save(new ODocument("test").field("name", "Zebra"));
            List<?> queryRes = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<Object>("select from test order by name"));
            Assert.assertEquals(queryRes.get(0), res2);
            Assert.assertEquals(queryRes.get(1), res1);
            Assert.assertEquals(queryRes.get(2), res3);
            queryRes = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<Object>("select from test order by name desc "));
            Assert.assertEquals(queryRes.get(0), res3);
            Assert.assertEquals(queryRes.get(1), res1);
            Assert.assertEquals(queryRes.get(2), res2);
        } finally {
            db.drop();
        }
    }
}

