package com.orientechnologies.orient.core.storage.index.sbtree.local;


import OSBTree.OSBTreeCursor;
import com.orientechnologies.DatabaseAbstractTest;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.index.OCompositeKey;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrey Lomakin (a.lomakin-at-orientdb.com)
 * @since 15.08.13
 */
public class SBTreeCompositeKeyTest extends DatabaseAbstractTest {
    private OSBTree<OCompositeKey, OIdentifiable> localSBTree;

    @Test
    public void testIterateBetweenValuesInclusive() {
        OSBTreeCursor<OCompositeKey, OIdentifiable> cursor = localSBTree.iterateEntriesBetween(compositeKey(2.0), true, compositeKey(3.0), true, true);
        Set<ORID> orids = extractRids(cursor);
        Assert.assertEquals(orids.size(), 18);
        for (int i = 2; i <= 3; i++) {
            for (int j = 1; j <= 9; j++) {
                Assert.assertTrue(orids.contains(new ORecordId(i, j)));
            }
        }
        cursor = localSBTree.iterateEntriesBetween(compositeKey(2.0), true, compositeKey(3.0), true, false);
        orids = extractRids(cursor);
        Assert.assertEquals(orids.size(), 18);
        for (int i = 2; i <= 3; i++) {
            for (int j = 1; j <= 9; j++) {
                Assert.assertTrue(orids.contains(new ORecordId(i, j)));
            }
        }
    }

    @Test
    public void testIterateBetweenValuesFromInclusive() {
        OSBTreeCursor<OCompositeKey, OIdentifiable> cursor = localSBTree.iterateEntriesBetween(compositeKey(2.0), true, compositeKey(3.0), false, true);
        Set<ORID> orids = extractRids(cursor);
        Assert.assertEquals(orids.size(), 9);
        for (int j = 1; j <= 9; j++) {
            Assert.assertTrue(orids.contains(new ORecordId(2, j)));
        }
        cursor = localSBTree.iterateEntriesBetween(compositeKey(2.0), true, compositeKey(3.0), false, false);
        orids = extractRids(cursor);
        Assert.assertEquals(orids.size(), 9);
        for (int j = 1; j <= 9; j++) {
            Assert.assertTrue(orids.contains(new ORecordId(2, j)));
        }
    }

    @Test
    public void testIterateBetweenValuesToInclusive() {
        OSBTreeCursor<OCompositeKey, OIdentifiable> cursor = localSBTree.iterateEntriesBetween(compositeKey(2.0), false, compositeKey(3.0), true, true);
        Set<ORID> orids = extractRids(cursor);
        Assert.assertEquals(orids.size(), 9);
        for (int i = 1; i <= 9; i++) {
            Assert.assertTrue(orids.contains(new ORecordId(3, i)));
        }
        cursor = localSBTree.iterateEntriesBetween(compositeKey(2.0), false, compositeKey(3.0), true, false);
        orids = extractRids(cursor);
        Assert.assertEquals(orids.size(), 9);
        for (int i = 1; i <= 9; i++) {
            Assert.assertTrue(orids.contains(new ORecordId(3, i)));
        }
    }

    @Test
    public void testIterateEntriesNonInclusive() {
        OSBTreeCursor<OCompositeKey, OIdentifiable> cursor = localSBTree.iterateEntriesBetween(compositeKey(2.0), false, compositeKey(3.0), false, true);
        Set<ORID> orids = extractRids(cursor);
        Assert.assertEquals(orids.size(), 0);
        cursor = localSBTree.iterateEntriesBetween(compositeKey(2.0), false, compositeKey(3.0), false, false);
        orids = extractRids(cursor);
        Assert.assertEquals(orids.size(), 0);
        cursor = localSBTree.iterateEntriesBetween(compositeKey(1.0), false, compositeKey(3.0), false, true);
        orids = extractRids(cursor);
        Assert.assertEquals(orids.size(), 9);
        for (int i = 1; i <= 9; i++) {
            Assert.assertTrue(orids.contains(new ORecordId(2, i)));
        }
        cursor = localSBTree.iterateEntriesBetween(compositeKey(1.0), false, compositeKey(3.0), false, false);
        orids = extractRids(cursor);
        Assert.assertEquals(orids.size(), 9);
        for (int i = 1; i <= 9; i++) {
            Assert.assertTrue(orids.contains(new ORecordId(2, i)));
        }
    }

