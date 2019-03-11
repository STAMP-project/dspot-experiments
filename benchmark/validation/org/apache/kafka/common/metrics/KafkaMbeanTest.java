/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.common.metrics;


import java.lang.management.ManagementFactory;
import java.util.List;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.MBeanServer;
import javax.management.RuntimeMBeanException;
import org.apache.kafka.common.MetricName;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;


public class KafkaMbeanTest {
    private final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

    private Sensor sensor;

    private MetricName countMetricName;

    private MetricName sumMetricName;

    private Metrics metrics;

    @Test
    public void testGetAttribute() throws Exception {
        sensor.record(2.5);
        Object counterAttribute = getAttribute(countMetricName);
        Assert.assertEquals(1.0, counterAttribute);
        Object sumAttribute = getAttribute(sumMetricName);
        Assert.assertEquals(2.5, sumAttribute);
    }

    @Test
    public void testGetAttributeUnknown() throws Exception {
        sensor.record(2.5);
        try {
            getAttribute(sumMetricName, "name");
            Assert.fail("Should have gotten attribute not found");
        } catch (AttributeNotFoundException e) {
            // Expected
        }
    }

    @Test
    public void testGetAttributes() throws Exception {
        sensor.record(3.5);
        sensor.record(4.0);
        AttributeList attributeList = getAttributes(countMetricName, countMetricName.name(), sumMetricName.name());
        List<Attribute> attributes = attributeList.asList();
        Assert.assertEquals(2, attributes.size());
        for (Attribute attribute : attributes) {
            if (countMetricName.name().equals(attribute.getName()))
                Assert.assertEquals(2.0, attribute.getValue());
            else
                if (sumMetricName.name().equals(attribute.getName()))
                    Assert.assertEquals(7.5, attribute.getValue());
                else
                    Assert.fail(("Unexpected attribute returned: " + (attribute.getName())));


        }
    }

    @Test
    public void testGetAttributesWithUnknown() throws Exception {
        sensor.record(3.5);
        sensor.record(4.0);
        AttributeList attributeList = getAttributes(countMetricName, countMetricName.name(), sumMetricName.name(), "name");
        List<Attribute> attributes = attributeList.asList();
        Assert.assertEquals(2, attributes.size());
        for (Attribute attribute : attributes) {
            if (countMetricName.name().equals(attribute.getName()))
                Assert.assertEquals(2.0, attribute.getValue());
            else
                if (sumMetricName.name().equals(attribute.getName()))
                    Assert.assertEquals(7.5, attribute.getValue());
                else
                    Assert.fail(("Unexpected attribute returned: " + (attribute.getName())));


        }
    }

    @Test
    public void testInvoke() throws Exception {
        try {
            mBeanServer.invoke(objectName(countMetricName), "something", null, null);
            Assert.fail("invoke should have failed");
        } catch (RuntimeMBeanException e) {
            MatcherAssert.assertThat(e.getCause(), IsInstanceOf.instanceOf(UnsupportedOperationException.class));
        }
    }

    @Test
    public void testSetAttribute() throws Exception {
        try {
            mBeanServer.setAttribute(objectName(countMetricName), new Attribute("anything", 1));
            Assert.fail("setAttribute should have failed");
        } catch (RuntimeMBeanException e) {
            MatcherAssert.assertThat(e.getCause(), IsInstanceOf.instanceOf(UnsupportedOperationException.class));
        }
    }

    @Test
    public void testSetAttributes() throws Exception {
        try {
            mBeanServer.setAttributes(objectName(countMetricName), new AttributeList(1));
            Assert.fail("setAttributes should have failed");
        } catch (Exception e) {
            MatcherAssert.assertThat(e.getCause(), IsInstanceOf.instanceOf(UnsupportedOperationException.class));
        }
    }
}

