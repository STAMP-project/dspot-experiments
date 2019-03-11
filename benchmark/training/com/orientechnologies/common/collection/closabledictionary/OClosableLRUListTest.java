package com.orientechnologies.common.collection.closabledictionary;


import org.junit.Assert;
import org.junit.Test;


public class OClosableLRUListTest {
    @Test
    public void tesMoveToTail() {
        OClosableLRUList<Long, OClosableLRUListTest.CIItem> lruList = new OClosableLRUList<Long, OClosableLRUListTest.CIItem>();
        OClosableEntry<Long, OClosableLRUListTest.CIItem> firstEntry = new OClosableEntry<Long, OClosableLRUListTest.CIItem>(new OClosableLRUListTest.CIItem());
        Assert.assertTrue((!(lruList.contains(firstEntry))));
        Assert.assertEquals(lruList.size(), 0);
        lruList.moveToTheTail(firstEntry);
        Assert.assertEquals(lruList.size(), 1);
        Assert.assertTrue(lruList.contains(firstEntry));
        assertContent(lruList, new OClosableEntry[]{ firstEntry });
        Assert.assertTrue(lruList.assertBackwardStructure());
        Assert.assertTrue(lruList.assertForwardStructure());
        OClosableEntry<Long, OClosableLRUListTest.CIItem> secondEntry = new OClosableEntry<Long, OClosableLRUListTest.CIItem>(new OClosableLRUListTest.CIItem());
        lruList.moveToTheTail(secondEntry);
        Assert.assertEquals(lruList.size(), 2);
        Assert.assertTrue(lruList.contains(firstEntry));
        Assert.assertTrue(lruList.contains(secondEntry));
        assertContent(lruList, new OClosableEntry[]{ firstEntry, secondEntry });
        Assert.assertTrue(lruList.assertBackwardStructure());
        Assert.assertTrue(lruList.assertForwardStructure());
        OClosableEntry<Long, OClosableLRUListTest.CIItem> thirdEntry = new OClosableEntry<Long, OClosableLRUListTest.CIItem>(new OClosableLRUListTest.CIItem());
        lruList.moveToTheTail(thirdEntry);
        Assert.assertEquals(lruList.size(), 3);
        Assert.assertTrue(lruList.contains(firstEntry));
        Assert.assertTrue(lruList.contains(secondEntry));
        Assert.assertTrue(lruList.contains(thirdEntry));
        assertContent(lruList, new OClosableEntry[]{ firstEntry, secondEntry, thirdEntry });
        Assert.assertTrue(lruList.assertBackwardStructure());
        Assert.assertTrue(lruList.assertForwardStructure());
        lruList.moveToTheTail(secondEntry);
        Assert.assertEquals(lruList.size(), 3);
        Assert.assertTrue(lruList.contains(firstEntry));
        Assert.assertTrue(lruList.contains(secondEntry));
        Assert.assertTrue(lruList.contains(thirdEntry));
        assertContent(lruList, new OClosableEntry[]{ firstEntry, thirdEntry, secondEntry });
        Assert.assertTrue(lruList.assertBackwardStructure());
        Assert.assertTrue(lruList.assertForwardStructure());
        lruList.moveToTheTail(firstEntry);
        Assert.assertEquals(lruList.size(), 3);
        Assert.assertTrue(lruList.contains(firstEntry));
        Assert.assertTrue(lruList.contains(secondEntry));
        Assert.assertTrue(lruList.contains(thirdEntry));
        assertContent(lruList, new OClosableEntry[]{ thirdEntry, secondEntry, firstEntry });
        Assert.assertTrue(lruList.assertBackwardStructure());
        Assert.assertTrue(lruList.assertForwardStructure());
        lruList.moveToTheTail(firstEntry);
        Assert.assertEquals(lruList.size(), 3);
        Assert.assertTrue(lruList.contains(firstEntry));
        Assert.assertTrue(lruList.contains(secondEntry));
        Assert.assertTrue(lruList.contains(thirdEntry));
        assertContent(lruList, new OClosableEntry[]{ thirdEntry, secondEntry, firstEntry });
        Assert.assertTrue(lruList.assertBackwardStructure());
        Assert.assertTrue(lruList.assertForwardStructure());
    }

