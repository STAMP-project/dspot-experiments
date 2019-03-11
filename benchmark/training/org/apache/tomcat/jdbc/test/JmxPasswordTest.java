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
package org.apache.tomcat.jdbc.test;


import PoolUtilities.PROP_PASSWORD;
import java.lang.management.ManagementFactory;
import java.util.Properties;
import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.tomcat.jdbc.pool.jmx.ConnectionPoolMBean;
import org.junit.Assert;
import org.junit.Test;


public class JmxPasswordTest extends DefaultTestCase {
    public static final String password = "password";

    public static final String username = "username";

    public ObjectName oname = null;

    @Test
    public void testPassword() throws Exception {
        Assert.assertEquals("Passwords should match when not using JMX.", JmxPasswordTest.password, datasource.getPoolProperties().getPassword());
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ConnectionPoolMBean mbean = JMX.newMBeanProxy(mbs, oname, ConnectionPoolMBean.class);
        String jmxPassword = mbean.getPassword();
        Properties jmxProperties = mbean.getDbProperties();
        Assert.assertFalse("Passwords should not match.", JmxPasswordTest.password.equals(jmxPassword));
        Assert.assertFalse("Password property should be missing", jmxProperties.containsKey(PoolUtilities.PROP_PASSWORD));
    }
}

