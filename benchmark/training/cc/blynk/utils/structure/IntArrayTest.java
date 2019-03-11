package cc.blynk.utils.structure;


import cc.blynk.utils.IntArray;
import org.junit.Assert;
import org.junit.Test;


public class IntArrayTest {
    @Test
    public void test() {
        IntArray intArray = new IntArray();
        Assert.assertEquals(0, intArray.toArray().length);
        intArray.add(10);
        int[] result = intArray.toArray();
        Assert.assertEquals(1, result.length);
        Assert.assertEquals(10, result[0]);
        intArray = new IntArray();
        for (int i = 0; i < 1000; i++) {
            intArray.add(i);
        }
        result = intArray.toArray();
        Assert.assertEquals(1000, result.length);
        for (int i = 0; i < 1000; i++) {
            Assert.assertEquals(i, result[i]);
        }
    }
}

