package com.orientechnologies.orient.core.tx;


import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.index.OIndexTxAwareMultiValue;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 28/05/17.
 */
public class IndexChangesQueryTest {
    private OrientDB orientDB;

    private ODatabaseDocument database;

    @Test
    public void testMultiplePut() {
        database.begin();
        final OIndex<?> index = database.getMetadata().getIndexManager().getIndex("idxTxAwareMultiValueGetEntriesTest");
        Assert.assertTrue((index instanceof OIndexTxAwareMultiValue));
        final int clusterId = database.getDefaultClusterId();
        ODocument doc = new ODocument();
        database.save(doc, database.getClusterNameById(database.getDefaultClusterId()));
        ODocument doc1 = new ODocument();
        database.save(doc1, database.getClusterNameById(database.getDefaultClusterId()));
        index.put(1, doc.getIdentity());
        index.put(1, doc.getIdentity());
        index.put(2, doc1.getIdentity());
        Assert.assertNotNull(database.getTransaction().getIndexChanges("idxTxAwareMultiValueGetEntriesTest"));
        OResultSet res = database.query("select from index:idxTxAwareMultiValueGetEntriesTest where key in [?, ?] order by key ASC ", 1, 2);
        Assert.assertEquals(res.stream().count(), 2);
        res.close();
        database.commit();
    }

    @Test
    public void testClearAndPut() {
        OIdentifiable id1 = database.save(new ODocument(), database.getClusterNameById(database.getDefaultClusterId()));
        OIdentifiable id2 = database.save(new ODocument(), database.getClusterNameById(database.getDefaultClusterId()));
        OIdentifiable id3 = database.save(new ODocument(), database.getClusterNameById(database.getDefaultClusterId()));
        database.begin();
        final OIndex<?> index = database.getMetadata().getIndexManager().getIndex("idxTxAwareMultiValueGetEntriesTest");
        Assert.assertTrue((index instanceof OIndexTxAwareMultiValue));
        index.put(1, id1.getIdentity());
        index.put(1, id2.getIdentity());
        index.put(2, id3.getIdentity());
        database.commit();
        OResultSet res = database.query("select from index:idxTxAwareMultiValueGetEntriesTest where key in [?, ?] order by key ASC ", 1, 2);
        Assert.assertEquals(res.stream().count(), 3);
        res = database.query("select count(*)  as count from index:idxTxAwareMultiValueGetEntriesTest where key in [?, ?] order by key ASC ", 1, 2);
        Assert.assertEquals(((long) (res.next().getProperty("count"))), 3);
        res.close();
        database.begin();
        index.clear();
        index.put(2, id3.getIdentity());
        ODocument doc = new ODocument();
        database.save(doc, database.getClusterNameById(database.getDefaultClusterId()));
        index.put(2, doc.getIdentity());
        res = database.query("select from index:idxTxAwareMultiValueGetEntriesTest where key in [?, ?] order by key ASC ", 1, 2);
        Assert.assertEquals(res.stream().count(), 2);
        res.close();
        res = database.query("select expand(rid) from index:idxTxAwareMultiValueGetEntriesTest where key = ?", 2);
        Assert.assertEquals(res.stream().count(), 2);
        res.close();
        res = database.query("select expand(rid) from index:idxTxAwareMultiValueGetEntriesTest where key = ?", 2);
        Assert.assertEquals(res.stream().map(( aa) -> aa.getIdentity().orElse(null)).collect(Collectors.toSet()).size(), 2);
        res.close();
        res = database.query("select count(*)  as count from index:idxTxAwareMultiValueGetEntriesTest where key in [?, ?] order by key ASC ", 1, 2);
        Assert.assertEquals(((long) (res.next().getProperty("count"))), 2);
        res.close();
        database.rollback();
        Assert.assertNull(database.getTransaction().getIndexChanges("idxTxAwareMultiValueGetEntriesTest"));
        res = database.query("select from index:idxTxAwareMultiValueGetEntriesTest where key in [?, ?] order by key ASC ", 1, 2);
        Assert.assertEquals(res.stream().count(), 3);
        res.close();
        res = database.query("select count(*)  as count  from index:idxTxAwareMultiValueGetEntriesTest where key in [?, ?] order by key ASC ", 1, 2);
        Assert.assertEquals(((long) (res.next().getProperty("count"))), 3);
        res.close();
    }
}

