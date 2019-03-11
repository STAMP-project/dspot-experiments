package fj.data;


import org.junit.Assert;
import org.junit.Test;


/**
 * Created by MarkPerry on 15/01/2015.
 */
public class OptionTest {
    @Test
    public void equals() {
        int max = 4;
        Assert.assertTrue(Option.some(1).equals(Option.some(1)));
        Assert.assertTrue(Option.some(List.range(1, max)).equals(Option.some(List.range(1, max))));
    }

    @Test
    public void traverseList() {
        int max = 3;
        List<Option<Integer>> actual = Option.some(max).traverseList(( a) -> List.range(1, (a + 1)));
        List<Option<Integer>> expected = List.range(1, (max + 1)).map(( i) -> some(i));
        Assert.assertTrue(actual.equals(expected));
    }
}

