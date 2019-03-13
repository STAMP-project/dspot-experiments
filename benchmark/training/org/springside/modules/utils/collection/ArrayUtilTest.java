package org.springside.modules.utils.collection;


import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springside.modules.utils.number.RandomUtil;


public class ArrayUtilTest {
    @Test
    public void shuffle() {
        String[] arrays = new String[]{ "d", "a", "c", "b", "e", "i", "g" };
        String[] arraysClone = Arrays.copyOf(arrays, arrays.length);
        Arrays.sort(arrays);
        Assert.assertThat(arrays).containsExactly("a", "b", "c", "d", "e", "g", "i");
        ArrayUtil.shuffle(arrays);
        Assert.assertFalse("should not be equal to origin array", Arrays.equals(arrays, arraysClone));
        // System.out.println(Arrays.toString(arrays));
        Arrays.sort(arrays);
        ArrayUtil.shuffle(arrays, RandomUtil.secureRandom());
        Assert.assertFalse("should not be equal to origin array", Arrays.equals(arrays, arraysClone));
    }

    @Test
    public void asList() {
        List<String> list = ArrayUtil.asList("d", "a", "c", "b", "e", "i", "g");
        Assert.assertThat(list).hasSize(7).containsExactly("d", "a", "c", "b", "e", "i", "g");
        try {
            list.add("a");
            Assert.fail("should fail before");
        } catch (Throwable t) {
            Assert.assertThat(t).isInstanceOf(UnsupportedOperationException.class);
        }
        List<Integer> list3 = ArrayUtil.intAsList(1, 2, 3);
        Assert.assertThat(list3).hasSize(3).containsExactly(1, 2, 3);
        List<Long> list4 = ArrayUtil.longAsList(1L, 2L, 3L);
        Assert.assertThat(list4).hasSize(3).containsExactly(1L, 2L, 3L);
        List<Double> list5 = ArrayUtil.doubleAsList(1.1, 2.2, 3.3);
        Assert.assertThat(list5).hasSize(3).containsExactly(1.1, 2.2, 3.3);
    }

    @Test
    public void contact() {
        String[] array = new String[]{ "d", "a", "c" };
        Assert.assertThat(ArrayUtil.concat("z", array)).containsExactly("z", "d", "a", "c");
        Assert.assertThat(ArrayUtil.concat(array, "z")).containsExactly("d", "a", "c", "z");
    }
}

