package com.orientechnologies.orient.core.storage.impl.local;


import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.record.OVertex;
import java.util.Iterator;
import junit.framework.TestCase;
import org.junit.Test;


public class OBrowseClusterTest {
    private ODatabaseSession db;

    private OrientDB orientDb;

    @Test
    public void testBrowse() {
        int numberOfEntries = 4962;
        for (int i = 0; i < numberOfEntries; i++) {
            OVertex v = db.newVertex("One");
            v.setProperty("a", i);
            db.save(v);
        }
        int cluster = db.getClass("One").getDefaultClusterId();
        Iterator<OClusterBrowsePage> browser = browseCluster(cluster);
        int count = 0;
        while (browser.hasNext()) {
            OClusterBrowsePage page = browser.next();
            for (OClusterBrowseEntry entry : page) {
                count++;
                TestCase.assertNotNull(entry.getBuffer());
                TestCase.assertNotNull(entry.getClusterPosition());
            }
        } 
        TestCase.assertEquals(numberOfEntries, count);
    }
}