    @Test
    public void testIterateBetweenValuesInclusivePartialKey() {
        OSBTreeCursor<OCompositeKey, OIdentifiable> cursor = localSBTree.iterateEntriesBetween(compositeKey(2.0, 4.0), true, compositeKey(3.0), true, true);
        Set<ORID> orids = extractRids(cursor);
        Assert.assertEquals(orids.size(), 15);
        for (int i = 2; i <= 3; i++) {
            for (int j = 1; j <= 9; j++) {
                if ((i == 2) && (j < 4))
                    continue;

                Assert.assertTrue(orids.contains(new ORecordId(i, j)));
            }
        }
        cursor = localSBTree.iterateEntriesBetween(compositeKey(2.0, 4.0), true, compositeKey(3.0), true, false);
        orids = extractRids(cursor);
        Assert.assertEquals(orids.size(), 15);
        for (int i = 2; i <= 3; i++) {
            for (int j = 1; j <= 9; j++) {
                if ((i == 2) && (j < 4))
                    continue;

                Assert.assertTrue(orids.contains(new ORecordId(i, j)));
            }
        }
    }

    @Test
    public void testIterateBetweenValuesFromInclusivePartialKey() {
        OSBTreeCursor<OCompositeKey, OIdentifiable> cursor = localSBTree.iterateEntriesBetween(compositeKey(2.0, 4.0), true, compositeKey(3.0), false, true);
        Set<ORID> orids = extractRids(cursor);
        Assert.assertEquals(orids.size(), 6);
        for (int j = 4; j <= 9; j++) {
            Assert.assertTrue(orids.contains(new ORecordId(2, j)));
        }
        cursor = localSBTree.iterateEntriesBetween(compositeKey(2.0, 4.0), true, compositeKey(3.0), false, false);
        orids = extractRids(cursor);
        Assert.assertEquals(orids.size(), 6);
        for (int j = 4; j <= 9; j++) {
            Assert.assertTrue(orids.contains(new ORecordId(2, j)));
        }
    }

    @Test
    public void testIterateBetweenValuesToInclusivePartialKey() {
        OSBTreeCursor<OCompositeKey, OIdentifiable> cursor = localSBTree.iterateEntriesBetween(compositeKey(2.0, 4.0), false, compositeKey(3.0), true, true);
        Set<ORID> orids = extractRids(cursor);
        Assert.assertEquals(orids.size(), 14);
        for (int i = 2; i <= 3; i++) {
            for (int j = 1; j <= 9; j++) {
                if ((i == 2) && (j <= 4))
                    continue;

                Assert.assertTrue(orids.contains(new ORecordId(i, j)));
            }
        }
        cursor = localSBTree.iterateEntriesBetween(compositeKey(2.0, 4.0), false, compositeKey(3.0), true, false);
        orids = extractRids(cursor);
        Assert.assertEquals(orids.size(), 14);
        for (int i = 2; i <= 3; i++) {
            for (int j = 1; j <= 9; j++) {
                if ((i == 2) && (j <= 4))
                    continue;

                Assert.assertTrue(orids.contains(new ORecordId(i, j)));
            }
        }
    }

