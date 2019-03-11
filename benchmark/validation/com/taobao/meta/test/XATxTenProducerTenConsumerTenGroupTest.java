package com.taobao.meta.test;


import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;


/**
 * ????????????????
 *
 * @author boyan(boyan@taobao.com)
 * @unknown 2011-8-31
 */
public class XATxTenProducerTenConsumerTenGroupTest extends BaseMetaTest {
    private final String topic = "meta-test";

    private final String UNIQUE_QUALIFIER = "XATxTenProducerTenConsumerTenGroupTest";

    private final AtomicInteger formatIdIdGenerator = new AtomicInteger();

    @Test
    public void sendConsume() throws Exception {
        this.create_nXAProducer(10);
        try {
            // ???????????????????
            final int count = 10;
            this.xaTxSendMessage_nProducer(count, "hello", this.topic, 10);
            // ??????????????????????
            this.subscribe_nConsumer(this.topic, (1024 * 1024), (count / 2), 10, 10);
        } catch (final Throwable e) {
            e.printStackTrace();
        } finally {
            for (int i = 0; i < 10; i++) {
                this.producerList.get(i).shutdown();
                this.consumerList.get(i).shutdown();
            }
        }
    }
}

