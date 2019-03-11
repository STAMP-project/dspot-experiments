/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.bugs;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ2084Test {
    private static final Logger LOG = LoggerFactory.getLogger(AMQ2084Test.class);

    BrokerService broker;

    CountDownLatch qreceived;

    String connectionUri;

    @Test
    public void tryXpathSelectorMatch() throws Exception {
        String xPath = "XPATH '//books//book[@lang=''en'']'";
        listenQueue("Consumer.Sample.VirtualTopic.TestXpath", xPath);
        publish("VirtualTopic.TestXpath", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><books><book lang=\"en\">ABC</book></books>");
        Assert.assertTrue("topic received: ", qreceived.await(20, TimeUnit.SECONDS));
    }

    @Test
    public void tryXpathSelectorNoMatch() throws Exception {
        String xPath = "XPATH '//books//book[@lang=''es'']'";
        listenQueue("Consumer.Sample.VirtualTopic.TestXpath", xPath);
        publish("VirtualTopic.TestXpath", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><books><book lang=\"en\">ABC</book></books>");
        Assert.assertFalse("topic did not receive unmatched", qreceived.await(5, TimeUnit.SECONDS));
    }
}