    @Test
    public void testIterateBetweenValuesNonInclusivePartial() {
        OSBTreeCursor<OCompositeKey, OIdentifiable> cursor = localSBTree.iterateEntriesBetween(compositeKey(2.0, 4.0), false, compositeKey(3.0), false, true);
        Set<ORID> orids = extractRids(cursor);
        Assert.assertEquals(orids.size(), 5);
        for (int i = 5; i <= 9; i++) {
            Assert.assertTrue(orids.contains(new ORecordId(2, i)));
        }
        cursor = localSBTree.iterateEntriesBetween(compositeKey(2.0, 4.0), false, compositeKey(3.0), false, false);
        orids = extractRids(cursor);
        Assert.assertEquals(orids.size(), 5);
        for (int i = 5; i <= 9; i++) {
            Assert.assertTrue(orids.contains(new ORecordId(2, i)));
        }
    }

    @Test
    public void testIterateValuesMajorInclusivePartial() {
        OSBTreeCursor<OCompositeKey, OIdentifiable> cursor = localSBTree.iterateEntriesMajor(compositeKey(2.0), true, true);
        Set<ORID> orids = extractRids(cursor);
        Assert.assertEquals(orids.size(), 18);
        for (int i = 2; i <= 3; i++)
            for (int j = 1; j <= 9; j++) {
                Assert.assertTrue(orids.contains(new ORecordId(i, j)));
            }

        cursor = localSBTree.iterateEntriesMajor(compositeKey(2.0), true, false);
        orids = extractRids(cursor);
        Assert.assertEquals(orids.size(), 18);
        for (int i = 2; i <= 3; i++)
            for (int j = 1; j <= 9; j++) {
                Assert.assertTrue(orids.contains(new ORecordId(i, j)));
            }

    }

    @Test
    public void testIterateMajorNonInclusivePartial() {
        OSBTreeCursor<OCompositeKey, OIdentifiable> cursor = localSBTree.iterateEntriesMajor(compositeKey(2.0), false, true);
        Set<ORID> orids = extractRids(cursor);
        Assert.assertEquals(orids.size(), 9);
        for (int i = 1; i <= 9; i++) {
            Assert.assertTrue(orids.contains(new ORecordId(3, i)));
        }
        cursor = localSBTree.iterateEntriesMajor(compositeKey(2.0), false, false);
        orids = extractRids(cursor);
        Assert.assertEquals(orids.size(), 9);
        for (int i = 1; i <= 9; i++) {
            Assert.assertTrue(orids.contains(new ORecordId(3, i)));
        }
    }

    @Test
    public void testIterateValuesMajorInclusive() {
        OSBTreeCursor<OCompositeKey, OIdentifiable> cursor = localSBTree.iterateEntriesMajor(compositeKey(2.0, 3.0), true, true);
        Set<ORID> orids = extractRids(cursor);
        Assert.assertEquals(orids.size(), 16);
        for (int i = 2; i <= 3; i++)
            for (int j = 1; j <= 9; j++) {
                if ((i == 2) && (j < 3))
                    continue;

                Assert.assertTrue(orids.contains(new ORecordId(i, j)));
            }

        cursor = localSBTree.iterateEntriesMajor(compositeKey(2.0, 3.0), true, false);
        orids = extractRids(cursor);
        Assert.assertEquals(orids.size(), 16);
        for (int i = 2; i <= 3; i++)
            for (int j = 1; j <= 9; j++) {
                if ((i == 2) && (j < 3))
                    continue;

                Assert.assertTrue(orids.contains(new ORecordId(i, j)));
            }

    }

    @Test
    public void testIterateValuesMajorNonInclusive() {
        OSBTreeCursor<OCompositeKey, OIdentifiable> cursor = localSBTree.iterateEntriesMajor(compositeKey(2.0, 3.0), false, true);
        Set<ORID> orids = extractRids(cursor);
        Assert.assertEquals(orids.size(), 15);
        for (int i = 2; i <= 3; i++)
            for (int j = 1; j <= 9; j++) {
                if ((i == 2) && (j <= 3))
                    continue;

                Assert.assertTrue(orids.contains(new ORecordId(i, j)));
            }

        cursor = localSBTree.iterateEntriesMajor(compositeKey(2.0, 3.0), false, false);
        orids = extractRids(cursor);
        Assert.assertEquals(orids.size(), 15);
        for (int i = 2; i <= 3; i++)
            for (int j = 1; j <= 9; j++) {
                if ((i == 2) && (j <= 3))
                    continue;

                Assert.assertTrue(orids.contains(new ORecordId(i, j)));
            }

    }

