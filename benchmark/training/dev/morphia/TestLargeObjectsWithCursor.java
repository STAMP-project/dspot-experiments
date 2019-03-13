package dev.morphia;


import dev.morphia.annotations.Entity;
import dev.morphia.query.Query;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test from list, but doesn't seems to be a problem. Here as an example.
 */
public class TestLargeObjectsWithCursor extends TestBase {
    private int documentsNb;

    @Test
    public void testWithManyElementsInCollection() {
        Query<TestLargeObjectsWithCursor.E> query = getDs().find(TestLargeObjectsWithCursor.E.class);
        final long countAll = query.count();
        query = getDs().find(TestLargeObjectsWithCursor.E.class);
        final List<TestLargeObjectsWithCursor.E> list = TestBase.toList(query.find());
        Assert.assertEquals(documentsNb, countAll);
        Assert.assertEquals(documentsNb, list.size());
    }

    @Entity
    public static class E extends TestMapping.BaseEntity {
        private final Integer index;

        private final byte[] largeContent;

        public E() {
            index = null;
            largeContent = null;
        }

        public E(final int i) {
            index = i;
            largeContent = createLargeByteArray();
        }

        public Integer getIndex() {
            return index;
        }

        private byte[] createLargeByteArray() {
            final int size = ((int) (4000 + ((Math.random()) * 10000)));
            final byte[] arr = new byte[size];
            for (int i = 0; i < (arr.length); i++) {
                arr[i] = 'a';
            }
            return arr;
        }
    }
}

