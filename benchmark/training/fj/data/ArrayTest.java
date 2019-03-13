package fj.data;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by MarkPerry on 14 Feb 16.
 */
public class ArrayTest {
    @Test
    public void array_is_safe() {
        List<Integer> list = List.range(1, 2);
        Assert.assertThat(list.toArray().array(Integer[].class), CoreMatchers.instanceOf(Integer[].class));
    }
}

