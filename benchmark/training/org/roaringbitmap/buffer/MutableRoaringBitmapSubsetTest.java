package org.roaringbitmap.buffer;


import com.google.common.base.Predicate;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class MutableRoaringBitmapSubsetTest {
    private static final Predicate<Integer> DIVISIBLE_BY_4 = ( i) -> (i % 4) == 0;

    private static final Predicate<Integer> DIVISIBLE_BY_3 = ( i) -> (i % 3) == 0;

    private final Set<Integer> superSet;

    private final Set<Integer> subSet;

    public MutableRoaringBitmapSubsetTest(Set<Integer> superSet, Set<Integer> subSet) {
        this.superSet = superSet;
        this.subSet = subSet;
    }

    @Test
    public void testProperSubset() {
        MutableRoaringBitmap superSetRB = create(superSet);
        MutableRoaringBitmap subSetRB = create(subSet);
        Assert.assertEquals(superSet.containsAll(subSet), superSetRB.contains(subSetRB));
        // reverse the test
        Assert.assertEquals(subSet.containsAll(superSet), subSetRB.contains(superSetRB));
    }
}

