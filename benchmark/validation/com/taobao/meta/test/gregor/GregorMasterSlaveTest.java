package com.taobao.meta.test.gregor;


import com.taobao.meta.test.BaseMetaTest;
import com.taobao.meta.test.Utils;
import com.taobao.metamorphosis.EnhancedBroker;
import java.util.Map;
import java.util.Properties;
import org.junit.Test;


/**
 *
 *
 * @author boyan(boyan@taobao.com)
 * @unknown 2011-12-29
 */
public class GregorMasterSlaveTest extends BaseMetaTest {
    // private final List<EnhancedBroker> slaveBrokers = new
    // ArrayList<EnhancedBroker>();
    private final String topic = "meta-test";

    private EnhancedBroker master;

    private EnhancedBroker slave;

    @Test
    public void testOneMasterOneSlaveOneProducerOneConsumer() throws Exception {
        // start slave
        this.slave = this.startEnhanceBroker("gregor_server1", true, true, this.getSlaveProperties());
        // start master
        this.master = this.startEnhanceBroker("samsa_server1", true, true, this.getMasterProperties());
        try {
            this.createProducer();
            this.producer.publish(this.topic);
            final byte[] data = "testOneMasterOneSlaveOneProducerOneConsumer".getBytes();
            this.sendMessage(100, data, this.topic);
            final String group = "GregorMasterSlaveTest";
            this.createConsumer(group);
            this.subscribe(this.topic, (1024 * 1024), 100);
        } finally {
            this.producer.shutdown();
            this.consumer.shutdown();
        }
    }

    @Test
    public void testOneMasterOneSlaveNProducerNConsumer() throws Exception {
        // start slave
        this.slave = this.startEnhanceBroker("gregor_server1", true, true, this.getSlaveProperties());
        // start master
        this.master = this.startEnhanceBroker("samsa_server1", true, true, this.getMasterProperties());
        try {
            this.create_nProducer(10);
            this.sendMessage_nProducer(100, "testOneMasterOneSlaveNProducerNConsumer", this.topic, 10);
            this.subscribe_nConsumer(this.topic, (1024 * 1024), 100, 10, 10);
            // this.subscribe(this.topic, 1024 * 1024, 100);
        } finally {
            Utils.shutdown(this.producerList);
            Utils.shutdown(this.consumerList);
        }
    }

    @Test
    public void testSlaveAsMaster() throws Exception {
        // ?????100???????master??slave
        // start slave
        this.testOneMasterOneSlaveOneProducerOneConsumer();
        try {
            this.stopMasterSlave();
            this.queue.clear();
            Thread.sleep(5000);
            // ???slave???master??????master???slave???????????100??
            this.slave = this.startEnhanceBroker("samsa_server1", false, false, this.getSlaveProperties());
            final Map<String, Properties> masterProperties = this.getMasterProperties();
            // ????slave??8123???
            masterProperties.get("samsa").put("slave", "localhost:8123");
            this.master = this.startEnhanceBroker("gregor_server1", false, false, masterProperties);
            // ???producer??consumer????
            Thread.sleep(2000);
            final byte[] data = "testSlaveAsMaster".getBytes();
            this.createProducer();
            this.producer.publish(this.topic);
            this.sendMessage(100, data, this.topic);
            final String group = "GregorMasterSlaveTest";
            this.createConsumer(group);
            this.subscribe(this.topic, (1024 * 1024), 100);
        } finally {
            Utils.shutdown(this.producer);
            Utils.shutdown(this.consumer);
            Utils.shutdown(this.producerList);
            Utils.shutdown(this.consumerList);
        }
    }
}

