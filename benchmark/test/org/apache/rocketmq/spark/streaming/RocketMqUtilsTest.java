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


import RocketMQConfig.NAME_SERVER_ADDR;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spark.ConsumerStrategy;
import org.apache.rocketmq.spark.LocationStrategy;
import org.apache.rocketmq.spark.OffsetRange;
import org.apache.rocketmq.spark.RocketMQServerMock;
import org.apache.rocketmq.spark.RocketMqUtils;
import org.apache.rocketmq.spark.TopicQueueId;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.junit.Test;


public class RocketMqUtilsTest {
    private static RocketMQServerMock mockServer = new RocketMQServerMock(9876, 10001);

    private static String NAME_SERVER = RocketMqUtilsTest.mockServer.getNameServerAddr();

    private static String TOPIC_DEFAULT = UUID.randomUUID().toString();

    private static int MESSAGE_NUM = 100;

    @Test
    public void testConsumer() throws UnsupportedEncodingException, InterruptedException, MQBrokerException, MQClientException {
        // start up spark
        Map<String, String> optionParams = new HashMap<>();
        optionParams.put(NAME_SERVER_ADDR, RocketMqUtilsTest.NAME_SERVER);
        SparkConf sparkConf = new SparkConf().setAppName("JavaCustomReceiver").setMaster("local[*]");
        JavaStreamingContext sc = new JavaStreamingContext(sparkConf, new Duration(1000));
        List<String> topics = new ArrayList<>();
        topics.add(RocketMqUtilsTest.TOPIC_DEFAULT);
        LocationStrategy locationStrategy = LocationStrategy.PreferConsistent();
        JavaInputDStream<MessageExt> stream = RocketMqUtils.createJavaMQPullStream(sc, UUID.randomUUID().toString(), topics, ConsumerStrategy.earliest(), false, false, false, locationStrategy, optionParams);
        final Set<MessageExt> result = Collections.synchronizedSet(new HashSet<MessageExt>());
        stream.foreachRDD(new org.apache.spark.api.java.function.VoidFunction<org.apache.spark.api.java.JavaRDD<MessageExt>>() {
            @Override
            public void call(org.apache.spark.api.java.JavaRDD<MessageExt> messageExtJavaRDD) throws Exception {
                result.addAll(messageExtJavaRDD.collect());
            }
        });
        sc.start();
        long startTime = System.currentTimeMillis();
        boolean matches = false;
        while ((!matches) && (((System.currentTimeMillis()) - startTime) < 20000)) {
            matches = (RocketMqUtilsTest.MESSAGE_NUM) == (result.size());
            Thread.sleep(50);
        } 
        sc.stop();
    }

    @Test
    public void testGetOffsets() throws UnsupportedEncodingException, InterruptedException, MQBrokerException, MQClientException {
        Map<String, String> optionParams = new HashMap<>();
        optionParams.put(NAME_SERVER_ADDR, RocketMqUtilsTest.NAME_SERVER);
        SparkConf sparkConf = new SparkConf().setAppName("JavaCustomReceiver").setMaster("local[*]");
        JavaStreamingContext sc = new JavaStreamingContext(sparkConf, new Duration(1000));
        List<String> topics = new ArrayList<>();
        topics.add(RocketMqUtilsTest.TOPIC_DEFAULT);
        LocationStrategy locationStrategy = LocationStrategy.PreferConsistent();
        JavaInputDStream<MessageExt> dStream = RocketMqUtils.createJavaMQPullStream(sc, UUID.randomUUID().toString(), topics, ConsumerStrategy.earliest(), false, false, false, locationStrategy, optionParams);
        // hold a reference to the current offset ranges, so it can be used downstream
        final AtomicReference<Map<TopicQueueId, OffsetRange[]>> offsetRanges = new AtomicReference<>();
        final Set<MessageExt> result = Collections.synchronizedSet(new HashSet<MessageExt>());
        dStream.transform(new org.apache.spark.api.java.function.Function<org.apache.spark.api.java.JavaRDD<MessageExt>, org.apache.spark.api.java.JavaRDD<MessageExt>>() {
            @Override
            public org.apache.spark.api.java.JavaRDD<MessageExt> call(org.apache.spark.api.java.JavaRDD<MessageExt> v1) throws Exception {
                Map<TopicQueueId, OffsetRange[]> offsets = offsetRanges();
                offsetRanges.set(offsets);
                return v1;
            }
        }).foreachRDD(new org.apache.spark.api.java.function.VoidFunction<org.apache.spark.api.java.JavaRDD<MessageExt>>() {
            @Override
            public void call(org.apache.spark.api.java.JavaRDD<MessageExt> messageExtJavaRDD) throws Exception {
                result.addAll(messageExtJavaRDD.collect());
            }
        });
        sc.start();
        long startTime = System.currentTimeMillis();
        boolean matches = false;
        while ((!matches) && (((System.currentTimeMillis()) - startTime) < 10000)) {
            matches = (RocketMqUtilsTest.MESSAGE_NUM) == (result.size());
            Thread.sleep(50);
        } 
        sc.stop();
    }
}

