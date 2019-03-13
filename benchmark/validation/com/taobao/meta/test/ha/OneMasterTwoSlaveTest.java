package com.taobao.meta.test.ha;


import com.taobao.meta.test.BaseMetaTest;
import com.taobao.metamorphosis.EnhancedBroker;
import org.junit.Test;


/**
 *
 *
 * @author ???
 * @since 2011-7-12 ????04:28:29
 */
// note:??server???????????????????????,????????????????????????????
public class OneMasterTwoSlaveTest extends HABaseMetaTest {
    private final String topic = "meta-test";

    private EnhancedBroker slaveBroker1;

    private EnhancedBroker slaveBroker2;

    @Test
    public void sendConsume() throws Exception {
        // ????????master????????????????????????????????slave??????????????????????????????????
        // ???????slave
        // ?????slave,????;??????slave???
        // start master
        super.startServer("server1");
        super.createProducer();
        this.producer.publish(this.topic);
        super.createConsumer("group1");
        final int count = 5;
        super.sendMessage(count, "hello", this.topic);
        super.subscribe(this.topic, (1024 * 1024), count);
        // start slave1
        this.slaveBroker1 = super.startSlaveServers("slave1-1", false, true);
        this.log.info("------------slave1 started--------------");
        super.sendMessage(count, "hello", this.topic);
        super.subscribeRepeatable(this.topic, (1024 * 1024), (count * 2));
        // start slave2
        this.slaveBroker2 = super.startSlaveServers("slave1-2", false, true);
        this.log.info("------------slave2 started--------------");
        super.sendMessage(count, "hello", this.topic);
        super.subscribeRepeatable(this.topic, (1024 * 1024), (count * 3));
        // stop slave1
        this.slaveBroker1.stop();
        this.log.info("------------slave1 stop--------------");
        super.sendMessage(count, "hello", this.topic);
        super.subscribeRepeatable(this.topic, (1024 * 1024), (count * 4));
        // start slave1 again
        this.slaveBroker1 = super.startSlaveServers("slave1-1", false, false);
        this.log.info("------------slave1 started again--------------");
        super.sendMessage(count, "hello", this.topic);
        super.subscribeRepeatable(this.topic, (1024 * 1024), (count * 5));
        Thread.sleep(3000);
    }
}

