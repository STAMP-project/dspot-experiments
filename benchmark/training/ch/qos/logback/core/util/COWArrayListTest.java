package ch.qos.logback.core.util;


import org.junit.Assert;
import org.junit.Test;


public class COWArrayListTest {
    Integer[] model = new Integer[0];

    COWArrayList<Integer> cowaList = new COWArrayList<Integer>(model);

    @Test
    public void basicToArray() {
        cowaList.add(1);
        Object[] result = cowaList.toArray();
        Assert.assertArrayEquals(new Integer[]{ 1 }, result);
    }

    @Test
    public void basicToArrayWithModel() {
        cowaList.add(1);
        Integer[] result = cowaList.toArray(model);
        Assert.assertArrayEquals(new Integer[]{ 1 }, result);
    }

    @Test
    public void basicToArrayTyped() {
        cowaList.add(1);
        Integer[] result = cowaList.asTypedArray();
        Assert.assertArrayEquals(new Integer[]{ 1 }, result);
    }
}

