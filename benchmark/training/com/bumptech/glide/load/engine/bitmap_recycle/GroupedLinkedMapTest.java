package com.bumptech.glide.load.engine.bitmap_recycle;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class GroupedLinkedMapTest {
    private GroupedLinkedMap<GroupedLinkedMapTest.Key, Object> map;

    @Test
    public void testReturnsNullForGetWithNoBitmap() {
        GroupedLinkedMapTest.Key key = /* width= */
        /* height= */
        new GroupedLinkedMapTest.Key("key", 1, 1);
        Assert.assertNull(map.get(key));
    }

    @Test
    public void testCanAddAndRemoveABitmap() {
        GroupedLinkedMapTest.Key key = new GroupedLinkedMapTest.Key("key", 1, 1);
        Object expected = new Object();
        map.put(key, expected);
        assertThat(map.get(key)).isEqualTo(expected);
    }

    @Test
    public void testCanAddAndRemoveMoreThanOneBitmapForAGivenKey() {
        GroupedLinkedMapTest.Key key = new GroupedLinkedMapTest.Key("key", 1, 1);
        Integer value = 20;
        int numToAdd = 10;
        for (int i = 0; i < numToAdd; i++) {
            map.put(key, value);
        }
        for (int i = 0; i < numToAdd; i++) {
            assertThat(map.get(key)).isEqualTo(value);
        }
    }

    @Test
    public void testLeastRecentlyRetrievedKeyIsLeastRecentlyUsed() {
        GroupedLinkedMapTest.Key firstKey = new GroupedLinkedMapTest.Key("key", 1, 1);
        Integer firstValue = 10;
        map.put(firstKey, firstValue);
        map.put(firstKey, firstValue);
        GroupedLinkedMapTest.Key secondKey = new GroupedLinkedMapTest.Key("key", 2, 2);
        Integer secondValue = 20;
        map.put(secondKey, secondValue);
        map.get(firstKey);
        assertThat(map.removeLast()).isEqualTo(secondValue);
    }

    @Test
    public void testAddingAnEntryDoesNotMakeItMostRecentlyUsed() {
        GroupedLinkedMapTest.Key firstKey = new GroupedLinkedMapTest.Key("key", 1, 1);
        Integer firstValue = 10;
        map.put(firstKey, firstValue);
        map.put(firstKey, firstValue);
        map.get(firstKey);
        Integer secondValue = 20;
        map.put(new GroupedLinkedMapTest.Key("key", 2, 2), secondValue);
        assertThat(map.removeLast()).isEqualTo(secondValue);
    }

    private static final class Key implements Poolable {
        private final String key;

        private final int width;

        private final int height;

        Key(String key, int width, int height) {
            this.key = key;
            this.width = width;
            this.height = height;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof GroupedLinkedMapTest.Key) {
                GroupedLinkedMapTest.Key other = ((GroupedLinkedMapTest.Key) (o));
                return ((key.equals(other.key)) && ((width) == (other.width))) && ((height) == (other.height));
            }
            return false;
        }

        @Override
        public int hashCode() {
            int result = ((key) != null) ? key.hashCode() : 0;
            result = (31 * result) + (width);
            result = (31 * result) + (height);
            return result;
        }

        @Override
        public void offer() {
            // Do nothing.
        }
    }
}

