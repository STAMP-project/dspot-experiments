package com.taobao.meta.test;


import com.taobao.metamorphosis.Message;
import com.taobao.metamorphosis.client.producer.SendResult;
import com.taobao.metamorphosis.consumer.MessageIterator;
import java.util.concurrent.atomic.AtomicInteger;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import org.junit.Assert;
import org.junit.Test;


/**
 * ?????????????
 *
 * @author boyan(boyan@taobao.com)
 * @unknown 2011-8-31
 */
public class OneProducerOneConsumerTxTimeoutTest extends BaseMetaTest {
    private final String topic = "meta-test";

    private final AtomicInteger formatIdIdGenerator = new AtomicInteger();

    @Test
    public void testTxTimeout() throws Exception {
        try {
            this.producer = createXAProducer();
            this.producer.publish(this.topic);
            final byte[] data = "hello world".getBytes();
            final Message msg = new Message(this.topic, data);
            final String uniqueQualifier = "testTxTimeout";
            final XAResource xares = getXAResource();
            final Xid xid = XIDGenerator.createXID(this.formatIdIdGenerator.incrementAndGet(), uniqueQualifier);
            // ??????????2??
            xares.setTransactionTimeout(2);
            xares.start(xid, XAResource.TMNOFLAGS);
            final SendResult result = this.producer.sendMessage(msg);
            if (!(result.isSuccess())) {
                xares.end(xid, XAResource.TMFAIL);
                xares.rollback(xid);
                throw new RuntimeException(("Send message failed:" + (result.getErrorMessage())));
            }
            // ???3??
            xares.end(xid, XAResource.TMSUCCESS);
            Thread.sleep(3000);
            // prepare???????
            try {
                xares.prepare(xid);
                Assert.fail();
            } catch (final XAException e) {
                e.printStackTrace();
            }
            this.createConsumer("consumer-test");
            // this.consumer.subscribe(this.topic, 1024,
            // null).completeSubscribe();
            System.out.println(result.getPartition());
            final MessageIterator it = this.consumer.get(this.topic, result.getPartition(), 0, 1024);
            Assert.assertNull(it);
        } finally {
            this.producer.shutdown();
            this.consumer.shutdown();
        }
    }
}

