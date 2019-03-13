package com.orientechnologies.spatial;


import com.orientechnologies.orient.core.db.OPartitionedDatabasePool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import java.io.File;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by frank on 15/12/2016.
 */
public class LuceneSpatialDropTest {
    private int insertcount;

    private String dbName;

    @Test
    public void testDeleteLuceneIndex1() {
        OPartitionedDatabasePool dbPool = new OPartitionedDatabasePool(dbName, "admin", "admin");
        ODatabaseDocumentTx db = dbPool.acquire();
        fillDb(db, insertcount);
        db.close();
        db = dbPool.acquire();
        // @maggiolo00 Remove the next three lines and the test will not fail anymore
        OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("select from test where [latitude,longitude] WITHIN [[50.0,8.0],[51.0,9.0]]");
        List<ODocument> result = db.command(query).execute();
        Assert.assertEquals(insertcount, result.size());
        db.close();
        dbPool.close();
        // reopen to drop
        db = new ODatabaseDocumentTx(dbName).open("admin", "admin");
        db.drop();
        File dbFolder = new File(dbName);
        Assert.assertEquals(false, dbFolder.exists());
    }
}

