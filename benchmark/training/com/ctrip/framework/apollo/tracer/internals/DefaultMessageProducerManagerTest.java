package com.ctrip.framework.apollo.tracer.internals;


import com.ctrip.framework.apollo.tracer.spi.MessageProducerManager;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public class DefaultMessageProducerManagerTest {
    private MessageProducerManager messageProducerManager;

    @Test
    public void testGetProducer() throws Exception {
        Assert.assertTrue(((messageProducerManager.getProducer()) instanceof NullMessageProducer));
    }
}

