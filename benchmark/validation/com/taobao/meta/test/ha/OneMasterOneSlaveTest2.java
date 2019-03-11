package com.taobao.meta.test.ha;


import com.taobao.meta.test.BaseMetaTest;
import com.taobao.meta.test.Utils;
import com.taobao.metamorphosis.EnhancedBroker;
import org.junit.Test;


/**
 *
 *
 * @author ???
 * @since 2011-7-12 ????04:29:02
 */
public class OneMasterOneSlaveTest2 extends HABaseMetaTest {
    private final String topic = "meta-test";

    private EnhancedBroker slaveBroker;

    @Test
    public void sendConsume() throws Exception {
        // ????????master????????????????????????????????slave??????????????????????????????????
        // ????????????????????????slave????master,??????????????;??????master
        // start master
        super.startServer("server1");
        super.createProducer();
        this.producer.publish(this.topic);
        super.createConsumer("group1");
        final int count = 5;
        super.sendMessage(count, "hello", this.topic);
        super.subscribe(this.topic, (1024 * 1024), count);
        // start slave
        this.log.info("------------start slave...--------------");
        this.slaveBroker = super.startSlaveServers("slave1-1", false, true);
        super.sendMessage(count, "hello", this.topic);
        // stop master
        Thread.sleep(6000);
        this.log.info("------------stop master...--------------");
        Utils.stopServers(super.brokers);
        super.subscribeRepeatable(this.topic, (1024 * 1024), (count * 2));
        // start master again
        this.log.info("------------start master again...--------------");
        super.startServer("server1", false, false);
        Thread.sleep(2000);
        super.sendMessage(count, "hello", this.topic);
        super.subscribeRepeatable(this.topic, (1024 * 1024), (count * 3));
        this.log.info("------------end--------------");
        Thread.sleep(3000);
    }
}

