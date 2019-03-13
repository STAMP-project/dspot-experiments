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
package org.apache.activemq.karaf.itest;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;


@RunWith(PaxExam.class)
public class ActiveMQBrokerNdCamelFeatureTest extends AbstractFeatureTest {
    @Test(timeout = (2 * 60) * 1000)
    public void test() throws Throwable {
        System.err.println(executeCommand("feature:list -i").trim());
        assertFeatureInstalled("activemq");
        assertBrokerStarted();
        AbstractFeatureTest.withinReason(new Runnable() {
            public void run() {
                getBundle("org.apache.activemq.activemq-camel");
                Assert.assertTrue("we have camel consumers", executeCommand("activemq:dstat").trim().contains("camel_in"));
            }
        });
        // produce and consume
        JMSTester jms = new JMSTester();
        jms.produceMessage("camel_in");
        Assert.assertEquals("got our message", "camel_in", jms.consumeMessage("camel_out"));
        jms.close();
    }
}

