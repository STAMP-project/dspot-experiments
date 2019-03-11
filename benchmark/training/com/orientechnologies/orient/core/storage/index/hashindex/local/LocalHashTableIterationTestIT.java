package com.orientechnologies.orient.core.storage.index.hashindex.local;


import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrey Lomakin (a.lomakin-at-orientdb.com)
 * @since 13.03.13
 */
public class LocalHashTableIterationTestIT {
    private static final int KEYS_COUNT = 500000;

    private ODatabaseDocumentTx databaseDocumentTx;

    private OLocalHashTable<Integer, String> localHashTable;

    @Test
    public void testNextHaveRightOrder() throws Exception {
        SortedSet<Integer> keys = new TreeSet<Integer>();
        keys.clear();
        final Random random = new Random();
        while ((keys.size()) < (LocalHashTableIterationTestIT.KEYS_COUNT)) {
            int key = random.nextInt();
            if ((localHashTable.get(key)) == null) {
                localHashTable.put(key, (key + ""));
                keys.add(key);
                Assert.assertEquals(localHashTable.get(key), ("" + key));
            }
        } 
        OHashIndexBucket<Integer, String>[] entries = localHashTable.ceilingEntries(Integer.MIN_VALUE);
        int curPos = 0;
        for (int key : keys) {
            int sKey = entries[curPos].key;
            Assert.assertEquals(key, sKey);
            curPos++;
            if (curPos >= (entries.length)) {
                entries = localHashTable.higherEntries(entries[((entries.length) - 1)].key);
                curPos = 0;
            }
        }
    }
}

