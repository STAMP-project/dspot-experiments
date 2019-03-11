package com.blade.ioc;


import com.blade.ioc.bean.OrderComparator;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author biezhi
 * @unknown 2017/9/21
 */
public class OrderComparatorTest {
    @Test
    public void testOrder() {
        OrderComparator orderComparator = new OrderComparator();
        int compare = orderComparator.compare("a", "b");
        Assert.assertEquals(0, compare);
    }
}

