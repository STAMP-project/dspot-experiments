/**
 * Copyright 2014-2016 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaaproject.kaa.common.channels.communication;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrey Panasenko
 */
public class IpParametersTest {
    /**
     * Test method for {@link IpParameters#hashCode()}.
     */
    @Test
    public void testHashCode() {
        IpParameters p1 = new IpParameters();
        IpParameters p2 = new IpParameters();
        p1.setHostName("host1");
        p2.setHostName("host1");
        p1.setPort(10);
        p2.setPort(10);
        Assert.assertEquals(p1.hashCode(), p2.hashCode());
        IpParameters p3 = new IpParameters();
        p3.setPort(10);
        Assert.assertNotEquals(p1.hashCode(), p3.hashCode());
        Assert.assertNotEquals(p2.hashCode(), p3.hashCode());
    }

    /**
     * Test method for {@link IpParameters#getHostName()}.
     */
    @Test
    public void testGetHostName() {
        IpParameters p1 = new IpParameters();
        p1.setHostName("host1");
        Assert.assertEquals("host1", p1.getHostName());
    }

    /**
     * Test method for {@link IpParameters#getPort()}.
     */
    @Test
    public void testGetPort() {
        IpParameters p1 = new IpParameters();
        p1.setPort(100);
        Assert.assertEquals(100, p1.getPort());
    }

    /**
     * Test method for {@link IpParameters#toString()}.
     */
    @Test
    public void testToString() {
        IpParameters p1 = new IpParameters();
        p1.setHostName("host1");
        p1.setPort(100);
        Assert.assertEquals("IpParameters [hostName=host1, port=100]", p1.toString());
    }

    /**
     * Test method for {@link IpParameters#equals(java.lang.Object)}.
     */
    @Test
    public void testEqualsObject() {
        IpParameters p1 = new IpParameters();
        IpParameters p2 = new IpParameters();
        p1.setHostName("host1");
        p2.setHostName("host1");
        p1.setPort(10);
        p2.setPort(10);
        if (!(p1.equals(p2))) {
            Assert.fail("TestEquals to objects failed");
        }
        if (!(p1.equals(p1))) {
            Assert.fail("TestEquals to himself to objects failed");
        }
        if (p1.equals(null)) {
            Assert.fail("TestEquals to null objects failed");
        }
        if (p1.equals(new Object())) {
            Assert.fail("TestEquals to Object() objects failed");
        }
        IpParameters p3 = new IpParameters();
        IpParameters p4 = new IpParameters();
        if (p3.equals(p1)) {
            Assert.fail("TestEquals to not equals objects failed");
        }
        if (!(p3.equals(p4))) {
            Assert.fail("TestEquals to not equals objects failed");
        }
        p3.setHostName("host2");
        if (p1.equals(p3)) {
            Assert.fail("TestEquals to not equals objects failed");
        }
        p4.setHostName("host1");
        p4.setPort(20);
        if (p1.equals(p4)) {
            Assert.fail("TestEquals to not equals objects failed");
        }
    }
}

