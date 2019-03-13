package cucumber.runtime.io;


import java.util.Arrays;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;


public class FlatteningIteratorTest {
    @Test
    public void flattens_iterators() {
        final FlatteningIterator<Integer> fi = new FlatteningIterator<Integer>();
        fi.push(Arrays.asList(3, 4).iterator());
        fi.push(Arrays.asList(1, 2).iterator());
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4), toList(fi));
        Assert.assertFalse(fi.hasNext());
        try {
            fi.next();
            Assert.fail();
        } catch (NoSuchElementException expected) {
        }
    }
}

