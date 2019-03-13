package zmq.util;


import java.util.Collection;
import java.util.Collections;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MultimapTest {
    private MultiMap<Long, Integer> map;

    @Test
    public void testInsertTwice() {
        boolean rc = map.insert(1L, 42);
        Assert.assertThat(rc, CoreMatchers.is(true));
        Assert.assertThat(map.entries().size(), CoreMatchers.is(1));
    }

    @Test
    public void testKey() {
        Long key = map.key(42);
        Assert.assertThat(key, CoreMatchers.is(1L));
    }

    @Test
    public void testContains() {
        Assert.assertThat(map.contains(42), CoreMatchers.is(true));
        assertSize(1);
    }

    @Test
    public void testEmpty() {
        Assert.assertThat(map.isEmpty(), CoreMatchers.is(false));
    }

    @Test
    public void testClear() {
        map.clear();
        assertSize(0);
    }

    @Test
    public void testHasValue() {
        Assert.assertThat(map.hasValues(1L), CoreMatchers.is(true));
        Assert.assertThat(map.hasValues(42L), CoreMatchers.is(false));
    }

    @Test
    public void testRemoveValue() {
        boolean rc = map.remove(42);
        Assert.assertThat(rc, CoreMatchers.is(true));
        assertSize(0);
    }

    @Test
    public void testRemoveWrongValue() {
        boolean rc = map.remove(41);
        Assert.assertThat(rc, CoreMatchers.is(false));
        assertSize(1);
    }

    @Test
    public void testRemoveKey() {
        Collection<Integer> removed = map.remove(1L);
        Assert.assertThat(removed, CoreMatchers.is(Collections.singletonList(42)));
        assertSize(0);
    }

    @Test
    public void testRemoveWrongKey() {
        Collection<Integer> removed = map.remove(1L);
        Assert.assertThat(removed, CoreMatchers.is(Collections.singletonList(42)));
        assertSize(0);
    }
}

