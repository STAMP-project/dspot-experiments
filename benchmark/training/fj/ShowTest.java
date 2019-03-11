package fj;


import Show.intShow;
import fj.data.Array;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by MarkPerry on 4/06/2015.
 */
public class ShowTest {
    @Test
    public void arrayShow() {
        Array<Integer> a = Array.array(3, 5, 7);
        String s = Show.arrayShow(intShow).showS(a);
        Assert.assertTrue(s.equals("Array(3,5,7)"));
    }
}

