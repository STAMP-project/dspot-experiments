package com.ctrip.framework.apollo.tracer.internals;


import com.ctrip.framework.apollo.tracer.spi.MessageProducer;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public class NullMessageProducerTest {
    private MessageProducer messageProducer;

    @Test
    public void testNewTransaction() throws Exception {
        String someType = "someType";
        String someName = "someName";
        Assert.assertTrue(((messageProducer.newTransaction(someType, someName)) instanceof NullTransaction));
    }
}

