package com.ql.util.express.test;


import org.junit.Test;


/**
 * Created by tianqiao on 16/12/15.
 */
public class BitTest {
    @Test
    public void testBit() throws Exception {
        assert (~(-3)) == (~(-3L));
        IntBit();
        LongBit();
    }
}