    @Test
    public void testIterateValuesMinorInclusivePartial() {
        OSBTreeCursor<OCompositeKey, OIdentifiable> cursor = localSBTree.iterateEntriesMinor(compositeKey(3.0), true, true);
        Set<ORID> orids = extractRids(cursor);
        Assert.assertEquals(orids.size(), 27);
        for (int i = 1; i <= 3; i++)
            for (int j = 1; j <= 9; j++) {
                Assert.assertTrue(orids.contains(new ORecordId(i, j)));
            }

        cursor = localSBTree.iterateEntriesMinor(compositeKey(3.0), true, false);
        orids = extractRids(cursor);
        Assert.assertEquals(orids.size(), 27);
        for (int i = 1; i <= 3; i++)
            for (int j = 1; j <= 9; j++) {
                Assert.assertTrue(orids.contains(new ORecordId(i, j)));
            }

    }

    @Test
    public void testIterateValuesMinorNonInclusivePartial() {
        OSBTreeCursor<OCompositeKey, OIdentifiable> cursor = localSBTree.iterateEntriesMinor(compositeKey(3.0), false, true);
        Set<ORID> orids = extractRids(cursor);
        Assert.assertEquals(orids.size(), 18);
        for (int i = 1; i < 3; i++)
            for (int j = 1; j <= 9; j++) {
                Assert.assertTrue(orids.contains(new ORecordId(i, j)));
            }

        cursor = localSBTree.iterateEntriesMinor(compositeKey(3.0), false, false);
        orids = extractRids(cursor);
        Assert.assertEquals(orids.size(), 18);
        for (int i = 1; i < 3; i++)
            for (int j = 1; j <= 9; j++) {
                Assert.assertTrue(orids.contains(new ORecordId(i, j)));
            }

    }

    @Test
    public void testIterateValuesMinorInclusive() {
        OSBTreeCursor<OCompositeKey, OIdentifiable> cursor = localSBTree.iterateEntriesMinor(compositeKey(3.0, 2.0), true, true);
        Set<ORID> orids = extractRids(cursor);
        Assert.assertEquals(orids.size(), 20);
        for (int i = 1; i <= 3; i++)
            for (int j = 1; j <= 9; j++) {
                if ((i == 3) && (j > 2))
                    continue;

                Assert.assertTrue(orids.contains(new ORecordId(i, j)));
            }

        cursor = localSBTree.iterateEntriesMinor(compositeKey(3.0, 2.0), true, false);
        orids = extractRids(cursor);
        Assert.assertEquals(orids.size(), 20);
        for (int i = 1; i <= 3; i++)
            for (int j = 1; j <= 9; j++) {
                if ((i == 3) && (j > 2))
                    continue;

                Assert.assertTrue(orids.contains(new ORecordId(i, j)));
            }

    }

    @Test
    public void testIterateValuesMinorNonInclusive() {
        OSBTreeCursor<OCompositeKey, OIdentifiable> cursor = localSBTree.iterateEntriesMinor(compositeKey(3.0, 2.0), false, true);
        Set<ORID> orids = extractRids(cursor);
        Assert.assertEquals(orids.size(), 19);
        for (int i = 1; i < 3; i++)
            for (int j = 1; j <= 9; j++) {
                Assert.assertTrue(orids.contains(new ORecordId(i, j)));
            }

        cursor = localSBTree.iterateEntriesMinor(compositeKey(3.0, 2.0), false, false);
        orids = extractRids(cursor);
        Assert.assertEquals(orids.size(), 19);
        for (int i = 1; i < 3; i++)
            for (int j = 1; j <= 9; j++) {
                Assert.assertTrue(orids.contains(new ORecordId(i, j)));
            }

    }
}

