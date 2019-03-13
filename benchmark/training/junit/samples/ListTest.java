package junit.samples;


import java.util.List;
import junit.framework.TestCase;


/**
 * A sample test case, testing {@link java.util.ArrayList}.
 */
public class ListTest extends TestCase {
    private List<Integer> emptyList;

    private List<Integer> fullList;

    public void testCapacity() {
        int size = fullList.size();
        for (int i = 0; i < 100; i++) {
            fullList.add(i);
        }
        TestCase.assertTrue(((fullList.size()) == (100 + size)));
    }

    public void testContains() {
        TestCase.assertTrue(fullList.contains(1));
        TestCase.assertFalse(emptyList.contains(1));
    }

    public void testElementAt() {
        int i = fullList.get(0);
        TestCase.assertEquals(1, i);
        try {
            fullList.get(fullList.size());
        } catch (IndexOutOfBoundsException e) {
            return;
        }
        TestCase.fail("Should raise an ArrayIndexOutOfBoundsException");
    }

    public void testRemoveAll() {
        fullList.removeAll(fullList);
        emptyList.removeAll(emptyList);
        TestCase.assertTrue(fullList.isEmpty());
        TestCase.assertTrue(emptyList.isEmpty());
    }

    public void testRemoveElement() {
        fullList.remove(Integer.valueOf(3));
        TestCase.assertFalse(fullList.contains(3));
    }
}

