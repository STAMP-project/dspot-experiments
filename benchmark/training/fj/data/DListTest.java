package fj.data;


import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class DListTest {
    @Test
    public void testConsSnoc() {
        Assert.assertThat(nil().snoc(2).cons(1).toJavaList(), Is.is(single(1).snoc(2).toJavaList()));
    }

    @Test
    public void testListDList() {
        DList.DList<Integer> d = listDList(List.range(0, 1000));
        Assert.assertThat(d.toJavaList(), Is.is(List.range(0, 1000).toJavaList()));
    }

    @Test
    public void testArrayDList() {
        DList.DList<Integer> d = arrayDList(Array.range(0, 1000).array(Integer[].class));
        Assert.assertThat(d.toJavaList(), Is.is(Array.range(0, 1000).toJavaList()));
    }

    @Test
    public void testIter() {
        DList.DList<Integer> d = iteratorDList(List.range(0, 1000).iterator());
        Assert.assertThat(d.toJavaList(), Is.is(List.range(0, 1000).toJavaList()));
    }
}