    @Test
    public void tesRemove() {
        OClosableLRUList<Long, OClosableLRUListTest.CIItem> lruList = new OClosableLRUList<Long, OClosableLRUListTest.CIItem>();
        OClosableEntry<Long, OClosableLRUListTest.CIItem> firstEntry = new OClosableEntry<Long, OClosableLRUListTest.CIItem>(new OClosableLRUListTest.CIItem());
        OClosableEntry<Long, OClosableLRUListTest.CIItem> secondEntry = new OClosableEntry<Long, OClosableLRUListTest.CIItem>(new OClosableLRUListTest.CIItem());
        OClosableEntry<Long, OClosableLRUListTest.CIItem> thirdEntry = new OClosableEntry<Long, OClosableLRUListTest.CIItem>(new OClosableLRUListTest.CIItem());
        lruList.moveToTheTail(firstEntry);
        lruList.moveToTheTail(secondEntry);
        lruList.moveToTheTail(thirdEntry);
        lruList.remove(firstEntry);
        assertContent(lruList, new OClosableEntry[]{ secondEntry, thirdEntry });
        Assert.assertEquals(lruList.size(), 2);
        Assert.assertTrue((!(lruList.contains(firstEntry))));
        Assert.assertTrue(lruList.contains(secondEntry));
        Assert.assertTrue(lruList.contains(thirdEntry));
        Assert.assertTrue(lruList.assertBackwardStructure());
        Assert.assertTrue(lruList.assertForwardStructure());
        lruList.remove(thirdEntry);
        assertContent(lruList, new OClosableEntry[]{ secondEntry });
        Assert.assertEquals(lruList.size(), 1);
        Assert.assertTrue((!(lruList.contains(firstEntry))));
        Assert.assertTrue(lruList.contains(secondEntry));
        Assert.assertTrue((!(lruList.contains(thirdEntry))));
        Assert.assertTrue(lruList.assertBackwardStructure());
        Assert.assertTrue(lruList.assertForwardStructure());
        lruList.remove(secondEntry);
        assertContent(lruList, new OClosableEntry[]{  });
        Assert.assertEquals(lruList.size(), 0);
        Assert.assertTrue((!(lruList.contains(firstEntry))));
        Assert.assertTrue((!(lruList.contains(secondEntry))));
        Assert.assertTrue((!(lruList.contains(thirdEntry))));
        Assert.assertTrue(lruList.assertBackwardStructure());
        Assert.assertTrue(lruList.assertForwardStructure());
        lruList.remove(secondEntry);
        assertContent(lruList, new OClosableEntry[]{  });
        Assert.assertEquals(lruList.size(), 0);
        Assert.assertTrue((!(lruList.contains(firstEntry))));
        Assert.assertTrue((!(lruList.contains(secondEntry))));
        Assert.assertTrue((!(lruList.contains(thirdEntry))));
        Assert.assertTrue(lruList.assertBackwardStructure());
        Assert.assertTrue(lruList.assertForwardStructure());
        lruList.moveToTheTail(firstEntry);
        lruList.moveToTheTail(secondEntry);
        lruList.moveToTheTail(thirdEntry);
        lruList.remove(secondEntry);
        assertContent(lruList, new OClosableEntry[]{ firstEntry, thirdEntry });
        Assert.assertEquals(lruList.size(), 2);
        Assert.assertTrue(lruList.contains(firstEntry));
        Assert.assertTrue((!(lruList.contains(secondEntry))));
        Assert.assertTrue(lruList.contains(thirdEntry));
        Assert.assertTrue(lruList.assertBackwardStructure());
        Assert.assertTrue(lruList.assertForwardStructure());
        lruList.moveToTheTail(secondEntry);
        assertContent(lruList, new OClosableEntry[]{ firstEntry, thirdEntry, secondEntry });
        Assert.assertEquals(lruList.size(), 3);
        lruList.remove(secondEntry);
        assertContent(lruList, new OClosableEntry[]{ firstEntry, thirdEntry });
        Assert.assertEquals(lruList.size(), 2);
        Assert.assertTrue(lruList.contains(firstEntry));
        Assert.assertTrue((!(lruList.contains(secondEntry))));
        Assert.assertTrue(lruList.contains(thirdEntry));
        Assert.assertTrue(lruList.assertBackwardStructure());
        Assert.assertTrue(lruList.assertForwardStructure());
    }

