package redis.clients.jedis.tests.collections;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class SetFromListTest {
    private static Method method;

    @Test
    public void setOperations() throws Exception {
        // add
        Set<String> cut = setFromList(new ArrayList<String>());
        cut.add("A");
        cut.add("B");
        cut.add("A");
        Assert.assertEquals(2, cut.size());
        // remove
        cut.remove("A");
        Assert.assertEquals(1, cut.size());
        cut.remove("C");
        Assert.assertEquals(1, cut.size());
        // contains
        Assert.assertTrue(cut.contains("B"));
        Assert.assertFalse(cut.contains("A"));
        cut.add("C");
        cut.add("D");
        // containsAll
        Assert.assertTrue(cut.containsAll(cut));
        // retainAll
        cut.retainAll(Arrays.asList("C", "D"));
        Assert.assertEquals(2, cut.size());
        Assert.assertTrue(cut.contains("C"));
        Assert.assertTrue(cut.contains("D"));
        // removeAll
        cut.removeAll(Arrays.asList("C"));
        Assert.assertEquals(1, cut.size());
        Assert.assertTrue(cut.contains("D"));
        // clear
        cut.clear();
        Assert.assertTrue(cut.isEmpty());
    }

    @Test
    public void iteration() throws Exception {
        List<String> list = new ArrayList<String>();
        for (int i = 'a'; i <= 'z'; i++) {
            list.add(String.valueOf(((char) (i))));
        }
        Set<String> cut = setFromList(list);
        // ordering guarantee
        int i = 0;
        for (String x : cut) {
            Assert.assertEquals(list.get((i++)), x);
        }
    }

    @Test
    public void equals() throws Exception {
        Set<String> hashSet = new HashSet<String>();
        for (int i = 'a'; i <= 'z'; i++) {
            hashSet.add(String.valueOf(((char) (i))));
        }
        Set<String> cut = setFromList(new ArrayList<String>(hashSet));
        Assert.assertTrue(hashSet.equals(cut));
        Assert.assertTrue(cut.equals(hashSet));
        // equals with null
        Assert.assertFalse(cut.equals(null));
        // equals with other types
        Assert.assertFalse(cut.equals(new ArrayList<String>()));
    }
}

