package org.osmdroid.util;


import java.util.Random;
import junit.framework.Assert;
import org.junit.Test;


/**
 * Created by Fabrice on 31/12/2017.
 *
 * @since 6.0.0
 */
public class ListPointLTest {
    private static final Random random = new Random();

    @Test
    public void test() throws Exception {
        final int size = 100;
        final long[] values = new long[2 * size];
        final ListPointL list = new ListPointL();
        Assert.assertEquals(0, list.size());
        reload(values);
        check(values, list);
        list.clear();
        reload(values);
        check(values, list);
        list.clear();
        Assert.assertEquals(0, list.size());
    }
}

