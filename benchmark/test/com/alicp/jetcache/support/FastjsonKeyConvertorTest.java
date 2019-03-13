/**
 * Created on  13-09-23 17:02
 */
package com.alicp.jetcache.support;


import com.alicp.jetcache.test.support.DynamicQuery;
import org.junit.Assert;
import org.junit.Test;

import static FastjsonKeyConvertor.INSTANCE;


/**
 *
 *
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
public class FastjsonKeyConvertorTest {
    static class C extends DynamicQuery {
        private FastjsonKeyConvertorTest.C mate;

        public FastjsonKeyConvertorTest.C getMate() {
            return mate;
        }

        public void setMate(FastjsonKeyConvertorTest.C mate) {
            this.mate = mate;
        }
    }

    @Test
    public void test() {
        FastjsonKeyConvertor g = INSTANCE;
        Object k1;
        Object k2;
        Object k3;
        k1 = g.apply(new Object[]{ 10, 20, 10 });
        k2 = g.apply(new Object[]{ 10, 20, 10 });
        k3 = g.apply(new Object[]{ 20, 10, 10 });
        Assert.assertEquals(k1, k2);
        Assert.assertNotEquals(k1, k3);
        k1 = g.apply(new Object[]{ 10, null });
        k2 = g.apply(new Object[]{ 10, "" });
        Assert.assertNotEquals(k1, k2);
        FastjsonKeyConvertorTest.C q1 = new FastjsonKeyConvertorTest.C();
        FastjsonKeyConvertorTest.C q2 = new FastjsonKeyConvertorTest.C();
        Assert.assertEquals(g.apply(new Object[]{ q1 }), g.apply(new Object[]{ q2 }));
        setId(10000);
        setId(10000);
        setName("N1");
        Assert.assertNotEquals(g.apply(new Object[]{ q1 }), g.apply(new Object[]{ q2 }));
        setName("N1");
        Assert.assertEquals(g.apply(new Object[]{ q1 }), g.apply(new Object[]{ q2 }));
        q1.setMate(q2);
        Assert.assertNotEquals(g.apply(new Object[]{ q1 }), g.apply(new Object[]{ q2 }));
        q2.setMate(q1);
        Assert.assertEquals(g.apply(new Object[]{ q1 }), g.apply(new Object[]{ q2 }));
        FastjsonKeyConvertorTest.C q3 = new FastjsonKeyConvertorTest.C();
        setId(20);
        q1.setMate(q3);
        Assert.assertNotEquals(g.apply(new Object[]{ q1 }), g.apply(new Object[]{ q2 }));
        q2.setMate(q3);
        Assert.assertEquals(g.apply(new Object[]{ q1 }), g.apply(new Object[]{ q2 }));
    }
}

