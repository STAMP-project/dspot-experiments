package com.thinkaurelius.titan.graphdb.query;


import Order.ASC;
import Order.DESC;
import com.thinkaurelius.titan.graphdb.internal.OrderList;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class QueryTest {
    private TitanGraph graph;

    private TitanTransaction tx;

    @Test
    public void testOrderList() {
        PropertyKey name = tx.makePropertyKey("name").dataType(String.class).make();
        PropertyKey weight = tx.makePropertyKey("weight").dataType(Double.class).make();
        PropertyKey time = tx.makePropertyKey("time").dataType(Long.class).make();
        OrderList ol1 = new OrderList();
        ol1.add(name, DESC);
        ol1.add(weight, ASC);
        ol1.makeImmutable();
        try {
            ol1.add(time, DESC);
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
        Assert.assertEquals(2, ol1.size());
        Assert.assertEquals(name, ol1.getKey(0));
        Assert.assertEquals(weight, ol1.getKey(1));
        Assert.assertEquals(DESC, ol1.getOrder(0));
        Assert.assertEquals(ASC, ol1.getOrder(1));
        Assert.assertFalse(ol1.hasCommonOrder());
        OrderList ol2 = new OrderList();
        ol2.add(time, ASC);
        ol2.add(weight, ASC);
        ol2.add(name, ASC);
        ol2.makeImmutable();
        Assert.assertTrue(ol2.hasCommonOrder());
        Assert.assertEquals(ASC, ol2.getCommonOrder());
        OrderList ol3 = new OrderList();
        ol3.add(weight, DESC);
        TitanVertex v1 = tx.addVertex("name", "abc", "time", 20, "weight", 2.5);
        TitanVertex v2 = tx.addVertex("name", "bcd", "time", 10, "weight", 2.5);
        TitanVertex v3 = tx.addVertex("name", "abc", "time", 10, "weight", 4.5);
        Assert.assertTrue(((ol1.compare(v1, v2)) > 0));
        Assert.assertTrue(((ol1.compare(v2, v3)) < 0));
        Assert.assertTrue(((ol1.compare(v1, v3)) < 0));
        Assert.assertTrue(((ol2.compare(v1, v2)) > 0));
        Assert.assertTrue(((ol2.compare(v2, v3)) < 0));
        Assert.assertTrue(((ol2.compare(v1, v3)) > 0));
        Assert.assertTrue(((ol3.compare(v1, v2)) == 0));
        Assert.assertTrue(((ol3.compare(v2, v3)) > 0));
        Assert.assertTrue(((ol3.compare(v1, v3)) > 0));
    }
}