    @Test
    public void testPool() {
        OClosableLRUList<Long, OClosableLRUListTest.CIItem> lruList = new OClosableLRUList<Long, OClosableLRUListTest.CIItem>();
        OClosableEntry<Long, OClosableLRUListTest.CIItem> firstEntry = new OClosableEntry<Long, OClosableLRUListTest.CIItem>(new OClosableLRUListTest.CIItem());
        OClosableEntry<Long, OClosableLRUListTest.CIItem> secondEntry = new OClosableEntry<Long, OClosableLRUListTest.CIItem>(new OClosableLRUListTest.CIItem());
        OClosableEntry<Long, OClosableLRUListTest.CIItem> thirdEntry = new OClosableEntry<Long, OClosableLRUListTest.CIItem>(new OClosableLRUListTest.CIItem());
        lruList.moveToTheTail(firstEntry);
        lruList.moveToTheTail(secondEntry);
        lruList.moveToTheTail(thirdEntry);
        OClosableEntry<Long, OClosableLRUListTest.CIItem> removed = lruList.poll();
        Assert.assertTrue((removed == firstEntry));
        Assert.assertEquals(lruList.size(), 2);
        Assert.assertTrue((!(lruList.contains(firstEntry))));
        Assert.assertTrue(lruList.contains(secondEntry));
        Assert.assertTrue(lruList.contains(thirdEntry));
        assertContent(lruList, new OClosableEntry[]{ secondEntry, thirdEntry });
        Assert.assertTrue(lruList.assertBackwardStructure());
        Assert.assertTrue(lruList.assertForwardStructure());
        removed = lruList.poll();
        Assert.assertTrue((removed == secondEntry));
        Assert.assertEquals(lruList.size(), 1);
        Assert.assertTrue((!(lruList.contains(firstEntry))));
        Assert.assertTrue((!(lruList.contains(secondEntry))));
        Assert.assertTrue(lruList.contains(thirdEntry));
        assertContent(lruList, new OClosableEntry[]{ thirdEntry });
        Assert.assertTrue(lruList.assertBackwardStructure());
        Assert.assertTrue(lruList.assertForwardStructure());
        removed = lruList.poll();
        Assert.assertTrue((removed == thirdEntry));
        Assert.assertEquals(lruList.size(), 0);
        Assert.assertTrue((!(lruList.contains(firstEntry))));
        Assert.assertTrue((!(lruList.contains(secondEntry))));
        Assert.assertTrue((!(lruList.contains(thirdEntry))));
        assertContent(lruList, new OClosableEntry[]{  });
        Assert.assertTrue(lruList.assertBackwardStructure());
        Assert.assertTrue(lruList.assertForwardStructure());
        removed = lruList.poll();
        Assert.assertTrue((removed == null));
    }

    public class CIItem implements OClosableItem {
        private volatile boolean open = true;

        @Override
        public boolean isOpen() {
            return open;
        }

        @Override
        public void close() {
            open = false;
        }

        @Override
        public void open() {
            open = true;
        }
    }
}

