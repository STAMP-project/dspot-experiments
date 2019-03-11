package fj.data;


import List.Buffer;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by MarkPerry on 17/08/2015.
 */
public class ListBufferTest {
    @Test
    public void testSnoc() {
        // test case for #181
        List.Buffer<Integer> buf = Buffer.empty();
        buf.snoc(1).snoc(2).snoc(3);
        List<Integer> list1 = buf.toList();
        buf.snoc(4);
        List<Integer> list2 = buf.toList();
        Assert.assertThat(list2, CoreMatchers.equalTo(Stream.range(1, 5).toList()));
    }
}

