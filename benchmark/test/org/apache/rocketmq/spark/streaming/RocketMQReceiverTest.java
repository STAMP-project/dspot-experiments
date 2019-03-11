/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.rocketmq.spark.streaming;


import RocketMQConfig.CONSUMER_GROUP;
import RocketMQConfig.CONSUMER_TOPIC;
import RocketMQConfig.NAME_SERVER_ADDR;
import java.util.Properties;
import org.apache.rocketmq.spark.RocketMQServerMock;
import org.apache.rocketmq.spark.RocketMqUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.junit.Test;


public class RocketMQReceiverTest {
    private static RocketMQServerMock mockServer = new RocketMQServerMock(9877, 10002);

    private static final String NAMESERVER_ADDR = RocketMQReceiverTest.mockServer.getNameServerAddr();

    private static final String CONSUMER_GROUP = "wordcount";

    private static final String CONSUMER_TOPIC = "wordcountsource";

    @Test
    public void testRocketMQReceiver() {
        SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount");
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(5));
        Properties properties = new Properties();
        properties.setProperty(NAME_SERVER_ADDR, RocketMQReceiverTest.NAMESERVER_ADDR);
        properties.setProperty(RocketMQConfig.CONSUMER_GROUP, RocketMQReceiverTest.CONSUMER_GROUP);
        properties.setProperty(RocketMQConfig.CONSUMER_TOPIC, RocketMQReceiverTest.CONSUMER_TOPIC);
        JavaInputDStream ds = RocketMqUtils.createJavaMQPushStream(jssc, properties, StorageLevel.MEMORY_ONLY());
        ds.print();
        jssc.start();
        try {
            jssc.awaitTerminationOrTimeout(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        jssc.stop();
    }
}

